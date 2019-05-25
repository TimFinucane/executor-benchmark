package group16.executor.service.thread.prediction;

public class WatermarkPredictor implements FuturePredictor {
    public WatermarkPredictor(int min, int max) {
        if(max < min)
            throw new IllegalArgumentException("watermark minimum must be less than or equal to maximum");
        if(min < 0)
            throw new IllegalArgumentException("watermark minimum must be equal to or above 0");

        this.min = min;
        this.max = max;
    }

    @Override
    public int predict(int value) {
        return Math.min(max, Math.max(min, value));
    }

    private final int min;
    private final int max;
}
