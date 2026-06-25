package com.littleapp.videoplayer.Activity

import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.littleapp.videoplayer.Unit.THEME
import com.littleapp.videoplayer.Adapter.VideoFolderAdapter
import com.littleapp.videoplayer.VideoFiles
import com.littleapp.videoplayer.databinding.ActivityVideoFolderBinding

class VideoFolderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoFolderBinding
    private val context: Context = this
    private var adapter: VideoFolderAdapter? = null
    private var myFolderName: String? = null
    private var list = ArrayList<VideoFiles?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(context)
        super.onCreate(savedInstanceState)
        binding = ActivityVideoFolderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        myFolderName = intent.getStringExtra("folderName")
        myFolderName?.let { folder ->
            list = getAllVideos(context, folder)
        }

        if (list.isNotEmpty()) {
            adapter = VideoFolderAdapter(context, list)
            binding.recyclerView.adapter = adapter
            binding.recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
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
            while (cursor.moveToNext()) {
                val id = cursor.getString(0)
                val path = cursor.getString(1)
                val title = cursor.getString(2)
                val size = cursor.getString(3)
                val dateAdded = cursor.getString(4)
                val duration = cursor.getString(5)
                val fileName = cursor.getString(6)
                val bucketName = cursor.getString(7)

                val videoFiles = VideoFiles(id, path, title, fileName, size, dateAdded, duration)
                if (folderName.endsWith(bucketName)) {
                    tempVideoFiles.add(videoFiles)
                }
            }
        }
        return tempVideoFiles
    }
}