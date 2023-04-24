package com.wishbook.catalog.Utils.networking.async.http;

import android.net.Uri;

/**
 * Created by vignesh on 8/25/13.
 */
public class AsyncHttpHead extends AsyncHttpRequest {
    public AsyncHttpHead(Uri uri) {
        super(uri, METHOD);
    }

    public static final String METHOD = "HEAD";
}
