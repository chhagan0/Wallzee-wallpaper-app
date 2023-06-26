package com.adarsh.wallzee.creator

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.adarsh.walzee.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class CreatorRecyclerAdapter(
    private val context: Context?,
    private val creatorList: ArrayList<CreatorDataClass>
) : RecyclerView.Adapter<CreatorRecyclerAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivCreatorProfile: ImageView = itemView.findViewById(R.id.ivCreatorProfile)
        val cv: CardView = itemView.findViewById(R.id.cv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context)
            .inflate(R.layout.creator_recycler_view_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = creatorList[position]

        Glide
            .with(context!!)
            .load(currentItem.creatorProfileUrl)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .override(200, 200)
            .transition(DrawableTransitionOptions.withCrossFade())
            .centerCrop()
            .into(holder.ivCreatorProfile)

        //animation
        holder.cv.animation =
            AnimationUtils.loadAnimation(holder.itemView.context, R.anim.recycler_item_scale)

        holder.cv.setOnClickListener {
            it.findNavController().navigate(
                R.id.creatorProfileFragment,
                bundleOf("creatorEmail" to currentItem.creatorEmail)
            )
        }
    }

    override fun getItemCount(): Int {
        return creatorList.size
    }

}
