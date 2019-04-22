package group16.executor.benchmark;

import group16.executor.benchmark.ProfileBuilder;
import org.apache.commons.math3.random.RandomGeneratorFactory;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A profile creates and submits tasks to an executor service.
 */
public abstract class Profile {

    /**
     * Reset the random seed. Can be done if batching/etc.
     */
    public void setSeed(int seed) {
        this.seed = seed;
    }

    /**
     * Time a running of the profile with the given executor service
     *
     * @return time in seconds of the run.
     */
    public double time(ExecutorService service) throws InterruptedException {
        long startTime = System.nanoTime();
        test(service);

        service.shutdown();
        service.awaitTermination(1, TimeUnit.HOURS);
        long endTime = System.nanoTime();

        return (endTime - startTime) / (double) TimeUnit.SECONDS.toNanos(1);
    }

    /**
     * Tests the given service by running the profile on it
     */
    public void test(ExecutorService service) {
        run(service, new ProfileBuilder(service, seed));
    }

    protected abstract void run(ExecutorService service, ProfileBuilder builder);

    private int seed = 0;
}
