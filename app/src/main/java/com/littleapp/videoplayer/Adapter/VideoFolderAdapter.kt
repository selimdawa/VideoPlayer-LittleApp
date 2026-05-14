package com.littleapp.videoplayer.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.littleapp.videoplayer.Unit.CLASS
import com.littleapp.videoplayer.Unit.VOID
import com.littleapp.videoplayer.VideoFiles
import com.littleapp.videoplayer.databinding.ItemVideoBinding
import java.io.File

class VideoFolderAdapter(
    private val context: Context,
    private var folderVideoFiles: ArrayList<VideoFiles?>,
) :
    RecyclerView.Adapter<VideoFolderAdapter.ViewHolder>() {

    private var binding: ItemVideoBinding? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemVideoBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding!!.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = folderVideoFiles[position]!!.title
        holder.duration.text = folderVideoFiles[position]!!.duration

        Glide.with(context).load(File(folderVideoFiles[position]!!.path!!)).into(holder.image)
        val duration = VOID.convertDuration(folderVideoFiles[position]!!.duration!!.toLong())
        holder.duration.text = duration

        holder.itemView.setOnClickListener {
            val intent = Intent(context, CLASS.VIDEO_PLAY)
            intent.putExtra("position", position)
            intent.putExtra("sender", "FolderIsSending")
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return folderVideoFiles.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView
        var name: TextView
        var duration: TextView

        init {
            image = binding!!.image
            name = binding!!.name
            duration = binding!!.duration
        }
    }

    companion object {
        var folderVideoFile: ArrayList<VideoFiles?>? = null
    }

    init {
        folderVideoFile = folderVideoFiles
    }
}