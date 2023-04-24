package com.wishbook.catalog.Utils.networking.async.callback;

public interface CompletedCallback {
    class NullCompletedCallback implements CompletedCallback {
        @Override
        public void onCompleted(Exception ex) {

        }
    }

    void onCompleted(Exception ex);
}
