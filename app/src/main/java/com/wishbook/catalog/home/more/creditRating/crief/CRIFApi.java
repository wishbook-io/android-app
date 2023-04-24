package com.wishbook.catalog.home.more.creditRating.crief;

import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.BuildConfig;
import com.wishbook.catalog.rest.WishbookClient;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CRIFApi {
    private static CRIFCreds creds;


    public static boolean isStaging = true;

    private static String BASE_URL = Application_Singleton.isStagging() ? "https://test.crifhighmark.com/" : "https://cir.crifhighmark.com/";
    public static String ENCRYPT_URL = BASE_URL + "Inquiry/do.getSecureService/encrypt";
    public static String SERVICE_URL = BASE_URL + "Inquiry/do.getSecureService/service";
    public static String RESPONSE_URL = BASE_URL + "Inquiry/do.getSecureService/response";
    private static String INITIATE_INQUIRY_URL = BASE_URL + "Inquiry/do.getSecureService/response";



    // Testing Credential

   /* public static String KEY = "782B62D78F950A61E30447965B8B45450A9ABA5E";
    public static String APP_ID = "X@Wqxuwz#nElygI3!";
    public static String MERCHANT_ID = "DTC0000017";
    public static String productCode = "BBC_CONSUMER_SCORE#85#2.0";
    public static String customerId = "DTC0000017";
    public static String password = "0DC8E5D8BDA6DC81C181AF658C01D5E2363D93ED";
    public static String userId = "chm_bbc_uat@wishbook.io";*/

    // Live Credential



    public static String KEY = Application_Singleton.isStagging()?"782B62D78F950A61E30447965B8B45450A9ABA5E": "C9248270FC1F8E4FEE3366E933952F1F30A7C364";
    public static String APP_ID = Application_Singleton.isStagging()?"X@Wqxuwz#nElygI3!":"K#wrmpeg!pGnqiY4@";
    public static String MERCHANT_ID = Application_Singleton.isStagging()?"DTC0000017":"DTC0000025";
    public static String productCode = "BBC_CONSUMER_SCORE#85#2.0";
    public static String customerId = Application_Singleton.isStagging()?"DTC0000017":"DTC0000025";
    public static String password = Application_Singleton.isStagging()?"0DC8E5D8BDA6DC81C181AF658C01D5E2363D93ED":"AEE7EC3D6C2BB3AD158D03BFB382374C5A269378";
    public static String userId = Application_Singleton.isStagging()?"chm_bbc_uat@wishbook.io":"chm_bbc_prod@wishbok.com";




    public static String BUREAU_STATUS_STAGE1 = "Verification Failed";
    public static String BUREAU_STATUS_STAGE2 = "Verification But Miss";
    public static String BUREAU_STATUS_STAGE3 = "Hit";

    public static final int READ_TIMEOUT = Application_Singleton.isStagging()?20:2;
    public static final int WRITE_TIMEOUT = Application_Singleton.isStagging()?20:2;

    public void init(CRIFCreds creds) {
        CRIFApi.creds = creds;
    }

    public static CRIFCreds getCreds() {
        return creds;
    }


    public WishbookClient initRetrofit() {
        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        OkHttpClient.Builder httpClient =
                new OkHttpClient.Builder();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

        CrifInterceptor tokeninterceptor =
                new CrifInterceptor(generateHeader());
        httpClient.connectTimeout(READ_TIMEOUT, TimeUnit.MINUTES);
        httpClient.readTimeout(READ_TIMEOUT, TimeUnit.MINUTES);
        httpClient.writeTimeout(WRITE_TIMEOUT, TimeUnit.MINUTES);
        httpClient.addInterceptor(tokeninterceptor);
        if (BuildConfig.DEBUG) {
            httpClient.addInterceptor(logging);
        }
        builder.client(httpClient.build());
        retrofit = builder.build();
        WishbookClient service = retrofit.create(WishbookClient.class);
        return service;
    }

    public Headers generateHeader() {
        Headers.Builder builder = new Headers.Builder();
        Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();
        HashMap<String, String> hashMap = Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(creds), type);
        Set set = hashMap.entrySet();
        Iterator iterator2 = set.iterator();
        while (iterator2.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator2.next();
            String value = entry.getValue().toString().replaceAll("\n", "");
            //Log.d("CRIEF", "generateHeader: "+entry.getKey() + "Value=>"+entry.getValue());
            builder.add(entry.getKey().toString(), value);
        }
        Headers headers = Headers.of(hashMap);
        return headers;
    }

    public class CrifInterceptor implements Interceptor {
        private Headers headers;

        public CrifInterceptor(Headers headers) {
            this.headers = headers;
        }

        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            Request.Builder builder = original.newBuilder()
                    .headers(headers);
            Request request = builder.build();
            return chain.proceed(request);
        }
    }
}
