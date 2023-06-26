package com.adarsh.wallzee.search

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.TextView
import androidx.annotation.MenuRes
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adarsh.wallzee.liveWallpaper.LiveWallpaperRecyclerAdapter
import com.adarsh.wallzee.ringtone.RingtoneDataClass
import com.adarsh.wallzee.ringtone.RingtoneRecyclerAdapter
import com.adarsh.wallzee.wallpaper.WallpaperDataClass
import com.adarsh.wallzee.wallpaper.WallpaperRecyclerAdapter
import com.adarsh.walzee.R
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase


class SearchFragment : Fragment() {
    private var searchQuery: String? = null
    private lateinit var wallpaperList: ArrayList<WallpaperDataClass>
    private lateinit var ringtoneList: ArrayList<RingtoneDataClass>
    private var tvNoMatch: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        wallpaperList = arrayListOf()
        ringtoneList = arrayListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btFilter = view.findViewById<MaterialButton>(R.id.btFilter)

        if (searchQuery != null) {
            view.findViewById<LottieAnimationView>(R.id.lottie_search_no_query).visibility =
                View.GONE
        } else {
            view.findViewById<LottieAnimationView>(R.id.lottie_search_no_query).visibility =
                View.VISIBLE
        }

        tvNoMatch = view.findViewById<TextView>(R.id.tvNoMatch)
        btFilter.setOnClickListener {
            showMenu(it, R.menu.filter_menu)
        }
        val searchView = view.findViewById<SearchView>(R.id.SearchView)
        searchView.setOnClickListener { searchView.isIconified = false }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                val searchLayout = view.findViewById<LinearLayout>(R.id.searchLayout)
                val imm =
                    context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(searchLayout.windowToken, 0)
                searchQuery = p0?.lowercase().toString()
                view.findViewById<LottieAnimationView>(R.id.lottie_search_no_query).visibility =
                    View.GONE
                search(searchQuery, 1)
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }
        })
    }

    private fun search(searchQuery: String?, type: Int) {
        val rv = view?.findViewById<RecyclerView>(R.id.rvSearch)
        val loadingAnimation = view?.findViewById<LottieAnimationView>(R.id.lottie_loading_search)
        loadingAnimation?.visibility = View.VISIBLE
        tvNoMatch!!.visibility = View.GONE
        var content = "wallpapers"
        var contentName = "wallpaperName"
        content = when (type) {
            2 -> "live wallpapers"
            3 -> "ringtones"
            else -> "wallpapers"
        }

        contentName = when (type) {
            3 -> "ringtoneName"
            else -> "wallpaperName"
        }

        Firebase.firestore.collection(content)
            .whereGreaterThanOrEqualTo(contentName, searchQuery!!).orderBy(contentName)
            .limit(25)
            .get()
            .addOnSuccessListener { documents ->
                loadingAnimation?.clearAnimation()
                loadingAnimation!!.visibility = View.GONE

                when (type) {
                    1 -> {
                        getWallpapers(documents, rv)
                    }

                    2 -> {
                        getLiveWallpapers(documents, rv)
                    }

                    3 -> getRingtones(documents, rv)

                    else -> {}
                }
            }
    }

    private fun getRingtones(documents: QuerySnapshot?, rv: RecyclerView?) {
        if (documents != null) {
            rv?.layoutManager = LinearLayoutManager(context)
            rv?.adapter = null
            ringtoneList.clear()
            for (document in documents) {
                val ringtone = document.toObject<RingtoneDataClass>()
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
            }
        } else {
            tvNoMatch!!.visibility = View.VISIBLE
        }
        rv?.adapter = RingtoneRecyclerAdapter(context, ringtoneList, findNavController())
    }

    private fun getLiveWallpapers(documents: QuerySnapshot?, rv: RecyclerView?) {
        if (documents != null) {
            rv?.layoutManager = GridLayoutManager(context, 3)
            wallpaperList.clear()
            rv?.adapter = null
            for (document in documents) {
                val wallpaper = document.toObject<WallpaperDataClass>()
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

            }
        } else {
            tvNoMatch!!.visibility = View.VISIBLE
        }
        rv?.adapter = LiveWallpaperRecyclerAdapter(context, wallpaperList)
    }

    private fun getWallpapers(documents: QuerySnapshot?, rv: RecyclerView?) {
        if (documents != null) {
            rv?.layoutManager = GridLayoutManager(context, 3)
            wallpaperList.clear()
            rv?.adapter = null
            for (document in documents) {
                val wallpaper = document.toObject<WallpaperDataClass>()
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

            }
        } else {
            tvNoMatch!!.visibility = View.VISIBLE
        }
        rv?.adapter = WallpaperRecyclerAdapter(context, wallpaperList)
    }


    private fun showMenu(v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(requireContext(), v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->

            // Respond to menu item click.

            when (menuItem.itemId) {
                R.id.action_wallpaper -> {
                    search(searchQuery, 1)
                    true
                }

                R.id.action_live_wallpaper -> {
                    search(searchQuery, 2)
                    true
                }

                R.id.action_ringtones -> {
                    search(searchQuery, 3)
                    true
                }

                else -> {
                    false
                }
            }
        }
        // Show the popup menu.
        popup.show()
    }
}