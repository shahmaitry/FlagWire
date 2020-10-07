package com.example.flagwire.retrofit

import com.example.flagwire.model.getdata.GetdataResponse
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiEndpointInterface {

    @POST("data/2.5/weather")
    fun get_data(@Query("q") city: String ,@Query("appid") App_id: String ): Call<GetdataResponse>

}