package com.adarsh.wallzee.category

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adarsh.wallzee.utils.Wallpaper
import com.adarsh.wallzee.wallpaper.WallpaperDataClass
import com.adarsh.wallzee.wallpaper.WallpaperRecyclerAdapter
import com.adarsh.walzee.R
import com.google.android.material.appbar.MaterialToolbar

class CategoryFullScreenFragment : Fragment() {
    private lateinit var wallpaperList: ArrayList<WallpaperDataClass>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wallpaperList = arrayListOf()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_category_full_screen, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoryName = arguments?.getString("categoryName")
        activity?.findViewById<MaterialToolbar>(R.id.toolbar)!!.title = categoryName

        val rvWallpapers = view.findViewById<RecyclerView>(R.id.category_fullscreen_recycler_layout)

        rvWallpapers.layoutManager = GridLayoutManager(context, 3)
        rvWallpapers.adapter = context?.let { WallpaperRecyclerAdapter(it, wallpaperList) }

        Wallpaper().getWallpaperFromCategory(wallpaperList, rvWallpapers, categoryName)
    }
}