package com.littleapp.videoplayer.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.littleapp.videoplayer.Unit.CLASS
import com.littleapp.videoplayer.Unit.VOID
import com.littleapp.videoplayer.Model.VideoFiles
import com.littleapp.videoplayer.databinding.ItemVideoBinding
import java.io.File

class VideoFolderAdapter(
    private val context: Context,
    private val folderVideoFiles: ArrayList<VideoFiles?>,
) : RecyclerView.Adapter<VideoFolderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVideoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = folderVideoFiles[position] ?: return

        with(holder.binding) {
            name.text = currentItem.title

            currentItem.path?.let { path ->
                Glide.with(context)
                    .load(File(path))
                    .into(image)
            }

            currentItem.duration?.toLongOrNull()?.let { durationLong ->
                duration.text = VOID.convertDuration(durationLong)
            }

            root.setOnClickListener {
                val intent = Intent(context, CLASS.VIDEO_PLAY).apply {
                    putExtra("position", position)
                    putExtra("sender", "FolderIsSending")
                }
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = folderVideoFiles.size

    class ViewHolder(val binding: ItemVideoBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        var folderVideoFile: ArrayList<VideoFiles?>? = null
    }

    init {
        folderVideoFile = folderVideoFiles
    }
}