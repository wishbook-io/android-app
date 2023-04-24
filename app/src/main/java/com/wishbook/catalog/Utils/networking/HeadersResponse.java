package com.wishbook.catalog.Utils.networking;

import com.wishbook.catalog.Utils.networking.async.http.Headers;

/**
 * Created by vignesh on 7/22/14.
 */
public class HeadersResponse {
    public HeadersResponse(int code, String message, Headers headers) {
        this.headers = headers;
        this.code = code;
        this.message = message;
    }

    Headers headers;
    public Headers getHeaders() {
        return headers;
    }

    int code;
    public int code() {
        return code;
    }

    public HeadersResponse code(int code) {
        this.code = code;
        return this;
    }

    String message;
    public String message() {
        return message;
    }
    public HeadersResponse message(String message) {
        this.message = message;
        return this;
    }
}
