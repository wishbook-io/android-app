package com.wishbook.catalog.Utils.networking.builder;

import java.io.File;

/**
* Created by vignesh on 5/30/13.
*/ // .load
public interface LoadBuilder<B> {
    /**
     * Load an uri.
     * @param uri Uri to load. This may be a http(s), file, or content uri.
     * @return
     */
    B load(String uri);

    /**
     * Load an url using the given an HTTP method such as GET or POST.
     * @param method HTTP method such as GET or POST.
     * @param url Url to load.
     * @return
     */
    B load(String method, String url);

    /**
     * Load a file.
     * @param file File to load.
     * @return
     */
    B load(File file);
}
