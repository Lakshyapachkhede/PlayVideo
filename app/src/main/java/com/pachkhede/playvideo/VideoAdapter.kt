package com.pachkhede.playvideo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class VideoAdapter(
    private val context: Context,
    private var videoList: List<Video>,
    private val onVideoClick: (Video) -> Unit
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    class VideoViewHolder(itemHolder: View) : RecyclerView.ViewHolder(itemHolder) {
        val thumbnail: ImageView = itemHolder.findViewById(R.id.thumbnail)
        val durationTextView : TextView = itemHolder.findViewById(R.id.videoDuration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun getItemCount(): Int = videoList.size

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = videoList[position]

        Glide.with(context)
            .load(video.contentUri)
            .placeholder(R.drawable.video_thumbnail_placeholder)
            .into(holder.thumbnail)

        holder.durationTextView.text = formatDuration(video.duration)

        holder.itemView.setOnClickListener { onVideoClick(video) }
    }

    fun updateVideos(newVideos : List<Video>) {
        videoList = newVideos
        notifyDataSetChanged()
    }

    private fun formatDuration(durationMillis: Long): String {
        val seconds = (durationMillis / 1000) % 60
        val minutes = (durationMillis / (1000 * 60)) % 60
        val hours = (durationMillis / (1000 * 60 * 60))

        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds) // hh:mm:ss
        } else {
            String.format("%d:%02d", minutes, seconds) // mm:ss if less than 1 hour
        }
    }

}