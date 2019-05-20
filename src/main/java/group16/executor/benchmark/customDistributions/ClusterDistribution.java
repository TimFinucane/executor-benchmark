package group16.executor.benchmark.customDistributions;

import group16.executor.benchmark.helpers.RealSampler;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.random.RandomGenerator;

/**
 * Generates a distribution across a set of clusters, is effectively a multi-modal distribution given n clusters
 * or peaks.
 */
public class ClusterDistribution implements RealSampler {

    /**
     * @param rng Random number generator
     * @param clusters Number of clusters evenly spaced
     * @param min Minimum value generated
     * @param max Maximum value generated
     */
    public ClusterDistribution(RandomGenerator rng, int clusters, double min, double max) {
        this.rng = rng;
        this.min = min;
        this.max = max;

        double gap = (max - min) / (clusters + 1);
        double sd = gap / 6; // 99.9% fall within +-50% of the gap.

        distributions = new NormalDistribution[clusters];
        for(int i = 0; i < clusters; ++i) {
            distributions[i] = new NormalDistribution(rng, min + gap * (i + 1), sd);
        }
    }
    public ClusterDistribution(RandomGenerator rng, int clusters, double max) {
        this(rng, clusters, 0.0, max);
    }

    public double sample() {
        int index = rng.nextInt(distributions.length);
        return Math.max(min, Math.min(max, distributions[index].sample()));
    }

    private RandomGenerator rng;
    private double min;
    private double max;
    private NormalDistribution[] distributions;
}
