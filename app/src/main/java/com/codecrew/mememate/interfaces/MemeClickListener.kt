package com.codecrew.mememate.interfaces

import com.codecrew.mememate.database.models.MemeModel

interface MemeClickListener {
    fun onMemeClick(position: Int, memes: ArrayList<MemeModel>)
}