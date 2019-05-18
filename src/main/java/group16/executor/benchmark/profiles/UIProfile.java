package group16.executor.benchmark.profiles;

import group16.executor.benchmark.Profile;
import group16.executor.benchmark.ProfileBuilder;
import group16.executor.benchmark.helpers.Dispatcher;
import group16.executor.benchmark.helpers.StaticDispatcher;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

import java.util.concurrent.ExecutorService;

/**
 * This profile submits halting tasks (involving a lot of waiting), and cpu intensive tasks
 */
public class UIProfile extends Profile {
    /**
     *
     * @param tasks Total number of tasks
     * @param ratio No. UI threads / No. computation threads
     * @param cpuTaskSize Accuracy of pi in our pi calculating threads
     * @param uiTaskTime Blocking time of a ui thread
     * @return
     */
    public UIProfile(int tasks, double ratio, int cpuTaskSize, double uiTaskTime) {
        this.tasks = tasks;
        this.ratio = ratio;
        this.cpuTaskSize = cpuTaskSize;
        this.uiTaskTime = uiTaskTime;
    }

    @Override
    protected Dispatcher generate(ProfileBuilder builder) {
        RealDistribution randomAccuracy = new NormalDistribution(
            builder.getRandom(),
            (double)cpuTaskSize,
            (double)cpuTaskSize / 30.0
        );

        RealDistribution randomTime = new NormalDistribution(
            builder.getRandom(),
            uiTaskTime,
            uiTaskTime / 30.0
        );

        StaticDispatcher dispatch = new StaticDispatcher(tasks);

        for(int i = 0; i < tasks; ++i) {
            if(builder.chance(ratio)) { // UI task
                double time = randomTime.sample();
                dispatch.submit(builder.waiter(time));
            } else { // CPU Task
                int accuracyValue = (int)Math.round(randomAccuracy.sample());
                dispatch.submit(builder.calculator(accuracyValue));
            }
        }

        return dispatch;
    }

    private int tasks;
    private double ratio;
    private int cpuTaskSize;
    private double uiTaskTime;
}
