package com.example.wewatchmvvm.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "cached_movies")
data class Movie(
    @ColumnInfo(name = "cover_url") val cover_url: String? = null,
    @ColumnInfo(name = "id") var id: Int? = null,
    @ColumnInfo(name ="created_at")  val created_at: String? = null,
    @ColumnInfo(name ="description") var description: String? = null,
    @ColumnInfo(name ="title") var title: String? = null,
    @ColumnInfo(name ="updated_at") val updated_at: String? = null,
    @PrimaryKey(autoGenerate = true) val movie_id: Int = 0,
    @ColumnInfo(name = "user_id") val user_id: Int? = null
)
