package group16.executor.service;

import group16.executor.service.DynamicExecutorService;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class DynamicExecutorServiceTests {

    @Test
    public void testTerminates() {
        DynamicExecutorService executorService = new DynamicExecutorService(1, 1, 50);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
