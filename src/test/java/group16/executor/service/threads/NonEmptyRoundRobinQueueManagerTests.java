package group16.executor.service.threads;

import group16.executor.service.NonEmptyRoundRobinQueueManager;
import group16.executor.service.QueueManager;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;

public class NonEmptyRoundRobinQueueManagerTests {

    @Test
    public void emptyList() {
        QueueManager qm = new NonEmptyRoundRobinQueueManager(new ArrayList<>());
        Assert.assertNull(qm.nextQueue());
    }

    @Test
    public void singleQueueEmptyQueue() {
        List<BlockingQueue<Callable>> queues = new ArrayList<>();
        queues.add(new LinkedBlockingQueue<>());
        QueueManager qm = new NonEmptyRoundRobinQueueManager(queues);

        Assert.assertNull(qm.nextQueue());
    }

    @Test
    public void singleQueueSingleQueueItem() {
        List<BlockingQueue<Callable>> queues = new ArrayList<>();
        BlockingQueue<Callable> queue = new LinkedBlockingQueue<>();
        Callable callable = () ->  null;

        queue.add(callable);
        queues.add(queue);

        QueueManager qm = new NonEmptyRoundRobinQueueManager(queues);

        BlockingQueue<Callable> returnedQueue = qm.nextQueue();

        Assert.assertEquals(1, returnedQueue.size());
        Assert.assertEquals(callable, returnedQueue.remove());
        try {
            returnedQueue.remove();
            Assert.fail();
        } catch (NoSuchElementException e) {}
    }

    @Test
    public void singleQueueTwoQueueItems() {
        List<BlockingQueue<Callable>> queues = new ArrayList<>();
        BlockingQueue<Callable> queue = new LinkedBlockingQueue<>();
        Callable callable1 = () ->  null;
        Callable callable2 = () ->  null;

        queue.add(callable1);
        queue.add(callable2);
        queues.add(queue);

        QueueManager qm = new NonEmptyRoundRobinQueueManager(queues);

        BlockingQueue<Callable> returnedQueue = qm.nextQueue();

        Assert.assertEquals(2, returnedQueue.size());
        Assert.assertEquals(callable1, returnedQueue.remove());
        Assert.assertEquals(callable2, returnedQueue.remove());
        try {
            returnedQueue.remove();
            Assert.fail();
        } catch (NoSuchElementException e) {}
    }

    @Test
    public void twoQueuesSingleQueueItemEach() {
        List<BlockingQueue<Callable>> queues = new ArrayList<>();
        BlockingQueue<Callable> queue1 = new LinkedBlockingQueue<>();
        BlockingQueue<Callable> queue2 = new LinkedBlockingQueue<>();

        Callable callable1 = () ->  null;
        Callable callable2 = () ->  null;

        queue1.add(callable1);
        queue2.add(callable2);

        queues.add(queue1);
        queues.add(queue2);

        QueueManager qm = new NonEmptyRoundRobinQueueManager(queues);

        BlockingQueue<Callable> returnedQueue1 = qm.nextQueue();

        Assert.assertEquals(1, returnedQueue1.size());
        Assert.assertEquals(callable1, returnedQueue1.remove());
        try {
            returnedQueue1.remove();
            Assert.fail();
        } catch (NoSuchElementException e) {}

        BlockingQueue<Callable> returnedQueue2 = qm.nextQueue();

        Assert.assertEquals(1, returnedQueue2.size());
        Assert.assertEquals(callable2, returnedQueue2.remove());
        try {
            returnedQueue2.remove();
            Assert.fail();
        } catch (NoSuchElementException e) {}
    }

    @Test
    public void twoQueuesOneIsEmpty() {
        List<BlockingQueue<Callable>> queues = new ArrayList<>();
        BlockingQueue<Callable> queue1 = new LinkedBlockingQueue<>();
        BlockingQueue<Callable> queue2 = new LinkedBlockingQueue<>();

        Callable callable1 = () ->  null;

        queue1.add(callable1);

        queues.add(queue1);
        queues.add(queue2);

        QueueManager qm = new NonEmptyRoundRobinQueueManager(queues);

        BlockingQueue<Callable> returnedQueue1 = qm.nextQueue();

        Assert.assertEquals(1, returnedQueue1.size());
        Assert.assertEquals(callable1, returnedQueue1.remove());
        try {
            returnedQueue1.remove();
            Assert.fail();
        } catch (NoSuchElementException e) {}
        Assert.assertNull(qm.nextQueue());
    }

    @Test
    public void twoQueuesOneIsReplenished() {
        List<BlockingQueue<Callable>> queues = new ArrayList<>();
        BlockingQueue<Callable> queue1 = new LinkedBlockingQueue<>();
        BlockingQueue<Callable> queue2 = new LinkedBlockingQueue<>();
        Callable callable1 = () ->  null;

        queue1.add(callable1);
        queues.add(queue1);
        queues.add(queue2);

        QueueManager qm = new NonEmptyRoundRobinQueueManager(queues);

        BlockingQueue<Callable> returnedQueue1 = qm.nextQueue();

        Assert.assertEquals(1, returnedQueue1.size());
        Assert.assertEquals(callable1, returnedQueue1.remove());
        try {
            returnedQueue1.remove();
            Assert.fail();
        } catch (NoSuchElementException e) {}

        Callable callable2 = () ->  null;
        queue1.add(callable2);

        BlockingQueue<Callable> returnedQueue2 = qm.nextQueue();

        Assert.assertEquals(returnedQueue1, returnedQueue2);
        Assert.assertEquals(1, returnedQueue2.size());
        Assert.assertEquals(callable2, returnedQueue2.remove());
        try {
            returnedQueue2.remove();
            Assert.fail();
        } catch (NoSuchElementException e) {}
    }

    /**
     * This test is basically the kitchen sink, 3 queues are initialized all with one callable each. The QueueManager
     * is called once and returns the first queue, then is called again to return the second queue. After this, another
     * task is added back to task 1. When the QueueManager is called again for the next non-empty queue it should return
     * queue 3, showing that is has 'remembered' where it was up to, and does not default back to returning queue 1 just
     * because it is non-empty again. After queue 3 is retrieved the next non-empty queue is called for once again where
     * it 'wraps around' to queue 1. QueueManager is called a final time which should return null because all queues are
     * now empty.
     */
    @Test
    public void threeQueuesIndexIsRememberedAndWraparound() {
        List<BlockingQueue<Callable>> queues = new ArrayList<>();
        BlockingQueue<Callable> queue1 = new LinkedBlockingQueue<>();
        BlockingQueue<Callable> queue2 = new LinkedBlockingQueue<>();
        BlockingQueue<Callable> queue3 = new LinkedBlockingQueue<>();
        Callable callable1 = () ->  null;
        Callable callable2 = () ->  null;
        Callable callable3 = () ->  null;
        queue1.add(callable1);
        queue2.add(callable2);
        queue3.add(callable3);
        queues.add(queue1);
        queues.add(queue2);
        queues.add(queue3);

        QueueManager qm = new NonEmptyRoundRobinQueueManager(queues);

        BlockingQueue<Callable> returnedQueue1 = qm.nextQueue();
        Assert.assertEquals(1, returnedQueue1.size());
        Assert.assertEquals(queue1, returnedQueue1);
        Assert.assertEquals(callable1, returnedQueue1.remove());
        try {
            returnedQueue1.remove();
            Assert.fail();
        } catch (NoSuchElementException e) {}

        BlockingQueue<Callable> returnedQueue2 = qm.nextQueue();
        Assert.assertEquals(1, returnedQueue2.size());
        Assert.assertEquals(queue2, returnedQueue2);
        Assert.assertEquals(callable2, returnedQueue2.remove());
        try {
            returnedQueue2.remove();
            Assert.fail();
        } catch (NoSuchElementException e) {}

        // Insert another element back at the first queue to test whether index is remembered
        Callable callable4 = () ->  null;
        queue1.add(callable4);

        BlockingQueue<Callable> returnedQueue3 = qm.nextQueue();
        Assert.assertEquals(1, returnedQueue3.size());
        Assert.assertEquals(queue3, returnedQueue3);
        Assert.assertEquals(callable3, returnedQueue3.remove());
        try {
            returnedQueue3.remove();
            Assert.fail();
        } catch (NoSuchElementException e) {}

        BlockingQueue<Callable> returnedQueue4 = qm.nextQueue();
        Assert.assertEquals(1, returnedQueue4.size());
        Assert.assertEquals(queue1, returnedQueue4);
        Assert.assertEquals(callable4, returnedQueue4.remove());
        try {
            returnedQueue4.remove();
            Assert.fail();
        } catch (NoSuchElementException e) {}
        Assert.assertNull(qm.nextQueue());
    }
}
