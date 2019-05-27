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
            Dispatcher dispatcher = new UniformProfile(10000, 1000000, 10).generate();
            ExecutorService defaultService = Executors.newFixedThreadPool(4);
            Metrics metrics = dispatcher.run(defaultService);
            metrics.profileType = "UniformProfile";
            metrics.serviceType = "FixedThreadPool";
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
