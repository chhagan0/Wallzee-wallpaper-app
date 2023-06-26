package com.adarsh.wallzee.category

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.adarsh.walzee.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class CategoryRecyclerAdapter(
    val context: Context?,
    private val categoryList: ArrayList<CategoryDataClass>,
    val findNavController: NavController
) :
    RecyclerView.Adapter<CategoryRecyclerAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivCategoryImage: ImageView = itemView.findViewById(R.id.ivCategoryImage)
        val tvCategoryName: TextView = itemView.findViewById(R.id.tvCategoryName)
        val cv: CardView = itemView.findViewById(R.id.cv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context)
            .inflate(R.layout.category_recycler_view_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = categoryList[position]

        //animation
        holder.cv.animation = AnimationUtils.loadAnimation(context, R.anim.recycler_item_scale)

        Glide
            .with(context!!)
            .load(currentItem.categoryImageUrl)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//            .override(200, 100)
            .transition(DrawableTransitionOptions.withCrossFade())
            .centerCrop()
            .into(holder.ivCategoryImage)

        holder.tvCategoryName.text = currentItem.categoryName

        holder.ivCategoryImage.setOnClickListener {
            findNavController.navigate(
                R.id.categoryFullScreenFragment,
                bundleOf("categoryName" to currentItem.categoryName)
            )
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

}


