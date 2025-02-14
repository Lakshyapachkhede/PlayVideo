package com.pachkhede.playvideo

import android.net.Uri

data class Video(
    val id : Long,
    val title : String,
    val contentUri : Uri,
    val duration : Long,
    val size : Long
)
