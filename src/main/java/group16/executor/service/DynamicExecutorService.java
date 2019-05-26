package group16.executor.service;

import group16.executor.service.task.management.NonEmptyRoundRobinTaskManager;
import group16.executor.service.task.management.TaskManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class DynamicExecutorService extends AbstractExecutorService {

    public DynamicExecutorService(int numberOfThreads, int numberOfQueues, int sampleRate) {
        this.sampleRate = sampleRate;

        // Set up watcher thread for counting tasks per time unit
        // TODO: We should find a way of doing this in LocalMetrics or GlobalMetrics instead. (e.g. query service.numTasks() or something)
        this.watcherThread = new Thread(() -> {
            while(!shutdown.get()) {
                // TODO: get executionsPerSample value and alter thread number appropriately
                int threadNo = executionsPerSample.getAndSet(0);

                try {
                    Thread.sleep(sampleRate);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        this.watcherThread.start();

        this.taskManager = new NonEmptyRoundRobinTaskManager(numberOfQueues);
        this.threads = new ArrayList<>();
        for (int i = 0; i < numberOfThreads; i++) {
            this.threads.add(new Thread(this::runThread));

        }
    }

    @Override
    public void shutdown() {
        shutdown.set(true);
    }

    @Override
    public List<Runnable> shutdownNow() {
        return null; // TODO:
    }

    @Override
    public boolean isShutdown() {
        return false; // TODO:
    }

    @Override
    public boolean isTerminated() {
        return false; // TODO:
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return false; // TODO:
    }

    @Override
    public void execute(Runnable command) {
        executionsPerSample.incrementAndGet();
        taskManager.addTask(command);
    }

    private void runThread() {
        int threadId = (int)Thread.currentThread().getId();

        taskManager.addThread(threadId);
        try {
            while (shutdown.get() && taskManager.remainingTasks() == 0) {
                // TODO: Check other reasons that could make us shut down

                Runnable task = taskManager.nextTask(10, TimeUnit.MILLISECONDS);
                if(task == null)
                    continue; // TODO: We have timed out
                task.run();
            }
        } catch(InterruptedException e) {

        } finally {
            taskManager.removeThread(threadId);
        }
    }

    private TaskManager taskManager;
    private List<Thread> threads;
    private Thread watcherThread;

    private int sampleRate;

    private AtomicInteger executionsPerSample = new AtomicInteger(0);
    private AtomicBoolean shutdown = new AtomicBoolean(false);
}
