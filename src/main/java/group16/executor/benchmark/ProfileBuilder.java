package group16.executor.benchmark;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.RandomGeneratorFactory;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * The profile builder provides tools for building profiles :)
 */
public class ProfileBuilder {
    public ProfileBuilder(int seed) {
        this.random = RandomGeneratorFactory.createRandomGenerator(new Random(seed));
    }

    public RandomGenerator getRandom() {
        return this.random;
    }

    public Callable<?> calculator(int accuracy) {
        return () -> {
            double val = 0;

            for (int i = 0; i < accuracy; ++i) {
                // If it's even, add. If it's odd, subtract. Sorry. Was looking for a source and got stuck reading the Pi bill.
                val += ((i % 2 == 0) ? 4 : -4) / (double) (2 * i + 1);
            }
            return val;
        };
    }
    public Callable<?> waiter(double time) {
        return () -> {
            TimeUnit.MILLISECONDS.sleep((long)(time * 1000));
            return 0;
        };
    }

    /**
     * Returns true with the likelihood of the given chance.
     */
    public boolean chance(double chance) {
        return random.nextDouble() < chance;
    }

    /**
     * Split a total amount of time into a series of chunks.
     * Returns an array of size splits, with each item being the length of the chunk.
     */
    public double[] splitTimeEvenly(double totalTime, int splits) {
        double averageDivision = totalTime / (double) splits;

        double[] nums = new double[splits];
        RealDistribution realRand = new NormalDistribution(
            random,
            averageDivision,
            averageDivision / DYNAMIC_DISPATCH_TIME_STANDARD_DEV
        );

        // TODO: Logic error, what if last number is negative??
        double timeLeft = totalTime;
        for (int i = 0; i < nums.length-1; i++) {
            nums[i] = Math.max(0,realRand.sample());
            timeLeft -= nums[i];
        }
        nums[nums.length-1] = Math.max(0, timeLeft);

        return nums;
    }

    public double[] splitTimeClustered(double totalTime, int splits, int clusters) {

        int toSplit = splits;
        List<Double> times = new ArrayList<>();
        for (int i = 1; i <= clusters; i++) {
            double peakCenter = totalTime / ((1.0 / i) * (clusters + 1));
            RealDistribution distribution = new NormalDistribution(
                    random,
                    peakCenter,
                    totalTime / clusters); // split cluster sd's into fairly even chunks for coverage

            for (int j = 0; j < totalTime / clusters; j++) {
                times.add(distribution.sample());
                toSplit--;
            }
        }

        //TODO: order, then loop through and find difference between each successive number
        double[] asArray = times.stream().mapToDouble(i -> i).toArray();
        Arrays.sort(asArray);

        return asArray;
    }

    private RandomGenerator random;

    private static final double DYNAMIC_DISPATCH_TIME_STANDARD_DEV = 5.0;
}
