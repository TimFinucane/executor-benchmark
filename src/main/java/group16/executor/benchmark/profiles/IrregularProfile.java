package group16.executor.benchmark.profiles;

import group16.executor.benchmark.DynamicDispatcher;
import group16.executor.benchmark.Profile;
import group16.executor.benchmark.ProfileBuilder;
import group16.executor.benchmark.customDistributions.BimodalDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.random.RandomGenerator;

import java.util.concurrent.ExecutorService;

/**
 * This profile submits a number of halting (computational) tasks of varying size according to some variance, and over
 * some time period according to some timing variance. The distribution of task size will be a bimodal distribution,
 * simulating a set of tasks with variance in order of magnitude.
 */
public class IrregularProfile extends Profile {

    private static final double DYNAMIC_DISPTACH_TIME_STANDARD_DEV = 5.0;

    private DynamicDispatcher dynamicDispatcher;
    private final int tasks;
    private final int task1Avg;
    private final int task1Sd;
    private final int task2Avg;
    private final int task2Sd;
    private final double ratioOfTasks;
    private final int over;

    /**
     *
     * @param dynamicDispatcher
     * @param tasks Total number of tasks to be submitted
     * @param task1Avg
     * @param task1Sd
     * @param task2Avg
     * @param task2Sd
     * @param ratioOfTasks
     * @param over How many milliseconds to submit the above number of tasks over. Defaults to 0 millis (i.e. static)
     */
    public IrregularProfile(DynamicDispatcher dynamicDispatcher, int tasks, int task1Avg, int task1Sd, int task2Avg,
                            int task2Sd, double ratioOfTasks, int over) {
        this.dynamicDispatcher = dynamicDispatcher;
        this.tasks = tasks;
        this.task1Avg = task1Avg;
        this.task1Sd = task1Sd;
        this.task2Avg = task2Avg;
        this.task2Sd = task2Sd;
        this.ratioOfTasks = ratioOfTasks;
        this.over = over;
    }

    @Override
    protected void run(ExecutorService service, ProfileBuilder builder) {
        BimodalDistribution randomTaskSize = new BimodalDistribution(
                builder.getRandom(),
                task1Avg,
                task1Sd,
                task2Avg,
                task2Sd,
                ratioOfTasks);

        if (over == 0) { // Static
            for (int i = 0; i < tasks; i++) {
                int accuracy = (int)Math.round(randomTaskSize.sample());
                service.submit(builder.calculatorTask(accuracy));
            }
        } else {
            double[] dispatchTimes = divideIntoRandomlySizedNumbers(over, tasks, builder.getRandom());
            for (int i = 0; i < tasks; i++) {
                int accuracy = (int)Math.round(randomTaskSize.sample());
                dynamicDispatcher.dynamicallyDispatch(
                        builder.calculatorTask(accuracy),
                        dispatchTimes[i]);
            }
            dynamicDispatcher.begin(service);
        }
    }

    private double[] divideIntoRandomlySizedNumbers(double numberToDivide, int divideInto, RandomGenerator rand) {
        double averageDivision = numberToDivide / (double) divideInto;
        double[] nums = new double[divideInto];
        RealDistribution realRand = new NormalDistribution(
                rand,
                averageDivision,
                averageDivision / DYNAMIC_DISPTACH_TIME_STANDARD_DEV);

        for (int i = 0; i < nums.length-1; i++) {
            nums[i] = Math.max(0,realRand.sample());
            numberToDivide -= nums[i];
        }
        nums[nums.length-1] = Math.max(0, numberToDivide);

        return nums;
    }
}
