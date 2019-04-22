package group16.executor.benchmark;

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.RandomGeneratorFactory;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The profile builder provides tools for building profiles :)
 */
public class ProfileBuilder {
    public ProfileBuilder(ExecutorService service, int seed) {
        this.service = service;
        this.random = RandomGeneratorFactory.createRandomGenerator(new Random(seed));
    }

    public RandomGenerator getRandom() {
        return this.random;
    }


    public Callable<?> calculatorTask(int accuracy) {
        return () -> {
            double val = 0;

            for (int i = 0; i < accuracy; ++i) {
                // If it's even, add. If it's odd, subtract. Sorry. Was looking for a source and got stuck reading the Pi bill.
                val += ((i % 2 == 0) ? 4 : -4) / (double) (2 * i + 1);
            }

            return val;
        };
    }
    public Callable<?> waitTask(double time) {
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


    private ExecutorService service;
    private RandomGenerator random;
}
