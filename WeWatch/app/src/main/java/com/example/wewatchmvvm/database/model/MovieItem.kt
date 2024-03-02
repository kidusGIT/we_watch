package com.example.wewatchmvvm.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_items")
data class MovieItem (
    @ColumnInfo(name = "cover_url")
    var cover_url: String,
    @ColumnInfo(name = "description")
    var description: String,
    @ColumnInfo(name = "title")
    var title: String,
    @ColumnInfo(name = "user_id")
    var user_id: Int,
    @ColumnInfo(name = "movie_id")
    var movie_id: Int
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}