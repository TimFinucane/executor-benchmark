package group16.executor.benchmark.metrics;

public class TaskDataSample {
    public final double submitTime;
    public final double startTime;
    public final double endTime;

    public TaskDataSample(double submitTime, double startTime, double endTime) {
        this.submitTime = submitTime;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
