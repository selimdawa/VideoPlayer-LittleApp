package com.littleapp.videoplayer.Activity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.littleapp.videoplayer.R
import com.littleapp.videoplayer.Unit.THEME
import com.littleapp.videoplayer.VideoFiles
import com.littleapp.videoplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private val context: Context = this@MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(context)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        permission()
        binding!!.bottomNavView.setOnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.folders -> {
                    val fragmentTransaction = supportFragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.constraint, FolderFragment())
                    fragmentTransaction.commit()
                    item.isChecked = true
                }

                R.id.files -> {
                    val fragmentTransaction2 = supportFragmentManager.beginTransaction()
                    fragmentTransaction2.replace(R.id.constraint, FilesFragment())
                    fragmentTransaction2.commit()
                    item.isChecked = true
                }
            }
            false
        }
    }

    private fun permission() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_CODE_PERMISSION
            )
        } else {
            videoFiles = getAllVideos(context)
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.constraint, FolderFragment())
            fragmentTransaction.commit()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.constraint, FolderFragment())
                fragmentTransaction.commit()
                permission()
            } else {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE_PERMISSION
                )
            }
        }
    }

    fun getAllVideos(context: Context): ArrayList<VideoFiles?> {
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
        val cursor = context.contentResolver
            .query(uri, projection, null, null, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val id = cursor.getString(0)
                val path = cursor.getString(1)
                val title = cursor.getString(2)
                val size = cursor.getString(3)
                val dateAdded = cursor.getString(4)
                val duration = cursor.getString(5)
                val fileName = cursor.getString(6)
                val videoFiles = VideoFiles(id, path, title, fileName, size, dateAdded, duration)
                val slashFirstIndex = path.lastIndexOf("/")
                val subString = path.substring(0, slashFirstIndex)
                if (!folderList!!.contains(subString)) folderList!!.add(subString)
                tempVideoFiles.add(videoFiles)
            }
            cursor.close()
        }
        return tempVideoFiles
    }

    companion object {
        private const val REQUEST_CODE_PERMISSION = 123
        var videoFiles: ArrayList<VideoFiles?>? = ArrayList()
        var folderList: ArrayList<String>? = ArrayList()
    }
}