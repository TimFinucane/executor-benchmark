package group16.executor.service.task.management;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RatioTaskManager implements TaskManager {
    public RatioTaskManager(double ratio) {
        this.ratio = ratio;
    }

    @Override
    public void addThread(int threadId) {
        this.mappingLock.writeLock().lock();
        try {
            queueMapping.put(threadId, /* TODO */ 0);
            // TODO: Make more queues if necessary
        } finally {
            this.mappingLock.writeLock().unlock();
        }
    }

    @Override
    public void removeThread(int threadId) {
        // TODO: Maybe we don't need to lock? If there's some other non-locking way of informing a thread is no longer used
        this.mappingLock.writeLock().lock();
        try {
            queueMapping.remove(threadId);
            // TODO: Remove queues if necessary
        } finally {
            this.mappingLock.writeLock().unlock();
        }
    }

    @Override
    public void addTask(Runnable task) {
        this.mappingLock.readLock().lock();
        try {
            // Use this index, then increment the round robin getter.
            int index = queueToAddToNext.getAndAccumulate(1, (x, y) -> (x + y) % queues.size());
            queues.get(index).add(task);
        } finally {
            this.mappingLock.readLock().unlock();
        }
    }

    @Override
    public void addTask(int threadId, Runnable task) {
        this.mappingLock.readLock().lock();
        try {
            queues.get(queueMapping.get(threadId)).add(task);
        } finally {
            this.mappingLock.readLock().unlock();
        }
    }

    @Override
    public Runnable nextTask(int threadId) throws InterruptedException {
        this.mappingLock.readLock().lock();
        try {
            int index = queueMapping.get(threadId);

            // Try with each queue, starting with the one belonging to the given thread, getting tasks
            for(int i = 0; i < queues.size(); ++i) {
                Runnable task = queues.get((index + i) % queues.size()).poll();
                if(task != null)
                    return task;
            }

            // At this point no queue has any tasks, so just hang trying to take from the original queue.
            // TODO: Is this best behaviour? We have to stay in the readLock() and if tasks are added to any other queue
            // TODO: then we won't know. Maybe an alternative is to keep going through a while loop of this func, occasionally exiting the lock?
            return queues.get(queueMapping.get(threadId)).take();
        } finally {
            this.mappingLock.readLock().unlock();
        }
    }

    private AtomicInteger queueToAddToNext = new AtomicInteger(0);
    // TODO: Should we use a readwrite lock for the mapping + queue? Or make the queue or the queue mapping or both blocking structures?
    // TODO: Or should we use one lock for each?
    private ReadWriteLock mappingLock = new ReentrantReadWriteLock();
    private Map<Integer, Integer> queueMapping = new HashMap<>();
    private List<BlockingQueue<Runnable>> queues = new ArrayList<>();
    private double ratio;
}
