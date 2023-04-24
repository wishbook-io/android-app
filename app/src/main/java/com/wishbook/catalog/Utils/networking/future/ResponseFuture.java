package com.wishbook.catalog.Utils.networking.future;

import com.wishbook.catalog.Utils.networking.async.future.Future;
import com.wishbook.catalog.Utils.networking.Response;

/**
 * Created by vignesh on 7/2/13.
 */
public interface ResponseFuture<T> extends Future<T> {
    Future<Response<T>> withResponse();
}
