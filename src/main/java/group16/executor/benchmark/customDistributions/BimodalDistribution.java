package group16.executor.benchmark.customDistributions;

import group16.executor.benchmark.helpers.RealSampler;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.random.RandomGenerator;

/**
 * Extension of NormalDistribution with only sample() overwritten and extended to sample from a bimodal distribution.
 * Approximates a bimodal distribution by using 2 normal distributions and randomly picking from each
 */
public class BimodalDistribution implements RealSampler {

    private RandomGenerator rg;
    private double ratio;
    private RealDistribution distribution1;
    private RealDistribution distribution2;

    /**
     * @param rg Source for random number generation
     * @param mean1 Mean value of first distribution
     * @param sd1 Standard deviation of first distribution
     * @param mean2 Mean value of second distribution
     * @param sd2 Standard deviation of second distribution
     * @param ratio Likelihood sample will be from first distribution
     */
    public BimodalDistribution(RandomGenerator rg, double mean1, double sd1, double mean2, double sd2, double ratio) {
        if (ratio > 1 || ratio < 0) throw new IllegalArgumentException("Ratio must be between 0 and 1");
        this.rg = rg;
        this.ratio = ratio;
        this.distribution1 = new NormalDistribution(rg, mean1, sd1);
        this.distribution2 = new NormalDistribution(rg, mean2, sd2);
    }

    @Override
    public double sample() {
        if (ratio > rg.nextDouble()) {
            return distribution1.sample();
        } else {
            return distribution2.sample();
        }
    }
}
