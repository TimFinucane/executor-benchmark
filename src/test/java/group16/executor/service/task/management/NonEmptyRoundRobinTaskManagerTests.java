package group16.executor.service.task.management;

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
import java.util.concurrent.TimeUnit;

public class NonEmptyRoundRobinTaskManagerTests {

    @Test
    public void emptyList() throws InterruptedException {
        TaskManager taskManager = new NonEmptyRoundRobinTaskManager(0);
        Assert.assertNull(taskManager.nextTask(1, TimeUnit.SECONDS));
    }

    @Test
    public void singleQueueEmptyQueue() throws InterruptedException {
        TaskManager taskManager = new NonEmptyRoundRobinTaskManager(1);

        Assert.assertNull(taskManager.nextTask(1, TimeUnit.SECONDS));
    }

    @Test
    public void singleQueueSingleQueueItem() throws InterruptedException {
        Runnable task = () -> {};
        TaskManager taskManager = new NonEmptyRoundRobinTaskManager(1);
        taskManager.addTask(task);

        Assert.assertEquals(task, taskManager.nextTask(1, TimeUnit.SECONDS));
        Assert.assertNull(taskManager.nextTask(1, TimeUnit.SECONDS));
    }

    @Test
    public void singleQueueTwoQueueItems() throws InterruptedException {
        Runnable task1 = () -> {};
        Runnable task2 = () -> {};

        TaskManager taskManager = new NonEmptyRoundRobinTaskManager(1);
        taskManager.addTask(task1);
        taskManager.addTask(task2);


        Assert.assertEquals(task1, taskManager.nextTask(1, TimeUnit.SECONDS));
        Assert.assertEquals(task2, taskManager.nextTask(1, TimeUnit.SECONDS));
        Assert.assertNull(taskManager.nextTask(1, TimeUnit.SECONDS));
    }

    @Test
    public void twoQueuesSingleQueueItemEach() throws InterruptedException {
        Runnable task1 = () -> {};
        Runnable task2 = () -> {};

        TaskManager taskManager = new NonEmptyRoundRobinTaskManager(2);
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Assert.assertEquals(task1, taskManager.nextTask(1, TimeUnit.SECONDS));
        Assert.assertEquals(task2, taskManager.nextTask(1, TimeUnit.SECONDS));
        Assert.assertNull(taskManager.nextTask(1, TimeUnit.SECONDS));
    }

    @Test
    public void twoQueuesOneIsEmpty() throws InterruptedException {
        Runnable task1 = () -> {};

        TaskManager taskManager = new NonEmptyRoundRobinTaskManager(2);
        taskManager.addTask(task1);

        Assert.assertEquals(task1, taskManager.nextTask(1, TimeUnit.SECONDS));
        Assert.assertNull(taskManager.nextTask(1, TimeUnit.SECONDS));
    }

    @Test
    public void twoQueuesOneIsReplenished() throws InterruptedException {
        Runnable task1 = () -> {};

        TaskManager taskManager = new NonEmptyRoundRobinTaskManager(2);
        taskManager.addTask(task1);

        Assert.assertEquals(task1, taskManager.nextTask(1, TimeUnit.SECONDS));
        Assert.assertNull(taskManager.nextTask(1, TimeUnit.SECONDS));
        Assert.assertEquals(0, taskManager.remainingTasks());

        Runnable task2 = () -> {};
        taskManager.addTask(task2);

        Assert.assertEquals(1, taskManager.remainingTasks());
        Assert.assertEquals(task2, taskManager.nextTask(1, TimeUnit.SECONDS));
        Assert.assertNull(taskManager.nextTask(1, TimeUnit.SECONDS));
        Assert.assertEquals(0, taskManager.remainingTasks());
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
    public void threeQueuesIndexIsRememberedAndWraparound() throws InterruptedException {
        Runnable task1 = () -> {};
        Runnable task2 = () -> {};
        Runnable task3 = () -> {};

        TaskManager taskManager = new NonEmptyRoundRobinTaskManager(3);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        Assert.assertEquals(3, taskManager.remainingTasks());

        Assert.assertEquals(task1, taskManager.nextTask(1, TimeUnit.SECONDS));
        Assert.assertEquals(task2, taskManager.nextTask(1, TimeUnit.SECONDS));
        Assert.assertEquals(1, taskManager.remainingTasks());

        // Insert another element back at the first queue to test whether index is remembered
        Runnable task4 = () -> {};
        taskManager.addTask(task4);
        Assert.assertEquals(2, taskManager.remainingTasks());

        Assert.assertEquals(task3, taskManager.nextTask(1, TimeUnit.SECONDS));
        Assert.assertEquals(task4, taskManager.nextTask(1, TimeUnit.SECONDS));
        Assert.assertNull(taskManager.nextTask(1, TimeUnit.SECONDS));
        Assert.assertEquals(0, taskManager.remainingTasks());
    }
}
