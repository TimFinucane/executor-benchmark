package group16.executor.service;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class implements the queue returning strategy 'look from where I was last to return the next non-empty queue'.
 */
public class NonEmptyRoundRobinQueueManager implements QueueManager {

    private List<BlockingQueue<Callable>> queues;
    private int currentIndex;

    private static final ReentrantLock queueIterationLock = new ReentrantLock();

    public NonEmptyRoundRobinQueueManager(List<BlockingQueue<Callable>> queues) {
        this.queues = queues;
        this.currentIndex = 0;
    }

    /**
     * Returns the next non empty queue, STARTING FROM where the last queue was successfully returned. If there are no
     * non-empty queues then return null.
     * @return next non-empty queue
     */
    @Override
    public BlockingQueue<Callable> nextQueue() {
        synchronized (queueIterationLock) {
            for (int i = 0; i < queues.size(); i++) {
                if (!queues.get(currentIndex).isEmpty()) {
                    return queues.get(currentIndex);
                }
                currentIndex = currentIndex >= queues.size() - 1 ? 0 : currentIndex + 1;
            }
        }

        return null;
    }
}
