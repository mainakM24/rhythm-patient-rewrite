package com.example.rhythmapp.api;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public static final String BASE_URL = "https://apex.oracle.com/pls/apex/wcm/";
    public static Retrofit retrofit = null;

    public static ApiService getApiService(){

        Interceptor headerInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                Request newRequest = originalRequest.newBuilder()
                        .header("Accept", "application/json")
                        .header("User-Agent", "Mozilla/5.0")
                        .build();
                return chain.proceed(newRequest);
            }
        };

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(headerInterceptor)
                .build();

        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
