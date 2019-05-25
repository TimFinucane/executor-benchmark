package group16.executor.service.task.management;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;

/**
 * This class implements the task returning strategy 'look in the queues from where I was last to return the next
 * non-empty queue's task'.
 *
 * Multiple instances of this class can be instantiated across an application and the queue accesses remain sequential,
 * hence is thread safe.
 */
public class NonEmptyRoundRobinTaskManager implements TaskManager {

    private final List<Queue<Callable>> queues;
    private int currentIndex;

    public NonEmptyRoundRobinTaskManager(List<Queue<Callable>> queues) {
        this.queues = queues;
        this.currentIndex = 0;
    }

    /**
     * Returns the next non empty queue's task, STARTING FROM where the last task was successfully returned. If there
     * are no non-empty queues then return null.
     * @return next non-empty queue
     */
    @Override
    public Callable nextTask() {
        for (int i = 0; i < queues.size(); i++) {
            synchronized (queues.get(currentIndex)) {
                if (!queues.get(currentIndex).isEmpty()) {
                    return queues.get(currentIndex).remove();
                }
                currentIndex = currentIndex >= queues.size() - 1 ? 0 : currentIndex + 1;
            }
        }

        return null;
    }
}
