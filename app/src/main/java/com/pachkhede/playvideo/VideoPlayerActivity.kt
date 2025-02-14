package com.pachkhede.playvideo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.ui.PlayerView
import androidx.media3.exoplayer.ExoPlayer

class VideoPlayerActivity : AppCompatActivity() {

    private lateinit var playerView: PlayerView
    private var exoPlayer: ExoPlayer? = null
    private var videoUri: Uri? = null
    private var playWhenReady = true
    private var playbackPosition: Long = 0L  // Save playback position

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set Full Screen Mode
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                )

        enableEdgeToEdge()
        setContentView(R.layout.activity_video_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        playerView = findViewById(R.id.playerView)
        videoUri = intent?.data ?: intent.getStringExtra("videoUri")?.toUri()

        if (videoUri == null) {
            Toast.makeText(this, "Invalid video URL", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            if (intent.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION != 0) {
                try {
                    contentResolver.takePersistableUriPermission(
                        videoUri!!,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: SecurityException) {
                    e.printStackTrace() // Avoid crash if permission is not needed
                }
            }
        }
    }

    private fun initializePlayer() {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(this).build()
            playerView.player = exoPlayer
            val mediaItem = MediaItem.fromUri(videoUri!!)
            exoPlayer?.setMediaItem(mediaItem)
            exoPlayer?.seekTo(playbackPosition) // Restore playback position
            exoPlayer?.playWhenReady = playWhenReady
            exoPlayer?.prepare()
        }
    }

    private fun releasePlayer() {
        exoPlayer?.let {
            playbackPosition = it.currentPosition // Save position
            playWhenReady = it.playWhenReady
            it.release()
        }
        exoPlayer = null
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer() // Release ExoPlayer when the app goes into the background
    }

    override fun onPause() {
        super.onPause()
        exoPlayer?.playWhenReady = false // Pause playback when app goes in background
    }

    override fun onResume() {
        super.onResume()
        exoPlayer?.playWhenReady = true // Resume playback when app comes to foreground
    }
}
