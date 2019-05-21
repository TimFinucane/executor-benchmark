package group16.executor.benchmark.helpers;

import group16.executor.benchmark.metrics.LocalMetrics;
import group16.executor.benchmark.metrics.Metrics;
import group16.executor.benchmark.profiles.DispatchListener;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import com.sun.management.OperatingSystemMXBean;

/**
 * A dispatcher controls the submitting of tasks to an ExecutorService, whilst gathering metrics about the tasks.
 * Publicly it contains methods for performing the submitting and measuring, whilst implementations of the Dispatcher
 * will determine how tasks can be submitted.
 */
public abstract class Dispatcher {
    /**
     * Create a dispatcher capable of submitting tasks to an ExecutorService.
     * For the purposes of gathering metrics the dispatcher needs to know the total number of tasks being submitted
     * NOTE: totalTasks could be removed if necessary, its a big help but it isn't required for performing local metrics
     */
    public Dispatcher(int totalTasks) {
        this(totalTasks, null);
    }

    public Dispatcher(int totalTasks, List<DispatchListener> dispatchListeners) {
        this.localMetrics = new LocalMetrics.Builder(totalTasks);
        this.dispatchListeners = dispatchListeners == null ? new ArrayList<>(): dispatchListeners;
    }

    public Metrics run(ExecutorService service) {
        this.service = service;
        // TODO: Here is where global metrics would go
        Metrics metrics = new Metrics();
        OperatingSystemMXBean bean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        long startTime = System.nanoTime();
        long cpuBefore = bean.getProcessCpuTime();
        dispatchAllAndWait();
        service.shutdown();
        try {
            service.awaitTermination(1, TimeUnit.MINUTES);
        } catch(InterruptedException e) {
            System.out.println("System was interrupted from waiting for termination!");
        }
        long cpuAfter = bean.getProcessCpuTime();
        long endTime = System.nanoTime();
        long percent;
        if (endTime > startTime)
            percent = ((cpuAfter-cpuBefore)*100L)/
                    (endTime-startTime);
        else percent = 0;
        int numberOfProcessors = Runtime.getRuntime().availableProcessors();
        percent = percent/numberOfProcessors;
        metrics.CPUUtilization = percent;
        for (DispatchListener listener : dispatchListeners) { // Notify listeners dispatch is complete
            listener.finishedDispatch();
        }

        // Gather metrics together
        metrics.local = localMetrics.build();
        metrics.totalTime = (endTime - startTime) / (double) TimeUnit.SECONDS.toNanos(1);

        this.service = null;
        return metrics;
    }

    protected abstract void dispatchAllAndWait();
    protected void dispatch(Callable callable) {
        if(service == null)
            throw new IllegalStateException("Dispatch called outside of run()");

        int task = currentTask.getAndIncrement();

        localMetrics.onTaskSubmitted(task);
        service.submit(() -> {
            localMetrics.onTaskStarted(task);
            try {
                callable.call(); // Why the fuck does this throw Exception?
            } catch(Exception e) {
                System.out.println("Callable threw an exception, clean it up: ");
                e.printStackTrace();
            }
            finally {
                localMetrics.onTaskCompleted(task);
            }
        });
    }

    // Next task index to be created. Does not indicate which tasks are completed.
    private AtomicInteger currentTask = new AtomicInteger(0);
    private LocalMetrics.Builder localMetrics;
    private List<DispatchListener> dispatchListeners;

    private ExecutorService service;
}
