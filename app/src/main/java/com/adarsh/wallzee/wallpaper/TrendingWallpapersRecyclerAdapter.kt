package com.adarsh.wallzee.wallpaper

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.adarsh.wallzee.fullscreen.EnterFullScreen
import com.adarsh.walzee.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import java.util.ArrayList

class TrendingWallpapersRecyclerAdapter(
    private val context: Context?,
    private val trendingWallpaperList: ArrayList<WallpaperDataClass>
) : RecyclerView.Adapter<TrendingWallpapersRecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivWallpaper: ImageView = itemView.findViewById(R.id.ivWallpaper)
        val tvWallpaperName: TextView = itemView.findViewById(R.id.tvWallpaper)
        val cv: CardView = itemView.findViewById(R.id.cv)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = trendingWallpaperList[position]

        Glide
            .with(context!!)
            .load(currentItem.thumbnailUrl)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .override(450, 550)
            .transition(DrawableTransitionOptions.withCrossFade())
            .centerCrop()
            .into(holder.ivWallpaper)

        holder.ivWallpaper.setOnClickListener {
            EnterFullScreen().enterFullScreen(context, currentItem, it, 1)
        }

        holder.tvWallpaperName.text = currentItem.wallpaperName
//
//        //animation
        holder.cv.animation =
            AnimationUtils.loadAnimation(holder.itemView.context, R.anim.recycler_item_scale)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context)
            .inflate(R.layout.wallpaper_recycler_view_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return trendingWallpaperList.size
    }
}
