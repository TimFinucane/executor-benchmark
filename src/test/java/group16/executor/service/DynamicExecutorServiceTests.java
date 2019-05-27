package group16.executor.service;

import group16.executor.service.DynamicExecutorService;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class DynamicExecutorServiceTests {

    @Test
    public void runsTask_within1Second() {
        DynamicExecutorService executorService = new DynamicExecutorService();

        AtomicBoolean b = new AtomicBoolean(false);

        executorService.submit(() -> b.set(true));

        try {
            Thread.sleep(1000);
            Assert.assertEquals(true, b.get());
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
