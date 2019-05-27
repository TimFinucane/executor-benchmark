package group16.executor;

import group16.executor.benchmark.JsonMetricsExporter;
import group16.executor.benchmark.MetricsExporter;
import group16.executor.benchmark.ProfileBuilder;
import group16.executor.benchmark.helpers.Dispatcher;
import group16.executor.benchmark.metrics.Metrics;
import group16.executor.benchmark.profiles.UniformProfile;
import group16.executor.service.DynamicExecutorService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    public static void main(String[] args) {

        try {
            Dispatcher dispatcher = new UniformProfile(10000, 1000000, 10) .generate();
            ExecutorService defaultService = Executors.newFixedThreadPool(4);
            Metrics metrics = dispatcher.run(defaultService);
            MetricsExporter exporter = new JsonMetricsExporter();
            exporter.exportMetrics(metrics);
//            for(int i = 0; i < 10; ++i) {
//                ExecutorService defaultService = Executors.newFixedThreadPool(4);
//                ExecutorService ourService = new DynamicExecutorService();
//
//                System.out.println("Fixed thread pool:");
//                Metrics metrics = dispatcher.run(defaultService);
//
//                System.out.println("\tTime to run: " + metrics.global.totalTime + "s");
//                System.out.println("\tAvg. request completion time: " + metrics.local.averageCompletionTime());
//                System.out.println("\tMax request completion time: " + metrics.local.maxCompletionTime());
//                System.out.println("\tAverage CPU load: " + metrics.global.averageCpuLoad());
//
//                MetricsExporter exporter = new JsonMetricsExporter();
//                exporter.exportMetrics(metrics);
//
//                System.out.println("Our service:");
//                metrics = dispatcher.run(ourService);
//
//                System.out.println("\tTime to run: " + metrics.global.totalTime + "s");
//                System.out.println("\tAvg. request completion time: " + metrics.local.averageCompletionTime());
//                System.out.println("\tMax request completion time: " + metrics.local.maxCompletionTime());
//                System.out.println("\tAverage CPU load: " + metrics.global.averageCpuLoad());
//            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // TODO: Put this and LoadSimulator somewhere else?
    public static Metrics dispatchWithLoad(Dispatcher dispatcher, ExecutorService service) {
        LoadSimulator loadSimulator = new LoadSimulator();
        loadSimulator.start(5);

        Metrics metrics = dispatcher.run(service);

        loadSimulator.stop();

        return metrics;
    }

    /**
     * Class that creates boring threads for a load
     */
    public static class LoadSimulator {

        /**
         * Starts running [count] simultaneous threads with load
         */
        public void start(int count) {
            // Create a thread pool and schedule [count] work tasks
            ExecutorService service = Executors.newFixedThreadPool(count);
            for(int i = 0; i < count; ++i) {
                service.submit(this::work);
            }
            service.shutdown();
        }
        public void stop() {
            shutdown.set(true);
        }

        // Generic work function.
        private void work() {
            while(!shutdown.get()) {
                try {
                    ProfileBuilder.calculator(10000).call();
                } catch(Exception e) {}
            }
        }

        // For some reason never stops with a regular boolean.
        private AtomicBoolean shutdown = new AtomicBoolean(false);
    }
}
