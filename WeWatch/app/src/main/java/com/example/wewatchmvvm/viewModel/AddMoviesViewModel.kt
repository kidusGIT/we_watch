package com.example.wewatchmvvm.viewModel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wewatchmvvm.MainActivity
import com.example.wewatchmvvm.model.Movie
import com.example.wewatchmvvm.repository.data.MovieApiCall
import com.example.wewatchmvvm.database.repository.MovieRepository as CacheRepository
import com.example.wewatchmvvm.repository.repository.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

class AddMoviesViewModel(moviesApiCall: MovieApiCall, private val cachedRepository: CacheRepository): ViewModel() {
    private val TAG = "AddMoviesViewModel"
    
    var uri: Uri? = null

    private val rep = MovieRepository(moviesApiCall)

    private val hasMovieCreated = MutableLiveData<Boolean>()

    private val isThereConnection = MutableLiveData<Int>()
    private val movie = MutableLiveData<Movie>()
    var editMovie: Movie? = null

    fun getIsThereConnection(): LiveData<Int> = isThereConnection

    fun getMovieLiveData(): LiveData<Movie> = movie
    fun getHasMovieAdd(): LiveData<Boolean> = hasMovieCreated
    fun getMovie(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val response = rep.getMovies(id)
            movie.postValue(response.body())
            editMovie = response.body()
            isThereConnection.postValue(MainActivity.HAS_INTERNET)
        } catch (ex: Exception) {
            isThereConnection.postValue(MainActivity.NO_INTERNET)
        }
    }

    fun editMovie(context: Context, movie: Movie, token: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val api = rep.editMovie(movie, context, uri, token)
            if (api.isSuccessful) {
                hasMovieCreated.postValue(true)
            } else {
                hasMovieCreated.postValue(false)
            }
        } catch (ex: SocketTimeoutException) {
            Log.d(TAG, "getUser: ${ex.stackTraceToString()}")
            isThereConnection.postValue(MainActivity.NO_INTERNET)
        } catch (ex: Exception) {
        }
    }
    fun createMovie(context: Context, movie: Movie, token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val api = rep.addMovies(movie, context, uri, token)
                if (api.isSuccessful) {
                    hasMovieCreated.postValue(true)
                } else {
                    hasMovieCreated.postValue(false)
                }
            } catch (ex: SocketTimeoutException) {
                Log.d(TAG, "getUser: ${ex.stackTraceToString()}")
                isThereConnection.postValue(MainActivity.NO_INTERNET)
            } catch (ex: Exception) {
            }
        }
    }

//    cached
    fun getCachedMovie(id: Int) = cachedRepository.getCachedMovie(id)
}