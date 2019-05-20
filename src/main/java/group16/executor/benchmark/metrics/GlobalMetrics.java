package group16.executor.benchmark.metrics;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

public class GlobalMetrics {
    public static double getProcessCpuLoad() throws Exception {
        javax.management.MBeanServer mbs    = ManagementFactory.getPlatformMBeanServer();
        javax.management.ObjectName name    = javax.management.ObjectName.getInstance("java.lang:type=OperatingSystem");
        javax.management.AttributeList list = mbs.getAttributes(name, new String[]{ "ProcessCpuLoad" });

        if (list.isEmpty())     return Double.NaN;

        javax.management.Attribute att = (javax.management.Attribute)list.get(0);
        Double value  = (Double)att.getValue();

        // usually takes a couple of seconds before we get real values
        if (value == -1.0)      return Double.NaN;
        // returns a percentage value with 1 decimal point precision. This value will be the total CPU Utilization
        return ((int)(value * 1000) / 10.0);
    }

    private double CPUUtilization;
}
