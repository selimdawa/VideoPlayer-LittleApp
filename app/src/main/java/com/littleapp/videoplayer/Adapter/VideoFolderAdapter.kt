package com.littleapp.videoplayer.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.littleapp.videoplayer.Unit.CLASS
import com.littleapp.videoplayer.Unit.VOID
import com.littleapp.videoplayer.VideoFiles
import com.littleapp.videoplayer.databinding.ItemVideoBinding
import java.io.File

class VideoFolderAdapter(
    private val context: Context,
    private val folderVideoFiles: ArrayList<VideoFiles?>,
) : RecyclerView.Adapter<VideoFolderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemVideoBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = folderVideoFiles[position] ?: return

        holder.binding.name.text = currentItem.title

        currentItem.path?.let { path ->
            Glide.with(context)
                .load(File(path))
                .into(holder.binding.image)
        }

        currentItem.duration?.toLongOrNull()?.let { durationLong ->
            val durationText = VOID.convertDuration(durationLong)
            holder.binding.duration.text = durationText
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, CLASS.VIDEO_PLAY).apply {
                putExtra("position", position)
                putExtra("sender", "FolderIsSending")
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = folderVideoFiles.size

    inner class ViewHolder(val binding: ItemVideoBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        var folderVideoFile: ArrayList<VideoFiles?>? = null
    }

    init {
        folderVideoFile = folderVideoFiles
    }
}