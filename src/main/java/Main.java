import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import profiles.UIGenerator;

import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        double avgTime = BenchmarkRunner.runWith(
            () -> Executors.newFixedThreadPool(32),
            UIGenerator.generate(100, 0.2,
                new UniformIntegerDistribution(10000, 500000),
                new UniformRealDistribution(0.1, 0.6)
            )
        );

        System.out.println("Average time to run: " + avgTime + "s");
    }
}
