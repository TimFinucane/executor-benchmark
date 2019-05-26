package group16.executor.service.task.management;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FixedQueueTaskManager implements TaskManager {
    public FixedQueueTaskManager(int queueCount) {
        this.queues = new ArrayList<>(queueCount);
        for(int i = 0; i < queueCount; ++i)
            this.queues.add(new LinkedBlockingQueue<>());
    }

    @Override
    public void addThread(int threadId) {
        this.mappingLock.lock();
        try {
            // Distribute threads along queues evenly
            AtomicInteger index = new AtomicInteger(activeThreads.size() % this.queues.size());
            activeThreads.add(new AbstractMap.SimpleImmutableEntry<>(threadId, index));
            queueIndex.set(index);
        } finally {
            this.mappingLock.unlock();
        }
    }

    @Override
    public void removeThread(int threadId) {
        this.mappingLock.lock();
        try {
            // Shift threads above this one down by one slot.
            int index = -1;
            for(int i = 0; i < activeThreads.size(); ++i) {
                if(activeThreads.get(i).getKey() == threadId)
                    index = i;
                else if(index > 0)
                    activeThreads.get(i).getValue().decrementAndGet();
            }
            activeThreads.remove(index);
        } finally {
            this.mappingLock.unlock();
        }
    }

    @Override
    public void addTask(Runnable task) {
        // Use this index, then increment the round robin getter.
        int index = getQueueIndex();
        queues.get(index).add(task);
        tasksRemaining.incrementAndGet();
    }

    @Override
    public Runnable nextTask(long amount, TimeUnit unit) throws InterruptedException {
        int index = getQueueIndex();

        // Try with each queue, starting with the one belonging to the given thread, getting tasks
        for(int i = 0; i < queues.size(); ++i) {
            Runnable task = queues.get((index + i) % queues.size()).poll();
            if(task != null) {
                tasksRemaining.decrementAndGet();
                return task;
            }
        }

        // At this point no queue has any tasks, so just hang trying to take from the original queue.
        // TODO: Is this best behaviour? We have to stay in the readLock() and if tasks are added to any other queue
        // TODO: then we won't know. Maybe an alternative is to keep going through a while loop of this func, occasionally exiting the lock?
        Runnable r = queues.get(index).poll(amount, unit);
        if(r != null)
            tasksRemaining.decrementAndGet();
        return r;
    }

    @Override
    public int remainingTasks() {
        return tasksRemaining.get();
    }

    private int getQueueIndex() {
        AtomicInteger threadsQueue = queueIndex.get();
        return threadsQueue != null ? threadsQueue.get() : queueToAddToNext.getAndAccumulate(1, (x, y) -> (x + y) % queues.size());
    }

    private AtomicInteger tasksRemaining = new AtomicInteger(0);
    private AtomicInteger queueToAddToNext = new AtomicInteger(0);

    private Lock mappingLock = new ReentrantLock();
    private List<AbstractMap.SimpleImmutableEntry<Integer, AtomicInteger>> activeThreads = new ArrayList<>();
    private ThreadLocal<AtomicInteger> queueIndex = new ThreadLocal<>();

    private List<BlockingQueue<Runnable>> queues;
}
