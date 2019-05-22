package com.codecrew.mememate.database.models

data class UserModel(
    val uid: String,
    val email: String,
    val userName: String,
    var likedMemes: ArrayList<String>?,
    var addedMemes: ArrayList<String>?
)