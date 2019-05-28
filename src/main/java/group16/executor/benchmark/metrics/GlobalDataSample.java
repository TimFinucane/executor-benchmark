package group16.executor.benchmark.metrics;

public class GlobalDataSample {
    public final long sampleTime;
    public final double cpuLoad;
    public final int threadCount;
    public final int responsiveWorkCompleted;

    public GlobalDataSample(long sampleTime, double cpuLoad, int threadCount, int responsiveWorkCompleted) {
        this.sampleTime = sampleTime;
        this.cpuLoad = cpuLoad;
        this.threadCount = threadCount;
        this.responsiveWorkCompleted = responsiveWorkCompleted;
    }
}
