package profiles;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * A profile describes a set of tasks that are meant to be executed by an ExecutorService.
 */
public class Profile {
    public Profile(List<Callable<?>> tasks) {
        this.tasks = tasks;
    }

    public List<Callable<?>> tasks;
}
