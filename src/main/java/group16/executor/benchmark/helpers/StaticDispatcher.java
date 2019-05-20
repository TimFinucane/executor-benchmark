package group16.executor.benchmark.helpers;

import group16.executor.benchmark.profiles.DispatchListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * A static dispatcher submits all tasks simultaneously
 */
public class StaticDispatcher extends Dispatcher {
    public StaticDispatcher(int totalTasks) {
        this(totalTasks, null);
    }

    public StaticDispatcher(int totalTasks, List<DispatchListener> listeners) {
        super(totalTasks, listeners);
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
