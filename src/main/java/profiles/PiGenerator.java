package profiles;

import org.apache.commons.math3.distribution.AbstractIntegerDistribution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Callable;

/**
 * This generates profiles using the pi calculation function
 */
public class PiGenerator {

    public static Profile generate(int tasks, int accuracy) {
        return new Profile(Collections.nCopies(tasks, () -> calculatePi(accuracy)));
    }
    public static Profile generate(int tasks, AbstractIntegerDistribution accuracy) {
        ArrayList<Callable<?>> list = new ArrayList<>(tasks);

        for(int i = 0; i < tasks; ++i) {
            // Calculate the value OUTSIDE of the task being run.
            int accuracyValue = accuracy.sample();
            list.add(() -> calculatePi(accuracyValue));
        }

        return new Profile(list);
    }

    static double calculatePi(int accuracy) {
        double val = 0;

        for(int i = 0; i < accuracy; ++i) {
            // If it's even, add. If it's odd, subtract. Sorry. Was looking for a source and got stuck reading the Pi bill.
            val += ((i % 2 == 0) ? 4 : -4) / (double)(2 * i + 1);
        }

        return val;
    }
}
