package com.wishbook.catalog.Utils.networking.async.callback;

import com.wishbook.catalog.Utils.networking.async.AsyncSocket;

public interface ConnectCallback {
    void onConnectCompleted(Exception ex, AsyncSocket socket);
}
