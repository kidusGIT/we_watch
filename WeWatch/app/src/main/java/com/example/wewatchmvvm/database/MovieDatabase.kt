package com.example.wewatchmvvm.database

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.example.wewatchmvvm.database.model.MovieItem
import com.example.wewatchmvvm.database.repository.CachedMoveDao
import com.example.wewatchmvvm.database.repository.MovieDao
import com.example.wewatchmvvm.model.Movie

@Database(entities = [(MovieItem::class), (Movie::class)], version = 10, exportSchema = false)
abstract class MovieDatabase: RoomDatabase() {
    abstract fun getMovieDao(): MovieDao
    abstract fun getCachedMovieDao(): CachedMoveDao

    companion object {
        private var instance: MovieDatabase? = null
        private val LOCK = Any()

        fun createDatabase(context: Context): MovieDatabase? {
            if (instance == null) {
                synchronized(LOCK) {
                    if (instance == null) {
                        instance = Room.databaseBuilder(
                            context,
                            MovieDatabase::class.java,
                            "MovieDb.db"
                        ).fallbackToDestructiveMigration()
                            .build()
                    }
                }

            }

            return instance
        }

    }

    override fun clearAllTables() {
        TODO("Not yet implemented")
    }

    override fun createInvalidationTracker(): InvalidationTracker {
        TODO("Not yet implemented")
    }

    override fun createOpenHelper(config: DatabaseConfiguration): SupportSQLiteOpenHelper {
        TODO("Not yet implemented")
    }

}