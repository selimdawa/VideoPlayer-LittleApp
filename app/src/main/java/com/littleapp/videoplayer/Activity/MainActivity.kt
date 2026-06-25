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
import com.littleapp.videoplayer.R
import com.littleapp.videoplayer.Unit.THEME
import com.littleapp.videoplayer.VideoFiles
import com.littleapp.videoplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val context: Context = this

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
        binding = ActivityMainBinding.inflate(layoutInflater)
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
            while (cursor.moveToNext()) {
                val id = cursor.getString(0)
                val path = cursor.getString(1)
                val title = cursor.getString(2)
                val size = cursor.getString(3)
                val dateAdded = cursor.getString(4)
                val duration = cursor.getString(5)
                val fileName = cursor.getString(6)

                val videoFilesInstance = VideoFiles(id, path, title, fileName, size, dateAdded, duration)
                val slashFirstIndex = path.lastIndexOf("/")
                if (slashFirstIndex != -1) {
                    val subString = path.substring(0, slashFirstIndex)
                    folderList?.let {
                        if (!it.contains(subString)) {
                            it.add(subString)
                        }
                    }
                }
                tempVideoFiles.add(videoFilesInstance)
            }
        }
        return tempVideoFiles
    }

    companion object {
        var videoFiles: ArrayList<VideoFiles?>? = ArrayList()
        var folderList: ArrayList<String>? = ArrayList()
    }
}