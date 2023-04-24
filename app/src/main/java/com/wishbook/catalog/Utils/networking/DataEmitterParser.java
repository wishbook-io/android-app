package com.wishbook.catalog.Utils.networking;

import com.wishbook.catalog.Utils.networking.async.DataEmitter;
import com.wishbook.catalog.Utils.networking.async.DataSink;
import com.wishbook.catalog.Utils.networking.async.Util;
import com.wishbook.catalog.Utils.networking.async.callback.CompletedCallback;
import com.wishbook.catalog.Utils.networking.async.future.Future;
import com.wishbook.catalog.Utils.networking.async.future.SimpleFuture;
import com.wishbook.catalog.Utils.networking.async.parser.AsyncParser;

import java.lang.reflect.Type;

/**
 * Created by vignesh on 5/27/15.
 */
class DataEmitterParser implements AsyncParser<DataEmitter> {
    @Override
    public Future<DataEmitter> parse(DataEmitter emitter) {
        SimpleFuture<DataEmitter> ret = new SimpleFuture<DataEmitter>();
        ret.setComplete(emitter);
        return ret;
    }

    @Override
    public void write(DataSink sink, DataEmitter value, CompletedCallback completed) {
        Util.pump(value, sink, completed);
    }

    @Override
    public Type getType() {
        return DataEmitter.class;
    }
}
