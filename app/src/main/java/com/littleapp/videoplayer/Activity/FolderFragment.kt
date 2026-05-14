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
import com.littleapp.videoplayer.R
import com.littleapp.videoplayer.Adapter.FolderAdapter

class FolderFragment : Fragment() {

    var adapter: FolderAdapter? = null
    var recyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_folder, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        if (folderList != null && folderList!!.size > 0 && videoFiles != null) {
            adapter = FolderAdapter(requireContext(), videoFiles, folderList!!)
            recyclerView!!.adapter = adapter
            recyclerView!!.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
        return view
    }
}