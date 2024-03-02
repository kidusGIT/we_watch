package com.example.wewatchmvvm.viewModel

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wewatchmvvm.MainActivity
import com.example.wewatchmvvm.model.Movie
import com.example.wewatchmvvm.repository.data.MovieApiCall
import com.example.wewatchmvvm.repository.repository.MovieRepository
import com.example.wewatchmvvm.database.repository.MovieRepository as CachedRepository
import com.example.wewatchmvvm.views.fragments.MyMovieFragmentFunc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.text.FieldPosition

class MyMoviesViewModel(moviesApiCall: MovieApiCall, private val cachedRepository: CachedRepository): ViewModel() {
    private val TAG = "MyMoviesViewModel"

    private val repository = MovieRepository(moviesApiCall)

    private val myMovies = MutableLiveData<List<Movie>>()
    private val isThereConnection = MutableLiveData<Int>()
    private val positionLiveData = MutableLiveData<Int>()

    fun getIsThereConnection(): LiveData<Int> = isThereConnection

    fun getMyMovies(): LiveData<List<Movie>> = myMovies

    fun getPositionLiveData(): LiveData<Int> = positionLiveData

    fun getMyMovies(token: String) = viewModelScope.launch {
        try {
            val movies = repository.getMyMovies(token)
            myMovies.postValue(movies.body())

        } catch (ex: Exception) {
            isThereConnection.postValue(MainActivity.NO_INTERNET)
        }
    }

    fun deleteMovie(token: String, id: Int, position: Int, list: ArrayList<Movie>) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val response = repository.deleteMovie(token, id)
            if(response.isSuccessful) {
                positionLiveData.postValue(position)
                deleteCachedMovie(id)
                list.removeAt(position)
            }
        } catch (ex: SocketTimeoutException) {
            isThereConnection.postValue(MainActivity.NO_INTERNET)
        } catch (ex: Exception) {
        }
    }

//    cached movie
    fun getMyCachedMovies(id: Int) = cachedRepository.getMyCachedMovies(id)

    private fun deleteCachedMovie(id: Int) = viewModelScope.launch {
        cachedRepository.deleteCachedMovie(id)
    }

}