package group16.executor.executorService.threadPrediction;

import java.util.Iterator;

public class ExponentialMovingAveragePredictor implements FuturePredictor {

    private final double alpha;

    private Double previousAverage;

    /**
     * @param alpha Weighting factor for exponential weighting reduction
     */
    public ExponentialMovingAveragePredictor(double alpha) {
        this.alpha = alpha;
    }

    /**
     * Predicts the future value needed given previous calls to this method and the Exponential Moving Average
     * technique. Previous data is weighted exponentially less in a summation to the predicted value given some
     * weighting/exponential factor alpha. The value returned is also modified using the technique described in:
     * DongHyun Kang, Saeyoung Han, SeoHee Yoo, and Sungyong Park, “Prediction-Based Dynamic Thread Pool Scheme
     * for Efficient Resource Usage,” 2008, pp. 159–164. which aims to reduce underestimation.
     * @param value A value to predict from
     * @return Predicted value based on exponential moving average
     */
    @Override
    public int predictValueInFuture(int value) {
        if (previousAverage == null) {
            previousAverage = (double) value;
        }

        double newAverage = previousAverage + alpha * (value - previousAverage);
        previousAverage = newAverage;

        return Math.toIntExact(Math.round(newAverage));
    }
}
