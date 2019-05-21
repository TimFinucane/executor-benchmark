package unitTests.executorService.threadPrediction;

import group16.executor.executorService.threadPrediction.ExponentialMovingAveragePredictor;
import group16.executor.executorService.threadPrediction.FuturePredictor;
import org.junit.Assert;
import org.junit.Test;

/**
 * This test class verifies a set of examples worked by hand. It assumes the unmodified EMA value is stored as state
 * and NOT the modified value. I am fairly sure this was the approach the researchers intended.
 */
public class ExponentialMovingAveragePredictorTests {

    @Test
    public void weightingOf0_5() {
        FuturePredictor predictor = new ExponentialMovingAveragePredictor(0.5);

        Assert.assertEquals(1, predictor.predict(1));
        Assert.assertEquals(1, predictor.predict(1));
        Assert.assertEquals(3, predictor.predict(2));
        Assert.assertEquals(4, predictor.predict(3));
        Assert.assertEquals(5, predictor.predict(4));
        Assert.assertEquals(28, predictor.predict(20));
        Assert.assertEquals(36, predictor.predict(28));
        Assert.assertEquals(11, predictor.predict(3));

    }

    @Test
    public void weightingOf0_1() {
        FuturePredictor predictor = new ExponentialMovingAveragePredictor(0.1);

        Assert.assertEquals(1, predictor.predict(1));
        Assert.assertEquals(3, predictor.predict(2));
        Assert.assertEquals(5, predictor.predict(3));
        Assert.assertEquals(6, predictor.predict(4));
        Assert.assertEquals(37, predictor.predict(20));
        Assert.assertEquals(67, predictor.predict(37));
        Assert.assertEquals(6, predictor.predict(3));
    }

    @Test
    public void weightingOf0_9() {
        FuturePredictor predictor = new ExponentialMovingAveragePredictor(0.9);

        Assert.assertEquals(1, predictor.predict(1));
        Assert.assertEquals(2, predictor.predict(2));
        Assert.assertEquals(3, predictor.predict(3));
        Assert.assertEquals(4, predictor.predict(4));
        Assert.assertEquals(22, predictor.predict(20));
        Assert.assertEquals(22, predictor.predict(22));
        Assert.assertEquals(5, predictor.predict(3));
    }

    @Test
    public void weightingOf0_0() {
        FuturePredictor predictor = new ExponentialMovingAveragePredictor(0);

        Assert.assertEquals(1, predictor.predict(1));
        Assert.assertEquals(1, predictor.predict(2));
        Assert.assertEquals(1, predictor.predict(3));
        Assert.assertEquals(1, predictor.predict(4));
        Assert.assertEquals(1, predictor.predict(20));
        Assert.assertEquals(1, predictor.predict(5384951));
        Assert.assertEquals(1, predictor.predict(3));
    }

    @Test
    public void weightingOf1_0() {
        FuturePredictor predictor = new ExponentialMovingAveragePredictor(1);

        Assert.assertEquals(1, predictor.predict(1));
        Assert.assertEquals(2, predictor.predict(2));
        Assert.assertEquals(3, predictor.predict(3));
        Assert.assertEquals(4, predictor.predict(4));
        Assert.assertEquals(20, predictor.predict(20));
        Assert.assertEquals(5384951, predictor.predict(5384951));
        Assert.assertEquals(3, predictor.predict(3));
    }
}
