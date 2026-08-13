package com.littleapp.videoplayer.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.decode.VideoFrameDecoder
import coil.load
import com.littleapp.videoplayer.utils.CLASS
import com.littleapp.videoplayer.utils.VOID
import com.littleapp.videoplayer.model.VideoFiles
import com.littleapp.videoplayer.databinding.ItemVideoBinding
import java.io.File

class VideoAdapter(
    private val context: Context,
    private val videoFiles: ArrayList<VideoFiles?>
) : RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVideoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = videoFiles[position] ?: return

        with(holder.binding) {
            name.text = currentItem.title

            currentItem.path?.let { path ->
                image.load(File(path)) {
                    decoderFactory { result, options, _ ->
                        VideoFrameDecoder(result.source, options)
                    }
                }
            }

            currentItem.duration?.toLongOrNull()?.let { durationLong ->
                duration.text = VOID.convertDuration(durationLong)
            }

            root.setOnClickListener {
                val intent = Intent(context, CLASS.VIDEO_PLAY).apply {
                    putExtra("position", position)
                    putExtra("sender", "FilesIsSending")
                }
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = videoFiles.size

    class ViewHolder(val binding: ItemVideoBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        var videoFile: ArrayList<VideoFiles?>? = null
    }

    init {
        videoFile = videoFiles
    }
}