package com.littleapp.videoplayer.Activity

import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.littleapp.videoplayer.Unit.THEME
import com.littleapp.videoplayer.Adapter.VideoFolderAdapter
import com.littleapp.videoplayer.Model.VideoFiles
import com.littleapp.videoplayer.databinding.ActivityVideoFolderBinding

class VideoFolderActivity : AppCompatActivity() {

    private var _binding: ActivityVideoFolderBinding? = null
    private val binding get() = _binding!!

    private val context: Context = this@VideoFolderActivity
    private var adapter: VideoFolderAdapter? = null
    private var myFolderName: String? = null
    private var list = ArrayList<VideoFiles?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(context)
        super.onCreate(savedInstanceState)
        _binding = ActivityVideoFolderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        myFolderName = intent.getStringExtra("folderName")
        myFolderName?.let { folder ->
            list = getAllVideos(context, folder)
        }

        if (list.isNotEmpty()) {
            adapter = VideoFolderAdapter(context, list)
            binding.recyclerView.apply {
                adapter = this@VideoFolderActivity.adapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }
        }
    }

    private fun getAllVideos(context: Context, folderName: String): ArrayList<VideoFiles?> {
        val tempVideoFiles = ArrayList<VideoFiles?>()
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME
        )
        val selection = "${MediaStore.Video.Media.DATA} LIKE ?"
        val selectionArgs = arrayOf("%$folderName%")

        context.contentResolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            val titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)
            val sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val dateAddedIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
            val durationIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val displayNameIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val bucketIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val id = cursor.getString(idIndex)
                val path = cursor.getString(dataIndex)
                val title = cursor.getString(titleIndex)
                val size = cursor.getString(sizeIndex)
                val dateAdded = cursor.getString(dateAddedIndex)
                val duration = cursor.getString(durationIndex)
                val fileName = cursor.getString(displayNameIndex)
                val bucketName = cursor.getString(bucketIndex)

                val videoFiles = VideoFiles(id, path, title, fileName, size, dateAdded, duration)
                if (folderName.endsWith(bucketName)) {
                    tempVideoFiles.add(videoFiles)
                }
            }
        }
        return tempVideoFiles
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}