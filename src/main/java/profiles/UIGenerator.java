package profiles;

import org.apache.commons.math3.distribution.AbstractIntegerDistribution;
import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.random.UniformRandomGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * This profile generator will produce profiles with UI and Computation (pi-based) classes.
 */
public class UIGenerator {
    /**
     *
     * @param tasks
     * @param ratio No. UI threads / No. computation threads
     * @param piAccuracy Accuracy of pi in our pi calculating threads
     * @param blockingTime Blocking time of a ui thread
     * @return
     */
    public static Profile generate(int tasks, double ratio,
                                   AbstractIntegerDistribution piAccuracy,
                                   AbstractRealDistribution blockingTime) {
        ArrayList<Callable<?>> list = new ArrayList<>(tasks);


        for(int i = 0; i < tasks; ++i) {
            if(Math.random() < ratio) {
                long sleepTime = (long)(blockingTime.sample() * 1000);
                list.add(() -> {
                    TimeUnit.MILLISECONDS.sleep(sleepTime);
                    return 0;
                });
            } else {
                int accuracyValue = piAccuracy.sample();
                list.add(() -> PiGenerator.calculatePi(accuracyValue));
            }
        }
        return new Profile(list);
    }
}
