package group16.executor.executorService.threadPrediction;

public interface FuturePredictor {

    /***
     * Given a reading now, will predict a value in the future given some implementation and previous calls to this
     * method.
     * @param value A number to predict from
     * @return A predicted value given this and previous calls to this method
     */
    int predictValueInFuture(int value);
}
