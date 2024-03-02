package com.example.wewatchmvvm.repository.data

import com.example.wewatchmvvm.model.User
import com.example.wewatchmvvm.model.UserInfo
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface UserApiCall {

//    Signing up a User
    @POST("")
    fun signIn(@Body user: User): Call<User>

    @FormUrlEncoded
    @POST("auth/login")
    fun logInAsync(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<UserInfo>

    @FormUrlEncoded
    @PUT("auth/edit")
    suspend fun editUser(
        @Header("Token") token: String,
        @Field("full_name") fullName: String,
        @Field("username") username: String,
        @Field("bio") bio: String?
    ): Response<User>

    @FormUrlEncoded
    @POST("auth")
    suspend fun addUser(
        @Field("full_name") fullName: String,
        @Field("username") username: String,
        @Field("bio") bio: String?,
        @Field("password") password: String,
    ): Response<UserInfo>

    @GET("auth/user")
    suspend fun getUser(@Header("Token") token: String): Response<User>

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<UserInfo>

}