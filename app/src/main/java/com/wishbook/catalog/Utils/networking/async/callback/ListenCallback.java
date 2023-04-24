package com.wishbook.catalog.Utils.networking.async.callback;

import com.wishbook.catalog.Utils.networking.async.AsyncServerSocket;
import com.wishbook.catalog.Utils.networking.async.AsyncSocket;


public interface ListenCallback extends CompletedCallback {
    void onAccepted(AsyncSocket socket);
    void onListening(AsyncServerSocket socket);
}
