package com.wishbook.catalog.Utils.networking.async.wrapper;

import com.wishbook.catalog.Utils.networking.async.DataEmitter;

public interface DataEmitterWrapper extends DataEmitter {
    DataEmitter getDataEmitter();
}
