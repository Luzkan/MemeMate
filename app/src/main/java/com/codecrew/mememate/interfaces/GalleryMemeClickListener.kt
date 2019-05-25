package com.codecrew.mememate.interfaces

import com.codecrew.mememate.database.models.MemeModel

interface GalleryMemeClickListener {

    fun onGalleryMemeClick(position: Int, memes: ArrayList<MemeModel>)
}