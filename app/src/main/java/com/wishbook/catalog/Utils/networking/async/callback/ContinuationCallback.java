package com.wishbook.catalog.Utils.networking.async.callback;

public interface ContinuationCallback {
    void onContinue(CompletedCallback next) throws Exception;
}
