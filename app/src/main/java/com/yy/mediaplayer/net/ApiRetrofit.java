package com.yy.mediaplayer.net;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiRetrofit {
    private static ApiRetrofit mApiRetrofit;
    private Retrofit retrofit;
    private ApiServer apiServer;
    private static final int DEFAULT_TIMEOUT = 15;
    public static String mBaseUrl = BaseContent.baseUrl;

    public ApiRetrofit() {
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();

        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder
                //.cookieJar(new CookieManger(App.getContext()))
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(logInterceptor);

        retrofit = new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                //支持RxJava2
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClientBuilder.build())
                .build();


        apiServer = retrofit.create(ApiServer.class);
    }


    public static ApiRetrofit getInstance() {
        if (mApiRetrofit == null) {
            synchronized (Object.class) {
                if (mApiRetrofit == null) {
                    mApiRetrofit = new ApiRetrofit();
                }
            }
        }
        return mApiRetrofit;
    }

    public ApiServer getApiService() {
        return apiServer;
    }
}
