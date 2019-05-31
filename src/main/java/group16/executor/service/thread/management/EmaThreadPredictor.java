package group16.executor.service.thread.management;

import java.util.AbstractMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Predicts next number of tasks based on the past number of tasks and the current rate of change.
 */
public class EmaThreadPredictor implements ThreadManager {
    /**
     * @param alpha Weighting factor for exponential weighting reduction
     * @param minThreads Minimum number of threads
     */
    public EmaThreadPredictor(double alpha, int minThreads) {
        this.alpha = alpha;
        this.minThreads = minThreads;

        // TODO: How do we stop this?
        watcherThread = new Thread(this::watcher);
        watcherThread.start();
        prediction = minThreads;
    }
    public EmaThreadPredictor(double alpha) {
        this(alpha, 4);
    }

    @Override
    public void taskAdded() {
        tasks.incrementAndGet();
    }

    @Override
    public void taskCompleted() {
        tasks.decrementAndGet();
    }

    @Override
    public int threadDeficit(int activeThreads) {
        return (int)Math.round(activeThreads - prediction);
    }

    @Override
    public boolean shouldKillIdleThread(int activeThreads, long idleTime) {
        return activeThreads > prediction;
    }

    @Override
    public boolean shouldKillThread(int activeThreads) {
        return false;
    }

    private void watcher() {
        try {
            while (true) {
                Thread.sleep(WATCHER_RESOLUTION);

                int currentTasks = tasks.getAndSet(0);

                double difference = currentTasks - movingAverage;
                movingAverage = alpha * currentTasks + (1 - alpha) * movingAverage;
                prediction = Math.max(movingAverage + difference, minThreads);
            }
        } catch(InterruptedException e) {}
    }

    private final Thread watcherThread;

    private final double alpha;
    private final int minThreads;
    private double movingAverage = 0.0;
    private double prediction = 0.0; // TODO: This needs to be atomic

    private AtomicInteger tasks = new AtomicInteger(0);

    private final static int WATCHER_RESOLUTION = 5;
}
