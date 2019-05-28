package group16.executor.service.thread.management;

import org.junit.Assert;
import org.junit.Test;

public class WatermarkPredictorTests {
    @Test
    public void watermark_underMin_expectMin() {
        Assert.assertEquals(
            4,
            new WatermarkPredictor(4, 16).threadDeficit(0)
        );
    }

    @Test
    public void watermark_overMax_expectMax() {
        Assert.assertTrue(new WatermarkPredictor(4, 8).shouldKillThread(18));
    }

    @Test
    public void watermark_betweenMark_expectValue() {
        Assert.assertEquals(0, new WatermarkPredictor(1, 6).threadDeficit(3));
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
        Assert.assertEquals(0, new WatermarkPredictor(5, 5).threadDeficit(5));
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
