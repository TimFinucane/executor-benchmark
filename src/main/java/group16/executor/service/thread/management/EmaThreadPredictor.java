package group16.executor.service.thread.management;

import java.util.AbstractMap;
import java.util.List;

/**
 * Predicts next number of tasks based on the past number of tasks and the current rate of change.
 */
public class EmaThreadPredictor implements ThreadManager {
    /**
     * @param alpha Weighting factor for exponential weighting reduction
     */
    public EmaThreadPredictor(double alpha) {
        this.alpha = alpha;
    }

    /**
     * Predicts the future value needed given previous calls to this method and the Exponential Moving Average
     * technique. Previous data is weighted exponentially less in a summation to the predicted value given some
     * weighting/exponential factor alpha. The value returned is also modified using the technique described in:
     * DongHyun Kang, Saeyoung Han, SeoHee Yoo, and Sungyong Park, “Prediction-Based Dynamic Thread Pool Scheme
     * for Efficient Resource Usage,” 2008, pp. 159–164. which aims to reduce underestimation.
     * @param newValue A value to predict from
     * @return Predicted value based on exponential moving average
     */
    /* TODO:
    @Override
    public int predict(int newValue) {
        if (lastAverage == null) {
            lastAverage = (double) newValue;
            return newValue;
        }

        double nextAverage = lastAverage + alpha * (newValue - lastAverage);
        double toReturn = nextAverage > lastAverage
                ? newValue + (newValue - nextAverage)
                : nextAverage;
        lastAverage = nextAverage;

        return (int) Math.round(toReturn);
    }*/

    @Override
    public void taskAdded() {
        long time = System.currentTimeMillis();
    }

    @Override
    public void taskCompleted() {

    }

    @Override
    public int threadDeficit(int activeThreads) {
        return 0;
    }

    @Override
    public boolean shouldKillIdleThread(int activeThreads, long idleTime) {
        return false;
    }

    @Override
    public boolean shouldKillThread(int activeThreads) {
        return false;
    }

    private final double alpha;
    private double lastPrediction = 0.0;
    private double predictedTasks = 0.0;
    private double rateOfChange = 0.0;

    private List<AbstractMap.SimpleImmutableEntry<Long, Integer>> threadCounts;
}
