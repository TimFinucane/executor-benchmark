package group16.executor.service;

import group16.executor.service.task.management.NonEmptyRoundRobinTaskManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DynamicExecutorService extends AbstractExecutorService {

    public DynamicExecutorService(int numberOfThreads, int numberOfQueues, int sampleRate) {
        this.sampleRate = sampleRate;
        this.executionsPerSample = new AtomicInteger(0);
        this.notShutDown = true;

        // Set up watcher thread for counting tasks per time unit
        this.watcherThread = new Thread(() -> {
            while(notShutDown) {
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

        // Set up queues and threads
        this.queues = new ArrayList<>();
        for (int i = 0; i < numberOfQueues; i++) {
            this.queues.add(new LinkedBlockingQueue<>());
        }

        this.threads = new ArrayList<>();
        for (int i = 0; i < numberOfThreads; i++) {
            this.threads.add(new DynamicExecutorThread(new NonEmptyRoundRobinTaskManager(queues)));
        }
    }

    @Override
    public void shutdown() {
        notShutDown = false;
        // TODO:
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
    }

    // TODO: Thread function. This could be made into a private static class instead
    private static void runThread() {}

    private List<Queue<Callable>> queues;
    private List<Thread> threads;
    private Thread watcherThread;

    private int sampleRate;

    private AtomicInteger executionsPerSample;
    private boolean notShutDown;
}
