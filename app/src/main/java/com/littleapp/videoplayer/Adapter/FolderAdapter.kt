package com.littleapp.videoplayer.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.littleapp.videoplayer.Model.VideoFiles
import com.littleapp.videoplayer.Unit.CLASS
import com.littleapp.videoplayer.databinding.ItemVideoPlayerFolderBinding

class FolderAdapter(
    private val context: Context,
    private val videoFiles: ArrayList<VideoFiles?>?,
    private val folderName: ArrayList<String>,
) : RecyclerView.Adapter<FolderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemVideoPlayerFolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val path = folderName[position]

        with(holder.binding) {
            name.text = path.substringAfterLast('/')
            count.text = numberOfFiles(path).toString()

            root.setOnClickListener {
                val intent = Intent(context, CLASS.VIDEO_FOLDER).apply {
                    putExtra("folderName", path)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = folderName.size

    private fun numberOfFiles(folderName: String?): Int {
        if (videoFiles == null || folderName == null) return 0
        return videoFiles.count { video ->
            val videoPath = video?.path ?: return@count false
            videoPath.substringBeforeLast('/', "").endsWith(folderName)
        }
    }

    class ViewHolder(val binding: ItemVideoPlayerFolderBinding) :
        RecyclerView.ViewHolder(binding.root)
}