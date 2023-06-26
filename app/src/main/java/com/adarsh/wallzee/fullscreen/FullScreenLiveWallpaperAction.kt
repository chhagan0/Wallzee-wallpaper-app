package com.adarsh.wallzee.fullscreen


import android.Manifest
import android.app.Activity
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleCoroutineScope
import com.adarsh.wallzee.liveWallpaper.VideoLiveWallpaperService
import com.adarsh.wallzee.wallpaper.WallpaperDataClass
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class FullScreenLiveWallpaperAction {

    fun download(
        context: Context?,
        liveWallpaperLinearProgressIndicator: LinearProgressIndicator,
        liveWallpaper: WallpaperDataClass?,
        lifecycleScope: LifecycleCoroutineScope
    ) {
        liveWallpaperLinearProgressIndicator.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            val storageRef =
                Firebase.storage.getReferenceFromUrl(liveWallpaper?.imageUrl.toString())

            val localFile = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "${liveWallpaper?.wallpaperName}.mp4"
            )
            storageRef.getFile(localFile).addOnSuccessListener {
                liveWallpaperLinearProgressIndicator.visibility = View.INVISIBLE
                Toast.makeText(context, "Live Wallpaper Downloaded!", Toast.LENGTH_SHORT).show()
            }

        }
    }

    fun setLiveWallpaper(
        context: Context?,
        liveWallpaperLinearProgressIndicator: LinearProgressIndicator,
        liveWallpaper: WallpaperDataClass?,
        lifecycleScope: LifecycleCoroutineScope
    ) {

        liveWallpaperLinearProgressIndicator.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            val storageRef =
                Firebase.storage.getReferenceFromUrl(liveWallpaper?.imageUrl.toString())

            val localFile = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "${liveWallpaper?.wallpaperName}.mp4"
            )

            val deferredUri = CompletableDeferred<Uri?>()

            storageRef.getFile(localFile).addOnSuccessListener {
                liveWallpaperLinearProgressIndicator.visibility = View.INVISIBLE
                Toast.makeText(context, "Live Wallpaper Downloaded!", Toast.LENGTH_SHORT).show()

                val videoUri = Uri.fromFile(localFile)
                deferredUri.complete(videoUri)
            }.addOnFailureListener { exception ->
                deferredUri.completeExceptionally(exception)
            }

            val videoUri = deferredUri.await()

            if (videoUri != null) {
                setLiveWallpaper(context, videoUri)
            } else {
                Toast.makeText(context, "Failed to download live wallpaper.", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    private fun setLiveWallpaper(context: Context?, videoUri: Uri) {
        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, videoUri)
        context?.startActivity(intent)
    }

    /*Todo-- Task1 - set video as wallpaper*/

    fun setVideoAsWallpaper(
        context: Context,
        liveWallpaperLinearProgressIndicator: LinearProgressIndicator,
        currentItem: WallpaperDataClass,
        lifecycleScope: LifecycleCoroutineScope,
    ) {
        liveWallpaperLinearProgressIndicator.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            val storageRef = Firebase.storage.getReferenceFromUrl(currentItem.imageUrl.toString())

            val localFile = File(
                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                "${currentItem.wallpaperName}.mp4"
            )

            try {
                storageRef.getFile(localFile).addOnSuccessListener {
                    liveWallpaperLinearProgressIndicator.visibility = View.INVISIBLE
//                    Toast.makeText(context, "Live Wallpaper Downloaded!", Toast.LENGTH_SHORT).show()



                    val filePath: String? = localFile.path
                    saveFileAndSetWallpaper(context, filePath)

                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.WRITE_SETTINGS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // Request the WRITE_SETTINGS permission
                        ActivityCompat.requestPermissions(
                            context as Activity,
                            arrayOf(Manifest.permission.WRITE_SETTINGS),
                            1
                        )
                    } else {
                        // Permission is already granted
                        saveFileAndSetWallpaper(context, filePath)
                    }

                }.addOnFailureListener { exception ->
                    exception.printStackTrace()
                    Toast.makeText(
                        context,
                        "Failed to download live wallpaper",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    context,
                    "Failed to set wallpaper",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun saveFileAndSetWallpaper(context: Context, filePath: String?) {
        context.openFileOutput(
            "video_live_wallpaper_file_path",
            Context.MODE_PRIVATE
        ).use { outputStream ->
            outputStream.write(filePath?.toByteArray())
        }
        VideoLiveWallpaperService.setToWallPaper(context)
    }

//    fun setVideoAsWallpaper(
//        context: Context,
//        liveWallpaperLinearProgressIndicator: LinearProgressIndicator,
//        currentItem: WallpaperDataClass,
//        lifecycleScope: LifecycleCoroutineScope
//    ) {
//
//        liveWallpaperLinearProgressIndicator.visibility = View.VISIBLE
//        lifecycleScope.launch(Dispatchers.IO) {
//            val storageRef =
//                Firebase.storage.getReferenceFromUrl(currentItem.imageUrl.toString())
//
//            val localFile = File(
//                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
//                "${currentItem.wallpaperName}.mp4"
//            )
//            storageRef.getFile(localFile).addOnSuccessListener {
//                liveWallpaperLinearProgressIndicator.visibility = View.INVISIBLE
//                Toast.makeText(context, "Live Wallpaper Downloaded!", Toast.LENGTH_SHORT).show()
//
//                val fileUri: Uri = FileProvider.getUriForFile(
//                    context,
//                    context.packageName + ".provider",
//                    localFile
//                )
//
//                try {
//                    val wallpaperManager = WallpaperManager.getInstance(context)
//                    wallpaperManager.setWallpaperOffsetSteps(1f, 1f)
//                    wallpaperManager.setWallpaperOffsets(
//                        window.decorView.rootWindowInsets.displayCutout?.boundingRects?.get(
//                            0
//                        )
//                    )
//
//                    if (!Settings.System.canWrite(context)) {
//                        Toast.makeText(
//                            context,
//                            "Please grant the permission to set wallpaper",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        return@addOnSuccessListener
//                    }
//
//                    wallpaperManager.setVideo(fileUri)
//                    wallpaperManager.setOnWallpaperChangeListener {
//                        // Handle wallpaper change events if needed
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    Toast.makeText(
//                        context,
//                        "Failed to set wallpaper",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//
//        }
//    }

}