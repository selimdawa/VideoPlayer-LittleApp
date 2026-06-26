package com.littleapp.videoplayer.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.littleapp.videoplayer.Fragment.FilesFragment
import com.littleapp.videoplayer.Fragment.FolderFragment
import com.littleapp.videoplayer.R
import com.littleapp.videoplayer.Unit.THEME
import com.littleapp.videoplayer.Model.VideoFiles
import com.littleapp.videoplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val context: Context = this@MainActivity

    private val videoPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            videoFiles = getAllVideos(context)
            loadFolderFragment()
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(context)
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkAndRequestPermissions()

        binding.bottomNavView.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.folders -> {
                    loadFolderFragment()
                    item.isChecked = true
                }
                R.id.files -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.constraint, FilesFragment())
                        .commit()
                    item.isChecked = true
                }
            }
            false
        }
    }

    @SuppressLint("InlinedApi")
    private fun checkAndRequestPermissions() {
        val videoPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_VIDEO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, videoPermission) != PackageManager.PERMISSION_GRANTED) {
            videoPermissionLauncher.launch(videoPermission)
        } else {
            videoFiles = getAllVideos(context)
            loadFolderFragment()
        }
    }

    private fun loadFolderFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.constraint, FolderFragment())
            .commit()
    }

    private fun getAllVideos(context: Context): ArrayList<VideoFiles?> {
        val tempVideoFiles = ArrayList<VideoFiles?>()
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.DISPLAY_NAME
        )

        context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            val titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)
            val sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val dateAddedIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
            val durationIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val displayNameIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val id = cursor.getString(idIndex)
                val path = cursor.getString(dataIndex)
                val title = cursor.getString(titleIndex)
                val size = cursor.getString(sizeIndex)
                val dateAdded = cursor.getString(dateAddedIndex)
                val duration = cursor.getString(durationIndex)
                val fileName = cursor.getString(displayNameIndex)

                val videoFilesInstance = VideoFiles(id, path, title, fileName, size, dateAdded, duration)

                val slashFirstIndex = path.lastIndexOf("/")
                if (slashFirstIndex != -1) {
                    val subString = path.substring(0, slashFirstIndex)
                    folderList?.let { list ->
                        if (!list.contains(subString)) {
                            list.add(subString)
                        }
                    }
                }
                tempVideoFiles.add(videoFilesInstance)
            }
        }
        return tempVideoFiles
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        var videoFiles: ArrayList<VideoFiles?>? = ArrayList()
        var folderList: ArrayList<String>? = ArrayList()
    }
}