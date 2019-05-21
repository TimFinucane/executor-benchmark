package group16.executor.executorService.threadPrediction;

public class ExponentialMovingAveragePredictor implements FuturePredictor {

    private final double alpha;

    private Double lastAverage;
    private int lastValue;

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
     * @param newValue A value to predict from
     * @return Predicted value based on exponential moving average
     */
    @Override
    public int predictValueInFuture(int newValue) {
        if (lastAverage == null) {
            lastAverage = (double) newValue;
        }

        double newAverage = lastAverage + alpha * (newValue - lastAverage);
        int toReturn = newAverage > lastAverage
                ? (int) Math.round(lastValue + (lastValue - newAverage))
                : (int) Math.round(newAverage);
        lastAverage = newAverage;
        lastValue = newValue;

        return toReturn;
    }
}
