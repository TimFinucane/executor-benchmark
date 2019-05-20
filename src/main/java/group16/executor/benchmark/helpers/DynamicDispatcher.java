package group16.executor.benchmark.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * A dynamic dispatcher dispatches tasks to the executor service over time.
 */
public class DynamicDispatcher extends Dispatcher {
    public DynamicDispatcher(int totalTasks) {
        super(totalTasks);
    }

    /**
     * @param timeTillNext The time to wait after submitting this task and before submitting the next.
     */
    public void submit(Callable task, double timeTillNext) {

        // Add tasks to a dispatch queue, one for every dispatch period defined in the DISPATCH_PERIOD_MILLIS constant
        // avoids the loss of precision with sleeping for trivially short time periods.
        dispatchPeriodAccumulation += timeTillNext * TimeUnit.SECONDS.toMillis(1);
        currentDispatch.add(task);
        while (dispatchPeriodAccumulation >= DISPATCH_PERIOD_MILLIS) {
            dispatchPeriodAccumulation -= DISPATCH_PERIOD_MILLIS;
            taskPerTimeUnitMap.add(currentDispatch);
            currentDispatch = new ArrayList<>();
        }
    }

    protected void dispatchAllAndWait() {
        for (List<Callable> tasks : taskPerTimeUnitMap) {
            for (Callable task : tasks) {
                dispatch(task);
            }
            try {
                Thread.sleep(DISPATCH_PERIOD_MILLIS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static final long DISPATCH_PERIOD_MILLIS = 30; // 15 millis and upwards seems to work pretty well

    private double dispatchPeriodAccumulation = 0;
    private List<Callable> currentDispatch = new ArrayList<>();
    private List<List<Callable>> taskPerTimeUnitMap = new ArrayList<>();
}
