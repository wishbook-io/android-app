package com.wishbook.catalog.Utils.networking.async.http.server;

import android.net.Uri;

import com.wishbook.catalog.Utils.networking.async.AsyncServer;
import com.wishbook.catalog.Utils.networking.async.http.AsyncHttpClient;
import com.wishbook.catalog.Utils.networking.async.http.AsyncHttpRequest;
import com.wishbook.catalog.Utils.networking.async.http.AsyncHttpResponse;
import com.wishbook.catalog.Utils.networking.async.http.callback.HttpConnectCallback;

/**
 * Created by vignesh on 7/22/14.
 */
public class AsyncProxyServer extends AsyncHttpServer {
    AsyncHttpClient proxyClient;
    public AsyncProxyServer(AsyncServer server) {
        proxyClient = new AsyncHttpClient(server);
    }

    @Override
    protected void onRequest(HttpServerRequestCallback callback, AsyncHttpServerRequest request, final AsyncHttpServerResponse response) {
        super.onRequest(callback, request, response);

        if (callback != null)
            return;

        try {
            Uri uri;

            try {
                uri = Uri.parse(request.getPath());
                if (uri.getScheme() == null)
                    throw new Exception("no host or full uri provided");
            }
            catch (Exception e) {
                String host = request.getHeaders().get("Host");
                int port = 80;
                if (host != null) {
                    String[] splits = host.split(":", 2);
                    if (splits.length == 2) {
                        host = splits[0];
                        port = Integer.parseInt(splits[1]);
                    }
                }
                uri = Uri.parse("http://" + host + ":" + port + request.getPath());
            }

            proxyClient.execute(new AsyncHttpRequest(uri, request.getMethod(), request.getHeaders()), new HttpConnectCallback() {
                @Override
                public void onConnectCompleted(Exception ex, AsyncHttpResponse remoteResponse) {
                    if (ex != null) {
                        response.code(500);
                        response.send(ex.getMessage());
                        return;
                    }
                    response.proxy(remoteResponse);
                }
            });
        }
        catch (Exception e) {
            response.code(500);
            response.send(e.getMessage());
        }
    }

    @Override
    protected boolean onRequest() {
        return true;
    }
}
