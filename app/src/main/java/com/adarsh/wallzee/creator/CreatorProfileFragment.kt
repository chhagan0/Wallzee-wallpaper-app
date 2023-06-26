package com.adarsh.wallzee.creator

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.adarsh.wallzee.liveWallpaper.LiveWallpaperRecyclerAdapter
import com.adarsh.wallzee.ringtone.RingtoneDataClass
import com.adarsh.wallzee.ringtone.RingtoneRecyclerAdapter
import com.adarsh.wallzee.utils.Creator
import com.adarsh.wallzee.wallpaper.WallpaperDataClass
import com.adarsh.wallzee.wallpaper.WallpaperRecyclerAdapter
import com.adarsh.walzee.R
import com.adarsh.walzee.databinding.FragmentCreatorProfileBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase


class CreatorProfileFragment : Fragment() {
    private var _binding: FragmentCreatorProfileBinding? = null
    private val binding get() = _binding!!
    private var creatorEmail: String? = null
    private lateinit var wallpaperList: ArrayList<WallpaperDataClass>
    private lateinit var liveWallpaperList: ArrayList<WallpaperDataClass>
    private lateinit var ringtoneList: ArrayList<RingtoneDataClass>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        wallpaperList = arrayListOf()
        liveWallpaperList = arrayListOf()
        ringtoneList = arrayListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatorProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvWallpapers = binding.rvWallpapers
        val rvLiveWallpapers = binding.rvLiveWallpapers
        val rvRingtone = binding.rvRingtones

        creatorEmail = arguments?.getString("creatorEmail", Firebase.auth.currentUser?.email)

        creatorEmail!!.substring(0, creatorEmail!!.length - 4)

        binding.ibMenu.setOnClickListener {
            showMenu(it, R.menu.creator_profile_menu)
        }

        rvWallpapers.layoutManager = GridLayoutManager(context, 3)
        rvWallpapers.adapter = WallpaperRecyclerAdapter(context, wallpaperList)
        Creator().getCreatorWallpapers(
            wallpaperList,
            rvWallpapers,
            creatorEmail,
        )

        rvLiveWallpapers.layoutManager = GridLayoutManager(context, 3)
        rvLiveWallpapers.adapter = LiveWallpaperRecyclerAdapter(context, liveWallpaperList)
        Creator().getCreatorLiveWallpapers(
            liveWallpaperList,
            rvLiveWallpapers,
            creatorEmail,
        )

        rvRingtone.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvRingtone.adapter = RingtoneRecyclerAdapter(context, ringtoneList, findNavController())

        Creator().getCreatorRingtones(ringtoneList, rvRingtone, creatorEmail)
        binding.tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
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


        if (creatorEmail != null) {
            getCreator()

            if (creatorEmail == Firebase.auth.currentUser?.email) {
                binding.btFollow.visibility = View.GONE
            } else {
                binding.ibMenu.visibility = View.GONE
            }

            if (Firebase.auth.currentUser?.email != null) {
                Followers().checkIfFollowing(creatorEmail!!, binding.btFollow, resources)
                binding.btFollow.setOnClickListener {
                    Followers().manageFollow(
                        creatorEmail!!,
                        binding.btFollow,
                        resources,
                        binding.tvFollowers
                    )
                }

            }
        }

    }


    @SuppressLint("SetTextI18n")
    private fun getCreator() {
        Firebase.firestore.collection("creators").whereEqualTo("creatorEmail", creatorEmail)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val creator = document.toObject<CreatorDataClass>()
                    binding.tvCreatorName.text = creator.creatorName
                    binding.tvFollowers.text = creator.followers.toString()
                    binding.tvFollowing.text = creator.followingList?.size.toString()

                    Glide.with(requireContext())
                        .load(document.toObject<CreatorDataClass>().creatorProfileUrl)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).centerCrop()
                        .into(binding.ivCreatorPic)
                }
            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(requireContext(), v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            // Respond to menu item click.
            when (menuItem.itemId) {
                R.id.action_edit_profile -> {
                    findNavController().navigate(
                        R.id.creatorProfileUpdateFragment
                    )
                    true
                }

                R.id.action_get_verified -> {
                    val email = Firebase.auth.currentUser?.email
                    if (email != null) {
                        Firebase.firestore.collection("creators")
                            .whereEqualTo("creatorEmail", email).get()
                            .addOnSuccessListener { documents ->
                                for (document in documents) {
                                    val creator = document.toObject<CreatorDataClass>()
                                    if (!creator.isVerified) {
                                        Firebase.firestore.collection("verification")
                                            .document(email).set(hashMapOf("email" to email))
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                    context,
                                                    "Verification request sent!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "You are a verified creator!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }


                    } else {
                        findNavController().navigate(
                            R.id.loginFragment,
                            bundleOf("fragment" to "profile")
                        )

                    }
                    true
                }

                R.id.action_sign_out -> {
                    Firebase.auth.signOut()
                    Toast.makeText(context, "Signed Out", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.FirstFragment)
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