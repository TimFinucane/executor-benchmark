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
            this.taskSubmitTimes = new long[totalTasks];
            this.taskEndTimes = new long[totalTasks];
            this.programStartTime = System.nanoTime();
        }

        public void onTaskSubmitted(int task) {
            taskSubmitTimes[task] = System.nanoTime();
        }
        public void onTaskStarted(int task) {
            taskStartTimes[task] = System.nanoTime();
        }
        public void onTaskCompleted(int task) {
            taskEndTimes[task] = System.nanoTime();
        }

        public LocalMetrics build() {
            TaskDataSample[] samples = new TaskDataSample[totalTasks];

            for(int i = 0; i < totalTasks; ++i) {
                samples[i] = new TaskDataSample(
                        (taskSubmitTimes[i] - programStartTime) / (double)TimeUnit.SECONDS.toNanos(1),
                        (taskStartTimes[i] - programStartTime) / (double)TimeUnit.SECONDS.toNanos(1),
                        (taskEndTimes[i] - programStartTime) / (double)TimeUnit.SECONDS.toNanos(1)
                );
            }

            return new LocalMetrics(totalTasks, samples);
        }
        long programStartTime;

        int totalTasks;
        long[] taskSubmitTimes;
        long[] taskStartTimes;
        long[] taskEndTimes;
    }

    public LocalMetrics(int totalTasks, TaskDataSample[] samples) {
        this.totalTasks = totalTasks;
        this.taskDataSamples = samples;
    }

    public double[] getTaskCompletionTimes() {
        // TODO: We would clone this if that wasn't so silly
        return Arrays.stream(taskDataSamples).map(x -> x.endTime - x.submitTime).mapToDouble(v -> v).toArray();
    }
    public double maxCompletionTime() {
        return Arrays.stream(taskDataSamples).map(x -> x.endTime - x.submitTime).mapToDouble(v -> v).max().orElse(-1.0);
    }
    public double averageCompletionTime() {
        return Arrays.stream(taskDataSamples).map(x -> x.endTime - x.submitTime).mapToDouble(v -> v).average().orElse(-1.0);
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    //private double[] taskStartTimes;
    public final int totalTasks;
    public final TaskDataSample[] taskDataSamples;
}
