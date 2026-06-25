package com.littleapp.videoplayer.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.littleapp.videoplayer.Unit.CLASS
import com.littleapp.videoplayer.VideoFiles
import com.littleapp.videoplayer.databinding.ItemVideoPlayerFolderBinding

class FolderAdapter(
    private val context: Context,
    private val videoFiles: ArrayList<VideoFiles?>?,
    private val folderName: ArrayList<String>,
) : RecyclerView.Adapter<FolderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemVideoPlayerFolderBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val path = folderName[position]
        val folder = path.substringAfterLast('/')

        holder.binding.name.text = folder
        holder.binding.count.text = numberOfFiles(path).toString()

        holder.itemView.setOnClickListener {
            val intent = Intent(context, CLASS.VIDEO_FOLDER).apply {
                putExtra("folderName", path)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = folderName.size

    class ViewHolder(val binding: ItemVideoPlayerFolderBinding) : RecyclerView.ViewHolder(binding.root)

    fun numberOfFiles(folderName: String?): Int {
        if (videoFiles == null || folderName == null) return 0
        var countFiles = 0
        for (video in videoFiles) {
            val videoPath = video?.path ?: continue
            val parentFolder = videoPath.substringBeforeLast('/', "")
            if (parentFolder.endsWith(folderName)) {
                countFiles++
            }
        }
        return countFiles
    }
}