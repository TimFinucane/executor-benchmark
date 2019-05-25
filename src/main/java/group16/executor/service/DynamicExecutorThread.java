package group16.executor.service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

public class DynamicExecutorThread extends Thread {

    private BlockingQueue<Callable> queue;
    private QueueManager queueManager;

    public DynamicExecutorThread(BlockingQueue<Callable> queue, QueueManager queueManager) {
        this.queue = queue;
        this.queueManager = queueManager;
    }

    @Override
    public void run() {
        // TODO: Better concurrency
        if (queue.isEmpty()) {
            queue = queueManager.nextNonEmptyQueue();
        }

        try {
            queue.remove().call();
        } catch (Exception e) {
            System.out.println("Task threw exception at DynamicExecutorService thread level:");
            e.printStackTrace();
        }
    }
}
