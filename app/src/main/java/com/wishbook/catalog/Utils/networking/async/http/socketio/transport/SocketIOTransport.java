package com.wishbook.catalog.Utils.networking.async.http.socketio.transport;

import com.wishbook.catalog.Utils.networking.async.AsyncServer;
import com.wishbook.catalog.Utils.networking.async.callback.CompletedCallback;

/**
 * A socket.io transport.
 *
 * Please, refer to the documentation in https://github.com/LearnBoost/socket.io-spec
 */
public interface SocketIOTransport {
    interface StringCallback {
        void onStringAvailable(String s);
    }

    /**
     * Send message to the server
     * @param string
     */
    void send(String string);

    /**
     * Close connection
     */
    void disconnect();

    void setStringCallback(StringCallback callback);
    void setClosedCallback(CompletedCallback handler);

    AsyncServer getServer();
    boolean isConnected();

    /**
     * Indicates whether heartbeats are enabled for this transport
     * @return
     */
    boolean heartbeats();
    
    String getSessionId();
}
