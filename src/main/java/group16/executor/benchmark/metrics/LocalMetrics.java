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
            this.taskCompletionTimes = new long[totalTasks];
        }

        public void onTaskSubmitted(int task) {
            taskCompletionTimes[task] = System.nanoTime();
        }
        public void onTaskStarted(int task) {

        }
        public void onTaskCompleted(int task) {
            taskCompletionTimes[task] = System.nanoTime() - taskCompletionTimes[task];
        }

        public LocalMetrics build() {
            LocalMetrics metrics = new LocalMetrics();

            metrics.totalTasks = totalTasks;
            metrics.taskCompletionTimes =
                Arrays.stream(taskCompletionTimes)
                .mapToDouble(time -> time / (double)TimeUnit.SECONDS.toNanos(1))
                .toArray();

            return metrics;
        }

        public int totalTasks;
        public long[] taskCompletionTimes;
    }

    private LocalMetrics() {}

    public double[] getTaskCompletionTimes() {
        // TODO: We would clone this if that wasn't so silly
        return taskCompletionTimes;
    }
    public double maxCompletionTime() {
        return Arrays.stream(taskCompletionTimes).max().orElse(-1.0);
    }
    public double averageCompletionTime() {
        return Arrays.stream(taskCompletionTimes).average().orElse(-1.0);
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    private double[] taskCompletionTimes;
    private int totalTasks;
}
