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
        if (clusters == 0) { throw new IllegalArgumentException("There must be at least one cluster"); }

        double splitsLeft = splits;
        List<Double> timesList = new ArrayList<>();
        RealDistribution distribution = null;
        for (int i = 1; i <= clusters; i++) {
            double peakCenter = totalTime / ((1.0 / i) * (clusters + 1));
            distribution = new NormalDistribution(
                    random,
                    peakCenter,
                    (totalTime / clusters) / DYNAMIC_DISPATCH_LOAD_CLUSTER_AGGRESSIVENESS);
            // split cluster sd's into fairly even chunks for coverage

            for (int j = 0; j < splits / clusters; j++) {
                timesList.add(Math.max(0, distribution.sample()));
                splitsLeft--;
            }
        }
        for (int i = 0; i < splitsLeft; i++) { // If splits doesn't perfects divide into clusters then add remaining
            timesList.add(Math.max(0, distribution.sample()));
        }

        double[] times = timesList.stream().mapToDouble(i -> i).toArray();
        Arrays.sort(times); // Sort and calculate intervals between these times generated
        double[] intervals = new double[times.length];
        for (int i = 0; i < intervals.length - 1; i++) {
            intervals[i] = times[i + 1] - times[i];
        }
        intervals[intervals.length - 1] = totalTime - times[times.length - 1]; // Last interval is between last reading
                                                                               // and totalTime
        return intervals;
    }

    private RandomGenerator random;

    private static final double DYNAMIC_DISPATCH_TIME_STANDARD_DEV = 5.0;
    private static final double DYNAMIC_DISPATCH_LOAD_CLUSTER_AGGRESSIVENESS = 6.0;
}
