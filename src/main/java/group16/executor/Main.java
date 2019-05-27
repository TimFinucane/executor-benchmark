package group16.executor;

import group16.executor.benchmark.ProfileBuilder;
import group16.executor.benchmark.helpers.Dispatcher;
import group16.executor.benchmark.metrics.Metrics;
import group16.executor.benchmark.profiles.UniformProfile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    public static void main(String[] args) {

        try {
            ExecutorService service = Executors.newFixedThreadPool(8);

            Metrics metrics =
                dispatchWithLoad(new UniformProfile(10000, 1000000, 10) .generate(), service);

//            System.out.println(
//                    "CPU Utilization: "
//                            + metrics.CPUUtilization + "%"
//            );
            System.out.println("Time to run: " + metrics.global.totalTime + "s");
            System.out.println("Avg. request completion time: " + metrics.local.averageCompletionTime());
            System.out.println("Max request completion time: " + metrics.local.maxCompletionTime());

            System.out.println("Average CPU load: " + metrics.global.averageCpuLoad());
            
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
