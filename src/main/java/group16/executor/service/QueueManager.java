package group16.executor.service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

/**
 * Provides an interface for any client that wants to be able to retrieve a queue given some strategy/implementation.
 */
public interface QueueManager {

    BlockingQueue<Callable> nextQueue();
}
