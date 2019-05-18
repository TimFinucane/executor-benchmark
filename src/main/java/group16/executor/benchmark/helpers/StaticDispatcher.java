package group16.executor.benchmark.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class StaticDispatcher extends Dispatcher {
    public StaticDispatcher(int totalTasks) {
        super(totalTasks);
    }

    public void submit(Callable task) {
        tasks.add(task);
    }

    @Override
    protected void dispatchAllAndWait() {
        for(Callable task : tasks)
            this.dispatch(task);
    }

    private List<Callable> tasks = new ArrayList<>();
}
