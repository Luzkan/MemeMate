package com.codecrew.mememate.ui.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel

// (MJ) See PlaceHolderFragment for more info
class PageViewModel : ViewModel() {

    private val _index = MutableLiveData<Int>()
    val text: LiveData<String> = Transformations.map(_index) {
        "Fragment: $it"
    }

    fun setIndex(index: Int) {
        _index.value = index
    }
}