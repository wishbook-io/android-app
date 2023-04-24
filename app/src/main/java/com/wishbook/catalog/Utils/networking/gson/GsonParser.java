package com.wishbook.catalog.Utils.networking.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.wishbook.catalog.Utils.networking.async.ByteBufferList;
import com.wishbook.catalog.Utils.networking.async.DataEmitter;
import com.wishbook.catalog.Utils.networking.async.DataSink;
import com.wishbook.catalog.Utils.networking.async.callback.CompletedCallback;
import com.wishbook.catalog.Utils.networking.async.future.Future;
import com.wishbook.catalog.Utils.networking.async.future.TransformFuture;
import com.wishbook.catalog.Utils.networking.async.parser.AsyncParser;
import com.wishbook.catalog.Utils.networking.async.parser.ByteBufferListParser;
import com.wishbook.catalog.Utils.networking.async.parser.StringParser;
import com.wishbook.catalog.Utils.networking.async.stream.ByteBufferListInputStream;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

/**
 * Created by vignesh on 5/27/13.
 */
public abstract class GsonParser<T extends JsonElement> implements AsyncParser<T> {
    Charset forcedCharset;
    Class<? extends JsonElement> clazz;
    public GsonParser(Class<? extends T> clazz) {
        this.clazz = clazz;
    }

    public GsonParser(Class<? extends T> clazz, Charset charset) {
        this(clazz);
        this.forcedCharset = charset;
    }

    @Override
    public Future<T> parse(DataEmitter emitter) {
        final String charset = emitter.charset();
        return new ByteBufferListParser().parse(emitter)
        .then(new TransformFuture<T, ByteBufferList>() {
            @Override
            protected void transform(ByteBufferList result) throws Exception {
                JsonParser parser = new JsonParser();
                ByteBufferListInputStream bis = new ByteBufferListInputStream(result);
                InputStreamReader isr;
                if (forcedCharset != null)
                    isr = new InputStreamReader(bis, forcedCharset);
                else if (charset != null)
                    isr = new InputStreamReader(bis, charset);
                else
                    isr = new InputStreamReader(bis);
                JsonElement parsed = parser.parse(new JsonReader(isr));
                if (parsed.isJsonNull() || parsed.isJsonPrimitive())
                    throw new JsonParseException("unable to parse json");
                if (!clazz.isInstance(parsed))
                    throw new ClassCastException(parsed.getClass().getCanonicalName() + " can not be casted to " + clazz.getCanonicalName());
                setComplete(null, (T)parsed);
            }
        });
    }

    @Override
    public void write(DataSink sink, T value, CompletedCallback completed) {
        new StringParser().write(sink, value.toString(), completed);
    }

    @Override
    public Type getType() {
        return clazz;
    }
}
