package group16.executor.benchmark.helpers;

import org.apache.commons.math3.distribution.RealDistribution;

public interface RealSampler {
    double sample();

    static RealSampler fromDistribution(RealDistribution distribution) {
        return distribution::sample;
    }

    default double[] sample(int numSamples) {
        double[] samples = new double[numSamples];
        for(int i = 0; i < numSamples; ++i)
            samples[i] = sample();
        return samples;
    }
}
