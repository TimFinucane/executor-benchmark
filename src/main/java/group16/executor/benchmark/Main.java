package group16.executor.benchmark;

import group16.executor.benchmark.customDistributions.BimodalDistribution;
import group16.executor.benchmark.profiles.IrregularProfile;
import group16.executor.benchmark.profiles.UIProfile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        try {
            ExecutorService service = Executors.newFixedThreadPool(8);
            //double time = new UIProfile(1000, 0.2, 10000, 0.6).time(service);
            double time = new IrregularProfile(10000, 100000, 10000, 10000).time(service);

            System.out.println("Time to run: " + time + "s");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
