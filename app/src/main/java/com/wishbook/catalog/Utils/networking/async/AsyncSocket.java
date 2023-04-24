package com.wishbook.catalog.Utils.networking.async;


public interface AsyncSocket extends DataEmitter, DataSink {
    AsyncServer getServer();
}
