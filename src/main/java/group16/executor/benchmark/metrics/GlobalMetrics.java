package group16.executor.benchmark.metrics;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.lang.Math;
import java.lang.management.ThreadMXBean;
import java.util.List;

public class GlobalMetrics
{
    OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
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
    public double getProcessCpuLoad(){
        double cpuUsage = osBean.getProcessCpuLoad() < 0 ? 0 : osBean.getProcessCpuLoad() * 100.0;
        return Math.min(
                (cpuUsage), 100.0);
    }

    public int getActiveThreads()
    {
        ThreadMXBean osBean = ManagementFactory.getThreadMXBean();
        int activeThread =osBean.getThreadCount();
        return activeThread;
    }

    public void start()
    {
        double cpuUtilization = getProcessCpuLoad();
        int threadInfo = getActiveThreads();
        this.CPULoads.add(cpuUtilization);
        this.numberofThreads.add(threadInfo);
    }

    public void end()
    {
        this.shutdown = false;
    }

    private List <Double> CPULoads;
    private List <Integer> numberofThreads;
    private Boolean shutdown;
}
