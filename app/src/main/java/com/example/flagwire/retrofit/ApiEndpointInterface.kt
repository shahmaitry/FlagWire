package com.example.flagwire.retrofit

import com.example.flagwire.model.getdata.GetdataResponse
import com.example.flagwire.model.registration.RegistrationResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiEndpointInterface {

    /*@POST("data/2.5/weather")
    fun get_data(@Query("q") city: String ,@Query("appid") App_id: String ): Call<GetdataResponse>*/

    @Headers("Apikey: Mobile")
    @FormUrlEncoded
    @POST("register")
    fun registration(@FieldMap queryMap: Map<String, String>): Call<RegistrationResponse>

}