package com.wishbook.catalog.Utils.networking.async;

import com.wishbook.catalog.Utils.networking.async.callback.CompletedCallback;
import com.wishbook.catalog.Utils.networking.async.callback.WritableCallback;

public interface DataSink {
    void write(ByteBufferList bb);
    void setWriteableCallback(WritableCallback handler);
    WritableCallback getWriteableCallback();
    
    boolean isOpen();
    void end();
    void setClosedCallback(CompletedCallback handler);
    CompletedCallback getClosedCallback();
    AsyncServer getServer();
}
