package unitTests.executorService.threadPrediction;

import group16.executor.executorService.threadPrediction.ExponentialMovingAveragePredictor;
import group16.executor.executorService.threadPrediction.FuturePredictor;
import org.junit.Assert;
import org.junit.Test;

public class ExponentialMovingAveragePredictorTests {

    @Test
    public void weightingOf0_5() {
        FuturePredictor predictor = new ExponentialMovingAveragePredictor(0.5);

        Assert.assertEquals(1, predictor.predictValueInFuture(1));
        Assert.assertEquals(1, predictor.predictValueInFuture(1));
        Assert.assertEquals(3, predictor.predictValueInFuture(2));
        Assert.assertEquals(3, predictor.predictValueInFuture(3));
        Assert.assertEquals(4, predictor.predictValueInFuture(4));
        Assert.assertEquals(28, predictor.predictValueInFuture(20));

    }
}
