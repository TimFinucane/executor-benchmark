package group16.executor.executorService.threadPrediction;

import java.util.ArrayList;
import java.util.List;

public class ExponentialMovingAveragePredictor implements FuturePredictor {

    private final double alpha;
    private final int historyLength;

    private List<Integer> history;

    /**
     * @param alpha Weighting factor for exponential weighting reduction
     * @param historyLength Length of history the moving average will remember. Will default to 10,000. //TODO: is this good?
     */
    public ExponentialMovingAveragePredictor(double alpha, int historyLength) {
        this.alpha = alpha;
        this.historyLength = historyLength;
        this.history = new ArrayList<>();
    }

    public ExponentialMovingAveragePredictor(double alpha) {
        this(alpha, 10000);
    }

    /**
     * Predicts the future number needed given previous calls to this method and the Exponential Moving Average
     * technique. Previous data is weighted exponentially less in a summation to the predicted value given some
     * weighting/exponential factor alpha.
     * @param number A number to predict from
     * @return Predicted number based on exponential moving average
     */
    @Override
    public int predictNumberInFuture(int number) {
        return 0;
    }
}
