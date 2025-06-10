package com.example.photogallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PhotoViewModel : ViewModel() {

    private val _photos = MutableLiveData<List<String>>()
    val photos: LiveData<List<String>> = _photos

    fun setPhotos(photoList: List<String>) {
        _photos.value = photoList
    }
}
