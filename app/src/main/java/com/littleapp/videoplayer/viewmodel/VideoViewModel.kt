package com.littleapp.videoplayer.viewmodel

import android.app.Application
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.littleapp.videoplayer.data.VideoRepository
import com.littleapp.videoplayer.model.Folder
import com.littleapp.videoplayer.model.VideoFiles
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VideoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VideoRepository(application)

    private val _videoFiles = MutableStateFlow<List<VideoFiles>>(emptyList())
    val videoFiles: StateFlow<List<VideoFiles>> = _videoFiles.asStateFlow()

    private val _folderList = MutableStateFlow<List<Folder>>(emptyList())
    val folderList: StateFlow<List<Folder>> = _folderList.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            loadVideos(true)
        }
    }

    init {
        application.contentResolver.registerContentObserver(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI, true, contentObserver
        )
        loadVideos()
    }

    fun loadVideos(isInternalUpdate: Boolean = false) {
        viewModelScope.launch {
            if (!isInternalUpdate) {
                _isRefreshing.value = true
                repository.refreshMediaStore()
            }
            val allVideos = repository.getAllVideos()
            _videoFiles.value = allVideos

            // Extract folders using bucketName and count videos
            val folders = allVideos.groupBy { video ->
                video.bucketName ?: video.path?.substringBeforeLast('/', "")
                    ?.substringAfterLast('/') ?: "Internal Storage"
            }.map { (name, videos) ->
                Folder(
                    name = name,
                    path = videos.firstOrNull()?.path?.substringBeforeLast('/', "") ?: "",
                    videoCount = videos.size
                )
            }
            _folderList.value = folders
            if (!isInternalUpdate) _isRefreshing.value = false
        }
    }

    override fun onCleared() {
        getApplication<Application>().contentResolver.unregisterContentObserver(contentObserver)
    }
}