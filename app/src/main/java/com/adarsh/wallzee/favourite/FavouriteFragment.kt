package com.adarsh.wallzee.favourite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.adarsh.wallzee.liveWallpaper.LiveWallpaperRecyclerAdapter
import com.adarsh.wallzee.ringtone.RingtoneDataClass
import com.adarsh.wallzee.ringtone.RingtoneFunctions
import com.adarsh.wallzee.ringtone.RingtoneRecyclerAdapter
import com.adarsh.wallzee.utils.Favourite
import com.adarsh.wallzee.wallpaper.WallpaperDataClass
import com.adarsh.wallzee.wallpaper.WallpaperRecyclerAdapter
import com.adarsh.walzee.R
import com.adarsh.walzee.databinding.FragmentFavouriteBinding
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class FavouriteFragment : Fragment() {
    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var wallpaperList: ArrayList<WallpaperDataClass>
    private lateinit var liveWallpaperList: ArrayList<WallpaperDataClass>
    private lateinit var ringtoneList: ArrayList<RingtoneDataClass>
    private val ringtoneFunctions = RingtoneFunctions()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wallpaperList = arrayListOf()
        liveWallpaperList = arrayListOf()
        ringtoneList = arrayListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rvWallpapers = binding.rvWallpapers
        val rvLiveWallpapers = binding.rvLiveWallpapers
        val rvRingtone = binding.rvRingtones
        val email = Firebase.auth.currentUser?.email

        if (email == null)
            findNavController().navigate(R.id.loginFragment)

        rvWallpapers.layoutManager = GridLayoutManager(context, 3)
        rvWallpapers.adapter = context?.let { WallpaperRecyclerAdapter(it, wallpaperList) }
        if (email != null) {
            Favourite().getFavouriteWallpapers(
                wallpaperList,
                rvWallpapers,
                email,
            )
        }
        rvLiveWallpapers.layoutManager = GridLayoutManager(context, 3)
        rvLiveWallpapers.adapter = LiveWallpaperRecyclerAdapter(context, liveWallpaperList)
        if (email != null) {
            Favourite().getFavouriteLiveWallpapers(
                liveWallpaperList,
                rvLiveWallpapers,
                email,
            )
        }

        rvRingtone.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvRingtone.adapter = RingtoneRecyclerAdapter(context, ringtoneList, findNavController())

        if (email != null) {
            Favourite().getFavouriteRingtones(ringtoneList, rvRingtone, email)
        }

        binding.tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val message = "Selected tab: ${tab.text}"
                when (tab.position) {
                    0 -> {
                        rvWallpapers.visibility = View.VISIBLE
                        rvLiveWallpapers.visibility = View.GONE
                        rvRingtone.visibility = View.GONE

                    }

                    1 -> {
                        rvLiveWallpapers.visibility = View.VISIBLE
                        rvWallpapers.visibility = View.GONE
                        rvRingtone.visibility = View.GONE
                    }

                    2 -> {
                        rvWallpapers.visibility = View.GONE
                        rvLiveWallpapers.visibility = View.GONE
                        rvRingtone.visibility = View.VISIBLE
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    override fun onPause() {
        super.onPause()
      ringtoneFunctions.releaseMediaPlayer()
    }

    override fun onStop() {
        super.onStop()
        ringtoneFunctions.releaseMediaPlayer()

    }


}