package group16.executor.service;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;

public class RoundRobinQueueManager implements QueueManager {

    private List<BlockingQueue<Callable>> queues;
    private int currentIndex;

    private static final ReentrantLock queueIterationLock = new ReentrantLock();

    public RoundRobinQueueManager(List<BlockingQueue<Callable>> queues) {
        this.queues = queues;
        this.currentIndex = 0;
    }

    @Override
    public BlockingQueue<Callable> nextNonEmptyQueue() {
        synchronized (queueIterationLock) {
            int index = currentIndex;
            for (int i = 0; i < queues.size(); i++) {
                if (!queues.get(index).isEmpty()) {
                    return queues.get(index);
                }
                index = index >= queues.size() ? 0 : index + 1;
            }
        }

        return null;
    }
}
