package com.codecrew.mememate.database.models

data class MemeModel(
    val id: Long = counter++,
    var dbId: String,
    var url: String,
    var location: String,
    var rate: Int,
    var seenBy: ArrayList<String>,
    var addedBy: String,
    var userID: String,
    var addDate : String
) {
    companion object {
        private var counter = 0L
    }
}


