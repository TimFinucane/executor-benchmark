package group16.executor.benchmark.profiles;

import group16.executor.benchmark.Profile;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.random.RandomGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public abstract class DynamicProfile extends Profile {

    private static final double DYNAMIC_DISPTACH_TIME_STANDARD_DEV = 5.0;

    private List<Callable> tasks = new ArrayList<>();

    protected void addToDynamicDispatch(Callable task) {
        tasks.add(task);
    }

    protected void dynamicallyDispatch(ExecutorService service, int over, RandomGenerator rand) {

        double[] waitTimes = divideIntoRandomlySizedNumbers(over, tasks.size(), rand);

        for (int i = 0; i < tasks.size(); i++) {
            service.submit(tasks.get(i));
            try {
                Thread.sleep(((Double)waitTimes[i]).longValue());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
