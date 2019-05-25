package group16.executor.service.thread.prediction;

public interface FuturePredictor {

    /***
     * Given a reading now, will predict a value in the future given some implementation and previous calls to this
     * method.
     * @param value A number to predict from
     * @return A predicted value given this and previous calls to this method
     */
    int predict(int value);
}
