package group16.executor.benchmark;

import javafx.util.Pair;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class DynamicDispatcher {

    private List<Pair<Callable, Double>> taskWaitPairs;

    public void dynamicallyDispatch(Callable task, double timeTillNext) {
        taskWaitPairs.add(new Pair<>(task, timeTillNext));
    }

    public void begin(ExecutorService service) {
        for (Pair<Callable, Double> taskWait : taskWaitPairs) {
            service.submit(taskWait.getKey());
            try {
                Thread.sleep(taskWait.getValue().longValue());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
