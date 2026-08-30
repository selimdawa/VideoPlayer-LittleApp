package com.littleapp.videoplayer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.littleapp.videoplayer.activity.PlayerActivity
import com.littleapp.videoplayer.adapter.VideoAdapter
import com.littleapp.videoplayer.databinding.FragmentFilesBinding
import com.littleapp.videoplayer.utils.intent1
import com.littleapp.videoplayer.viewmodel.VideoViewModel
import kotlinx.coroutines.launch

class FilesFragment : Fragment() {

    private var _binding: FragmentFilesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: VideoViewModel by activityViewModels()
    private lateinit var adapter: VideoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFilesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = VideoAdapter { position ->
            VideoAdapter.videoFile = ArrayList(viewModel.videoFiles.value)
            requireContext().intent1(PlayerActivity::class.java) {
                putExtra("position", position)
                putExtra("sender", "FilesIsSending")
            }
        }

        binding.recyclerView.apply {
            adapter = this@FilesFragment.adapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadVideos()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.videoFiles.collect { files ->
                        adapter.submitList(files)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}