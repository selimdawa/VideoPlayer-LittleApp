package com.littleapp.videoplayer.activity

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.littleapp.videoplayer.utils.CLASS
import com.littleapp.videoplayer.utils.THEME
import com.littleapp.videoplayer.utils.VOID
import com.littleapp.videoplayer.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private var _binding: ActivitySplashBinding? = null
    private val binding get() = _binding!!

    private val context: Context = this@SplashActivity

    private val timePerSecond = 2
    private val timeFinal = TIME_PER_MILLIS * timePerSecond

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(context)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Handler(Looper.getMainLooper()).postDelayed({ launch() }, timeFinal.toLong())
    }

    private fun launch() {
        VOID.Intent1(context, CLASS.MAIN)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TIME_PER_MILLIS = 1000
    }
}