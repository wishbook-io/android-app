package com.wishbook.catalog.Utils.networking.async.http.filter;

import com.wishbook.catalog.Utils.networking.async.ByteBufferList;
import com.wishbook.catalog.Utils.networking.async.DataEmitter;
import com.wishbook.catalog.Utils.networking.async.FilteredDataEmitter;

public class ContentLengthFilter extends FilteredDataEmitter {
    public ContentLengthFilter(long contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    protected void report(Exception e) {
        if (e == null && totalRead != contentLength)
            e = new PrematureDataEndException("End of data reached before content length was read: " + totalRead + "/" + contentLength + " Paused: " + isPaused());
        super.report(e);
    }

    long contentLength;
    long totalRead;
    ByteBufferList transformed = new ByteBufferList();
    @Override
    public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
        assert totalRead < contentLength;

        int remaining = bb.remaining();
        long toRead = Math.min(contentLength - totalRead, remaining);

        bb.get(transformed, (int)toRead);

        int beforeRead = transformed.remaining();

        super.onDataAvailable(emitter, transformed);

        totalRead += (beforeRead - transformed.remaining());
        transformed.get(bb);

        if (totalRead == contentLength)
            report(null);
    }
}
