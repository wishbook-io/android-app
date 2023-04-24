package com.wishbook.catalog.rest;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.BuildConfig;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetroFitServiceGenerator {

    private static Context context;

    public static final String CACHE_CONTROL = "Cache-Control";
    public static final int MAX_AGE = 7; //7 mins
    public static final int MAX_STALE = 7; //7 days
    public static final int READ_TIMEOUT = 90;
    public static final int WRITE_TIMEOUT = 300; // second

    public RetroFitServiceGenerator(Context context) {
        this.context = context;
        if(Application_Singleton.baseClient == null){
            Application_Singleton.baseClient = new OkHttpClient();
        }

    }


    private Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(URLConstants.APP_URL)
                    .addConverterFactory(GsonConverterFactory.create());
    private Retrofit.Builder builderGeneric =
            new Retrofit.Builder()
                    .baseUrl(URLConstants.APP_URL)
                    .addConverterFactory(GsonConverterFactory.create());


    private Retrofit retrofit = builder.build();

    private Retrofit retrofitGeneric = builderGeneric.build();


    private ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
            .tlsVersions(TlsVersion.TLS_1_2)
            .cipherSuites(
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                    CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                    CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
            .build();




//    private OkHttpClient.Builder httpClient =
//            new OkHttpClient.Builder();



    public static HttpLoggingInterceptor logging = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);


    public static void getTokenNReload() {



    }


    public <S> S createService(Class<S> serviceClass) {

        OkHttpClient.Builder httpClient = Application_Singleton.baseClient
                                            .newBuilder();


        if(Application_Singleton.Token.equals("") && Application_Singleton.Token.isEmpty()){

        }
        else {
            AuthenticationInterceptor tokeninterceptor =
                    new AuthenticationInterceptor(Application_Singleton.Token);


            httpClient.addInterceptor(tokeninterceptor);
        }
        if (BuildConfig.DEBUG) {
            // do something for a debug build
            httpClient.addInterceptor(logging);

        }


               /* //RETROFIT HTTPS HANDSHAKE ERROR REOLVE
                httpClient.connectionSpecs(Collections.singletonList(spec));*/

        //common error toast bar
        httpClient.addInterceptor(provideErrorInterceptor());
        httpClient.connectTimeout(READ_TIMEOUT,TimeUnit.SECONDS);
        httpClient.readTimeout(READ_TIMEOUT,TimeUnit.SECONDS);
        httpClient.writeTimeout(WRITE_TIMEOUT,TimeUnit.SECONDS);

        /* for Cache */
        httpClient.addInterceptor(provideOfflineCacheInterceptor(MAX_STALE));
        httpClient.addInterceptor(userAgentInterceptor());
        httpClient.addNetworkInterceptor(provideCacheInterceptor(MAX_AGE));

        httpClient.cache(provideCache());
        /* for cache end */

        builder.client(httpClient.build());
        retrofit = builder.build();



        return retrofit.create(serviceClass);
    }


    public <S> S updateService(Class<S> serviceClass) {

        OkHttpClient.Builder httpClient = Application_Singleton.baseClient
                .newBuilder();

        if (!TextUtils.isEmpty(Application_Singleton.Token)) {
            AuthenticationInterceptor tokeninterceptor =
                    new AuthenticationInterceptor(Application_Singleton.Token);

            if (!httpClient.interceptors().contains(tokeninterceptor)) {
                httpClient.addInterceptor(tokeninterceptor);

                if (BuildConfig.DEBUG) {
                    // do something for a debug build
                    httpClient.addInterceptor(logging);
                }

              /*  //RETROFIT HTTPS HANDSHAKE ERROR REOLVE
                httpClient.connectionSpecs(Collections.singletonList(spec));
*/
                //common error toast bar
                httpClient.addInterceptor(provideErrorInterceptor());
                httpClient.connectTimeout(READ_TIMEOUT,TimeUnit.SECONDS);
                httpClient.readTimeout(READ_TIMEOUT,TimeUnit.SECONDS);
                httpClient.writeTimeout(WRITE_TIMEOUT,TimeUnit.SECONDS);

                /* for Cache */
                httpClient.addInterceptor(forceNetworkCache());
                httpClient.addInterceptor(userAgentInterceptor());
                httpClient.addNetworkInterceptor(provideCacheInterceptor(MAX_AGE));

                httpClient.cache(provideCache());
                /* for cache end */

                builder.client(httpClient.build());
                retrofit = builder.build();
            }
        } else
            getTokenNReload();

        return retrofit.create(serviceClass);
    }


    public <S> S createLongCacheService(Class<S> serviceClass) {

        OkHttpClient.Builder httpClient = Application_Singleton.baseClient
                .newBuilder();

        if(Application_Singleton.Token.equals("") && Application_Singleton.Token.isEmpty()){

        }
        else {
            AuthenticationInterceptor tokeninterceptor =
                    new AuthenticationInterceptor(Application_Singleton.Token);


            httpClient.addInterceptor(tokeninterceptor);
        }
        if (BuildConfig.DEBUG) {
            // do something for a debug build
            httpClient.addInterceptor(logging);
        }

      /*  //RETROFIT HTTPS HANDSHAKE ERROR REOLVE
        httpClient.connectionSpecs(Collections.singletonList(spec));
*/
        //common error toast bar
        httpClient.addInterceptor(provideErrorInterceptor());
        httpClient.addInterceptor(userAgentInterceptor());
        httpClient.connectTimeout(READ_TIMEOUT,TimeUnit.SECONDS);
        httpClient.readTimeout(READ_TIMEOUT,TimeUnit.SECONDS);
        httpClient.writeTimeout(WRITE_TIMEOUT,TimeUnit.SECONDS);

        /* for Cache */
        httpClient.addInterceptor(provideOfflineCacheInterceptor(MAX_STALE));
        httpClient.addNetworkInterceptor(provideCacheInterceptor(60));

        httpClient.cache(provideCache());
        /* for cache end */

        builder.client(httpClient.build());
        retrofit = builder.build();



        return retrofit.create(serviceClass);
    }

    public <S> S createServiceNoCache(
            Class<S> serviceClass) {

        OkHttpClient.Builder httpClient = Application_Singleton.baseClient
                .newBuilder();

        if (!TextUtils.isEmpty(Application_Singleton.Token)) {
            AuthenticationInterceptor tokeninterceptor =
                    new AuthenticationInterceptor(Application_Singleton.Token);

            if (!httpClient.interceptors().contains(tokeninterceptor)) {
                httpClient.addInterceptor(tokeninterceptor);

                if (BuildConfig.DEBUG) {
                    // do something for a debug build
                   // httpClient.addInterceptor(logging);
                }

              /*  //RETROFIT HTTPS HANDSHAKE ERROR REOLVE
                httpClient.connectionSpecs(Collections.singletonList(spec));
*/
                //common error toast bar
                httpClient.addInterceptor(provideErrorInterceptor());
                httpClient.connectTimeout(READ_TIMEOUT,TimeUnit.SECONDS);
                httpClient.readTimeout(READ_TIMEOUT,TimeUnit.SECONDS);
                httpClient.writeTimeout(WRITE_TIMEOUT,TimeUnit.SECONDS);

                /* for Cache */
                httpClient.addInterceptor(provideOfflineCacheInterceptor(0));
                httpClient.addInterceptor(userAgentInterceptor());
                httpClient.addNetworkInterceptor(provideCacheInterceptor(0));
                httpClient.cache(provideCache());
                /* for cache end */

                builder.client(httpClient.build());
                retrofit = builder.build();
            }
        } else
            getTokenNReload();

        return retrofit.create(serviceClass);
    }



    private Cache provideCache() {
        Cache cache = null;
        try {
            cache = new Cache(new File(context.getCacheDir(), "http-cache"),
                    10 * 1024 * 1024); // 10 MB
        } catch (Exception e) {
            Log.e("Error", "Could not create Cache File!");
        }
        return cache;
    }


    public Cache deleteRetrofitCache() {
        Cache cache = null;
        try {
            cache = new Cache(new File(context.getCacheDir(), "http-cache"),
                    10 * 1024 * 1024); // 10 MB
            cache.delete();
        } catch (Exception e) {
            Log.e("Error", "Could not create Cache File!");
        }
        return cache;
    }




    public Interceptor provideCacheInterceptor(final int minutes) {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());

                // re-write response header to force use of cache
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxAge(minutes, TimeUnit.MINUTES)
                        .build();

                return response.newBuilder()
                        .header(CACHE_CONTROL, cacheControl.toString())
                        .build();
            }
        };
    }

    public void backgroundThreadShortToast(final Context context,
                                           final String msg) {
        /*if (context != null && msg != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    if (BuildConfig.DEBUG)
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

                }
            });
        }*/
    }


    public Interceptor provideErrorInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());
                if (!response.isSuccessful()) {
                    if (BuildConfig.DEBUG)
                        backgroundThreadShortToast(context, "Status : " + response.code() + " Msg: " + response.message());
                } else {

                }return response;
            }
        };
    }
    public Interceptor userAgentInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                request = request.newBuilder()
                        .header("User-Agent", getDefaultUserAgent())
                        .build();

                return chain.proceed(request);
            }
        };
    }

    public Interceptor provideOfflineCacheInterceptor(final int minutes) {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
//                if ( !AdeptAndroid.hasNetwork() )
//                {
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxAge(MAX_AGE, TimeUnit.MINUTES)
                        .build();

                if (!StaticFunctions.isOnline(context)) {
                    cacheControl = new CacheControl.Builder()
                            .maxStale(MAX_STALE, TimeUnit.DAYS)
                            .build();
                }
                request = request.newBuilder()
                        .cacheControl(cacheControl)
                        .build();
//                }

                return chain.proceed(request);
            }
        };
    }

    public Interceptor forceNetworkCache() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_NETWORK)
                        .build();
//                }

                return chain.proceed(request);
            }
        };
    }


    protected static String getDefaultUserAgent() {
        String agent = System.getProperty("http.agent");
        return agent != null ? agent : ("Java" + System.getProperty("java.version"));
    }

}