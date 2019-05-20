package group16.executor.benchmark.profiles;

import group16.executor.benchmark.Profile;
import group16.executor.benchmark.ProfileBuilder;
import group16.executor.benchmark.customDistributions.BimodalDistribution;
import group16.executor.benchmark.helpers.Dispatcher;
import group16.executor.benchmark.helpers.DynamicDispatcher;
import group16.executor.benchmark.helpers.StaticDispatcher;

/**
 * This profile submits a number of halting (computational) tasks of varying size according to some variance, and over
 * some time period according to some timing variance. The distribution of task size will be a bimodal distribution,
 * simulating a set of tasks with variance in order of magnitude.
 */
public class IrregularProfile extends Profile {

    private final int tasks;
    private final int task1Avg;
    private final int task1Sd;
    private final int task2Avg;
    private final int task2Sd;
    private final double ratioOfTasks;
    private final double over;

    /**
     * @param tasks Total number of tasks to be submitted
     * @param task1Avg Average value for task size in first distribution
     * @param task1Sd Standard deviation for task size in first distribution
     * @param task2Avg Average value for task size in second distribution (Defaults to 10x avg. of first distribution)
     * @param task2Sd Standard deviation for task size in second distribution (Defaults to 10x sd of first distribution)
     * @param ratioOfTasks Ratio of tasks sizes from the first distribution that will be dispatched (defaults to 0.5)
     * @param over How many seconds to submit the above number of tasks over. Defaults to 0 seconds (i.e. static)
     */
    public IrregularProfile(int tasks, int task1Avg, int task1Sd, int task2Avg,
                            int task2Sd, double ratioOfTasks, double over) {
        this.tasks = tasks;
        this.task1Avg = task1Avg;
        this.task1Sd = task1Sd;
        this.task2Avg = task2Avg;
        this.task2Sd = task2Sd;
        this.ratioOfTasks = ratioOfTasks;
        this.over = over;
    }

    public IrregularProfile(int tasks, int task1Avg, int task1Sd) {
        this(tasks, task1Avg, task1Sd, 10* task1Avg, 10* task1Sd, 0.5, 0);
    }

    public IrregularProfile(int tasks, int task1Avg, int task1Sd, double over) {
        this(tasks, task1Avg, task1Sd, 10* task1Avg, 10* task1Sd, 0.5, over);
    }

    public IrregularProfile(int tasks, int task1Avg, int task1Sd, int task2Avg,
                            int task2Sd, double ratioOfTasks) {
        this(tasks, task1Avg, task1Sd, task2Avg, task2Sd, ratioOfTasks, 0);
    }

    @Override
    protected Dispatcher generate(ProfileBuilder builder) {
        BimodalDistribution randomTaskSize = new BimodalDistribution(
                builder.getRandom(),
                task1Avg,
                task1Sd,
                task2Avg,
                task2Sd,
                ratioOfTasks
        );

        if (over == 0) { // Static
            StaticDispatcher dispatch = new StaticDispatcher(tasks);

            for (int i = 0; i < tasks; i++) {
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
}
