package group16.executor.benchmark.helpers;

import group16.executor.benchmark.metrics.GlobalMetrics;
import group16.executor.benchmark.metrics.LocalMetrics;
import group16.executor.benchmark.metrics.Metrics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
        this.totalTasks = totalTasks;
        this.callableResults = new double[totalTasks];
    }

    public Metrics run(ExecutorService service) {
        // Set service so that we can process calls to this::dispatch
        this.service = service;
        this.currentTask.set(0);
        this.localMetrics = new LocalMetrics.Builder(totalTasks);

        // Start gathering global metrics
        GlobalMetrics.Builder globalMetrics = new GlobalMetrics.Builder();
        globalMetrics.start();

        // Dispatch our profile's tasks, and wait until they're finished
        dispatchAllAndWait();
        service.shutdown();
        try {
            service.awaitTermination(1, TimeUnit.MINUTES);
        } catch(InterruptedException e) {
            System.out.println("System was interrupted from waiting for termination!");
        }
        // Stop gathering global metrics
        globalMetrics.finish();

        System.out.println("Finished! Please ignore this value: " + // Actually use the output of the callables because Java
            Arrays.stream(callableResults).average().orElse(-1.0));

        this.service = null;

        Metrics metrics = new Metrics();
        metrics.local = localMetrics.build();
        metrics.global = globalMetrics.build();
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
                // Not locking the callableResults list is acceptable because we don't care about correctness and we
                // don't want to impact performance
                callableResults[task] = (double) callable.call();
            } catch(Exception e) {
                System.out.println("Callable threw an exception, clean it up: ");
                e.printStackTrace();
            }
            finally {
                localMetrics.onTaskCompleted(task);
            }
        });
    }

    private int totalTasks;
    // Next task index to be created. Does not indicate which tasks are completed.
    private AtomicInteger currentTask = new AtomicInteger(0);
    private LocalMetrics.Builder localMetrics;
    private double[] callableResults;

    private ExecutorService service;
}
