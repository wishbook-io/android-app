package com.wishbook.catalog.Utils.networking.async.parser;

import com.wishbook.catalog.Utils.networking.async.DataEmitter;
import com.wishbook.catalog.Utils.networking.async.DataSink;
import com.wishbook.catalog.Utils.networking.async.callback.CompletedCallback;
import com.wishbook.catalog.Utils.networking.async.future.Future;
import com.wishbook.catalog.Utils.networking.async.future.TransformFuture;

import org.json.JSONArray;

import java.lang.reflect.Type;

/**
 * Created by vignesh on 5/27/13.
 */
public class JSONArrayParser implements AsyncParser<JSONArray> {
    @Override
    public Future<JSONArray> parse(DataEmitter emitter) {
        return new StringParser().parse(emitter)
        .then(new TransformFuture<JSONArray, String>() {
            @Override
            protected void transform(String result) throws Exception {
                setComplete(new JSONArray(result));
            }
        });
    }

    @Override
    public void write(DataSink sink, JSONArray value, CompletedCallback completed) {
        new StringParser().write(sink, value.toString(), completed);
    }

    @Override
    public Type getType() {
        return JSONArray.class;
    }
}
