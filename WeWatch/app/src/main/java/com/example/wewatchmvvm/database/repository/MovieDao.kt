package com.example.wewatchmvvm.database.repository

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.wewatchmvvm.database.model.MovieItem

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: MovieItem): Unit

    @Delete
    suspend fun delete(item: MovieItem): Unit

    @Query("SELECT * FROM movie_items WHERE movie_id = :id")
    fun getMovie(id: Int): LiveData<MovieItem>

    @Query("SELECT * FROM movie_items")
    fun getAllMovieItems(): LiveData<List<MovieItem>>

}