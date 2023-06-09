package com.wishbook.catalog.Utils.networking.cookie;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.wishbook.catalog.Utils.networking.async.http.Headers;
import com.wishbook.catalog.Utils.networking.async.http.SimpleMiddleware;
import com.wishbook.catalog.Utils.networking.NetworkProcessor;

import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Created by vignesh on 5/29/13.
 */
public class CookieMiddleware extends SimpleMiddleware {
    CookieManager manager;
    SharedPreferences preferences;

    public CookieStore getCookieStore() {
        return manager.getCookieStore();
    }

    public void clear() {
        maybeInit();
        getCookieStore().removeAll();
        preferences.edit().clear().apply();
    }

    public CookieManager getCookieManager() {
        maybeInit();
        return manager;
    }

    NetworkProcessor ion;
    public CookieMiddleware(NetworkProcessor ion) {
        this.ion = ion;
    }

    public void reinit() {
        manager = new CookieManager(null, null);
        preferences = ion.getContext().getSharedPreferences(ion.getName() + "-cookies", Context.MODE_PRIVATE);

        Map<String, ?> allPrefs = preferences.getAll();
        for (String key: allPrefs.keySet()) {
            try {
                String value = preferences.getString(key, null);
                Headers headers = new Headers();
                String[] lines = value.split("\n");
                boolean first = true;
                for (String line: lines) {
                    if (first) {
                        first = false;
                    }
                    else if (!TextUtils.isEmpty(line)) {
                        headers.addLine(line);
                    }
                }
                manager.put(URI.create(key), headers.getMultiMap());
            }
            catch (Exception e) {
                Log.e("NetworkProcessor", "unable to load cookies", e);
            }
        }
    }

    public static void addCookies(Map<String, List<String>> allCookieHeaders, Headers headers) {
        for (Map.Entry<String, List<String>> entry : allCookieHeaders.entrySet()) {
            String key = entry.getKey();
            if ("Cookie".equalsIgnoreCase(key) || "Cookie2".equalsIgnoreCase(key)) {
                headers.addAll(key, entry.getValue());
            }
        }
    }

    private void maybeInit() {
        if (manager == null)
            reinit();
    }

    @Override
    public void onRequest(OnRequestData data) {
        maybeInit();
        try {
            Map<String, List<String>> cookies = manager.get(
                URI.create(
                    data.request.getUri().toString()),
                    data.request.getHeaders().getMultiMap());
            addCookies(cookies, data.request.getHeaders());
        }
        catch (Exception e) {
        }
    }

    @Override
    public void onHeadersReceived(OnHeadersReceivedDataOnRequestSentData data) {
        maybeInit();
        try {
            put(URI.create(data.request.getUri().toString()), data.response.headers());
        }
        catch (Exception e) {
        }
    }

    public void put(URI uri, Headers headers) {
        maybeInit();
        try {
            manager.put(uri, headers.getMultiMap());

            // no cookies to persist.
            if (headers.get("Set-Cookie") == null)
                return;

            List<HttpCookie> cookies = manager.getCookieStore().get(uri);

            Headers dump = new Headers();
            for (HttpCookie cookie: cookies) {
                dump.add("Set-Cookie", cookie.getName() + "=" + cookie.getValue());
            }

            String key = uri.getScheme() + "://" + uri.getAuthority();
            preferences.edit().putString(key, dump.toPrefixString("HTTP/1.1 200 OK")).commit();
        }
        catch (Exception e) {
        }
    }
}
