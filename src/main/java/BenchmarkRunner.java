import profiles.Profile;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * A benchmark runner is used to run a series of benchmarks in a controlled environment.
 */
public class BenchmarkRunner {
    public static double runWith(Supplier<ExecutorService> serviceConstructor, Profile profile) {
        return runWith(serviceConstructor, profile, DEFAULT_RUN_COUNT);
    }
    public static double runWith(Supplier<ExecutorService> serviceConstructor, Profile profile, int times) {
        // TODO: We should be careful this doesnt speed up over time when the processor or OS gets used to the threading.
        double totalTime = 0;

        try {
            for (int i = 0; i < times; ++i) {
                // Create a new service
                ExecutorService service = serviceConstructor.get();

                long startTime = System.nanoTime();

                // Allocate every task
                for (Callable<?> task : profile.tasks) {
                    service.submit(task);
                }

                // Wait for tasks to complete
                service.shutdown();
                service.awaitTermination(1, TimeUnit.MINUTES);

                long endTime = System.nanoTime();

                totalTime += (endTime - startTime) / (double)TimeUnit.SECONDS.toNanos(1);
            }
        } catch(InterruptedException e) {
            throw new Error("Did not expect an interrupt");
        }

        return totalTime / (double)times;
    }

    private static final int DEFAULT_RUN_COUNT = 10;
}
