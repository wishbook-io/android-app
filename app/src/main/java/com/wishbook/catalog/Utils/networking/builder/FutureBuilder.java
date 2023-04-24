package com.wishbook.catalog.Utils.networking.builder;

import com.wishbook.catalog.Utils.networking.async.DataEmitter;
import com.wishbook.catalog.Utils.networking.async.parser.AsyncParser;
import com.wishbook.catalog.Utils.networking.future.ResponseFuture;

import org.w3c.dom.Document;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
* Created by vignesh on 5/30/13.
*/ // get the result, transformed to how you want it
public interface FutureBuilder extends BitmapFutureBuilder, ImageViewFutureBuilder, GsonFutureBuilder {
    /**
     * Execute the request and get the result as a String
     * @return
     */
    ResponseFuture<String> asString();

    /**
     * Execute the request and get the result as a String
     * @param charset Specify a charset to use.
     * @return
     */
    ResponseFuture<String> asString(Charset charset);

    /**
     * Execute the request and get the result as an InputStream.
     * This method will load the entire response into memory
     * and should not be used for large responses.
     * @return
     */
    ResponseFuture<InputStream> asInputStream();

    /**
     * Execute the request and get the result as a DataEmitter.
     * @return
     */
    ResponseFuture<DataEmitter> asDataEmitter();

    /**
     * Execute the request and get the result as an XML Document
     * @return
     */
    ResponseFuture<Document> asDocument();

    /**
     * Use the request as a Bitmap which can then be modified and/or applied to an ImageView.
     * @return
     */
    Builders.Any.BF<? extends Builders.Any.BF<?>> withBitmap();

    /**
     * Execute the request and write it to the given OutputStream.
     * The OutputStream will be closed upon finishing.
     * @param outputStream OutputStream to write the request
     * @return
     */
    <T extends OutputStream> ResponseFuture<T> write(T outputStream);

    /**
     * Execute the request and write it to the given OutputStream.
     * Specify whether the OutputStream will be closed upon finishing.
     * @param outputStream OutputStream to write the request
     * @param close Indicate whether the OutputStream should be closed on completion.
     * @return
     */
    <T extends OutputStream> ResponseFuture<T> write(T outputStream, boolean close);

    /**
     * Execute the request and write the results to a file
     * @param file File to write
     * @return
     */
    ResponseFuture<File> write(File file);

    /**
     * Deserialize a response into an object given a custom parser.
     * @param parser
     * @param <T>
     * @return
     */
    <T> ResponseFuture<T> as(AsyncParser<T> parser);

    /**
     * Execute the request and get the result as a byte array
     * @return
     */
    ResponseFuture<byte[]> asByteArray();

    /**
     * Add this request to a group specified by groupKey. This key can be used in a later call to
     * NetworkProcessor.cancelAll(groupKey) to cancel all the requests in the same group.
     * @param groupKey
     * @return
     */
    FutureBuilder group(Object groupKey);
}
