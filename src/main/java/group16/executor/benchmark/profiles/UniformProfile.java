package group16.executor.benchmark.profiles;

import java.util.concurrent.ExecutorService;

import group16.executor.benchmark.Profile;
import group16.executor.benchmark.ProfileBuilder;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

/**
 * This profile submits uniform/same sized tasks of the given size, at the given rate.
 */
public class UniformProfile extends Profile {
    /**
     *
     * @param taskSize - Size of task. Generally recommend 10000 - 1000000. Will submit +-20% of this amount
     * @param tasks - Number of tasks
     * @param over - How many milliseconds to submit the above number of tasks over. Defaults to 0 millis (i.e. static)
     */
    public UniformProfile(int taskSize, int tasks, double over) {
        this.taskSize = taskSize;
        this.tasks = tasks;
        this.over = over;
    }
    public UniformProfile(int taskSize, int size) {
        this(taskSize, size, 0.0);
    }


    @Override
    protected void run(ExecutorService service, ProfileBuilder builder) {
        RealDistribution randomTaskSize = new NormalDistribution(
            builder.getRandom(),
            (double)taskSize, // Mean
            ((double)taskSize) / 30.0); // 99.7% of results lie within +-20% of the task size

        if(over == 0.0) {
            for(int i = 0; i < tasks; ++i) {
                int accuracy = (int)Math.round(randomTaskSize.sample());
                service.submit(builder.calculatorTask(accuracy));
            }
        }
        else {
            // TODO:
            throw new UnsupportedOperationException("Not implemented");
        }
    }

    private int taskSize;
    private int tasks;
    private double over;
}
