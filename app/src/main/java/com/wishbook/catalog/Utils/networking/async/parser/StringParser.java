package com.wishbook.catalog.Utils.networking.async.parser;

import com.wishbook.catalog.Utils.networking.async.ByteBufferList;
import com.wishbook.catalog.Utils.networking.async.DataEmitter;
import com.wishbook.catalog.Utils.networking.async.DataSink;
import com.wishbook.catalog.Utils.networking.async.callback.CompletedCallback;
import com.wishbook.catalog.Utils.networking.async.future.Future;
import com.wishbook.catalog.Utils.networking.async.future.TransformFuture;

import java.lang.reflect.Type;
import java.nio.charset.Charset;

/**
 * Created by vignesh on 5/27/13.
 */
public class StringParser implements AsyncParser<String> {
    Charset forcedCharset;

    public StringParser() {
    }

    public StringParser(Charset charset) {
        this.forcedCharset = charset;
    }

    @Override
    public Future<String> parse(DataEmitter emitter) {
        final String charset = emitter.charset();
        return new ByteBufferListParser().parse(emitter)
        .then(new TransformFuture<String, ByteBufferList>() {
            @Override
            protected void transform(ByteBufferList result) throws Exception {
                Charset charsetToUse = forcedCharset;
                if (charsetToUse == null && charset != null)
                    charsetToUse = Charset.forName(charset);
                setComplete(result.readString(charsetToUse));
            }
        });
    }

    @Override
    public void write(DataSink sink, String value, CompletedCallback completed) {
        new ByteBufferListParser().write(sink, new ByteBufferList(value.getBytes()), completed);
    }

    @Override
    public Type getType() {
        return String.class;
    }
}
