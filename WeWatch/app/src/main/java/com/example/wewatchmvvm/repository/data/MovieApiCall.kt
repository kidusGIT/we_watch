package com.example.wewatchmvvm.repository.data

import android.content.ClipDescription
import com.example.wewatchmvvm.model.Movie
import com.example.wewatchmvvm.model.MovieInfo
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path

interface MovieApiCall {
    //    get all Movies
    @GET("movies")
    suspend fun getAllMovies(): Response<List<Movie>>

//    get my Movies
    @GET("user-movies")
    suspend fun getMyMovies(@Header("Token") token: String): Response<List<Movie>>

//    get selected Movies
    @GET("movies/{id}")
    suspend fun getSelectedMovies(@Path("id") id: Int): Response<Movie>

//    create a Movie
    @Multipart
    @POST("movies")
    suspend fun createMovies(
        @Header("Token") token: String,
        @PartMap() partMap: MutableMap<String, RequestBody?>,
        @Part file: MultipartBody.Part? = null
    ): Response<Movie>

//    edit a Movie
    @Multipart
    @PUT("movies/{id}")
    suspend fun editMovie(
        @Path("id") id: Int,
        @Header("Token") token: String,
        @PartMap() partMap: MutableMap<String, RequestBody?>,
        @Part file: MultipartBody.Part? = null
    ): Response<Movie>

//    delete a Movie
    @DELETE("movies/{id}")
    suspend fun deleteMovie(
        @Header("Token") token: String,
        @Path("id") id: Int
    ): Response<MovieInfo>
}