package group16.executor.benchmark;

import group16.executor.benchmark.metrics.Metrics;
import group16.executor.benchmark.profiles.DynamicLoadProfile;
import group16.executor.benchmark.profiles.GlobalLoadProfile;
import group16.executor.benchmark.profiles.UniformProfile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {

        try {
            ExecutorService service = Executors.newFixedThreadPool(8);

//            Metrics metrics =
//                new DynamicLoadProfile(10000, 1000000, 3, 5)
//                    .generate()
//                    .run(service);
            // Metrics metrics = new GlobalLoadProfile(10000, 1000000, 5).generate().run(service);
            Metrics metrics = new UniformProfile(10000, 1000000, 10) .generate().run(service);

            System.out.println(
                    "CPU Utilization: "
                            + metrics.CPUUtilization + "%"
            );
            System.out.println("Time to run: " + metrics.totalTime + "s");
            System.out.println(
                "Avg. request completion time: "
                + metrics.local.averageCompletionTime()
            );
            System.out.println(
                "Max request completion time: "
                + metrics.local.maxCompletionTime()
            );

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
