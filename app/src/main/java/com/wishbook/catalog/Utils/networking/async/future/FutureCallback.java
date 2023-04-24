package com.wishbook.catalog.Utils.networking.async.future;

/**
 * Created by vignesh on 5/20/13.
 */
public interface FutureCallback<T> {
    /**
     * onCompleted is called by the Future with the result or exception of the asynchronous operation.
     * @param e Exception encountered by the operation
     * @param result Result returned from the operation
     */
    void onCompleted(Exception e, T result);
}
