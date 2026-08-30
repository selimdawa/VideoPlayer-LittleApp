package com.littleapp.videoplayer.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.littleapp.videoplayer.R
import com.littleapp.videoplayer.databinding.ActivityMainBinding
import com.littleapp.videoplayer.fragment.FilesFragment
import com.littleapp.videoplayer.fragment.FolderFragment
import com.littleapp.videoplayer.utils.THEME
import com.littleapp.videoplayer.viewmodel.VideoViewModel

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val context: Context = this@MainActivity
    private val viewModel: VideoViewModel by viewModels()

    private val videoPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(context, R.string.permission_granted, Toast.LENGTH_SHORT).show()
            viewModel.loadVideos()
            loadFolderFragment()
        } else {
            Toast.makeText(context, R.string.permission_denied, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(this)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        checkAndRequestPermissions()

        binding.bottomNavView.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.folders -> {
                    loadFolderFragment()
                    true
                }

                R.id.files -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.constraint, FilesFragment()).commit()
                    true
                }

                else -> false
            }
        }
    }

    @SuppressLint("InlinedApi")
    private fun checkAndRequestPermissions() {
        val videoPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_VIDEO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(
                this, videoPermission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            videoPermissionLauncher.launch(videoPermission)
        } else {
            viewModel.loadVideos()
            loadFolderFragment()
        }
    }

    private fun loadFolderFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.constraint, FolderFragment())
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}