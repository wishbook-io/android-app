package com.wishbook.catalog.Utils.networking.async;

import com.wishbook.catalog.Utils.networking.async.callback.CompletedCallback;
import com.wishbook.catalog.Utils.networking.async.callback.DataCallback;

public interface DataEmitter {
    void setDataCallback(DataCallback callback);
    DataCallback getDataCallback();
    boolean isChunked();
    void pause();
    void resume();
    void close();
    boolean isPaused();
    void setEndCallback(CompletedCallback callback);
    CompletedCallback getEndCallback();
    AsyncServer getServer();
    String charset();
}
