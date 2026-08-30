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
import com.littleapp.videoplayer.utils.intent1
import com.littleapp.videoplayer.activity.VideoFolderActivity
import com.littleapp.videoplayer.adapter.FolderAdapter
import com.littleapp.videoplayer.viewmodel.VideoViewModel
import com.littleapp.videoplayer.databinding.FragmentFolderBinding
import kotlinx.coroutines.launch

class FolderFragment : Fragment() {

    private var _binding: FragmentFolderBinding? = null
    private val binding get() = _binding!!

    private val viewModel: VideoViewModel by activityViewModels()
    private lateinit var adapter: FolderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFolderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FolderAdapter { folder ->
            requireContext().intent1(VideoFolderActivity::class.java) {
                putExtra("folderName", folder.path)
            }
        }

        binding.recyclerView.apply {
            adapter = this@FolderFragment.adapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadVideos()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.folderList.collect { folders ->
                        adapter.submitList(folders)
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