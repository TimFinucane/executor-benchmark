package group16.executor.service.task.management;

import java.util.concurrent.Callable;

/**
 * Provides an interface for any client that wants to be able to retrieve a task given some strategy/implementation.
 */
public interface TaskManager {

    Callable nextTask();
}
