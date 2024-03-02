package com.example.wewatchmvvm.database.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.wewatchmvvm.database.MovieDatabase
import com.example.wewatchmvvm.database.model.MovieItem
import com.example.wewatchmvvm.model.Movie

class MovieRepository(context: Context) {
    private var movieDao: MovieDao
    private var movieDatabase: MovieDatabase? = null
    private var cachedMovieDao: CachedMoveDao

    init {
        this.movieDatabase = MovieDatabase.createDatabase(context)
        this.movieDao = movieDatabase?.getMovieDao()!!
        this.cachedMovieDao = movieDatabase?.getCachedMovieDao()!!
    }

    suspend fun insert(movieItem: MovieItem) = movieDao.upsert(movieItem)

    suspend fun delete(movieItem: MovieItem) = movieDao.delete(movieItem)

    fun getMovie(id: Int): LiveData<MovieItem> = movieDao.getMovie(id)

    fun getAllMovies(): LiveData<List<MovieItem>> = movieDao.getAllMovieItems()

//    cached functions
    suspend fun insertCachedMovie(movies: List<Movie>) = cachedMovieDao.insertCachedMovie(movies)

    fun getAllCachedMovies() = cachedMovieDao.getAllCachedMovie()

    suspend fun deleteCachedMovies() = cachedMovieDao.deleteCachedMovies()

    suspend fun deleteCachedMovie(id: Int) = cachedMovieDao.deleteCachedMovie(id)

    fun getCachedMovie(id: Int) = cachedMovieDao.getCachedMovie(id)

    fun getMyCachedMovies(id: Int) = cachedMovieDao.getMyCachedMovie(id)

}