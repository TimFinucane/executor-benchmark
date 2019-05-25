package group16.executor.service;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;

public class DynamicExecutorThread extends Thread {

    private List<Queue<Callable>> queues;

    public DynamicExecutorThread(List<Queue<Callable>> queues) {
        this.queues = queues;
    }

    @Override
    public void run() {

    }
}
