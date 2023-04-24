package com.wishbook.catalog.Utils.networking;

/**
 * Created by vignesh on 1/18/14.
 */
public class DeferredLoadBitmap extends BitmapCallback {
    public static int DEFER_COUNTER = 0;

    BitmapFetcher fetcher;
    int priority = ++DEFER_COUNTER;
    public DeferredLoadBitmap(NetworkProcessor ion, String key, BitmapFetcher fetcher)  {
        super(ion, key, false);
        this.fetcher = fetcher;
    }
}
