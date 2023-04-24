package com.wishbook.catalog.Utils.networking.async.http.socketio;

public interface ConnectCallback {
    void onConnectCompleted(Exception ex, SocketIOClient client);
}