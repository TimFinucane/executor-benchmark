package group16.executor.benchmark;

import group16.executor.benchmark.helpers.Dispatcher;
import group16.executor.benchmark.metrics.Metrics;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A profile creates and submits tasks to an executor service.
 */
public abstract class Profile {
    public Dispatcher generate() {
        return generate(new Random().nextInt());
    }
    public Dispatcher generate(int seed) {
        return generate(new ProfileBuilder(seed));
    }
    protected abstract Dispatcher generate(ProfileBuilder builder);
}
