package com.wishbook.catalog.rest;


import androidx.annotation.Keep;

import com.google.gson.JsonObject;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface WishbookClient {

    @Keep
    @GET()
    Call<ResponseBody> getData(@Url String url, @QueryMap Map<String, String> options);

    @Keep
    @GET
    Call<ResponseBody> getData(@Url String url);

    @Keep
    @POST
    Call<ResponseBody> postData(@Url String url, @Body JsonObject jsonObject);

    @Keep
    @POST
    Call<ResponseBody> postData(@Url String url);

    @Keep
    @DELETE
    Call<ResponseBody> deleteData(@Url String url);

    @Keep
    @Multipart
    @POST
    Call<ResponseBody> postImageUpload(@Url String url, @Part MultipartBody.Part image,  @PartMap() Map<String, RequestBody> partMap);
}
