package com.adarsh.wallzee.fullscreen

import android.app.WallpaperManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URL

class FullScreenWallpaperAction(val context: Context) {

    fun setWallpaper(
        wallpaperImageUrl: String,
        linearProgressIndicator: LinearProgressIndicator,
        wallpaperType: Int,
        setWallpaperBottomSheet: SetWallpaperBottomSheet,
        wallpaperName: String,
        lifecycleScope: LifecycleCoroutineScope,
    ) {

//        linearProgressIndicator.visibility = View.VISIBLE
//        linearProgressIndicator.progress = 50

        val wallpaperManager = WallpaperManager.getInstance(context)

        when (wallpaperType) {
            0 -> {
                val wallpaperJob = lifecycleScope.launch(Dispatchers.IO) {
                    val inputStream = URL(wallpaperImageUrl).openStream()
                    wallpaperManager.setStream(
                        inputStream,
                        null,
                        true,
                        WallpaperManager.FLAG_SYSTEM
                    )
                }
                setWallpaperBottomSheet.dismiss()
//                Toast.makeText(context, "HomeScreen Wallpaper Set", Toast.LENGTH_SHORT)
//                    .show()
                if (wallpaperJob.isCompleted) {
                    linearProgressIndicator.clearAnimation()
                    linearProgressIndicator.visibility = View.GONE
                    Toast.makeText(context, "HomeScreen Wallpaper Set", Toast.LENGTH_SHORT)
                        .show()
                }

            }

            1 -> {
                val wallpaperJob = lifecycleScope.launch(Dispatchers.IO) {
                    val inputStream = URL(wallpaperImageUrl).openStream()
                    wallpaperManager.setStream(
                        inputStream,
                        null,
                        true,
                        WallpaperManager.FLAG_LOCK
                    )

                }
                setWallpaperBottomSheet.dismiss()

                if (wallpaperJob.isCompleted) {
                    linearProgressIndicator.clearAnimation()
                    linearProgressIndicator.visibility = View.GONE
                    Toast.makeText(context, "Lockscreen Wallpaper Set", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            2 -> {
                val wallpaperJob = lifecycleScope.launch(Dispatchers.IO) {
                    val inputStream = URL(wallpaperImageUrl).openStream()
                    wallpaperManager.setStream(
                        inputStream
                    )

                }
                setWallpaperBottomSheet.dismiss()

                if (wallpaperJob.isCompleted) {
                    linearProgressIndicator.clearAnimation()
                    linearProgressIndicator.visibility = View.GONE
                    Toast.makeText(context, "Wallpaper Set", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            3 -> {
                lifecycleScope.launch(Dispatchers.IO) {

                    val inputStream = URL(wallpaperImageUrl).openStream()
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    cropAndSetWallpaper(bitmap, wallpaperName, linearProgressIndicator)

                }
                setWallpaperBottomSheet.dismiss()
            }
        }
    }

    private fun getImageUri(resource: Bitmap, wallpaperName: String): Uri? {
        val bytes = ByteArrayOutputStream()
        resource.compress(Bitmap.CompressFormat.JPEG, 100, bytes)

        val path = MediaStore.Images.Media.insertImage(
            context.contentResolver,
            resource,
            wallpaperName,
            "Downloaded From Wallbyte"
        )!!

        return Uri.parse(path)
    }

    private fun cropAndSetWallpaper(
        bitmap: Bitmap?,
        wallpaperName: String?,
        linearProgressIndicator: LinearProgressIndicator?
    ) {
        val intent = Intent(WallpaperManager.ACTION_CROP_AND_SET_WALLPAPER);
        val mime = "image/*";
        intent.setDataAndType(
            wallpaperName?.let { bitmap?.let { it1 -> getImageUri(it1, it) } },
            mime
        );
        try {
            context.startActivity(intent)
            linearProgressIndicator?.clearAnimation()
            if (linearProgressIndicator != null) {
                linearProgressIndicator.visibility = View.GONE
            }
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "Suitable App not found!", Toast.LENGTH_SHORT)
                .show()
        }

    }

    fun downloadWallpaper(
        wallpaperImageUrl: String,
        key: Int?,
        downloads: Int,
        context: Context,
        wallpaperName: String,
        downloadAnimation: LottieAnimationView,
        linearProgressIndicator: LinearProgressIndicator
    ) {

        linearProgressIndicator.visibility = View.VISIBLE
        linearProgressIndicator.progress = 50

        val storageRef = Firebase.storage.getReferenceFromUrl(wallpaperImageUrl)

        val localFile = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), "$wallpaperName.png"
        )

        storageRef.getFile(localFile).addOnSuccessListener {
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val contentUri = Uri.fromFile(localFile)
            mediaScanIntent.data = contentUri
            context.sendBroadcast(mediaScanIntent)

            linearProgressIndicator.progress = 100
            linearProgressIndicator.visibility = View.INVISIBLE

            Toast.makeText(context, "Wallpaper Downloaded!", Toast.LENGTH_SHORT).show()

        }.addOnFailureListener {
            linearProgressIndicator.progress = 100
            linearProgressIndicator.visibility = View.INVISIBLE
            Toast.makeText(context, "Download failed! Try again later", Toast.LENGTH_SHORT).show()
        }

    }

    fun shareWallpaper(wallpaperThumbnailUrl: String, wallpaperName: String) {
        Glide.with(context)
            .asBitmap()
            .load(wallpaperThumbnailUrl)
            .into(object : CustomTarget<Bitmap>() {

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val imageUri = getImageUri(resource, wallpaperName)

                    val shareIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        type = "*/*"
                        putExtra(Intent.EXTRA_STREAM, imageUri)
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "wallpapers from wallzee"
                        )

                    }
                    shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    context.startActivity(Intent.createChooser(shareIntent, null))
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    fun deleteWallpaper(wallpaperid: Int?, type: Int) {
        val content =
            if (type == 2) "live wallpapers" else if (type == 3) "ringtones" else "wallpapers"
        Firebase.firestore.collection(content).document(wallpaperid.toString())
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this.context, "Wallpaper deleted successfully!", Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this.context,
                    e.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


}
