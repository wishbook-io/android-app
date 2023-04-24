package com.wishbook.catalog.Utils.networking.async.future;


public interface Future<T> extends Cancellable, java.util.concurrent.Future<T> {
    /**
     * Set a callback to be invoked when this Future completes.
     * @param callback
     * @return
     */
    Future<T> setCallback(FutureCallback<T> callback);

    /**
     * Set a callback to be invoked when this Future completes.
     * @param callback
     * @param <C>
     * @return The callback
     */
    <C extends FutureCallback<T>> C then(C callback);

    /**
     * Get the result, if any. Returns null if still in progress.
     * @return
     */
    T tryGet();

    /**
     * Get the exception, if any. Returns null if still in progress.
     * @return
     */
    Exception tryGetException();
}
