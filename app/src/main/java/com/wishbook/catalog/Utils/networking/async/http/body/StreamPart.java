package com.wishbook.catalog.Utils.networking.async.http.body;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.wishbook.catalog.Utils.networking.async.DataSink;
import com.wishbook.catalog.Utils.networking.async.callback.CompletedCallback;
import com.wishbook.catalog.Utils.networking.async.http.NameValuePair;

public abstract class StreamPart extends Part {
    public StreamPart(String name, long length, List<NameValuePair> contentDisposition) {
        super(name, length, contentDisposition);
    }
    
    @Override
    public void write(DataSink sink, CompletedCallback callback) {
        try {
            InputStream is = getInputStream();
            com.wishbook.catalog.Utils.networking.async.Util.pump(is, sink, callback);
        }
        catch (Exception e) {
            callback.onCompleted(e);
        }
    }
    
    protected abstract InputStream getInputStream() throws IOException;
}
