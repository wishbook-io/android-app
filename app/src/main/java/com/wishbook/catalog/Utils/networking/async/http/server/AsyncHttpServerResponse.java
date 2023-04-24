package com.wishbook.catalog.Utils.networking.async.http.server;

import com.wishbook.catalog.Utils.networking.async.AsyncSocket;
import com.wishbook.catalog.Utils.networking.async.DataSink;
import com.wishbook.catalog.Utils.networking.async.callback.CompletedCallback;
import com.wishbook.catalog.Utils.networking.async.http.AsyncHttpResponse;
import com.wishbook.catalog.Utils.networking.async.http.Headers;

import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;

public interface AsyncHttpServerResponse extends DataSink, CompletedCallback {
    void end();
    void send(String contentType, byte[] bytes);
    void send(String contentType, String string);
    void send(String string);
    void send(JSONObject json);
    void sendFile(File file);
    void sendStream(InputStream inputStream, long totalLength);
    void code(int code);
    int code();
    Headers getHeaders();
    void writeHead();
    void setContentType(String contentType);
    void redirect(String location);

    // NOT FINAL
    void proxy(AsyncHttpResponse response);

    /**
     * Alias for end. Used with CompletedEmitters
     */
    void onCompleted(Exception ex);
    AsyncSocket getSocket();
}
