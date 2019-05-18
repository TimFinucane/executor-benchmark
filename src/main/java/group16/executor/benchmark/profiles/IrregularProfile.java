package group16.executor.benchmark.profiles;

import group16.executor.benchmark.DynamicDispatcher;
import group16.executor.benchmark.Profile;
import group16.executor.benchmark.ProfileBuilder;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;

/**
 * This profile submits a number of halting (computational) tasks of varying size according to some variance, and over
 * some time period according to some timing variance. The distribution of task size will be a uniform distribution.
 */
public class IrregularProfile extends Profile {

    private DynamicDispatcher dynamicDispatcher;
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
    public IrregularProfile(DynamicDispatcher dynamicDispatcher, int tasks, int taskSizeMin, int taskSizeMax, int over) {
        this.dynamicDispatcher = dynamicDispatcher;
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
            for (int i = 0; i < tasks; i++) {
                int accuracy = (int)Math.round(randomTaskSize.sample());
                service.submit(builder.calculatorTask(accuracy));
            }
        } else {
            double averageTimeSlice = (double)over / (double)tasks;
            double[] dispatchTimes = divideIntoRandomlySizedNumbers(averageTimeSlice, tasks, builder.getRandom());
        }
    }

    private double[] divideIntoRandomlySizedNumbers(double numberToDivide, int divideInto, RandomGenerator rand) {
        double[] nums = new double[divideInto];

        for (int i = 0; i < nums.length-1; i++) {
            nums[i] = rand.nextDouble() * numberToDivide;
            numberToDivide -= nums[i];
        }
        nums[nums.length-1] = numberToDivide;
        Arrays.sort(nums);

        return nums;
    }
}
