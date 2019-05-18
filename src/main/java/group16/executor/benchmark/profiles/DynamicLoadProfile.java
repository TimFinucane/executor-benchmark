package group16.executor.benchmark.profiles;

import group16.executor.benchmark.Profile;
import group16.executor.benchmark.ProfileBuilder;
import group16.executor.benchmark.helpers.Dispatcher;

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
        return null;
    }
}
