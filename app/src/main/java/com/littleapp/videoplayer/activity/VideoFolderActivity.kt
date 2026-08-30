package com.littleapp.videoplayer.activity

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.littleapp.videoplayer.adapter.VideoFolderAdapter
import com.littleapp.videoplayer.databinding.ActivityVideoFolderBinding
import com.littleapp.videoplayer.utils.intent1
import com.littleapp.videoplayer.viewmodel.VideoViewModel
import kotlinx.coroutines.launch

class VideoFolderActivity : AppCompatActivity() {

    private var _binding: ActivityVideoFolderBinding? = null
    private val binding get() = _binding!!

    private val context: Context = this@VideoFolderActivity
    private val viewModel: VideoViewModel by viewModels()
    private lateinit var adapter: VideoFolderAdapter
    private var myFolderName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        _binding = ActivityVideoFolderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        myFolderName = intent.getStringExtra("folderName")

        adapter = VideoFolderAdapter { position ->
            val currentList = adapter.currentList
            VideoFolderAdapter.folderVideoFile = ArrayList(currentList)
            context.intent1(PlayerActivity::class.java) {
                putExtra("position", position)
                putExtra("sender", "FolderIsSending")
            }
        }

        binding.recyclerView.apply {
            adapter = this@VideoFolderActivity.adapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadVideos()
        }

        myFolderName?.let { folder ->
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    launch {
                        viewModel.videoFiles.collect { allVideos ->
                            val filteredVideos = allVideos.filter {
                                it.path?.substringBeforeLast('/', "") == folder
                            }
                            adapter.submitList(filteredVideos)
                        }
                    }
                    launch {
                        viewModel.isRefreshing.collect { isRefreshing ->
                            binding.swipeRefresh.isRefreshing = isRefreshing
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}