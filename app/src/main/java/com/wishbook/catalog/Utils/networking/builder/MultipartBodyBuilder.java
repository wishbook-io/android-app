package com.wishbook.catalog.Utils.networking.builder;

import com.wishbook.catalog.Utils.networking.async.http.body.Part;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
* Created by vignesh on 5/30/13.
*/ // set additional body parameters for multipart/form-data
public interface MultipartBodyBuilder<M extends MultipartBodyBuilder> {
    /**
     * Specify a multipart/form-data parameter to send to the HTTP server. If no HTTP method was explicitly
     * provided in the load call, the default HTTP method, POST, is used.
     * @param name Multipart name
     * @param value Multipart String value
     * @return
     */
    M setMultipartParameter(String name, String value);

    /**
     * Specify a multipart/form-data parameter to send to the HTTP server. If no HTTP method was explicitly
     * provided in the load call, the default HTTP method, POST, is used.
     * @param params The map containing key value pairs
     * @return
     */
    M setMultipartParameters(Map<String, List<String>> params);

    /**
     * Specify a multipart/form-data file to send to the HTTP server. If no HTTP method was explicitly
     * provided in the load call, the default HTTP method, POST, is used.
     * @param name Multipart name
     *    Name part of Content-Disposition header.
     * @param file Multipart file to send
     * @return
     */
    M setMultipartFile(String name, File file);

    /**
     * Specify a multipart/form-data file to send to the HTTP server. If no HTTP method was explicitly
     * provided in the load call, the default HTTP method, POST, is used.
     * @param name Multipart name
     *    Name part of Content-Disposition header.
     * @param file Multipart file's content type
     *    MIME type of file.
     * @param file Multipart file to send
     * @return
     */
    M setMultipartFile(String name, String contentType, File file);

    /**
     * Specify multipart/form-data parameters to send to the HTTP server. If no HTTP method was explicitly
     * provided in the load call, the default HTTP method, POST, is used.
     * @param parameters
     * @return
     */
    M addMultipartParts(Iterable<Part> parameters);

    /**
     * Specify multipart/form-data parameters to send to the HTTP server. If no HTTP method was explicitly
     * provided in the load call, the default HTTP method, POST, is used.
     * @param parameters
     * @return
     */
    M addMultipartParts(Part... parameters);

    /**
     * Specify the content type to use in this request. By default it is
     * multipart/form-data
     * @param contentType
     * @return
     */
    M setMultipartContentType(String contentType);
}
