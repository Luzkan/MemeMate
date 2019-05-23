package com.codecrew.mememate.interfaces

import com.codecrew.mememate.database.models.MemeModel

interface GalleryMemeClickListener {

    fun onGalleryMemeClickListener(position: Int, memes: ArrayList<MemeModel>)
}