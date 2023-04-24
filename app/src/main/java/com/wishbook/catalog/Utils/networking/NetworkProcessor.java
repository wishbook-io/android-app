package com.wishbook.catalog.Utils.networking;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.google.gson.Gson;

import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLContext;

import com.wishbook.catalog.Utils.networking.async.AsyncServer;
import com.wishbook.catalog.Utils.networking.async.future.Future;
import com.wishbook.catalog.Utils.networking.async.future.FutureCallback;
import com.wishbook.catalog.Utils.networking.async.http.AsyncHttpClient;
import com.wishbook.catalog.Utils.networking.async.http.AsyncHttpRequest;
import com.wishbook.catalog.Utils.networking.async.http.Headers;
import com.wishbook.catalog.Utils.networking.async.http.cache.ResponseCacheMiddleware;
import com.wishbook.catalog.Utils.networking.async.util.FileCache;
import com.wishbook.catalog.Utils.networking.async.util.FileUtility;
import com.wishbook.catalog.Utils.networking.async.util.HashList;
import com.wishbook.catalog.Utils.networking.bitmap.BitmapInfo;
import com.wishbook.catalog.Utils.networking.bitmap.NetworkProcessorBitmapCache;
import com.wishbook.catalog.Utils.networking.builder.Builders;
import com.wishbook.catalog.Utils.networking.builder.LoadBuilder;
import com.wishbook.catalog.Utils.networking.conscrypt.ConscryptMiddleware;
import com.wishbook.catalog.Utils.networking.cookie.CookieMiddleware;
import com.wishbook.catalog.Utils.networking.loader.AssetLoader;
import com.wishbook.catalog.Utils.networking.loader.AsyncHttpRequestFactory;
import com.wishbook.catalog.Utils.networking.loader.ContentLoader;
import com.wishbook.catalog.Utils.networking.loader.FileLoader;
import com.wishbook.catalog.Utils.networking.loader.HttpLoader;
import com.wishbook.catalog.Utils.networking.loader.PackageIconLoader;
import com.wishbook.catalog.Utils.networking.loader.ResourceLoader;
import com.wishbook.catalog.Utils.networking.loader.VideoLoader;

/**
 * Created by vignesh on 5/21/13.
 */
public class NetworkProcessor {
    static final Handler mainHandler = new Handler(Looper.getMainLooper());
    static int availableProcessors = Runtime.getRuntime().availableProcessors();
    static ExecutorService ioExecutorService = Executors.newFixedThreadPool(4);
    static ExecutorService bitmapExecutorService  = availableProcessors > 2 ? Executors.newFixedThreadPool(availableProcessors - 1) : Executors.newFixedThreadPool(1);
    static HashMap<String, NetworkProcessor> instances = new HashMap<String, NetworkProcessor>();

    /**
     * Get the default NetworkProcessor object instance and begin building a request
     * @param context
     * @return
     */
    public static LoadBuilder<Builders.Any.B> with(Context context) {
        return getDefault(context).build(context);
    }

    /**
     * the default NetworkProcessor object instance and begin building a request
     * @param fragment
     * @return
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static LoadBuilder<Builders.Any.B> with(Fragment fragment) {
        return getDefault(fragment.getActivity()).build(fragment);
    }

    /**
     * the default NetworkProcessor object instance and begin building a request
     * @param fragment
     * @return
     */
    public static LoadBuilder<Builders.Any.B> with(androidx.fragment.app.Fragment fragment) {
        return getDefault(fragment.getActivity()).build(fragment);
    }

    /**
     * Get the default NetworkProcessor instance
     * @param context
     * @return
     */
    public static NetworkProcessor getDefault(Context context) {
        return getInstance(context, "ion");
    }

    /**
     * Get the given NetworkProcessor instance by name
     * @param context
     * @param name
     * @return
     */
    public static NetworkProcessor getInstance(Context context, String name) {
        if (context == null)
            throw new NullPointerException("Can not pass null context in to retrieve ion instance");
        NetworkProcessor instance = instances.get(name);
        if (instance == null)
            instances.put(name, instance = new NetworkProcessor(context, name));
        return instance;
    }

    /**
     * Create a ImageView bitmap request builder
     * @param imageView
     * @return
     */
    public static Builders.IV.F<? extends Builders.IV.F<?>> with(ImageView imageView) {
        return getDefault(imageView.getContext()).build(imageView);
    }

    AsyncHttpClient httpClient;
    ConscryptMiddleware conscryptMiddleware;
    CookieMiddleware cookieMiddleware;
    ResponseCacheMiddleware responseCache;
    FileCache storeCache;
    HttpLoader httpLoader;
    ContentLoader contentLoader;
    ResourceLoader resourceLoader;
    AssetLoader assetLoader;
    VideoLoader videoLoader;
    PackageIconLoader packageIconLoader;
    FileLoader fileLoader;
    String logtag;
    int logLevel;
    Gson gson;
    String userAgent;
    ArrayList<Loader> loaders = new ArrayList<Loader>();
    String name;
    HashList<FutureCallback<BitmapInfo>> bitmapsPending = new HashList<FutureCallback<BitmapInfo>>();
    Config config = new Config();
    NetworkProcessorBitmapCache bitmapCache;
    Context context;
    NetworkProcessorImageViewRequestBuilder bitmapBuilder = new NetworkProcessorImageViewRequestBuilder(this);

    private NetworkProcessor(Context context, String name) {
        this.context = context = context.getApplicationContext();
        this.name = name;

        httpClient = new AsyncHttpClient(new AsyncServer("ion-" + name));
        httpClient.getSSLSocketMiddleware().setHostnameVerifier(new BrowserCompatHostnameVerifier());
        httpClient.getSSLSocketMiddleware().setSpdyEnabled(true);
        httpClient.insertMiddleware(conscryptMiddleware = new ConscryptMiddleware(context, httpClient.getSSLSocketMiddleware()));

        File ionCacheDir = new File(context.getCacheDir(), name);
        try {
            responseCache = ResponseCacheMiddleware.addCache(httpClient, ionCacheDir, 10L * 1024L * 1024L);
        }
        catch (IOException e) {
            NetworkProcessorLog.w("unable to set up response cache, clearing", e);
            FileUtility.deleteDirectory(ionCacheDir);
            try {
                responseCache = ResponseCacheMiddleware.addCache(httpClient, ionCacheDir, 10L * 1024L * 1024L);
            }
            catch (IOException ex) {
                NetworkProcessorLog.w("unable to set up response cache, failing", e);
            }
        }

        storeCache = new FileCache(new File(context.getFilesDir(), name), Long.MAX_VALUE, false);

        // TODO: Support pre GB?
        if (Build.VERSION.SDK_INT >= 9)
            addCookieMiddleware();

        httpClient.getSocketMiddleware().setConnectAllAddresses(true);
        httpClient.getSSLSocketMiddleware().setConnectAllAddresses(true);

        bitmapCache = new NetworkProcessorBitmapCache(this);

        configure()
                .addLoader(videoLoader = new VideoLoader())
                .addLoader(packageIconLoader = new PackageIconLoader())
                .addLoader(httpLoader = new HttpLoader())
                .addLoader(contentLoader = new ContentLoader())
                .addLoader(resourceLoader = new ResourceLoader())
                .addLoader(assetLoader = new AssetLoader())
                .addLoader(fileLoader = new FileLoader());
    }

    public static ExecutorService getBitmapLoadExecutorService() {
        return bitmapExecutorService;
    }

    public static ExecutorService getIoExecutorService() {
        return ioExecutorService;
    }

    /**
     * Begin building a request
     * @param context
     * @return
     */
    public LoadBuilder<Builders.Any.B> build(Context context) {
        return new NetworkProcessorRequestBuilder(ContextReference.fromContext(context), this);
    }

    /**
     * Begin building a request
     * @param fragment
     * @return
     */
    public LoadBuilder<Builders.Any.B> build(Fragment fragment) {
        return new NetworkProcessorRequestBuilder(new ContextReference.FragmentContextReference(fragment), this);
    }

    /**
     * Begin building a request
     * @param fragment
     * @return
     */
    public LoadBuilder<Builders.Any.B> build(androidx.fragment.app.Fragment fragment) {
        return new NetworkProcessorRequestBuilder(new ContextReference.SupportFragmentContextReference(fragment), this);
    }

    /**
     * Create a builder that can be used to build an network request
     * @param imageView
     * @return
     */
    public Builders.IV.F<? extends Builders.IV.F<?>> build(ImageView imageView) {
        if (Thread.currentThread() != Looper.getMainLooper().getThread())
            throw new IllegalStateException("must be called from UI thread");
        bitmapBuilder.reset();
        bitmapBuilder.ion = this;
        return bitmapBuilder.withImageView(imageView);
    }

    int groupCount(Object group) {
        FutureSet members;
        synchronized (this) {
            members = inFlight.get(group);
        }

        if (members == null)
            return 0;

        return members.size();
    }

    private static Comparator<DeferredLoadBitmap> DEFERRED_COMPARATOR = new Comparator<DeferredLoadBitmap>() {
        @Override
        public int compare(DeferredLoadBitmap lhs, DeferredLoadBitmap rhs) {
            // higher is more recent
            if (lhs.priority == rhs.priority)
                return 0;
            if (lhs.priority < rhs.priority)
                return 1;
            return -1;
        }
    };

    private Runnable processDeferred = new Runnable() {
        @Override
        public void run() {
            if (BitmapFetcher.shouldDeferImageView(NetworkProcessor.this))
                return;
            ArrayList<DeferredLoadBitmap> deferred = null;
            for (String key: bitmapsPending.keySet()) {
                Object owner = bitmapsPending.tag(key);
                if (owner instanceof DeferredLoadBitmap) {
                    DeferredLoadBitmap deferredLoadBitmap = (DeferredLoadBitmap)owner;
                    if (deferred == null)
                        deferred = new ArrayList<DeferredLoadBitmap>();
                    deferred.add(deferredLoadBitmap);
                }
            }

            if (deferred == null)
                return;
            int count = 0;
            Collections.sort(deferred, DEFERRED_COMPARATOR);
            for (DeferredLoadBitmap deferredLoadBitmap: deferred) {
                bitmapsPending.tag(deferredLoadBitmap.key, null);
                bitmapsPending.tag(deferredLoadBitmap.fetcher.bitmapKey, null);
                deferredLoadBitmap.fetcher.execute();
                count++;
                // do MAX_IMAGEVIEW_LOAD max. this may end up going over the MAX_IMAGEVIEW_LOAD threshhold
                if (count > BitmapFetcher.MAX_IMAGEVIEW_LOAD)
                    return;
            }
        }
    };

    void processDeferred() {
        mainHandler.removeCallbacks(processDeferred);
        mainHandler.post(processDeferred);
    }

    /**
     * Cancel all pending requests associated with the request group
     * @param group
     */
    public void cancelAll(Object group) {
        FutureSet members;
        synchronized (this) {
            members = inFlight.remove(group);
        }

        if (members == null)
            return;

        for (Future future: members.keySet()) {
            if (future != null)
                future.cancel();
        }
    }

    void addFutureInFlight(Future future, Object group) {
        if (group == null || future == null || future.isDone() || future.isCancelled())
            return;

        FutureSet members;
        synchronized (this) {
            members = inFlight.get(group);
            if (members == null) {
                members = new FutureSet();
                inFlight.put(group, members);
            }
        }

        members.put(future, true);
    }

    /**
     * Cancel all pending requests
     */
    public void cancelAll() {
        ArrayList<Object> groups;

        synchronized (this) {
            groups = new ArrayList<Object>(inFlight.keySet());
        }

        for (Object group: groups)
            cancelAll(group);
    }

    /**
     * Cancel all pending requests associated with the given context
     * @param context
     */
    public void cancelAll(Context context) {
        cancelAll((Object)context);
    }

    public int getPendingRequestCount(Object group) {
        synchronized (this) {
            FutureSet members = inFlight.get(group);
            if (members == null)
                return 0;
            int ret = 0;
            for (Future future: members.keySet()) {
                if (!future.isCancelled() && !future.isDone())
                    ret++;
            }
            return ret;
        }
    }

    public void dump() {
        bitmapCache.dump();
        Log.i(logtag, "Pending bitmaps: " + bitmapsPending.size());
        Log.i(logtag, "Groups: " + inFlight.size());
        for (FutureSet futures: inFlight.values()) {
            Log.i(logtag, "Group size: " + futures.size());
        }
    }

    /**
     * Get the application Context object in use by this NetworkProcessor instance
     * @return
     */
    public Context getContext() {
        return context;
    }

    static class FutureSet extends WeakHashMap<Future, Boolean> {
    }
    // maintain a list of futures that are in being processed, allow for bulk cancellation
    WeakHashMap<Object, FutureSet> inFlight = new WeakHashMap<Object, FutureSet>();

    private void addCookieMiddleware() {
        httpClient.insertMiddleware(cookieMiddleware = new CookieMiddleware(this));
    }

    /**
     * Get or put an item from the cache
     * @return
     */
    public FileCacheStore cache(String key) {
        return new FileCacheStore(this, responseCache.getFileCache(), key);
    }

    public FileCache getCache() {
        return responseCache.getFileCache();
    }

    /**
     * Get or put an item in the persistent store
     * @return
     */
    public FileCacheStore store(String key) {
        return new FileCacheStore(this, storeCache, key);
    }

    public FileCache getStore() {
        return storeCache;
    }

    public String getName() {
        return name;
    }

    /**
     * Get the Cookie middleware that is attached to the AsyncHttpClient instance.
     * @return
     */
    public CookieMiddleware getCookieMiddleware() {
        return cookieMiddleware;
    }

    public ConscryptMiddleware getConscryptMiddleware() {
        return conscryptMiddleware;
    }

    /**
     * Get the AsyncHttpClient object in use by this NetworkProcessor instance
     * @return
     */
    public AsyncHttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * Get the AsyncServer reactor in use by this NetworkProcessor instance
     * @return
     */
    public AsyncServer getServer() {
        return httpClient.getServer();
    }

    public class Config {
        public HttpLoader getHttpLoader() {
            return httpLoader;
        }

        public VideoLoader getVideoLoader() {
            return videoLoader;
        }

        public PackageIconLoader getPackageIconLoader() {
            return packageIconLoader;
        }

        public ContentLoader getContentLoader() {
            return contentLoader;
        }

        public FileLoader getFileLoader() {
            return fileLoader;
        }

        public ResponseCacheMiddleware getResponseCache() {
            return responseCache;
        }

        public SSLContext createSSLContext(String algorithm) throws NoSuchAlgorithmException {
            conscryptMiddleware.initialize();
            return SSLContext.getInstance(algorithm);
        }

        /**
         * Get the Gson object in use by this NetworkProcessor instance.
         * This can be used to customize serialization and deserialization
         * from java objects.
         * @return
         */
        public synchronized Gson getGson() {
            if (gson == null)
                gson = new Gson();
            return gson;
        }

        /**
         * Set the log level for all requests made by NetworkProcessor.
         * @param logtag
         * @param logLevel
         * @return
         */
        public Config setLogging(String logtag, int logLevel) {
            NetworkProcessor.this.logtag = logtag;
            NetworkProcessor.this.logLevel = logLevel;
            return this;
        }

        /**
         * Route all http requests through the given proxy.
         * @param host
         * @param port
         */
        public void proxy(String host, int port) {
            httpClient.getSocketMiddleware().enableProxy(host, port);
        }

        /**
         * Route all https requests through the given proxy.
         * Note that https proxying requires that the Android device has the appropriate
         * root certificate installed to function properly.
         * @param host
         * @param port
         */
        public void proxySecure(String host, int port) {
            httpClient.getSSLSocketMiddleware().enableProxy(host, port);
        }

        /**
         * Disable routing of http requests through a previous provided proxy
         */
        public void disableProxy() {
            httpClient.getSocketMiddleware().disableProxy();
        }

        /**
         * Disable routing of https requests through a previous provided proxy
         */
        public void disableSecureProxy() {
            httpClient.getSSLSocketMiddleware().disableProxy();
        }

        /**
         * Set the Gson object in use by this NetworkProcessor instance.
         * This can be used to customize serialization and deserialization
         * from java objects.
         * @param gson
         */
        public void setGson(Gson gson) {
            NetworkProcessor.this.gson = gson;
        }

        AsyncHttpRequestFactory asyncHttpRequestFactory = new AsyncHttpRequestFactory() {
            @Override
            public AsyncHttpRequest createAsyncHttpRequest(Uri uri, String method, Headers headers) {
                AsyncHttpRequest request = new AsyncHttpRequest(uri, method, headers);
                if (!TextUtils.isEmpty(userAgent))
                    request.getHeaders().set("User-Agent", userAgent);
                return request;
            }
        };

        public AsyncHttpRequestFactory getAsyncHttpRequestFactory() {
            return asyncHttpRequestFactory;
        }

        public Config setAsyncHttpRequestFactory(AsyncHttpRequestFactory asyncHttpRequestFactory) {
            this.asyncHttpRequestFactory = asyncHttpRequestFactory;
            return this;
        }

        public String userAgent() {
            return userAgent;
        }

        public Config userAgent(String userAgent) {
            NetworkProcessor.this.userAgent = userAgent;
            return this;
        }

        public void addLoader(int index, Loader loader) {
            loaders.add(index, loader);
        }
        public Config insertLoader(Loader loader) {
            loaders.add(0, loader);
            return this;
        }
        public Config addLoader(Loader loader) {
            loaders.add(loader);
            return this;
        }
        public List<Loader> getLoaders() {
            return loaders;
        }
    }

    public Config configure() {
        return config;
    }

    /**
     * Return the bitmap cache used by this NetworkProcessor instance
     * @return
     */
    public NetworkProcessorBitmapCache getBitmapCache() {
        return bitmapCache;
    }
}
