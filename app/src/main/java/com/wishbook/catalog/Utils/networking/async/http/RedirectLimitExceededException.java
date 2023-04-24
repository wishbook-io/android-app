package com.wishbook.catalog.Utils.networking.async.http;

public class RedirectLimitExceededException extends Exception {
    public RedirectLimitExceededException(String message) {
        super(message);
    }
}
