package group16.executor.benchmark.helpers;

import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * A dynamic dispatcher dispatches tasks to the executor service over time.
 */
public class DynamicDispatcher extends Dispatcher {
    public DynamicDispatcher(int totalTasks) {
        super(totalTasks);
        tasks = new ArrayList<>(totalTasks);
    }

    /**
     * @param time The time at which the task should be run
     */
    public void submit(Callable task, double time) {
        int index = Collections.binarySearch(tasks, null, (item, obj) -> Double.compare(item.getFirst(), time));
        if(index < 0)
            index = -index - 1;

        tasks.add(index, new Pair<>(time, task));
    }

    protected void dispatchAllAndWait() {

        // Our specific adjustment to try and anticipate lags
        double predictedLag = 0.0;

        long startTime;
        long curTime = startTime = System.nanoTime();
        for (Pair<Double, Callable> item : tasks) {
            long runTime = startTime + (long)(item.getFirst() * TimeUnit.SECONDS.toNanos(1));
            Callable task = item.getSecond();

            long diff = runTime - curTime;

            // If we need to wait, then wait.
            if(runTime > curTime && diff > MINIMUM_DISPATCH_PERIOD) {
                try {
                    // Sleep necessary amount of time to get up to date with expected time.
                    Thread.sleep(TimeUnit.NANOSECONDS.toMillis(
                            Math.max(0, Math.round(diff - predictedLag)))
                    );

                    long expectedTime = curTime + diff;
                    curTime = System.nanoTime();

                    // Sleep always goes at or over time, therefore curTime should always exceed expectedTime
                    double lag = curTime - (double)expectedTime;
                    // A moving average of how much a single sleep seems to come OVER the limit
                    predictedLag = lag / 2.0 + predictedLag / 2.0;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            dispatch(task);
        }
    }

    // In nanoseconds.
    // 15 millis and upwards seems to work pretty well
    private static final long MINIMUM_DISPATCH_PERIOD = 30000000;

    private List<Pair<Double, Callable>> tasks;
}
