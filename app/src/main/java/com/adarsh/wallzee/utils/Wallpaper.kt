package com.adarsh.wallzee.utils

import android.content.Context
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.recyclerview.widget.RecyclerView
import com.adarsh.wallzee.wallpaper.WallpaperDataClass
import com.adarsh.walzee.R
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class Wallpaper {

    fun getWallpapers(
        wallpaperList: ArrayList<WallpaperDataClass>,
        rvWallpapers: RecyclerView,
        lottieLoadingDiamond: LottieAnimationView,
    ) {
        val docRef =
            Firebase.firestore.collection("wallpapers").whereEqualTo("creatorVerified", true)
        docRef.get().addOnSuccessListener { documents ->
//
//            if (documents.isEmpty) {
////                binding.tvNewQuestionsComingSoon.visibility = View.VISIBLE
//            }

            for (document in documents) {

                val wallpaper = document.toObject<WallpaperDataClass>()
                wallpaperList.add(
                    WallpaperDataClass(
                        wallpaper.wallpaperid,
                        wallpaper.thumbnailUrl,
                        wallpaper.imageUrl,
                        wallpaper.wallpaperName,
                        wallpaper.wallpaperCategory,
                        wallpaper.downloads,
                        wallpaper.dateUploaded,
                        wallpaper.imageSize,
                        wallpaper.creatorName,
                        wallpaper.creatorEmail,
                        wallpaper.isCreatorVerified
                    )
                )
            }
            lottieLoadingDiamond.visibility = View.GONE
            rvWallpapers.visibility = View.VISIBLE
            rvWallpapers.adapter?.notifyItemInserted(0)
        }

    }

    fun getWallpaperFromCategory(
        wallpaperList: java.util.ArrayList<WallpaperDataClass>,
        rvWallpapers: RecyclerView?,
        categoryName: String?
    ) {

        val docRef = Firebase.firestore.collection("wallpapers")
            .whereEqualTo("wallpaperCategory", categoryName)
        docRef.get().addOnSuccessListener { documents ->
//
//            if (documents.isEmpty) {
////                binding.tvNewQuestionsComingSoon.visibility = View.VISIBLE
//            }
            for (document in documents) {

                val wallpaper = document.toObject<WallpaperDataClass>()
                wallpaperList.add(
                    WallpaperDataClass(
                        wallpaper.wallpaperid,
                        wallpaper.thumbnailUrl,
                        wallpaper.imageUrl,
                        wallpaper.wallpaperName,
                        wallpaper.wallpaperCategory,
                        wallpaper.downloads,
                        wallpaper.dateUploaded,
                        wallpaper.imageSize,
                        wallpaper.creatorName,
                        wallpaper.creatorEmail,
                        wallpaper.isCreatorVerified
                    )
                )
            }
//            Toast.makeText(context, wallpaperList.toString(), Toast.LENGTH_SHORT).show()
            if (rvWallpapers != null) {
                rvWallpapers.adapter?.notifyItemInserted(0)
            }
        }
    }

    fun getLiveWallpapers(
        wallpaperList: java.util.ArrayList<WallpaperDataClass>,
        rvWallpapers: RecyclerView?,
        lottieLoadingDiamond: LottieAnimationView
    ) {
        val docRef =
            Firebase.firestore.collection("live wallpapers").whereEqualTo("creatorVerified", true)
        docRef.get().addOnSuccessListener { documents ->

            for (document in documents) {

                val wallpaper = document.toObject<WallpaperDataClass>()
                wallpaperList.add(
                    WallpaperDataClass(
                        wallpaper.wallpaperid,
                        wallpaper.thumbnailUrl,
                        wallpaper.imageUrl,
                        wallpaper.wallpaperName,
                        wallpaper.wallpaperCategory,
                        wallpaper.downloads,
                        wallpaper.dateUploaded,
                        wallpaper.imageSize,
                        wallpaper.creatorName,
                        wallpaper.creatorEmail,
                        wallpaper.isCreatorVerified
                    )
                )
            }

            if (rvWallpapers != null) {
                rvWallpapers.visibility = View.VISIBLE
                lottieLoadingDiamond.visibility = View.GONE
                rvWallpapers.adapter?.notifyItemInserted(0)
            }
        }
    }

    fun showMenu(
        context: Context,
        v: View,
        @MenuRes menuRes: Int,
        wallpaperList: ArrayList<WallpaperDataClass>,
        rvWallpapers: RecyclerView
    ) {
        val popup = PopupMenu(context, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            // Respond to menu item click.
            when (menuItem.itemId) {
                R.id.action_wallpaper_new_to_old -> {
                    wallpaperList.sortByDescending { it.dateUploaded }
                    rvWallpapers.adapter?.notifyItemRangeChanged(0, wallpaperList.size)
                    true
                }

                R.id.action_wallpaper_popular -> {
                    wallpaperList.sortByDescending { it.downloads }
                    rvWallpapers.adapter?.notifyItemRangeChanged(0, wallpaperList.size)
                    true
                }

                else -> {
                    false
                }
            }
        }
        // Show the popup menu.
        popup.show()
    }

}