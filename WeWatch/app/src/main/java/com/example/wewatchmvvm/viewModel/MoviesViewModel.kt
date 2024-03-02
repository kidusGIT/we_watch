package com.example.wewatchmvvm.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wewatchmvvm.MainActivity
import com.example.wewatchmvvm.model.Movie
import com.example.wewatchmvvm.repository.data.MovieApiCall
import com.example.wewatchmvvm.repository.repository.MovieRepository
import com.example.wewatchmvvm.database.repository.MovieRepository as CachedMovieRepository
import kotlinx.coroutines.launch


class MoviesViewModel(movieApiCall: MovieApiCall, private val cachedRepository: CachedMovieRepository): ViewModel() {
    private val TAG = "MoviesViewModel"

    private val repository = MovieRepository(movieApiCall)

    private val moviesList = MutableLiveData<List<Movie>>()
    private val isThereConnection = MutableLiveData<Int>()

    fun getIsThereConnection(): LiveData<Int> = isThereConnection
    fun getMoviesList(): LiveData<List<Movie>> = moviesList

    fun getMovies() = viewModelScope.launch {
        try {
            val movies = repository.getAllMovies()
            moviesList.postValue(movies.body())
            isThereConnection.postValue(MainActivity.HAS_INTERNET)
        } catch (ex: Exception) {
            isThereConnection.postValue(MainActivity.NO_INTERNET)
        }
    }

    fun getAllCachedMovies(): LiveData<List<Movie>> = cachedRepository.getAllCachedMovies()

    fun deleteCachedMovies() = viewModelScope.launch {
        cachedRepository.deleteCachedMovies()
    }

    fun insertAllCachedMovie(movies: List<Movie>) = viewModelScope.launch {
        cachedRepository.insertCachedMovie(movies)
    }
}