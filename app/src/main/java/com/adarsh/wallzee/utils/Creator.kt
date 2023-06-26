package com.adarsh.wallzee.utils

import androidx.recyclerview.widget.RecyclerView
import com.adarsh.wallzee.ringtone.RingtoneDataClass
import com.adarsh.wallzee.wallpaper.WallpaperDataClass
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.util.ArrayList

class Creator {
    fun getCreatorWallpapers(
        wallpaperList: ArrayList<WallpaperDataClass>,
        rvWallpapers: RecyclerView?,
        creatorEmail: String?,
    ) {
        val docRef =
            Firebase.firestore.collection("wallpapers").whereEqualTo("creatorEmail", creatorEmail)
        docRef.get().addOnSuccessListener { documents ->
            wallpaperList.clear()
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
            rvWallpapers?.adapter?.notifyItemInserted(0)
        }
    }

    fun getCreatorRingtones(
        ringtoneList: ArrayList<RingtoneDataClass>,
        rvRingtone: RecyclerView,
        creatorEmail: String?
    ) {
        val email = creatorEmail!!.subSequence(0, creatorEmail.length - 4).toString()
        val docRef = Firebase.firestore.collection("ringtones").whereEqualTo("creatorEmail", email)

        ringtoneList.clear()
        docRef.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val ringtone = document.toObject<RingtoneDataClass>()
                ringtoneList.add(
                    RingtoneDataClass(
                        ringtone.ringtoneid,
                        ringtone.ringtoneName,
                        ringtone.ringtoneDuration,
                        ringtone.ringtoneImage,
                        ringtone.ringtoneUrl,
                        ringtone.downloads,
                        ringtone.dateUploaded,
                        ringtone.ringtoneSize,
                        ringtone.creatorName,
                        ringtone.creatorEmail,
                        ringtone.creatorVerified
                    )
                )
            }
            rvRingtone.adapter?.notifyItemInserted(0)
        }
    }

    fun getCreatorLiveWallpapers(
        liveWallpaperList: ArrayList<WallpaperDataClass>,
        rvLiveWallpapers: RecyclerView,
        creatorEmail: String?
    ) {
        val email = creatorEmail!!.subSequence(0, creatorEmail.length - 4).toString()
        val docRef = Firebase.firestore.collection("live wallpapers").whereEqualTo("creatorEmail",email)
        docRef.get().addOnSuccessListener { documents ->
            liveWallpaperList.clear()
            for (document in documents) {

                val wallpaper = document.toObject<WallpaperDataClass>()
                liveWallpaperList.add(
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
            rvLiveWallpapers.adapter?.notifyItemInserted(0)
        }
    }

}