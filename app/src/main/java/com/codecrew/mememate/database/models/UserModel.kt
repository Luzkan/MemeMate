package com.codecrew.mememate.database.models

data class UserModel(
    val uid: String,
    val email: String,
    val userName: String,
    var lickedMemes: ArrayList<String>?,
    var addedMemes: ArrayList<String>?
)