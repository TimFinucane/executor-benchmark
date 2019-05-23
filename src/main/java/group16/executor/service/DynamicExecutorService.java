package group16.executor.service;

import java.util.List;
import java.util.concurrent.*;

public class DynamicExecutorService extends AbstractExecutorService {

    @Override
    public void shutdown() {
        // TODO:
    }

    @Override
    public List<Runnable> shutdownNow() {
        return null; // TODO:
    }

    @Override
    public boolean isShutdown() {
        return false; // TODO:
    }

    @Override
    public boolean isTerminated() {
        return false; // TODO:
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return false; // TODO:
    }

    @Override
    public void execute(Runnable command) {
        // TODO: Add task to appropriate queue
    }

    // TODO: Thread function. This could be made into a private static class instead
    private static void runThread() {}

    private List<Thread> threads;
}
