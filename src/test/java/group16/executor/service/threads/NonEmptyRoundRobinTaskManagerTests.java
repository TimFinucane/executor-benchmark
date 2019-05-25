package group16.executor.service.threads;

import group16.executor.service.task.management.NonEmptyRoundRobinTaskManager;
import group16.executor.service.task.management.TaskManager;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;

public class NonEmptyRoundRobinTaskManagerTests {

    @Test
    public void emptyList() {
        TaskManager taskManager = new NonEmptyRoundRobinTaskManager(new ArrayList<>());
        Assert.assertNull(taskManager.nextTask());
    }

    @Test
    public void singleQueueEmptyQueue() {
        List<Queue<Callable>> queues = new ArrayList<>();
        queues.add(new LinkedBlockingQueue<>());
        TaskManager taskManager = new NonEmptyRoundRobinTaskManager(queues);

        Assert.assertNull(taskManager.nextTask());
    }

    @Test
    public void singleQueueSingleQueueItem() {
        List<Queue<Callable>> queues = new ArrayList<>();
        BlockingQueue<Callable> queue = new LinkedBlockingQueue<>();
        Callable callable = () ->  null;

        queue.add(callable);
        queues.add(queue);

        TaskManager taskManager = new NonEmptyRoundRobinTaskManager(queues);

        Assert.assertEquals(callable, taskManager.nextTask());
        Assert.assertNull(taskManager.nextTask());
    }

    @Test
    public void singleQueueTwoQueueItems() {
        List<Queue<Callable>> queues = new ArrayList<>();
        BlockingQueue<Callable> queue = new LinkedBlockingQueue<>();
        Callable callable1 = () ->  null;
        Callable callable2 = () ->  null;
        queue.add(callable1);
        queue.add(callable2);
        queues.add(queue);

        TaskManager taskManager = new NonEmptyRoundRobinTaskManager(queues);


        Assert.assertEquals(callable1, taskManager.nextTask());
        Assert.assertEquals(callable2, taskManager.nextTask());
        Assert.assertNull(taskManager.nextTask());
    }

    @Test
    public void twoQueuesSingleQueueItemEach() {
        List<Queue<Callable>> queues = new ArrayList<>();
        BlockingQueue<Callable> queue1 = new LinkedBlockingQueue<>();
        BlockingQueue<Callable> queue2 = new LinkedBlockingQueue<>();
        Callable callable1 = () ->  null;
        Callable callable2 = () ->  null;
        queue1.add(callable1);
        queue2.add(callable2);
        queues.add(queue1);
        queues.add(queue2);

        TaskManager taskManager = new NonEmptyRoundRobinTaskManager(queues);

        Assert.assertEquals(callable1, taskManager.nextTask());
        Assert.assertEquals(callable2, taskManager.nextTask());
        Assert.assertNull(taskManager.nextTask());
    }

    @Test
    public void twoQueuesOneIsEmpty() {
        List<Queue<Callable>> queues = new ArrayList<>();
        BlockingQueue<Callable> queue1 = new LinkedBlockingQueue<>();
        BlockingQueue<Callable> queue2 = new LinkedBlockingQueue<>();
        Callable callable1 = () ->  null;
        queue1.add(callable1);
        queues.add(queue1);
        queues.add(queue2);

        TaskManager taskManager = new NonEmptyRoundRobinTaskManager(queues);

        Assert.assertEquals(callable1, taskManager.nextTask());
        Assert.assertNull(taskManager.nextTask());
    }

    @Test
    public void twoQueuesOneIsReplenished() {
        List<Queue<Callable>> queues = new ArrayList<>();
        BlockingQueue<Callable> queue1 = new LinkedBlockingQueue<>();
        BlockingQueue<Callable> queue2 = new LinkedBlockingQueue<>();
        Callable callable1 = () ->  null;
        queue1.add(callable1);
        queues.add(queue1);
        queues.add(queue2);

        TaskManager taskManager = new NonEmptyRoundRobinTaskManager(queues);

        Assert.assertEquals(callable1, taskManager.nextTask());
        Assert.assertNull(taskManager.nextTask());

        Callable callable2 = () ->  null;
        queue1.add(callable2);

        Assert.assertEquals(callable2, taskManager.nextTask());
        Assert.assertNull(taskManager.nextTask());
    }

    /**
     * This test is basically the kitchen sink, 3 queues are initialized all with one callable each. The TaskManager
     * is called once and returns the first queue task, then is called again to return the second queue task. After
     * this, another task is added back to queue 1. When the TaskManager is called again for the next non-empty queue's
     * task, it should return queue 3's task, showing that is has 'remembered' where it was up to, and does not default
     * back to returning queue 1's task just because it is non-empty again. After queue 3's task is retrieved the next
     * non-empty queue's task is called for once again where it 'wraps around' to queue 1. TaskManager is called a final
     * time which should return null because all queues are now empty.
     */
    @Test
    public void threeQueuesIndexIsRememberedAndWraparound() {
        List<Queue<Callable>> queues = new ArrayList<>();
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

        TaskManager taskManager = new NonEmptyRoundRobinTaskManager(queues);

        Assert.assertEquals(callable1, taskManager.nextTask());
        Assert.assertEquals(callable2, taskManager.nextTask());

        // Insert another element back at the first queue to test whether index is remembered
        Callable callable4 = () ->  null;
        queue1.add(callable4);

        Assert.assertEquals(callable3, taskManager.nextTask());
        Assert.assertEquals(callable4, taskManager.nextTask());
        Assert.assertNull(taskManager.nextTask());
    }
}
