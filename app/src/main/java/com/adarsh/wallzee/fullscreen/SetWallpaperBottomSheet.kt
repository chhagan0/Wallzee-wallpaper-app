package com.adarsh.wallzee.fullscreen

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.adarsh.wallzee.wallpaper.WallpaperDataClass
import com.adarsh.walzee.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.progressindicator.LinearProgressIndicator

class SetWallpaperBottomSheet(
    private val parent: Context,
    private val currentItem: WallpaperDataClass,
    private val linearProgressIndicator: LinearProgressIndicator,
) : BottomSheetDialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_set_wallpaper_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonClicks()
    }

    private fun buttonClicks() {
        val tvSetToHomeScreen = view?.findViewById<TextView>(R.id.tvSetToHomeScreen)
        val tvSetToLockScreen = view?.findViewById<TextView>(R.id.tvSetToLockScreen)
        val tvSetToBothScreen = view?.findViewById<TextView>(R.id.tvSetToBothScreen)
        val tvCloseBottomSheet = view?.findViewById<TextView>(R.id.tvCloseBottomSheet)
        val tvCropAndSetWallpaper = view?.findViewById<TextView>(R.id.tvCropAndSetWallpaper)

        tvCloseBottomSheet?.setOnClickListener { this.dismiss() }
        tvSetToHomeScreen?.setOnClickListener {
            currentItem.imageUrl?.let { it1 ->
                currentItem.wallpaperName?.let { it2 ->
                    FullScreenWallpaperAction(parent).setWallpaper(
                        it1,
                        linearProgressIndicator,
                        0,
                        this,
                        it2,
                        lifecycleScope
                    )
                }
            }
        }

        tvSetToLockScreen?.setOnClickListener {
            currentItem.imageUrl?.let { it1 ->
                currentItem.wallpaperName?.let { it2 ->
                    FullScreenWallpaperAction(parent).setWallpaper(
                        it1,
                        linearProgressIndicator,
                        1,
                        this,
                        it2,
                        lifecycleScope,
                    )
                }
            }
        }

        tvSetToBothScreen?.setOnClickListener {
            currentItem.imageUrl?.let { it1 ->
                currentItem.wallpaperName?.let { it2 ->
                    FullScreenWallpaperAction(parent).setWallpaper(
                        it1,
                        linearProgressIndicator,
                        2,
                        this,
                        it2,
                        lifecycleScope,
                    )
                }
            }
        }

        tvCropAndSetWallpaper?.setOnClickListener {
            currentItem.imageUrl?.let { it1 ->
                currentItem.wallpaperName?.let { it2 ->
                    FullScreenWallpaperAction(parent).setWallpaper(
                        it1,
                        linearProgressIndicator,
                        3,
                        this,
                        it2,
                        lifecycleScope,
                    )
                }
            }
        }

    }

}