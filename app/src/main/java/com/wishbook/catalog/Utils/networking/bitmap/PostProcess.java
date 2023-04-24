package com.wishbook.catalog.Utils.networking.bitmap;

/**
 * Created by vignesh on 7/1/14.
 */
public interface PostProcess {
    void postProcess(BitmapInfo info) throws Exception;
    String key();
}
