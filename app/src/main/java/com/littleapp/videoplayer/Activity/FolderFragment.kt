package com.littleapp.videoplayer.Activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.littleapp.videoplayer.Activity.MainActivity.Companion.folderList
import com.littleapp.videoplayer.Activity.MainActivity.Companion.videoFiles
import com.littleapp.videoplayer.Adapter.FolderAdapter
import com.littleapp.videoplayer.databinding.FragmentFolderBinding

class FolderFragment : Fragment() {

    private var _binding: FragmentFolderBinding? = null
    private val binding get() = _binding!!
    private var adapter: FolderAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFolderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentFolders = folderList
        val currentVideos = videoFiles

        if (!currentFolders.isNullOrEmpty() && currentVideos != null) {
            adapter = FolderAdapter(requireContext(), currentVideos, currentFolders)
            binding.recyclerView.adapter = adapter
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}