package com.codecrew.mememate.database.models

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "meme")
class Meme(
    @PrimaryKey(autoGenerate = true)
    var tId: Int = 0,
    @ColumnInfo(name = "meme_name")
    var name:String = "",
    @ColumnInfo(name = "meme_description")
    var description:String = "",
    @ColumnInfo(name = "meme_url")
    var url: String = "",
    @ColumnInfo(name = "meme_rating")
    var rating: Double = 50.0)
