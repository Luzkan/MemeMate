package com.codecrew.mememate.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.codecrew.mememate.database.models.Meme

@Database(entities = [Meme::class], version = 1, exportSchema = false)
abstract class MemeListDatabase: RoomDatabase(){

    abstract fun getMeme(): MemeInterface

    companion object {
        private const val databaseName = "memedb"
        var memeListDatabase: MemeListDatabase? = null

        fun getInstance(context: Context): MemeListDatabase?{
            if (memeListDatabase == null){
                memeListDatabase = Room.databaseBuilder(context, MemeListDatabase::class.java, databaseName).allowMainThreadQueries().build()
            }
            return memeListDatabase
        }
    }
}