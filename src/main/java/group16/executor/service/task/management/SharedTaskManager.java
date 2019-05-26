package group16.executor.service.task.management;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SharedTaskManager implements TaskManager {

    @Override
    public void addTask(Runnable task) {
        queue.add(task);
    }

    @Override
    public Runnable nextTask(long amount, TimeUnit unit) throws InterruptedException {
        return queue.poll(amount, unit);
    }

    @Override
    public int remainingTasks() {
        return queue.size();
    }

    private BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
}
