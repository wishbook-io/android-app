package com.wishbook.catalog.Utils.networking.async.future;

/**
 * Created by vignesh on 12/22/13.
 */
public interface FutureRunnable<T> {
    T run() throws Exception;
}
