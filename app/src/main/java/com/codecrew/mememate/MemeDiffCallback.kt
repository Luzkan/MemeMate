package com.codecrew.mememate

import android.support.v7.util.DiffUtil
import com.codecrew.mememate.database.models.MemeModel

class MemeDiffCallback(private val old: List<MemeModel>, private val new: List<MemeModel>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return old.size
    }

    override fun getNewListSize(): Int {
        return new.size
    }

    override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition].id == new[newPosition].id
    }

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition] == new[newPosition]
    }

}