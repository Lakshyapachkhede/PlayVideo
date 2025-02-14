package com.pachkhede.playvideo

import android.app.Application
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.MediaStore.Video.Media
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VideoViewModel(application: Application) : AndroidViewModel(application) {
    private val videoRepository = VideoRepository(application.applicationContext)
    private val _videos = MutableLiveData<List<Video>>()
    val videos: LiveData<List<Video>> get() = _videos

    private val contentResolver = application.contentResolver
    private val handler = Handler(Looper.getMainLooper())
    
    private val videoObserver = object : ContentObserver(handler) {
        override fun onChange(selfChange: Boolean) {
            loadVideos()
        }
    }

    init {
        contentResolver.registerContentObserver(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            true,
            videoObserver
        )
        loadVideos()
    }

    fun loadVideos() {
        viewModelScope.launch(Dispatchers.IO) {
            val videoList = videoRepository.getAllVideos()
            _videos.postValue(videoList)
        }
    }

    override fun onCleared() {
        super.onCleared()
        contentResolver.unregisterContentObserver(videoObserver)
    }


}