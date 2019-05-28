package group16.executor.service;

import group16.executor.service.task.management.FixedQueueTaskManager;
import group16.executor.service.task.management.NonEmptyRoundRobinTaskManager;
import group16.executor.service.task.management.TaskManager;
import group16.executor.service.thread.management.ThreadManager;
import group16.executor.service.thread.management.WatermarkPredictor;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DynamicExecutorService extends AbstractExecutorService {

    public DynamicExecutorService() {
        this.taskManager = new FixedQueueTaskManager(4);
        this.threadManager = new WatermarkPredictor(4, 12);

        this.threads = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            this.threads.add(new Thread(this::runThread));
            this.threads.get(i).start();
        }

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
        return shutdown.get();
    }

    @Override
    public boolean isTerminated() {
        return activeThreads.get() == 0 && shutdown.get();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        shutdown();
        terminatorLock.lock();
        try {
            if(activeThreads.get() > 0)
                terminator.await();
        } finally {
            terminatorLock.unlock();
        }
        return true; // ?
    }

    @Override
    public void execute(Runnable command) {
        if(!shutdown.get()) {
            executionsPerSample.incrementAndGet();
            taskManager.addTask(command);
            threadManager.taskAdded();
        }
    }

    private void runThread() {
        int threadId = (int)Thread.currentThread().getId();

        activeThreads.incrementAndGet();
        taskManager.addThread(threadId);
        try {
            while (
                (!shutdown.get() || taskManager.remainingTasks() > 0)
            || threadManager.shouldKillThread(activeThreads.get())) {
                // TODO: Check other reasons that could make us shut down

                Runnable task = taskManager.nextTask(10, TimeUnit.MILLISECONDS);
                if(task == null) {
                    // TODO: Count multiple idlings
                    if(threadManager.shouldKillIdleThread(activeThreads.get(), 10))
                        break;
                }
                else {
                    task.run();
                    threadManager.taskCompleted();
                }
            }
        } catch(InterruptedException e) {
            System.out.println(e);
        } finally {
            taskManager.removeThread(threadId);
            terminatorLock.lock();
            try {
                if (activeThreads.decrementAndGet() == 0 && isShutdown())
                    terminator.signalAll();
            } finally {
                terminatorLock.unlock();
            }
        }
    }

    private TaskManager taskManager;
    private ThreadManager threadManager;

    private List<Thread> threads;
    private Thread watcherThread;

    private int sampleRate;

    private AtomicInteger executionsPerSample = new AtomicInteger(0);
    private AtomicBoolean shutdown = new AtomicBoolean(false);
    private AtomicInteger activeThreads = new AtomicInteger(0);

    private ReentrantLock terminatorLock = new ReentrantLock();
    private Condition terminator = terminatorLock.newCondition();
}
