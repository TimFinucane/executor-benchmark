package group16.executor.service.thread.management;

public class WatermarkPredictor implements ThreadManager {
    public WatermarkPredictor(int min, int max) {
        if(max < min)
            throw new IllegalArgumentException("watermark minimum must be less than or equal to maximum");
        if(min < 0)
            throw new IllegalArgumentException("watermark minimum must be equal to or above 0");

        this.min = min;
        this.max = max;
    }

    @Override
    public void taskAdded() {}

    @Override
    public void taskCompleted() {}

    @Override
    public int threadDeficit(int activeThreads) {
        if(activeThreads < min)
            return min - activeThreads;
        else
            return 0;
    }

    @Override
    public boolean shouldKillIdleThread(int activeThreads, long idleTime) {
        return activeThreads > min;
    }

    @Override
    public boolean shouldKillThread(int activeThreads) {
        return activeThreads > max;
    }

    private final int min;
    private final int max;
}
