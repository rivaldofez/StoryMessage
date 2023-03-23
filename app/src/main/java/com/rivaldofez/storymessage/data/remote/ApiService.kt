package com.rivaldofez.storymessage.data.remote

import com.rivaldofez.storymessage.data.remote.response.LoginResponse
import com.rivaldofez.storymessage.data.remote.response.RegisterResponse
import com.rivaldofez.storymessage.data.remote.response.StoriesResponse
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("login")
    suspend fun userLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("register")
    suspend fun userRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse


    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int?,
        @Query("size") size: Int?
    ): StoriesResponse

}