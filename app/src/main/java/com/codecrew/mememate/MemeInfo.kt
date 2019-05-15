package com.codecrew.mememate

import java.io.Serializable

data class MemeInfo(
    val id: Long = counter++,
    val name: String,
    val description: String,
    val url: String
) : Serializable {
    companion object {
        private var counter = 0L
    }
}