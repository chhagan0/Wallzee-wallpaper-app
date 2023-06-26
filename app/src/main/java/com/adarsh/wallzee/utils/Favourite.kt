package com.adarsh.wallzee.utils

import android.content.Context
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.adarsh.wallzee.ringtone.RingtoneDataClass
import com.adarsh.wallzee.wallpaper.WallpaperDataClass
import com.adarsh.walzee.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.util.ArrayList


class Favourite {

    private val db = Firebase.firestore
    val email = Firebase.auth.currentUser?.email.toString()
    private val favRef = db.collection("favourites").document(email)

    fun isFavourite(
        type: Int?, id: Int?, bt: MaterialButton?, favouriteIcon: ImageView?, context: Context
    ) {
        val contentType =
            if (type == 2) "live wallpapers" else if (type == 3) "ringtones" else "wallpapers"

        favRef.collection(contentType).document(id.toString()).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document.exists()) {
                    bt?.setIconResource(R.drawable.heart_filled)
                    favouriteIcon?.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            context.resources, R.drawable.heart_filled, null
                        )
                    )
                } else {
                    bt?.setIconResource(R.drawable.heart_outline)
                    favouriteIcon?.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            context.resources, R.drawable.heart_outline, null
                        )
                    )
                    //                        Log.d("savitha", "Document does not exist!")
                }
            }
        }
    }


    fun checkFavourites(
        context: Context?, type: Int?, id: Int?, bt: MaterialButton?, iv: ImageView?
    ) {

        val contentType =
            if (type == 2) "live wallpapers" else if (type == 3) "ringtones" else "wallpapers"

        favRef.collection(contentType).document(id.toString()).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document.exists()) {

                    removeFromFavourites(context, type, id, bt, iv)
                } else {
                    addToFavourites(context, type, id, bt, iv)
                }
            }
        }

    }

    private fun addToFavourites(
        context: Context?, type: Int?, id: Int?, bt: MaterialButton?, iv: ImageView?
    ) {
        val data = hashMapOf(
            "id" to id,
        )

        val contentType =
            if (type == 2) "live wallpapers" else if (type == 3) "ringtones" else "wallpapers"

        Toast.makeText(
            context, "Added to favourites", Toast.LENGTH_SHORT
        ).show()
        favRef.collection(contentType).document(id.toString()).set(data)
        bt?.setIconResource(R.drawable.heart_filled)
        iv?.setImageDrawable(context?.let {
            ResourcesCompat.getDrawable(
                it.resources, R.drawable.heart_filled, null
            )
        })

    }

    private fun removeFromFavourites(
        context: Context?, type: Int?, id: Int?, bt: MaterialButton?, iv: ImageView?
    ) {
        val contentType =
            if (type == 2) "live wallpapers" else if (type == 3) "ringtones" else "wallpapers"

        favRef.collection(contentType).document(id.toString()).delete()
        bt?.setIconResource(R.drawable.heart_outline)
        Toast.makeText(
            context, "Removed from favourites", Toast.LENGTH_SHORT
        ).show()
        iv?.setImageDrawable(context?.let {
            ResourcesCompat.getDrawable(
                it.resources, R.drawable.heart_outline, null
            )
        })

    }

    fun getFavouriteWallpapers(
        wallpaperList: ArrayList<WallpaperDataClass>, rvWallpapers: RecyclerView, email: String
    ) {
        val docRef =
            Firebase.firestore.collection("favourites").document(email).collection("wallpapers")
        docRef.get().addOnSuccessListener { documents ->
            for (document in documents) {
                wallpaperList.clear()
                Firebase.firestore.collection("wallpapers").document(document.id).get()
                    .addOnSuccessListener { wallpaperData ->
                        val wallpaper = wallpaperData.toObject<WallpaperDataClass>()
                        if (wallpaper != null) {
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
                            rvWallpapers.adapter?.notifyItemInserted(0)
                        }
                    }
            }
        }
    }

    fun getFavouriteLiveWallpapers(
        wallpaperList: ArrayList<WallpaperDataClass>, rvWallpapers: RecyclerView, email: String
    ) {
        val docRef = Firebase.firestore.collection("favourites").document(email)
            .collection("live wallpapers")
        docRef.get().addOnSuccessListener { documents ->
            for (document in documents) {
                wallpaperList.clear()
                Firebase.firestore.collection("live wallpapers").document(document.id).get()
                    .addOnSuccessListener { wallpaperData ->
                        val wallpaper = wallpaperData.toObject<WallpaperDataClass>()
                        if (wallpaper != null) {
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
                            rvWallpapers.adapter?.notifyItemInserted(0)
                        }
                    }
            }
        }
    }

    fun getFavouriteRingtones(
        ringtoneList: ArrayList<RingtoneDataClass>, rvRingtone: RecyclerView, email: String
    ) {
        val docRef = Firebase.firestore.collection("favourites").document(email)
            .collection("ringtones")
        docRef.get().addOnSuccessListener { documents ->
            for (document in documents) {
                ringtoneList.clear()
                Firebase.firestore.collection("ringtones").document(document.id).get()
                    .addOnSuccessListener { wallpaperData ->
                        val ringtone = wallpaperData.toObject<RingtoneDataClass>()
                        if (ringtone != null) {
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
                            rvRingtone.adapter?.notifyItemInserted(0)
                        }
                    }
            }
        }
    }
}