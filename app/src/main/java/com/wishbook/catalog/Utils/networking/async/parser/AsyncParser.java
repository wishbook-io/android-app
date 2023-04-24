package com.wishbook.catalog.Utils.networking.async.parser;

import com.wishbook.catalog.Utils.networking.async.DataEmitter;
import com.wishbook.catalog.Utils.networking.async.DataSink;
import com.wishbook.catalog.Utils.networking.async.callback.CompletedCallback;
import com.wishbook.catalog.Utils.networking.async.future.Future;

import java.lang.reflect.Type;

/**
 * Created by vignesh on 5/27/13.
 */
public interface AsyncParser<T> {
    Future<T> parse(DataEmitter emitter);
    void write(DataSink sink, T value, CompletedCallback completed);
    Type getType();
}
