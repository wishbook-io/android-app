package com.wishbook.catalog.Utils.networking.async.callback;

import com.wishbook.catalog.Utils.networking.async.ByteBufferList;
import com.wishbook.catalog.Utils.networking.async.DataEmitter;


public interface DataCallback {
    class NullDataCallback implements DataCallback {
        @Override
        public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
            bb.recycle();
        }
    }

    void onDataAvailable(DataEmitter emitter, ByteBufferList bb);
}
