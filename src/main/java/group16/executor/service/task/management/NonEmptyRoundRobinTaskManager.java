package group16.executor.service.task.management;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class implements the task returning strategy 'look in the queues from where I was last to return the next
 * non-empty queue's task'.
 *
 * Multiple instances of this class can be instantiated across an application and the queue accesses remain sequential,
 * hence is thread safe.
 */
public class NonEmptyRoundRobinTaskManager implements TaskManager {

    private final List<BlockingQueue<Runnable>> queues;
    private AtomicInteger currentIndex = new AtomicInteger(0);
    private AtomicInteger tasksRemaining = new AtomicInteger(0);

    public NonEmptyRoundRobinTaskManager(int queueCount) {
        // Set up queues and threads
        this.queues = new ArrayList<>(queueCount);
        for (int i = 0; i < queueCount; i++) {
            this.queues.add(new LinkedBlockingQueue<>());
        }
    }

    // TODO: Buster, is this what you wanted? I'm sorry for taking away the whole initial passing in list of queues thing
    // TODO:  but we would'nt be able to do dynamic loads with that manager.
    public void addTask(Runnable task) {
        int index = currentIndex.getAndAccumulate(1, (a, b) -> (a + b) % this.queues.size());
        queues.get(index).add(task);
        tasksRemaining.incrementAndGet();
    }

    /**
     * Returns the next non empty queue's task, STARTING FROM where the last task was successfully returned. If there
     * are no non-empty queues then return null.
     * @return next non-empty queue
     */
    @Override
    public Runnable nextTask(long time, TimeUnit unit) {
        int index = currentIndex.get();

        for (int i = 0; i < queues.size(); i++) {
            // Try take an item off the queue. Returns null (immediately) if empty.
            Runnable task = queues.get(index).poll();
            if (task != null) {
                tasksRemaining.decrementAndGet();
                return task;
            }

            index = currentIndex.accumulateAndGet(1, (a, b) -> (a + b) % this.queues.size());
        }

        return null;
    }

    @Override
    public int remainingTasks() {
        return tasksRemaining.get();
    }
}
