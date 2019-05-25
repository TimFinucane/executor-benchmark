package group16.executor.service.threads;

import group16.executor.service.QueueManager;
import group16.executor.service.RoundRobinQueueManager;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;

public class RoundRobinQueueManagerTests {

    @Test
    public void emptyList() {
        QueueManager qm = new RoundRobinQueueManager(new ArrayList<>());
        Assert.assertNull(qm.nextNonEmptyQueue());
    }

    @Test
    public void singleListItemEmptyQueue() {
        List<BlockingQueue<Callable>> queues = new ArrayList<>();
        queues.add(new LinkedBlockingQueue<>());
        QueueManager qm = new RoundRobinQueueManager(queues);

        Assert.assertNull(qm.nextNonEmptyQueue());
    }

    @Test
    public void singleListItemSingleQueueItem() {
        List<BlockingQueue<Callable>> queues = new ArrayList<>();
        BlockingQueue<Callable> queue = new LinkedBlockingQueue<>();
        Callable callable = () ->  null;

        queue.add(callable);
        queues.add(queue);

        QueueManager qm = new RoundRobinQueueManager(queues);

        BlockingQueue<Callable> returnedQueue = qm.nextNonEmptyQueue();

        Assert.assertEquals(1, returnedQueue.size());
        Assert.assertEquals(callable, returnedQueue.remove());
        try {
            returnedQueue.remove();
            Assert.fail();
        } catch (NoSuchElementException e) {}
    }
}
