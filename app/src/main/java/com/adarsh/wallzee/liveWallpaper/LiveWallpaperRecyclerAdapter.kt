package com.adarsh.wallzee.liveWallpaper

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.adarsh.wallzee.fullscreen.EnterFullScreen
import com.adarsh.wallzee.wallpaper.WallpaperDataClass
import com.adarsh.walzee.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class LiveWallpaperRecyclerAdapter(
    private val context: Context?,
    private val liveWallpaperList: ArrayList<WallpaperDataClass>
) : RecyclerView.Adapter<LiveWallpaperRecyclerAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivWallpaper: ImageView = itemView.findViewById(R.id.ivWallpaper)
        val tvWallpaperName : TextView = itemView.findViewById(R.id.tvWallpaper)
        val ibPlay :ImageButton = itemView.findViewById(R.id.ibPlay)
        val cv : CardView = itemView.findViewById(R.id.cv)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context)
            .inflate(R.layout.wallpaper_recycler_view_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = liveWallpaperList[position]
        Glide
            .with(context!!)
            .load(currentItem.thumbnailUrl)
//            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .override(250, 350)
            .transition(DrawableTransitionOptions.withCrossFade())
            .centerCrop()
            .into(holder.ivWallpaper)

        holder.ivWallpaper.setOnClickListener {
            EnterFullScreen().enterFullScreen(context, currentItem, it,2)
        }

        holder.tvWallpaperName.text = currentItem.wallpaperName

    //animation
        holder.cv.animation = AnimationUtils.loadAnimation(context, R.anim.recycler_item_scale)

    //play button for live wallpapers
        holder.ibPlay.visibility = View.VISIBLE
    }

    override fun getItemCount(): Int {
        return liveWallpaperList.size
    }


}
