package group16.executor.benchmark.metrics;

public class TaskDataSample {
    public final long taskSubmitTime;
    public final double taskCompletionTime;

    public TaskDataSample(long taskSubmitTime, double taskCompletionTime) {
        this.taskSubmitTime = taskSubmitTime;
        this.taskCompletionTime = taskCompletionTime;
    }
}
