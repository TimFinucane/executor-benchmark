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
        Assert.assertEquals(4, predictor.predictValueInFuture(3));
        Assert.assertEquals(5, predictor.predictValueInFuture(4));
        Assert.assertEquals(28, predictor.predictValueInFuture(20));
        Assert.assertEquals(36, predictor.predictValueInFuture(28));

    }

    @Test
    public void weightingOf0_1() {
        FuturePredictor predictor = new ExponentialMovingAveragePredictor(0.1);

        Assert.assertEquals(1, predictor.predictValueInFuture(1));
        Assert.assertEquals(3, predictor.predictValueInFuture(2));
        Assert.assertEquals(5, predictor.predictValueInFuture(3));
        Assert.assertEquals(6, predictor.predictValueInFuture(4));
        Assert.assertEquals(37, predictor.predictValueInFuture(20));
        Assert.assertEquals(67, predictor.predictValueInFuture(37));
    }

    @Test
    public void weightingOf0_9() {
        FuturePredictor predictor = new ExponentialMovingAveragePredictor(0.9);

        Assert.assertEquals(1, predictor.predictValueInFuture(1));
        Assert.assertEquals(2, predictor.predictValueInFuture(2));
        Assert.assertEquals(3, predictor.predictValueInFuture(3));
        Assert.assertEquals(4, predictor.predictValueInFuture(4));
        Assert.assertEquals(22, predictor.predictValueInFuture(20));
        Assert.assertEquals(22, predictor.predictValueInFuture(22));
    }

    @Test
    public void weightingOf0_0() {

    }

    @Test
    public void weightingOf1_0() {

    }

    @Test
    public void weightingOf0_5Decreasing() {

    }
}
