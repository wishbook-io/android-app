package com.wishbook.catalog.Utils.networking.async.http.socketio;

import org.json.JSONArray;

public interface EventCallback {
    void onEvent(JSONArray argument, Acknowledge acknowledge);
}