package com.example.flagwire.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitBuilder private constructor() //constructer
{

    val retrofit: ApiEndpointInterface
        get() {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val httpClient = OkHttpClient.Builder()
            httpClient.connectTimeout(10, TimeUnit.MINUTES)
            httpClient.readTimeout(10, TimeUnit.MINUTES)
            httpClient.writeTimeout(10, TimeUnit.MINUTES)
            httpClient.addInterceptor(logging)
            val retrofit = Retrofit.Builder()
                .baseUrl(AppUtils.BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
            return retrofit.create(ApiEndpointInterface::class.java)
        }


    companion object {
        val instance = RetrofitBuilder()
    }

}