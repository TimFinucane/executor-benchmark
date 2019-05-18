package group16.executor.benchmark.profiles;

import group16.executor.benchmark.Profile;
import group16.executor.benchmark.ProfileBuilder;
import org.apache.commons.math3.distribution.UniformRealDistribution;

import java.util.concurrent.ExecutorService;

/**
 * This profile submits a number of halting (computational) tasks of varying size according to some variance, and over
 * some time period according to some timing variance. The distribution of task size will be a uniform distribution.
 */
public class IrregularProfile extends Profile {

    private final int tasks;
    private final int taskSizeMin;
    private final int taskSizeMax;
    private final int over;

    /**
     * @param tasks Total number of tasks to be submitted
     * @param taskSizeMax maximum size of tasks generated. Generally recommend 10000 - 1000000.
     * @param taskSizeMin minimum size of tasks generated. Generally recommend 10000 - 1000000.
     * @param over How many seconds to submit the above number of tasks over. Defaults to 0 seconds (i.e. static)
     */
    public IrregularProfile(int tasks, int taskSizeMin, int taskSizeMax, int over) {

        this.tasks = tasks;
        this.taskSizeMin = taskSizeMin;
        this.taskSizeMax = taskSizeMax;
        this.over = over;
    }

    @Override
    protected void run(ExecutorService service, ProfileBuilder builder) {
        UniformRealDistribution randomTaskSize = new UniformRealDistribution(
                builder.getRandom(),
                taskSizeMin,
                taskSizeMax);

        if (over == 0) { // Static
            
        }
    }
}
