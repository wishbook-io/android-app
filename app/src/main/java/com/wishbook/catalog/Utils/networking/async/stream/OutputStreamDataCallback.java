package com.wishbook.catalog.Utils.networking.async.stream;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import com.wishbook.catalog.Utils.networking.async.ByteBufferList;
import com.wishbook.catalog.Utils.networking.async.DataEmitter;
import com.wishbook.catalog.Utils.networking.async.callback.CompletedCallback;
import com.wishbook.catalog.Utils.networking.async.callback.DataCallback;

public class OutputStreamDataCallback implements DataCallback, CompletedCallback {
    private OutputStream mOutput;
    public OutputStreamDataCallback(OutputStream os) {
        mOutput = os;
    }

    public OutputStream getOutputStream() {
        return mOutput;
    }

    @Override
    public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
        try {
            while (bb.size() > 0) {
                ByteBuffer b = bb.remove();
                mOutput.write(b.array(), b.arrayOffset() + b.position(), b.remaining());
                ByteBufferList.reclaim(b);
            }
        }
        catch (Exception ex) {
            onCompleted(ex);
        }
        finally {
            bb.recycle();
        }
    }
    
    public void close() {
        try {
            mOutput.close();
        }
        catch (IOException e) {
            onCompleted(e);
        }
    }

    @Override
    public void onCompleted(Exception error) {
        error.printStackTrace();       
    }
}
