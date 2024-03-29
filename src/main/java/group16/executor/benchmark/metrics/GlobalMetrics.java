package group16.executor.benchmark.metrics;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import group16.executor.benchmark.ProfileBuilder;

import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
            new Thread(this::runResponsivenessThread).start();
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
                globalDataSamples
            );
        }

        private void runGatherMetricsThread() {
            try {
                while (!shutdown) {
                    gatherMetrics();
                    Thread.sleep(RESOLUTION);
                }
            } catch (InterruptedException e) {
                System.out.println("Exception thrown while sleeping between metrics gathering period");
                e.printStackTrace();
            }
            // Ensure responsiveness thread values are used to prevent JIT optimizations
            System.out.println("Please ignore this value also: " + responsiveThreadResults.stream().mapToDouble(v -> v).average().getAsDouble());
        }

        private void runResponsivenessThread() {
            timeOfLastResponsivenessCall = System.currentTimeMillis();
            while (!shutdown) {
                try {
                    responsiveThreadResults.add((Double) ProfileBuilder.calculator(RESPONSIVENESS_THREAD_WORKLOAD).call());
                } catch (Exception e) {
                    System.out.println("Exception thrown while running responsiveness thread work simulation");
                    e.printStackTrace();
                }

                responsiveWorkDone.incrementAndGet();
            }
        }

        private void gatherMetrics() {
            // TODO: Could split this up if so desired
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
            long systemTime = System.nanoTime();
            double cpuLoad = osBean.getProcessCpuLoad() * 100.0;
            ThreadMXBean osBean = ManagementFactory.getThreadMXBean();
            int threadCount = osBean.getThreadCount();
            // Scale the responsive work done by work done per sample period
            int responsiveWorkDoneInLastTimeUnit = (int)(responsiveWorkDone.getAndSet(0) /
                    ((System.currentTimeMillis() - timeOfLastResponsivenessCall) / (double)RESOLUTION));
            globalDataSamples.add(new GlobalDataSample(systemTime, cpuLoad, threadCount, responsiveWorkDoneInLastTimeUnit));
            timeOfLastResponsivenessCall = System.currentTimeMillis();
        }

        // Wait time in ms between getting measurements
        private static final long RESOLUTION = 200;
        private static final int RESPONSIVENESS_THREAD_WORKLOAD = 100000;
        private static final OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

        private boolean shutdown = false;

        private long timeOfLastResponsivenessCall = 0;
        private long totalTime;
        private ArrayList<GlobalDataSample> globalDataSamples = new ArrayList<>();
        private AtomicInteger responsiveWorkDone = new AtomicInteger(0); // Fine to have atomic here as contention will be very low
        private List<Double> responsiveThreadResults = new ArrayList<>(); // needed to prevent JIT optimizations
    }




    public GlobalMetrics(double totalTime, List<GlobalDataSample> globalDataSamples) {
        this.totalTime = totalTime;
        this.globalDataSamples = globalDataSamples;
    }

    public double averageCpuLoad() {
        return globalDataSamples.stream().map(x -> x.cpuLoad).mapToDouble(val -> val).average().orElse(-1);
    }

    public final double totalTime;
    public final List<GlobalDataSample> globalDataSamples;
}
