package com.littleapp.videoplayer.data

import android.content.ContentUris
import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import android.provider.MediaStore
import com.littleapp.videoplayer.model.VideoFiles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class VideoRepository(private val context: Context) {

    suspend fun getAllVideos(): List<VideoFiles> = withContext(Dispatchers.IO) {
        val tempVideoFiles = mutableListOf<VideoFiles>()
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
        )

        context.contentResolver.query(
            uri, projection, null, null, "${MediaStore.Video.Media.DATE_ADDED} DESC"
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            val titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)
            val sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val dateAddedIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
            val durationIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val displayNameIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val bucketIndex =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val path = cursor.getString(dataIndex)
                val title = cursor.getString(titleIndex)
                val size = cursor.getString(sizeIndex)
                val dateAdded = cursor.getString(dateAddedIndex)
                val duration = cursor.getString(durationIndex)
                val fileName = cursor.getString(displayNameIndex)
                val bucketName = cursor.getString(bucketIndex) ?: "Internal Storage"

                val contentUri =
                    ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                        .toString()

                val videoFile = VideoFiles(
                    id.toString(),
                    path,
                    contentUri,
                    title,
                    fileName,
                    size,
                    dateAdded,
                    duration,
                    bucketName
                )
                tempVideoFiles.add(videoFile)
            }
        }
        tempVideoFiles
    }

    suspend fun refreshMediaStore() = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { continuation ->
            val root = Environment.getExternalStorageDirectory().absolutePath
            MediaScannerConnection.scanFile(context, arrayOf(root), null) { _, _ ->
                continuation.resume(Unit)
            }
        }
    }
}