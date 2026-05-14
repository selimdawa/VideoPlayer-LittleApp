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

    private var binding: ActivityVideoFolderBinding? = null
    private val context: Context = this@VideoFolderActivity
    var adapter: VideoFolderAdapter? = null
    var myFolderName: String? = null
    var list = ArrayList<VideoFiles?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(context)
        super.onCreate(savedInstanceState)
        binding = ActivityVideoFolderBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        myFolderName = intent.getStringExtra("folderName")
        if (myFolderName != null) {
            list = getAllVideos(context, myFolderName!!)
        }
        if (list.size > 0) {
            adapter = VideoFolderAdapter(context, list)
            binding!!.recyclerView.adapter = adapter
            binding!!.recyclerView.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
    }

    fun getAllVideos(context: Context, folderName: String): ArrayList<VideoFiles?> {
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
        val selection = MediaStore.Video.Media.DATA + " like?"
        val selectionArgs = arrayOf("%$folderName%")
        val cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val id = cursor.getString(0)
                val path = cursor.getString(1)
                val title = cursor.getString(2)
                val size = cursor.getString(3)
                val dateAdded = cursor.getString(4)
                val duration = cursor.getString(5)
                val fileName = cursor.getString(6)
                val bucket_name = cursor.getString(7)
                val videoFiles = VideoFiles(id, path, title, fileName, size, dateAdded, duration)
                if (folderName.endsWith(bucket_name)) tempVideoFiles.add(videoFiles)
            }
            cursor.close()
        }
        return tempVideoFiles
    }
}