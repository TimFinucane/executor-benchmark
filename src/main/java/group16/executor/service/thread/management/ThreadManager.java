package group16.executor.service.thread.management;

public interface ThreadManager {

    /**
     * Called when a task is added
     */
    void taskAdded();

    /**
     * Called when a task is completed.
     */
    void taskCompleted();

    /**
     * The number of threads that should be created
     * @return How many threads should be created
     */
    int threadDeficit(int activeThreads);

    /**
     * Whether an idle thread should be removed
     * @param idleTime - The time for which the thread has been idle (in ms)
     */
    boolean shouldKillIdleThread(int activeThreads, long idleTime);
    /**
     * Whether a non-idle thread should be removed
     */
    boolean shouldKillThread(int activeThreads);
}
