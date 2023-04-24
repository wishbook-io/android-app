package com.wishbook.catalog.Utils.networking.loader;

import android.net.Uri;

import com.wishbook.catalog.Utils.networking.async.http.AsyncHttpRequest;
import com.wishbook.catalog.Utils.networking.async.http.Headers;

/**
 * Created by vignesh on 7/15/13.
 */
public interface AsyncHttpRequestFactory {
    AsyncHttpRequest createAsyncHttpRequest(Uri uri, String method, Headers headers);
}
