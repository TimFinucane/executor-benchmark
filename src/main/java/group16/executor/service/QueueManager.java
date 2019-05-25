package group16.executor.service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

public interface QueueManager {

    BlockingQueue<Callable> nextNonEmptyQueue();
}
