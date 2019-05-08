package com.codecrew.mememate.database

import android.arch.persistence.room.*
import com.codecrew.mememate.database.models.Meme

@Dao
interface MemeInterface{

    @Query("SELECT*FROM meme ORDER BY tId ASC")
    fun getMemeList(): List<Meme>

    @Query("SELECT*FROM meme ORDER BY meme_rating DESC")
    fun getMemeListRatingSorted(): List<Meme>

    @Query("SELECT*FROM meme WHERE tId=:tid")
    fun getMemeItem(tid: Int): Meme

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun saveMeme(meme: Meme)

    @Update
    fun updateMeme(meme: Meme)

    @Delete
    fun removeMeme(meme: Meme)

}