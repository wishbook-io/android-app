package com.wishbook.catalog.Utils.networking.async.http.filter;

import java.nio.ByteBuffer;

import com.wishbook.catalog.Utils.networking.async.ByteBufferList;
import com.wishbook.catalog.Utils.networking.async.DataSink;
import com.wishbook.catalog.Utils.networking.async.FilteredDataSink;

public class ChunkedOutputFilter extends FilteredDataSink {
    public ChunkedOutputFilter(DataSink sink) {
        super(sink);
    }

    @Override
    public ByteBufferList filter(ByteBufferList bb) {
        String chunkLen = Integer.toString(bb.remaining(), 16) + "\r\n";
        bb.addFirst(ByteBuffer.wrap(chunkLen.getBytes()));
        bb.add(ByteBuffer.wrap("\r\n".getBytes()));
        return bb;
    }

    @Override
    public void end() {
        setMaxBuffer(Integer.MAX_VALUE);
        ByteBufferList fin = new ByteBufferList();
        write(fin);
        setMaxBuffer(0);
        // do NOT call through to super.end, as chunking is a framing protocol.
        // we don't want to close the underlying transport.
    }
}
