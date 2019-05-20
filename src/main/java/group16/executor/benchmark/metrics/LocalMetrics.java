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
            this.completionTimes = new long[totalTasks];
        }

        public void onTaskSubmitted(int task) {
            completionTimes[task] = System.nanoTime();
        }
        public void onTaskStarted(int task) {

        }
        public void onTaskCompleted(int task) {
            completionTimes[task] = System.nanoTime() - completionTimes[task];
        }

        public LocalMetrics build() {
            LocalMetrics metrics = new LocalMetrics();

            metrics.totalTasks = totalTasks;
            metrics.completionTimes =
                Arrays.stream(completionTimes)
                .mapToDouble(time -> time / (double)TimeUnit.SECONDS.toNanos(1))
                .toArray();

            return metrics;
        }

        public int totalTasks;
        public long[] completionTimes;
    }

    private LocalMetrics() {}

    public double[] getCompletionTimes() {
        // TODO: We would clone this if that wasn't so silly
        return completionTimes;
    }
    public double maxCompletionTime() {
        return Arrays.stream(completionTimes).max().orElse(-1.0);
    }
    public double averageCompletionTime() {
        return Arrays.stream(completionTimes).average().orElse(-1.0);
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    private double[] completionTimes;
    private int totalTasks;
}
