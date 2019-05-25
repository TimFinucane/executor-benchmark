package group16.executor.service.task.management;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

/**
 * Provides an interface for any client that wants to be able to retrieve a task given some strategy/implementation.
 */
public interface TaskManager {
    /**
     * Called when a new thread is created, with the id of the thread
     */
    default void addThread(int threadId) {}

    /**
     * Called when a thread is removed, with the id of the thread
     */
    default void removeThread(int threadId) {}

    void addTask(Runnable task);
    default void addTask(int threadId, Runnable task) {
        addTask(task);
    }

    /**
     * Note: Is blocking
     */
    Runnable nextTask(int threadId) throws InterruptedException;
}
