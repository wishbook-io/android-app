package com.wishbook.catalog.Utils.networking.async.http;

import javax.net.ssl.SSLEngine;

public interface AsyncSSLEngineConfigurator {
    void configureEngine(SSLEngine engine, AsyncHttpClientMiddleware.GetSocketData data, String host, int port);
}
