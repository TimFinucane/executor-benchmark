package group16.executor.benchmark;

import group16.executor.benchmark.distributionVisualisation.SplitTimeClusteredVisualisation;
import group16.executor.benchmark.metrics.Metrics;
import group16.executor.benchmark.customDistributions.BimodalDistribution;
import group16.executor.benchmark.profiles.IrregularProfile;
import group16.executor.benchmark.profiles.UIProfile;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        SplitTimeClusteredVisualisation s = new SplitTimeClusteredVisualisation();
        s.showTimesPlot();
//        try {
//            ExecutorService service = Executors.newFixedThreadPool(8);
//            Metrics metrics =
//                new IrregularProfile(1000, 1000000, 100000, 5.0)
//                    .generate()
//                    .run(service);
//
//            System.out.println("Time to run: " + metrics.totalTime + "s");
//            System.out.println(
//                "Avg. request completion time: "
//                + Arrays.stream(metrics.local.getCompletionTimes()).average().getAsDouble()
//            );
//            System.out.println(
//                "Max request completion time: "
//                + Arrays.stream(metrics.local.getCompletionTimes()).max().getAsDouble()
//            );
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
    }
}
