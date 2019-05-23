package group16.executor.service.threads;

import group16.executor.service.threadPrediction.WatermarkPredictor;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import org.junit.Assert;
import org.junit.Test;

public class WatermarkPredictorTests {
    @Test
    public void watermark_underMin_expectMin() {
        Assert.assertEquals(
            4,
            new WatermarkPredictor(4, 16).predict(3)
        );
    }

    @Test
    public void watermark_overMax_expectMax() {
        Assert.assertEquals(
            16,
            new WatermarkPredictor(4, 28).predict(18)
        );
    }

    @Test
    public void watermark_betweenMark_expectValue() {
        Assert.assertEquals(
            3,
            new WatermarkPredictor(1, 6).predict(3)
        );
    }

    @Test
    public void constructor_swappedValues_throw() {
        try {
            new WatermarkPredictor(4, 3);
            Assert.fail();
        } catch(IllegalArgumentException exception) {
            Assert.assertEquals(exception.getMessage(), "watermark minimum must be less than or equal to maximum");
        }
    }

    @Test
    public void watermark_equalValues_ok() {
        Assert.assertEquals(
            5,
            new WatermarkPredictor(5, 5).predict(2)
        );
    }

    @Test
    public void constructor_minimumBelowZero_throw() {
        try {
            new WatermarkPredictor(-1, 1);
            Assert.fail();
        } catch(IllegalArgumentException exception) {
            Assert.assertEquals(exception.getMessage(), "watermark minimum must be equal to or above 0");
        }
    }
}
