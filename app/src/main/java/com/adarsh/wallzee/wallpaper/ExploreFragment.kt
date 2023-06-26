package com.adarsh.wallzee.wallpaper

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.adarsh.wallzee.liveWallpaper.LiveWallpaperRecyclerAdapter
import com.adarsh.wallzee.utils.LiveWallpaper
import com.adarsh.wallzee.utils.Wallpaper
import com.adarsh.walzee.R
import com.adarsh.walzee.databinding.FragmentExploreBinding

class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!
    private lateinit var wallpaperList: ArrayList<WallpaperDataClass>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wallpaperList = arrayListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val type = arguments?.getInt("type")
        val rvWallpapers = binding.wallpaperRecyclerViewLayout.rvWallpapers
        rvWallpapers.layoutManager = GridLayoutManager(context, 3)

        binding.lottieLoadingDiamond.visibility = View.VISIBLE
        binding.wallpaperRecyclerViewLayout.rvWallpapers.visibility = View.GONE

        binding.btSortBy.setOnClickListener {
            Wallpaper().showMenu(requireContext(),it, R.menu.sort_by_menu,wallpaperList,binding.wallpaperRecyclerViewLayout.rvWallpapers)
        }

        if (type == 2) {
            rvWallpapers.adapter = LiveWallpaperRecyclerAdapter(context, wallpaperList)
            LiveWallpaper().getLiveWallpapers(
                wallpaperList,
                binding.wallpaperRecyclerViewLayout.rvWallpapers,binding.lottieLoadingDiamond
            )
        } else {
            rvWallpapers.adapter = WallpaperRecyclerAdapter(context, wallpaperList)
            Wallpaper().getWallpapers(
                wallpaperList,
                binding.wallpaperRecyclerViewLayout.rvWallpapers,binding.lottieLoadingDiamond,
            )
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}