package com.adarsh.wallzee.chipFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adarsh.wallzee.liveWallpaper.LiveWallpaperRecyclerAdapter
import com.adarsh.wallzee.utils.Wallpaper
import com.adarsh.wallzee.wallpaper.WallpaperDataClass
import com.adarsh.wallzee.wallpaper.WallpaperRecyclerAdapter
import com.adarsh.walzee.R
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton


class WallpaperChipFragment : Fragment() {
    private lateinit var wallpaperList: ArrayList<WallpaperDataClass>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wallpaperList = arrayListOf()

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().moveTaskToBack(false)
        }

        callback.isEnabled = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_wallpaper_chip, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val type = arguments?.getInt("type")
        val rvWallpapers = view.findViewById<RecyclerView>(R.id.rvWallpapers_1)


        rvWallpapers.layoutManager = GridLayoutManager(context, 3)
        rvWallpapers.visibility = View.GONE
        view.findViewById<LottieAnimationView>(R.id.lottie_loading_diamond).visibility =
            View.VISIBLE

        view.findViewById<MaterialButton>(R.id.btSortBy).setOnClickListener {
            Wallpaper().showMenu(requireContext(),it, R.menu.sort_by_menu,wallpaperList,rvWallpapers)
        }

        if (type == 1) {
            rvWallpapers.adapter = context?.let { WallpaperRecyclerAdapter(it, wallpaperList) }
            Wallpaper().getWallpapers(
                wallpaperList,
                rvWallpapers,
                view.findViewById(R.id.lottie_loading_diamond),
            )
        } else {
            rvWallpapers.adapter = LiveWallpaperRecyclerAdapter(context, wallpaperList)
            Wallpaper().getLiveWallpapers(
                wallpaperList, rvWallpapers, view.findViewById(R.id.lottie_loading_diamond)
            )
        }
    }
}