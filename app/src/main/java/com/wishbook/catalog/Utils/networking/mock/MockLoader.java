package com.wishbook.catalog.Utils.networking.mock;

import com.wishbook.catalog.Utils.networking.async.http.AsyncHttpRequest;
import com.wishbook.catalog.Utils.networking.NetworkProcessor;
import com.wishbook.catalog.Utils.networking.Loader;
import com.wishbook.catalog.Utils.networking.future.ResponseFuture;
import com.wishbook.catalog.Utils.networking.loader.SimpleLoader;

import java.lang.reflect.Type;

/**
 * Created by vignesh on 3/6/15.
 */
public class MockLoader extends SimpleLoader {
    public static void install(NetworkProcessor ion, MockRequestHandler requestHandler) {
        MockLoader mockLoader = new MockLoader(requestHandler);
        for (Loader loader: ion.configure().getLoaders()) {
            if (loader instanceof MockLoader)
                throw new RuntimeException("MockLoader already installed.");
        }
        ion.configure().addLoader(0, mockLoader);
    }

    MockRequestHandler requestHandler;
    private MockLoader(MockRequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    public <T> ResponseFuture<T> load(NetworkProcessor ion, AsyncHttpRequest request, Type type) {
        T result = (T)requestHandler.request(request.getUri().toString());
        if (result != null) {
            MockResponseFuture<T> ret = new MockResponseFuture<T>(request);
            ret.setComplete(result);
            return ret;
        }
        return super.load(ion, request, type);
    }
}
