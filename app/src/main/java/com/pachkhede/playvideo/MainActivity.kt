package com.pachkhede.playvideo

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.Manifest
import android.widget.LinearLayout

class MainActivity : AppCompatActivity() {

    private lateinit var videoViewModel: VideoViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var videoAdapter: VideoAdapter
    private lateinit var permissionDeniedLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        recyclerView = findViewById(R.id.recyclerView)
        permissionDeniedLayout = findViewById(R.id.permissionDeniedLayout)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        videoAdapter = VideoAdapter(this, emptyList()) {video ->
            val intent = Intent(this, VideoPlayerActivity::class.java).apply {
                putExtra("videoUri", video.contentUri.toString())
            }
            startActivity(intent)
        }

        recyclerView.adapter = videoAdapter

        videoViewModel = ViewModelProvider(this).get(VideoViewModel::class.java)

        videoViewModel.videos.observe(this) {videos ->
            videoAdapter.updateVideos(videos)
        }


        checkAndRequestPermission()
    }

    private fun checkAndRequestPermission() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_VIDEO)
                } else {
                    showVideos()
                }
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                showVideos()
            }

            else -> {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                } else {
                    showVideos()
                }
            }
        }
    }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            showVideos()
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            showPermissionDeniedMessage()
        }
    }

    private fun showVideos() {
        videoViewModel.loadVideos()
        recyclerView.visibility = View.VISIBLE
        permissionDeniedLayout.visibility = View.GONE
    }

    private fun showPermissionDeniedMessage() {
        recyclerView.visibility = View.GONE
        permissionDeniedLayout.visibility = View.VISIBLE
    }


}