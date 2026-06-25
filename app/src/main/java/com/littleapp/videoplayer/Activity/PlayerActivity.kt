package com.littleapp.videoplayer.Activity

import android.content.Context
import android.os.Bundle
import android.view.Window
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.littleapp.videoplayer.Adapter.VideoAdapter.Companion.videoFile
import com.littleapp.videoplayer.Adapter.VideoFolderAdapter.Companion.folderVideoFile
import com.littleapp.videoplayer.Unit.THEME
import com.littleapp.videoplayer.VideoFiles
import com.littleapp.videoplayer.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private val context: Context = this
    private var exoPlayer: ExoPlayer? = null
    private var position = -1
    private var myFiles: ArrayList<VideoFiles?>? = ArrayList()

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(context)
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setFullScreen()
        setContentView(binding.root)

        position = intent.getIntExtra("position", -1)
        val sender = intent.getStringExtra("sender")

        myFiles = if (sender == "FolderIsSending") folderVideoFile else videoFile

        myFiles?.getOrNull(position)?.path?.let { path ->
            val uri = path.toUri()

            exoPlayer = ExoPlayer.Builder(context).build().apply {
                val dataSourceFactory = DefaultDataSource.Factory(context)
                val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(uri))

                binding.expo.player = this
                binding.expo.keepScreenOn = true

                setMediaSource(mediaSource)
                prepare()
                playWhenReady = true
            }
        }
    }

    private fun setFullScreen() {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun onPause() {
        super.onPause()
        exoPlayer?.playWhenReady = false
    }

    override fun onStop() {
        super.onStop()
        exoPlayer?.playWhenReady = false
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.release()
        exoPlayer = null
    }
}