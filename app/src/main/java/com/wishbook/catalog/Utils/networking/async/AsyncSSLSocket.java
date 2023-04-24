package com.wishbook.catalog.Utils.networking.async;

import java.security.cert.X509Certificate;

import javax.net.ssl.SSLEngine;

public interface AsyncSSLSocket extends AsyncSocket {
    X509Certificate[] getPeerCertificates();
    SSLEngine getSSLEngine();
}
