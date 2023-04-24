package com.wishbook.catalog.Utils.networking.async.wrapper;

import com.wishbook.catalog.Utils.networking.async.AsyncSocket;

public interface AsyncSocketWrapper extends AsyncSocket, DataEmitterWrapper {
    AsyncSocket getSocket();
}
