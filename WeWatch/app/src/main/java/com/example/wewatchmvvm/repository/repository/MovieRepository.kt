package com.example.wewatchmvvm.repository.repository

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.wewatchmvvm.model.Movie
import com.example.wewatchmvvm.repository.data.MovieApiCall
import com.example.wewatchmvvm.views.fragments.AddMovieFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

class MovieRepository(private val apiCall: MovieApiCall) {
    private val TAG = "MovieRepository"

//    get all Movies
    suspend fun getAllMovies() = apiCall.getAllMovies()

//    get my Movies
    suspend fun getMyMovies(token: String) = apiCall.getMyMovies(token)

//    get selected Movie
    suspend fun getMovies(id: Int) = apiCall.getSelectedMovies(id)

//    create a Movie
    suspend fun addMovies(
        movie: Movie,
        context: Context,
        uri: Uri?,
        token: String
    ): Response<Movie> {
        val map: MutableMap<String, RequestBody?> = mutableMapOf()

        val title = movie.title?.let { createPartFromString(it) }
        val description = movie.description?.let { createPartFromString(it) }
        val userId = createPartFromString(movie.user_id.toString())

        map["title"] = title!!
        map["description"] = description
        map["user_id"] = userId

        if (uri == null) {
            return apiCall.createMovies(partMap = map, token = token)
        }

        val imagePath = getImagePath(uri!!, context)
        val file = File(imagePath)

        val requestFile: RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val multipartImage = MultipartBody.Part.createFormData("cover", file.name, requestFile)

        return apiCall.createMovies(partMap = map, file = multipartImage, token = token)
    }

//    edit a Movie
    suspend fun editMovie(movie: Movie, context: Context, uri: Uri?, token: String): Response<Movie> {
        val map: MutableMap<String, RequestBody?> = mutableMapOf()
        val title = movie.title?.let { createPartFromString(it) }
        val description = movie.description?.let { createPartFromString(it) }
        val cover = movie.cover_url?.let { createPartFromString(it) }
        val userId = createPartFromString(movie.user_id.toString())

        map["title"] = title!!
        map["description"] = description
        map["user_id"] = userId

        if (uri == null) {
            return apiCall.editMovie(partMap = map, token = token, id = movie.id!!)
        }

        val imagePath = getImagePath(uri!!, context)
        val file = File(imagePath)

        val requestFile: RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val multipartImage = MultipartBody.Part.createFormData("cover", file.name, requestFile)

        return apiCall.editMovie(partMap = map, file = multipartImage, token = token, id = movie.id!!)
    }

//    delete a Movie
    suspend fun deleteMovie(token: String, id: Int) = apiCall.deleteMovie(token, id)

    private fun createPartFromString(stringData: String) = stringData.toRequestBody("text/plain".toMediaTypeOrNull())

    private fun getImagePath(uri: Uri, context: Context): String? {
        var imagePath: String? = null
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(uri!!, null, null, null, null)
            val nameIndex = cursor?.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            cursor?.moveToFirst()
            imagePath = cursor?.getString(nameIndex!!)
        } catch (ex: Exception) {
            Log.d(AddMovieFragment.TAG, "getImagePath: ${ex.stackTraceToString()}")
        } finally {
            cursor?.close()
        }
        return imagePath
    }
}