package com.littleapp.videoplayer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.littleapp.videoplayer.model.Folder
import com.littleapp.videoplayer.databinding.ItemVideoPlayerFolderBinding

class FolderAdapter(
    private val onItemClick: (Folder) -> Unit
) : ListAdapter<Folder, FolderAdapter.ViewHolder>(FolderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemVideoPlayerFolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val folder = getItem(position)

        with(holder.binding) {
            name.text = folder.name
            count.text = folder.videoCount.toString()

            root.setOnClickListener {
                onItemClick(folder)
            }
        }
    }

    class ViewHolder(val binding: ItemVideoPlayerFolderBinding) :
        RecyclerView.ViewHolder(binding.root)

    class FolderDiffCallback : DiffUtil.ItemCallback<Folder>() {
        override fun areItemsTheSame(oldItem: Folder, newItem: Folder): Boolean {
            return oldItem.path == newItem.path
        }

        override fun areContentsTheSame(oldItem: Folder, newItem: Folder): Boolean {
            return oldItem == newItem
        }
    }
}