package com.adarsh.wallzee.utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.adarsh.wallzee.wallpaper.WallpaperDataClass
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class LiveWallpaper {

    fun getLiveWallpapers(
        wallpaperList: ArrayList<WallpaperDataClass>,
        rvWallpapers: RecyclerView,
        lottieLoadingDiamond: LottieAnimationView
    ) {
        val docRef = Firebase.firestore.collection("live wallpapers")
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
            lottieLoadingDiamond.visibility = View.GONE
            rvWallpapers.visibility = View.VISIBLE
            rvWallpapers.adapter?.notifyItemInserted(0)
        }

    }
}