package group16.executor;

import group16.executor.benchmark.JsonMetricsExporter;
import group16.executor.benchmark.MetricsExporter;
import group16.executor.benchmark.Profile;
import group16.executor.benchmark.helpers.Dispatcher;
import group16.executor.benchmark.metrics.Metrics;
import group16.executor.benchmark.profiles.DynamicLoadProfile;
import group16.executor.benchmark.profiles.IOProfile;
import group16.executor.benchmark.profiles.UniformProfile;
import group16.executor.service.DynamicExecutorService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

public class Main {
    public static void main(String[] args) {
        final int attempts = 1;

        Profile[] profiles = new Profile[] {
            // Few tasks, relatively beefy, should be a BREEZE for the services. Should take ~5s.
            new UniformProfile(30000, 100000, 5),
            // Few tasks, but lots of them. Tasks coming in waves should put even more stress on the system.
            // Should take ~6s per run
            new DynamicLoadProfile(100000, 45000, 5, 10),
            // Few tasks, but the IO blocking tasks will confuse most systems.
            new IOProfile(6000, 0.1, 100000, 0.1),
            // Let it rip. All tasks come in at same time.
            new UniformProfile(20000, 500000),
        };
        String[] profileNames = new String[] {
            "UniformProfile-DynamicLoad",
            "DynamicProfile-ManyTasks-10Peaks",
            "IOProfile",
            "UniformProfile-HeavyTasks-StaticLoad"
        };

        String[] serviceNames = getExecutorServiceNames();

        MetricsExporter exporter = new JsonMetricsExporter();

        // Perform a warm-up, discard the results (as the JVM will be unstable)
        new UniformProfile(100000, 100000).generate().run(Executors.newFixedThreadPool(8));

        for(int profileIndex = 0; profileIndex < profiles.length; ++profileIndex) {
            Profile profile = profiles[profileIndex];
            String profileName = profileNames[profileIndex];

            for(int attempt = 0; attempt < attempts; ++attempt) {
                Dispatcher dispatcher = profile.generate();
                // Get it here as we need a fresh executor service every time.
                ExecutorService[] services = getExecutorServices();

                for(int serviceIndex = 0; serviceIndex < services.length; ++serviceIndex) {
                    ExecutorService service = services[serviceIndex];
                    String serviceName = serviceNames[serviceIndex];

                    Metrics metrics = dispatcher.run(service);

                    exporter.exportMetrics(metrics, serviceName, profileName);
                    printMetrics(metrics, serviceName, profileName);
                }
            }
        }
    }

    static ExecutorService[] getExecutorServices() {
        int cores = Runtime.getRuntime().availableProcessors();

        return new ExecutorService[] {
            DynamicExecutorService.fixedQueueWatermark(),
            DynamicExecutorService.fixedQueueEma(),
            DynamicExecutorService.sharedQueueWatermark(),
            DynamicExecutorService.sharedQueueEma(),
            Executors.newFixedThreadPool(cores),
            new ForkJoinPool(),
            Executors.newWorkStealingPool(),
        };
    }
    static String[] getExecutorServiceNames() {
        return new String[] {
            "Group16Service-FixedQueue-Watermark",
            "Group16Service-FixedQueue-Ema",
            "Group16Service-SharedQueue-Watermark",
            "Group16Service-SharedQueue-Ema",
            "FixedThreadPool",
            "ForkJoinPool",
            "WorkStealingPool",
        };
    }

    static void printMetrics(Metrics metrics, String serviceType, String profileType) {
        System.out.println("Running " + serviceType + " on " + profileType);
        System.out.println("\tTime to run: " + metrics.global.totalTime + "s");
        System.out.println("\tAvg. request completion time: " + metrics.local.averageCompletionTime() + "s");
        System.out.println("\tMax request completion time: " + metrics.local.maxCompletionTime() + "s");
        System.out.println("\tAverage CPU load: " + metrics.global.averageCpuLoad() + "%");
    }
}
