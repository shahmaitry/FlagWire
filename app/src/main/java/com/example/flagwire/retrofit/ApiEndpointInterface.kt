package com.example.flagwire.retrofit

import com.example.flagwire.model.category.CategoryResponse
import com.example.flagwire.model.profileData.ProfileDataResponse
import com.example.flagwire.model.registration.RegistrationResponse
import com.example.flagwire.model.todays_list.TodaysListResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiEndpointInterface {

    @POST("data/2.5/weather")
    fun get_open_wether_data(@Query("q") city: String ,@Query("appid") App_id: String ): Call<ResponseBody>

    @Headers("Apikey: Mobile")
    @FormUrlEncoded
    @POST("register")
    fun registration(@FieldMap queryMap: Map<String, String>): Call<ResponseBody>

    @Headers("Apikey: Mobile")
    @FormUrlEncoded
    @POST("login")
    fun login(@FieldMap queryMap: Map<String, String>): Call<ResponseBody>

    @Headers("Apikey: Mobile")
    @FormUrlEncoded
    @POST("forgetPassword")
    fun forget_password(@FieldMap queryMap: Map<String, String>): Call<ResponseBody>

    @Headers("Apikey: Mobile")
    @FormUrlEncoded
    @POST("verifyOpt")
    fun verify_account(@FieldMap queryMap: Map<String, String>): Call<ResponseBody>

    @Headers("Apikey: Mobile")
    @FormUrlEncoded
    @POST("purchase")
    fun purchase(@FieldMap queryMap: Map<String, String>): Call<ResponseBody>

    @Headers("Apikey: Mobile")
    @GET("TodayFlagList")
    fun todays_flag_list(): Call<TodaysListResponse>

    @Headers("Apikey: Mobile")
    @GET("UpcomingFlagList")
    fun upcoming_flag_list(): Call<TodaysListResponse>

    @Headers("Apikey: Mobile")
    @GET("CategoryList")
    fun category(): Call<CategoryResponse>

    @Headers("Apikey: Mobile")
    @GET("profileData")
    fun profileData(): Call<ProfileDataResponse>


    @Headers("Apikey: Mobile")
    @GET("userProfile/{profile_id}")
    fun get_profile_data(@Path("profile_id")id:String): Call<ResponseBody>


    @Multipart
    @POST("editProfile")
    @Headers("Apikey:Mobile")
    fun updateProfile(
        @Part file: MultipartBody.Part?,
        @Part("user_id") customer_id: RequestBody?,
        @Part("fullname") name: RequestBody?
    ): Call<ResponseBody>


}