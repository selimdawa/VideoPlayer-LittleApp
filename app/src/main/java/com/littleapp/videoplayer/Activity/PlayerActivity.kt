package com.littleapp.videoplayer.Activity

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.littleapp.videoplayer.Unit.THEME
import com.littleapp.videoplayer.Adapter.VideoAdapter.Companion.videoFile
import com.littleapp.videoplayer.Adapter.VideoFolderAdapter.Companion.folderVideoFile
import com.littleapp.videoplayer.VideoFiles
import com.littleapp.videoplayer.databinding.ActivityPlayerBinding
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class PlayerActivity : AppCompatActivity() {

    private var binding: ActivityPlayerBinding? = null
    private val context: Context = this@PlayerActivity
    var simpleExoPlayer: SimpleExoPlayer? = null
    var position = -1
    var myFiles: ArrayList<VideoFiles?>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(context)
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        val view = binding!!.root
        setFullScreen()
        setContentView(view)

        position = intent.getIntExtra("position", -1)
        val sender = intent.getStringExtra("sender")
        myFiles = if (sender == "FolderIsSending") folderVideoFile!!
        else videoFile!!

        val path = myFiles!![position]!!.path
        if (path != null) {
            val uri = Uri.parse(path)
            simpleExoPlayer = SimpleExoPlayer.Builder(context).build()
            val factory: DataSource.Factory =
                DefaultDataSourceFactory(context, Util.getUserAgent(context, "My App Name"))
            val extractorsFactory: ExtractorsFactory = DefaultExtractorsFactory()
            val mediaItem: MediaItem = MediaItem.fromUri(uri)

            val mediaSource: MediaSource = ProgressiveMediaSource.Factory(
                factory, extractorsFactory
            ).createMediaSource(mediaItem)
            binding!!.expo.player = simpleExoPlayer
            binding!!.expo.keepScreenOn = true
            simpleExoPlayer!!.prepare(mediaSource)
            simpleExoPlayer!!.playWhenReady = true
        }
    }

    private fun setFullScreen() {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    override fun onPause() {
        simpleExoPlayer!!.pause()
        super.onPause()
    }

    override fun onStop() {
        simpleExoPlayer!!.stop()
        super.onStop()
    }
}