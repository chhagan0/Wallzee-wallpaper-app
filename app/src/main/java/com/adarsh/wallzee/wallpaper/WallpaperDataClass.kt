package com.adarsh.wallzee.wallpaper


data class WallpaperDataClass(
    var wallpaperid: Int? = null,
    var thumbnailUrl: String? = null,
    var imageUrl: String? = null,
    var wallpaperName: String? = null,
    var wallpaperCategory: String? = null,
    var downloads: Int = 1,
    var dateUploaded: String? = null,
    var imageSize: Int? = null,
    var creatorName: String? = null,
    var creatorEmail: String? = null,
    var isCreatorVerified: Boolean = false,
)