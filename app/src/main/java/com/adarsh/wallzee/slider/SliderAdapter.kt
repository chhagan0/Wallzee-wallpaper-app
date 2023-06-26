package com.adarsh.wallzee.slider

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.adarsh.walzee.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.smarteist.autoimageslider.SliderViewAdapter
import kotlin.collections.ArrayList


class SliderAdapter(
    private val context: Context?,
    private val bannerList: ArrayList<SliderDataClass>
) :
    SliderViewAdapter<SliderAdapter.MyViewHolder>() {
    override fun getCount(): Int {
        return bannerList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.slider_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder?, position: Int) {
        val currentItem = bannerList[position]

        if (viewHolder != null) {
            Glide
                .with(context!!)
                .load(currentItem.imageUrl)
                .override(700, 500)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .into(viewHolder.ivBanner)

            viewHolder.ivBanner.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(currentItem.link)
                context.startActivity(intent)
            }
        }


    }


    class MyViewHolder(itemView: View) : ViewHolder(itemView) {
        val ivBanner: ImageView = itemView.findViewById(R.id.ivBanner)
        val cv: CardView? = itemView.findViewById<CardView>(R.id.cardView)
    }

}


