package group16.executor.benchmark.helpers;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class DynamicDispatcher extends Dispatcher {
    public DynamicDispatcher(int totalTasks) {
        super(totalTasks);
    }

    public void submit(Callable task, double timeTillNext) {
        taskWaitPairs.add(new Pair<>(task, timeTillNext));
    }

    protected void dispatchAllAndWait() {
        for (Pair<Callable, Double> taskWait : taskWaitPairs) {
            dispatch(taskWait.getKey());

            // Wait before dispatching next task
            try {
                Thread.sleep((long)(taskWait.getValue() * TimeUnit.SECONDS.toMillis(1)));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Pair<Callable, Double>> taskWaitPairs = new ArrayList<>();
}
