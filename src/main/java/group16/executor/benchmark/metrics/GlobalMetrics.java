package group16.executor.benchmark.metrics;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GlobalMetrics
{
    /**
     * The builder gathers the metrics and then creates a GlobalMetrics class from them.
     * It must be start()-ed before running the service, and finish()-ed afterwards.
     */
    public static class Builder {

        /**
         * Starts recording values
         */
        public void start() {
            totalTime = System.nanoTime();
            new Thread(this::runGatherMetricsThread).start();
            // TODO start responsiveness thread in another method, in that thread do work and write to the data periodically
        }

        /**
         * Finishes recording values
         */
        public void finish() {
            totalTime = (System.nanoTime() - totalTime);
            shutdown = true;
        }
        public GlobalMetrics build() {
            return new GlobalMetrics(
                totalTime / (double)TimeUnit.SECONDS.toNanos(1),
                times,
                cpuLoads,
                threadCounts
            );
        }

        private void runGatherMetricsThread() {
            try {
                while (!shutdown) {
                    gatherMetrics();
                    Thread.sleep(RESOLUTION);
                }
            } catch (InterruptedException e) {
            }
        }

        private void gatherMetrics() {
            // TODO: Could split this up if so desired
            // Time
            times.add(System.nanoTime());
            /**
             * Although there seems to be various methods available to get the CPU Utilization with varying accuracies, this seems the
             * most accepted method to getthe CPU utilization of the JVM process from the OS. getProcessCpuLoad() Returns the "recent cpu usage"
             * for the Java Virtual Machine process. This value is a double in the [0.0,1.0] interval. A value of 0.0 means that none of the
             * CPUs were running threads from the JVM process during the recent period of time observed, while a value of 1.0 means that all
             * CPUs were actively running threads from the JVM 100% of the time during the recent period being observed. Threads from the JVM
             * include the application threads as well as the JVM internal threads. All values between 0.0 and 1.0 are possible depending
             * of the activities going on in the JVM process and the whole system. If the Java Virtual Machine recent CPU usage is not
             * available, the method returns a negative value.
             */
            // CPU load
            cpuLoads.add(osBean.getProcessCpuLoad() * 100.0);
            // Get thread count
            ThreadMXBean osBean = ManagementFactory.getThreadMXBean();
            threadCounts.add(osBean.getThreadCount());
        }

        // Wait time in ms between getting measurements
        private static final long RESOLUTION = 500;
        private static final OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

        private boolean shutdown = false;

        private long totalTime;
        private ArrayList<Long> times = new ArrayList<>();
        private ArrayList<Double> cpuLoads = new ArrayList<>();
        private ArrayList<Integer> threadCounts = new ArrayList<>();
    }




    public GlobalMetrics(double totalTime, List<Long> times, List<Double> cpuLoads, List<Integer> threadCounts) {
        this.totalTime = totalTime;
        this.times = times;
        this.cpuLoads = cpuLoads;
        this.threadCounts = threadCounts;
    }

    public double averageCpuLoad() {
        return cpuLoads.stream().mapToDouble(val -> val).average().orElse(-1);
    }

    public final double totalTime;

    // Times in nanos since epoch at which each reading was performed
    public final List<Long> times;
    public final List<Double> cpuLoads;
    public final List<Integer> threadCounts;
}
