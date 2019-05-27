package group16.executor.benchmark.metrics;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * This class is used for gathering and showing metrics related to the tasks submitted to the service.
 */
public class LocalMetrics {
    public static class Builder {
        public Builder(int totalTasks) {
            this.totalTasks = totalTasks;
            this.taskStartTimes = new long[totalTasks];
            this.taskDataSamples = new TaskDataSample[totalTasks];
        }

        public void onTaskSubmitted(int task) {
            taskStartTimes[task] = System.nanoTime();
        }
        public void onTaskStarted(int task) {

        }
        public void onTaskCompleted(int task) {
            taskDataSamples[task] = new TaskDataSample(
                    taskStartTimes[task],
                    (System.nanoTime() - taskStartTimes[task]) / (double)TimeUnit.SECONDS.toNanos(1));
        }

        public LocalMetrics build() {
            LocalMetrics metrics = new LocalMetrics();

            metrics.totalTasks = totalTasks;
            metrics.taskDataSamples = taskDataSamples;

            return metrics;
        }

        public int totalTasks;
        public long[] taskStartTimes;
        public TaskDataSample[] taskDataSamples;
    }

    private LocalMetrics() {}

    public double[] getTaskCompletionTimes() {
        // TODO: We would clone this if that wasn't so silly
        return Arrays.stream(taskDataSamples).map(x -> x.taskCompletionTime).mapToDouble(v -> v).toArray();
    }
    public double maxCompletionTime() {
        return Arrays.stream(taskDataSamples).map(x -> x.taskCompletionTime).mapToDouble(v -> v).max().orElse(-1.0);
    }
    public double averageCompletionTime() {
        return Arrays.stream(taskDataSamples).map(x -> x.taskCompletionTime).mapToDouble(v -> v).average().orElse(-1.0);
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    //private double[] taskStartTimes;
    private int totalTasks;
    private TaskDataSample[] taskDataSamples;
}
