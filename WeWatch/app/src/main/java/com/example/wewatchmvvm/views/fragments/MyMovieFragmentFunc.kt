package com.example.wewatchmvvm.views.fragments

import com.example.wewatchmvvm.model.Movie

interface MyMovieFragmentFunc {
    fun onPressCard(id: Int)

    fun deleteMovie(id: Int, position: Int, list: List<Movie>)
}