package com.example.wewatchmvvm.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wewatchmvvm.database.model.MovieItem
import com.example.wewatchmvvm.database.repository.MovieRepository
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: MovieRepository): ViewModel() {

    var movieItem: MovieItem? = null

    fun insert(movieItem: MovieItem) = viewModelScope.launch {
        repository.insert(movieItem)
    }

    fun delete(movieItem: MovieItem) = viewModelScope.launch {
        repository.delete(movieItem)
    }

    fun getMovie(id: Int): LiveData<MovieItem> = repository.getMovie(id)

    fun getAllMovies(): LiveData<List<MovieItem>> = repository.getAllMovies()

}