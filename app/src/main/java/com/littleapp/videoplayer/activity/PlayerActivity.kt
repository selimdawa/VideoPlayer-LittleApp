package com.littleapp.videoplayer.activity

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import android.util.Log
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.littleapp.videoplayer.R
import com.littleapp.videoplayer.adapter.VideoAdapter.Companion.videoFile
import com.littleapp.videoplayer.adapter.VideoFolderAdapter.Companion.folderVideoFile
import com.littleapp.videoplayer.model.VideoFiles
import com.littleapp.videoplayer.databinding.ActivityPlayerBinding
import com.littleapp.videoplayer.utils.THEME

class PlayerActivity : AppCompatActivity() {

    private var _binding: ActivityPlayerBinding? = null
    private val binding get() = _binding!!

    private val context: Context = this@PlayerActivity
    private var exoPlayer: ExoPlayer? = null
    private var position = -1
    private var myFiles: ArrayList<VideoFiles?>? = ArrayList()

    private val playerListener = object : Player.Listener {
        @OptIn(UnstableApi::class)
        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            Log.e("PlayerActivity", "ExoPlayer Error: ${error.errorCodeName}", error)
            Toast.makeText(context, "Playback Error: ${error.errorCodeName}\nCheck Logcat for details.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(this)
        setFullScreen()
        super.onCreate(savedInstanceState)
        _binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        position = intent.getIntExtra("position", -1)
        val sender = intent.getStringExtra("sender")

        myFiles = if (sender == "FolderIsSending") folderVideoFile else videoFile

        if (myFiles.isNullOrEmpty() || position == -1) {
            Toast.makeText(context, R.string.data_not_found, Toast.LENGTH_SHORT).show()
            finish()
            return
        }
    }

    @OptIn(UnstableApi::class)
    private fun initializePlayer() {
        val video = myFiles?.getOrNull(position) ?: return
        val uri = (video.uriString ?: video.path)?.toUri() ?: return

        val mediaCodecSelector = MediaCodecSelector { mimeType, requiresSecureDecoder, requiresTunnelingDecoder ->
            val decoders = MediaCodecSelector.DEFAULT.getDecoderInfos(
                mimeType, requiresSecureDecoder, requiresTunnelingDecoder
            )
            if (Build.PRODUCT.contains("sdk_gphone") || Build.MODEL.contains("Emulator")) {
                // On emulator, prefer Google's software decoders (c2.android.*) over goldfish/hardware ones
                decoders.sortedBy { it.name.startsWith("c2.android") }.reversed()
            } else {
                decoders
            }
        }

        val renderersFactory = DefaultRenderersFactory(context)
            .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
            .setMediaCodecSelector(mediaCodecSelector)
            .setEnableDecoderFallback(true)

        exoPlayer = ExoPlayer.Builder(context, renderersFactory).build().apply {
            addListener(playerListener)
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

    private fun releasePlayer() {
        exoPlayer?.let { player ->
            player.release()
            exoPlayer = null
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

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onResume() {
        super.onResume()
        if (exoPlayer == null) {
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}