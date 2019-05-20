package group16.executor.benchmark.profiles;

import group16.executor.benchmark.Profile;
import group16.executor.benchmark.ProfileBuilder;
import group16.executor.benchmark.customDistributions.ClusterDistribution;
import group16.executor.benchmark.helpers.Dispatcher;
import group16.executor.benchmark.helpers.DynamicDispatcher;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.ml.clustering.Cluster;

public class DynamicLoadProfile extends Profile {

    private final int tasks;
    private final int taskSize;
    private final int loadPeaks;
    private final int over;

    /**
     * @param tasks
     * @param taskSize
     * @param loadPeaks
     * @param over
     */
    public DynamicLoadProfile(int tasks, int taskSize, int loadPeaks, int over) {
        if (over == 0) { throw new IllegalArgumentException("A dynamic load profile cannot be statically dispatched"); }
        this.tasks = tasks;
        this.taskSize = taskSize;
        this.loadPeaks = loadPeaks;
        this.over = over;
    }

    @Override
    protected Dispatcher generate(ProfileBuilder builder) {
        RealDistribution randomTaskSize = new NormalDistribution(
                builder.getRandom(),
                (double)taskSize, // Mean
                ((double)taskSize) / 30.0); // 99.7% of results lie within +-20% of the task size

        DynamicDispatcher dispatch = new DynamicDispatcher(tasks);
        double[] waitTimes = new ClusterDistribution(builder.getRandom(), this.loadPeaks, this.over).sample(this.tasks);

        for (int i = 0; i < tasks; i++) {
            int accuracy = (int)Math.round(randomTaskSize.sample());
            dispatch.submit(builder.calculator(accuracy), waitTimes[i]);
        }
        return dispatch;
    }
}
