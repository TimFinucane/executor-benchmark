package group16.executor.service.task.management;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;

public class SharedTaskManager implements TaskManager {

    @Override
    public void addTask(Runnable task) {
        queue.add(task);
    }

    @Override
    public Runnable nextTask(int threadId) throws InterruptedException {
        return queue.take();
    }

    private BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
}
