package group16.executor.benchmark.profiles;

import group16.executor.benchmark.Profile;
import group16.executor.benchmark.ProfileBuilder;
import group16.executor.benchmark.helpers.Dispatcher;
import group16.executor.benchmark.helpers.DynamicDispatcher;
import group16.executor.benchmark.helpers.StaticDispatcher;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

/**
 * This profile submits uniform/same sized tasks of the given size, at the given rate.
 */
public class UniformProfile extends Profile {
    /**
     *  @param tasks - Number of tasks
     * @param taskSize - Size of task. Generally recommend 10000 - 1000000. Will submit +-20% of this amount
     * @param over - How many milliseconds to submit the above number of tasks over. Defaults to 0 millis (i.e. static)
     */
    public UniformProfile(int tasks, int taskSize, int over) {
        this.taskSize = taskSize;
        this.tasks = tasks;
        this.over = over;
    }
    public UniformProfile(int tasks, int taskSize) {
        this(tasks, taskSize, 0);
    }

    @Override
    protected Dispatcher generate(ProfileBuilder builder) {
        RealDistribution randomTaskSize = new NormalDistribution(
            builder.getRandom(),
            (double)taskSize, // Mean
            ((double)taskSize) / 30.0); // 99.7% of results lie within +-20% of the task size

        if(over == 0.0) { // Static
            StaticDispatcher dispatch = new StaticDispatcher(tasks);

            for(int i = 0; i < tasks; ++i) {
                int accuracy = (int)Math.round(randomTaskSize.sample());
                dispatch.submit(builder.calculator(accuracy));
            }

            return dispatch;
        }
        else {
            DynamicDispatcher dispatch = new DynamicDispatcher(tasks);
            double[] waitTimes = builder.splitTimeEvenly(over, tasks);

            for (int i = 0; i < tasks; i++) {
                int accuracy = (int)Math.round(randomTaskSize.sample());
                dispatch.submit(builder.calculator(accuracy), waitTimes[i]);
            }

            return dispatch;
        }
    }

    private int taskSize;
    private int tasks;
    private int over;
}
