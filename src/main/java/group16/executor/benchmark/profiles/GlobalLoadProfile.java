package group16.executor.benchmark.profiles;

import group16.executor.benchmark.Profile;
import group16.executor.benchmark.ProfileBuilder;
import group16.executor.benchmark.helpers.Dispatcher;
import group16.executor.benchmark.helpers.DynamicDispatcher;
import group16.executor.benchmark.helpers.StaticDispatcher;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A profile identical to tje UniformProfile expect there is an optional set of threads working on top of the threads
 * dispatched to simulate other work occurring elsewhere on the system.
 */
public class GlobalLoadProfile extends Profile {

    private final int tasks;
    private final int taskSize;
    private final int over;
    private final int otherThreads;

    /**
     * @param tasks Number of tasks
     * @param taskSize Size of task. Generally recommend 10000 - 1000000. Will submit +-20% of this amount
     * @param over - How many seconds to submit the above number of tasks over. Defaults to 0 seconds (i.e. static)
     * @param otherThreads Number of other threads dispatched separately simulating other work on the system.
     */
    public GlobalLoadProfile(int tasks, int taskSize, int over, int otherThreads) {
        this.tasks = tasks;
        this.taskSize = taskSize;
        this.over = over;
        this.otherThreads = otherThreads;
    }

    public GlobalLoadProfile(int tasks, int taskSize, int otherThreads) {
        this(tasks, taskSize, 0, otherThreads);
    }

    @Override
    protected Dispatcher generate(ProfileBuilder builder) {
        RealDistribution randomTaskSize = new NormalDistribution(
                builder.getRandom(),
                (double)taskSize, // Mean
                ((double)taskSize) / 30.0); // 99.7% of results lie within +-20% of the task size

        List<DispatchListener> listeners = new ArrayList<>();
        if (this.otherThreads != 0) { // Start n threads running in other service.
            ExecutorService executorService = Executors.newFixedThreadPool(otherThreads);
            AtomicBoolean keepAlive = new AtomicBoolean(true);
            for (int i = 0; i < otherThreads; i++) { // Run these threads until listener method is called.
                executorService.submit(() -> { while (keepAlive.get()) { } });
            }
            executorService.shutdown();
            listeners.add(() -> keepAlive.set(false));
        }

        if(over == 0.0) { // Static
            StaticDispatcher dispatch = new StaticDispatcher(tasks, listeners);

            for(int i = 0; i < tasks; ++i) {
                int accuracy = (int)Math.round(randomTaskSize.sample());
                dispatch.submit(builder.calculator(accuracy));
            }

            return dispatch;
        } else {
            DynamicDispatcher dispatch = new DynamicDispatcher(tasks, listeners);
            double[] waitTimes = builder.splitTimeEvenly(over, tasks);

            for (int i = 0; i < tasks; i++) {
                int accuracy = (int)Math.round(randomTaskSize.sample());
                dispatch.submit(builder.calculator(accuracy), waitTimes[i]);
            }

            return dispatch;
        }
    }
}
