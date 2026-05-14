package com.littleapp.videoplayer.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.littleapp.videoplayer.Unit.CLASS
import com.littleapp.videoplayer.VideoFiles
import com.littleapp.videoplayer.databinding.ItemVideoPlayerFolderBinding

class FolderAdapter(
    private val context: Context,
    private val videoFiles: ArrayList<VideoFiles?>?,
    private val folderName: ArrayList<String>,
) : RecyclerView.Adapter<FolderAdapter.ViewHolder>() {

    private var binding: ItemVideoPlayerFolderBinding? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemVideoPlayerFolderBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding!!.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val index = folderName[position].lastIndexOf("/")
        val folder = folderName[position].substring(index + 1)
        holder.name.text = folder
        holder.count.text = NumberOfFiles(folderName[position]).toString()

        holder.itemView.setOnClickListener {
            val intent = Intent(context, CLASS.VIDEO_FOLDER)
            intent.putExtra("folderName", folderName[position])
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return folderName.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView
        var count: TextView

        init {
            name = binding!!.name
            count = binding!!.count
        }
    }

    fun NumberOfFiles(folderName: String?): Int {
        var countFiles = 0
        for (videoFiles in videoFiles!!) {
            if (videoFiles!!.path!!.substring(0, videoFiles.path!!.lastIndexOf("/")).endsWith(
                    folderName!!
                )
            ) {
                countFiles++
            }
        }
        return countFiles
    }
}