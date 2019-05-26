package group16.executor.service.task.management;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class SharedTaskManagerTests {

    @Test
    public void empty_timeout() throws InterruptedException {
        TaskManager taskManager = new SharedTaskManager();
        Assert.assertNull(taskManager.nextTask(1, TimeUnit.SECONDS));
        Assert.assertEquals(0, taskManager.remainingTasks());
    }

    @Test
    public void oneItem_getThenTimeout() throws InterruptedException {
        Runnable task = () -> {};
        TaskManager taskManager = new SharedTaskManager();
        taskManager.addTask(task);

        Assert.assertEquals(task, taskManager.nextTask(1, TimeUnit.SECONDS));
        Assert.assertNull(taskManager.nextTask(1, TimeUnit.SECONDS));
    }

    @Test
    public void twoItems_getInOrder() throws InterruptedException {
        Runnable task1 = () -> {};
        Runnable task2 = () -> {};

        TaskManager taskManager = new SharedTaskManager();
        Assert.assertEquals(0, taskManager.remainingTasks());
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Assert.assertEquals(2, taskManager.remainingTasks());
        Assert.assertEquals(task1, taskManager.nextTask(1, TimeUnit.SECONDS));
        Assert.assertEquals(task2, taskManager.nextTask(1, TimeUnit.SECONDS));
        Assert.assertEquals(0, taskManager.remainingTasks());
    }

    @Test
    public void addAfterGet_addsTask() throws InterruptedException {
        Runnable task1 = () -> {};

        TaskManager taskManager = new SharedTaskManager();
        taskManager.addTask(task1);

        Assert.assertEquals(task1, taskManager.nextTask(1, TimeUnit.SECONDS));
        Assert.assertEquals(0, taskManager.remainingTasks());

        Runnable task2 = () -> {};
        taskManager.addTask(task2);

        Assert.assertEquals(1, taskManager.remainingTasks());
        Assert.assertEquals(task2, taskManager.nextTask(1, TimeUnit.SECONDS));
        Assert.assertEquals(0, taskManager.remainingTasks());
    }

    @Test
    public void addAfterGet_completesInOrder() throws InterruptedException {
        Runnable task1 = () -> {};
        Runnable task2 = () -> {};
        Runnable task3 = () -> {};

        TaskManager taskManager = new SharedTaskManager();
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
        Assert.assertEquals(0, taskManager.remainingTasks());
    }
}
