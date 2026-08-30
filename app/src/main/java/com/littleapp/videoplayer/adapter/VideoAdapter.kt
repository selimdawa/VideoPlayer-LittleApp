package com.littleapp.videoplayer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.decode.VideoFrameDecoder
import coil.load
import com.littleapp.videoplayer.databinding.ItemVideoBinding
import com.littleapp.videoplayer.model.VideoFiles
import com.littleapp.videoplayer.utils.formatDuration
import java.io.File

class VideoAdapter(
    private val onItemClick: (Int) -> Unit
) : ListAdapter<VideoFiles, VideoAdapter.ViewHolder>(VideoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentVideo = getItem(position)

        with(holder.binding) {
            name.text = currentVideo.title

            val durationMs = currentVideo.duration?.toLongOrNull() ?: 0L
            duration.text = durationMs.formatDuration()

            currentVideo.path?.let { path ->
                image.load(File(path)) {
                    decoderFactory { result, options, _ ->
                        VideoFrameDecoder(result.source, options)
                    }
                }
            }

            root.setOnClickListener {
                onItemClick(position)
            }
        }
    }

    class ViewHolder(val binding: ItemVideoBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        var videoFile: ArrayList<VideoFiles?>? = null
    }

    class VideoDiffCallback : DiffUtil.ItemCallback<VideoFiles>() {
        override fun areItemsTheSame(oldItem: VideoFiles, newItem: VideoFiles): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: VideoFiles, newItem: VideoFiles): Boolean {
            return oldItem == newItem
        }
    }
}