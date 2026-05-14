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

class VideoAdapter(private val context: Context, var videoFiles: ArrayList<VideoFiles?>) :
    RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    private var binding: ItemVideoBinding? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemVideoBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding!!.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = videoFiles[position]!!.title
        holder.duration.text = videoFiles[position]!!.duration

        Glide.with(context).load(File(videoFiles[position]!!.path!!)).into(holder.image)
        val duration = VOID.convertDuration(videoFiles[position]!!.duration!!.toLong())
        holder.duration.text = duration

        holder.itemView.setOnClickListener {
            val intent = Intent(context, CLASS.VIDEO_PLAY)
            intent.putExtra("position", position)
            intent.putExtra("sender", "FilesIsSending")
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return videoFiles.size
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
        var videoFile: ArrayList<VideoFiles?>? = null
    }

    init {
        videoFile = videoFiles
    }
}