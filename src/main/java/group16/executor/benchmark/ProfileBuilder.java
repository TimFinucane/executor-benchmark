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

    public static Callable<?> calculator(int accuracy) {
        return () -> {
            double val = 0;

            for (int i = 0; i < accuracy; ++i) {
                // If it's even, add. If it's odd, subtract. Sorry. Was looking for a source and got stuck reading the Pi bill.
                val += ((i % 2 == 0) ? 4 : -4) / (double) (2 * i + 1);
            }
            return val;
        };
    }
    public static Callable<?> waiter(double time) {
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
        double gap = totalTime / (double) (splits + 1);
        double[] times = new double[splits];

        RealDistribution realRand = new NormalDistribution(
            random,
            0,
            gap / DYNAMIC_DISPATCH_TIME_STANDARD_DEV
        );

        for (int i = 0; i < times.length; i++)
            times[i] = Math.max(0, gap * (i + 1) + realRand.sample());

        return times;
    }

    private double[] calculateIntervals(double[] times, double totalTime) {
        double[] intervals = new double[times.length];
        for (int i = 0; i < intervals.length - 1; i++) {
            intervals[i] = times[i + 1] - times[i];
        }
        intervals[intervals.length - 1] = totalTime - times[times.length - 1]; // Last interval is between last reading
        return intervals;                                                      // and totalTime
    }

    private RandomGenerator random;

    private static final double DYNAMIC_DISPATCH_TIME_STANDARD_DEV = 5.0;
}
