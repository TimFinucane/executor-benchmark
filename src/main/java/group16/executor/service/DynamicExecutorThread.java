package group16.executor.service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

public class DynamicExecutorThread extends Thread {

    private BlockingQueue<Callable> queue;
    private TaskManager taskManager;

    public DynamicExecutorThread(BlockingQueue<Callable> queue, TaskManager taskManager) {
        this.queue = queue;
        this.taskManager = taskManager;
    }

    @Override
    public void run() {
        // TODO: Better concurrency
        if (queue.isEmpty()) {
            //queue = taskManager.nextQueue();
        }

        try {
            queue.remove().call();
        } catch (Exception e) {
            System.out.println("Task threw exception at DynamicExecutorService thread level:");
            e.printStackTrace();
        }
    }
}
