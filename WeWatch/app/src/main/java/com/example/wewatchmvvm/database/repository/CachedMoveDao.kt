package com.example.wewatchmvvm.database.repository

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.wewatchmvvm.model.Movie

@Dao
interface CachedMoveDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCachedMovie(movies: List<Movie>)

    @Query("SELECT * FROM cached_movies")
    fun getAllCachedMovie(): LiveData<List<Movie>>

    @Query("SELECT * FROM cached_movies WHERE id = :id")
    fun getCachedMovie(id: Int): LiveData<Movie>

    @Query("DELETE FROM cached_movies WHERE id = :id")
    suspend fun deleteCachedMovie(id: Int)

    @Query("DELETE FROM cached_movies")
    suspend fun deleteCachedMovies()

    @Query("SELECT * From cached_movies WHERE user_id = :id")
    fun getMyCachedMovie(id: Int): LiveData<List<Movie>>

}