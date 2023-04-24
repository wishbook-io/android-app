package com.wishbook.catalog.Utils.networking.builder;

import android.app.ProgressDialog;
import android.os.Handler;
import android.widget.ProgressBar;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Utils.networking.async.http.NameValuePair;
import com.wishbook.catalog.Utils.networking.HeadersCallback;
import com.wishbook.catalog.Utils.networking.ProgressCallback;

import org.w3c.dom.Document;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
* Created by vignesh on 5/30/13.
*/ // set parameters
public interface RequestBuilder<F, R extends RequestBuilder, M extends MultipartBodyBuilder, U extends UrlEncodedBuilder> extends MultipartBodyBuilder<M>, UrlEncodedBuilder<U> {
    /**
     * Enable logging for this request
     * @param tag LOGTAG to use
     * @param level Log level of messages to display
     * @return
     */
    R setLogging(String tag, int level);

    /**
     * Route the request through the given proxy server.
     * @param host
     * @param port
     */
    R proxy(String host, int port);

    /**
     * Specify a callback that is invoked on download progress. This will not be invoked
     * on the UI thread.
     * @param callback
     * @return
     */
    R progress(ProgressCallback callback);

    /**
     * Specify a callback that is invoked on download progress. This will be invoked
     * on the UI thread.
     * @param callback
     * @return
     */
    R progressHandler(ProgressCallback callback);

    /**
     * Specify a ProgressBar to update during the request
     * @param progressBar
     * @return
     */
    R progressBar(ProgressBar progressBar);

    /**
     * Specify a ProgressDialog to update during the request
     * @param progressDialog
     * @return
     */
    R progressDialog(ProgressDialog progressDialog);

    /**
     * Specify a callback that is invoked on upload progress of a HTTP
     * request body.
     * @param callback
     * @return
     */
    R uploadProgress(ProgressCallback callback);

    /**
     * Specify a callback that is invoked on upload progress of a HTTP
     * request body. This will be invoked on the UI thread.
     * @param callback
     * @return
     */
    R uploadProgressHandler(ProgressCallback callback);

    /**
     * Specify a ProgressBar to update while uploading
     * a request body.
     * @param progressBar
     * @return
     */
    R uploadProgressBar(ProgressBar progressBar);

    /**
     * Specify a ProgressDialog to update while uploading
     * a request body.
     * @param progressDialog
     * @return
     */
    R uploadProgressDialog(ProgressDialog progressDialog);

    /**
     * Post the Future callback onto the given handler. Not specifying this explicitly
     * results in the default handle of Thread.currentThread to be used, if one exists.
     * @param handler Handler to use or null
     * @return
     */
    R setHandler(Handler handler);

    /**
     * Set a HTTP header
     * @param name Header name
     * @param value Header value
     * @return
     */
    R setHeader(String name, String value);

    /**
     * Set HTTP headers
     * @param header
     * @return
     */
    R setHeader(NameValuePair... header);

    /**
     * Disable usage of the cache for this request
     * @return
     */
    R noCache();

    /**
     * Set whether this request will follow redirects
     */
    R followRedirect(boolean follow);

    /**
     * Add an HTTP header
     * @param name Header name
     * @param value Header value
     * @return
     */
    R addHeader(String name, String value);

    /**
     * Add multiple headers at once
     * @param params
     * @return
     */
    R addHeaders(Map<String, List<String>> params);

    /**
     * Add a query parameter
     * @param name
     * @param value
     * @return
     */
    R addQuery(String name, String value);

    /**
     * Add multiple query parameters at once
     * @param params
     * @return
     */
    R addQueries(Map<String, List<String>> params);

    /**
     * Set the user agent of this request.
     * @param userAgent
     * @return
     */
    R userAgent(String userAgent);

    /**
     * Specify the timeout in milliseconds before the request will cancel.
     * A CancellationException will be returned as the result.
     * @param timeoutMilliseconds Timeout in milliseconds
     * @return
     */
    R setTimeout(int timeoutMilliseconds);

    /**
     * Invoke the given callback when the http request headers are received.
     * @param callback
     * @return
     */
    R onHeaders(HeadersCallback callback);

    /**
     * Provide Basic authentication credentials to be sent with the request.
     * @param username
     * @param password
     * @return
     */
    R basicAuthentication(String username, String password);

    /**
     * Specify a JsonObject to send to the HTTP server. If no HTTP method was explicitly
     * provided in the load call, the default HTTP method, POST, is used.
     * @param jsonObject JsonObject to send with the request
     * @return
     */
    F setJsonObjectBody(JsonObject jsonObject);

    /**
     * Specify an object to convert to json and send to the HTTP server. If no HTTP
     * method was explicitly provided in the load call, the default HTTP method,
     * POST, is used.
     * @param object Object to serialize with Json and send with the request
     * @param token Type token to assist with generic type serialization
     * @return
     */
    <T> F setJsonPojoBody(T object, TypeToken<T> token);

    /**
     * Specify an object to convert to json and send to the HTTP server. If no HTTP
     * method was explicitly provided in the load call, the default HTTP method,
     * POST, is used.
     * @param object Object to serialize with Json and send with the request
     * @return
     */
    <T> F setJsonPojoBody(T object);

    /**
     * Specify a JsonArray to send to the HTTP server. If no HTTP method was explicitly
     * provided in the load call, the default HTTP method, POST, is used.
     * @param jsonArray JsonObject to send with the request
     * @return
     */
    F setJsonArrayBody(JsonArray jsonArray);

    /**
     * Specify a String to send to the HTTP server. If no HTTP method was explicitly
     * provided in the load call, the default HTTP method, POST, is used.
     * @param string String to send with the request
     * @return
     */
    F setStringBody(String string);

    /**
     * Specify an XML Document to send to the HTTP server. If no HTTP method was explicitly
     * provided in the load call, the default HTTP method, POST, is used.
     * @param document Document to send with the request
     * @return
     */
    F setDocumentBody(Document document);

    /**
     * Specify a File to send to the HTTP server. If no HTTP method was explicitly
     * provided in the load call, the default HTTP method, POST, is used.
     * @param file File to send with the request
     * @return
     */
    F setFileBody(File file);

    /**
     * Specify a byte array to send to the HTTP server. If no HTTP method was explicitly
     * provided in the load call, the default HTTP method, POST, is used.
     * @param bytes Bytes to send with the request
     * @return
     */
    F setByteArrayBody(byte[] bytes);

    /**
     * Specify an InputStream to send to the HTTP server. If no HTTP method was explicitly
     * provided in the load call, the default HTTP method, POST, is used.
     * @param inputStream InputStream to send with the request
     * @return
     */
    Builders.Any.F setStreamBody(InputStream inputStream);

    /**
     * Specify an InputStream to send to the HTTP server. If no HTTP method was explicitly
     * provided in the load call, the default HTTP method, POST, is used.
     * @param inputStream InputStream to send with the request
     * @param length length of the input stream (in bytes) to read
     * @return
     */
    Builders.Any.F setStreamBody(InputStream inputStream, int length);
}
