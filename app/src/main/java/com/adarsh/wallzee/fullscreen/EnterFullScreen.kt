package com.adarsh.wallzee.fullscreen

import android.content.Context
import android.content.Intent
import android.view.View
import com.adarsh.wallzee.wallpaper.WallpaperDataClass

class EnterFullScreen {

    fun enterFullScreen(
        context: Context,
        currentItem: WallpaperDataClass,
        ivWallpaper: View?,
        type: Int
    ) {
        val intent = Intent(context, FullscreenActivity::class.java)

        intent.putExtra("wallpaperName", currentItem.wallpaperName)
        intent.putExtra("wallpaperCategory", currentItem.wallpaperCategory)
        intent.putExtra("wallpaperImageUrl", currentItem.imageUrl)
        intent.putExtra("wallpaperThumbnailUrl", currentItem.thumbnailUrl)
        intent.putExtra("parentKey", currentItem.wallpaperid)
        intent.putExtra("downloads", currentItem.downloads)
        intent.putExtra("profileName", currentItem.creatorName)
        intent.putExtra("dateUploaded", currentItem.dateUploaded)
        intent.putExtra("wallpaperSize", currentItem.imageSize)
        intent.putExtra("creatorName", currentItem.creatorName)
        intent.putExtra("creatorEmail", currentItem.creatorEmail)
        intent.putExtra("isCreatorVerified", currentItem.isCreatorVerified)
        intent.putExtra("contentType", type)

        context.startActivity(intent)
    }
}