package com.wishbook.catalog.Utils.networking;

import com.wishbook.catalog.Utils.networking.async.ByteBufferList;
import com.wishbook.catalog.Utils.networking.async.DataEmitter;
import com.wishbook.catalog.Utils.networking.async.DataSink;
import com.wishbook.catalog.Utils.networking.async.callback.CompletedCallback;
import com.wishbook.catalog.Utils.networking.async.future.Future;
import com.wishbook.catalog.Utils.networking.async.future.TransformFuture;
import com.wishbook.catalog.Utils.networking.async.parser.AsyncParser;
import com.wishbook.catalog.Utils.networking.async.parser.ByteBufferListParser;
import com.wishbook.catalog.Utils.networking.async.stream.ByteBufferListInputStream;

import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * Created by vignesh on 11/3/13.
 */
class InputStreamParser implements AsyncParser<InputStream> {
    @Override
    public Future<InputStream> parse(DataEmitter emitter) {
        return new ByteBufferListParser().parse(emitter)
        .then(new TransformFuture<InputStream, ByteBufferList>() {
            @Override
            protected void transform(ByteBufferList result) throws Exception {
                setComplete(new ByteBufferListInputStream(result));
            }
        });
    }

    @Override
    public void write(DataSink sink, InputStream value, CompletedCallback completed) {
        throw new AssertionError("not implemented");
    }

    @Override
    public Type getType() {
        return InputStream.class;
    }
}
