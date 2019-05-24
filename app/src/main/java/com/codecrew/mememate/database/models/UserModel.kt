package com.codecrew.mememate.database.models

data class UserModel(
    val uid: String,
    val email: String,
    val userName: String,
    var likedMemes: ArrayList<String>?,
    var addedMemes: ArrayList<String>?,
    // (SG) Accounts user follows
    var following: ArrayList<String>?,
    // (SG) Accounts which follow user
    var followers: ArrayList<String>?

)