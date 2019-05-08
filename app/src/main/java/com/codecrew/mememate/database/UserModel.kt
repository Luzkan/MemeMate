package com.codecrew.mememate.database

data class UserModel(val uid : String, val email : String, val userName : String) {
    constructor():this(uid="",email="", userName = "")
    var addedMemes = ArrayList<String>()
}