package com.adarsh.wallzee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.adarsh.wallzee.DataClass.ImageAdapter
import com.adarsh.wallzee.DataClass.ImageItem
import com.adarsh.wallzee.category.CategoryDataClass
import com.adarsh.wallzee.category.CategoryRecyclerAdapter
import com.adarsh.wallzee.creator.CreatorDataClass
import com.adarsh.wallzee.creator.CreatorRecyclerAdapter
import com.adarsh.wallzee.liveWallpaper.LiveWallpaperRecyclerAdapter
import com.adarsh.wallzee.ringtone.RingtoneDataClass
import com.adarsh.wallzee.ringtone.RingtoneRecyclerAdapter
import com.adarsh.wallzee.slider.SliderAdapter
import com.adarsh.wallzee.slider.SliderDataClass
import com.adarsh.wallzee.utils.Category
import com.adarsh.wallzee.utils.Ringtone
import com.adarsh.wallzee.wallpaper.TrendingWallpapersRecyclerAdapter
import com.adarsh.wallzee.wallpaper.WallpaperDataClass
import com.adarsh.wallzee.wallpaper.WallpaperRecyclerAdapter
import com.adarsh.walzee.R
import com.adarsh.walzee.databinding.FragmentFirstBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private val db = Firebase.firestore
    private lateinit var wallpaperList: ArrayList<WallpaperDataClass>
    private lateinit var liveWallpaperList: ArrayList<WallpaperDataClass>
    private lateinit var categoryList: ArrayList<CategoryDataClass>
    private lateinit var creatorList: ArrayList<CreatorDataClass>
    private lateinit var bannerList: ArrayList<SliderDataClass>
    private lateinit var ringtoneList: ArrayList<RingtoneDataClass>
    private lateinit var trendingWallpaperList: ArrayList<WallpaperDataClass>
    private lateinit var imageAdapter: ImageAdapter

    private val imageUrls = listOf(
        "https://firebasestorage.googleapis.com/v0/b/walzee.appspot.com/o/funonoffical%40gmail%2F115thumbnail?alt=media&token=4bdb1d79-2b88-4037-b6d2-f86a06678c5d",
         "https://firebasestorage.googleapis.com/v0/b/walzee.appspot.com/o/funonoffical%40gmail%2F111thumbnail?alt=media&token=978f7120-32a7-46c3-8eca-5dcf867fe8cb",
        "https://firebasestorage.googleapis.com/v0/b/walzee.appspot.com/o/funonoffical%40gmail%2F124thumbnail?alt=media&token=66237d28-e51c-4ab7-9049-302b92e0adc0",
        "https://firebasestorage.googleapis.com/v0/b/walzee.appspot.com/o/funonoffical%40gmail%2F157thumbnail?alt=media&token=251d12a1-5c81-40b4-b4e7-5e2a8e4695ab",
        "https://firebasestorage.googleapis.com/v0/b/walzee.appspot.com/o/funonoffical%40gmail%2F235thumbnail?alt=media&token=2d46e199-be79-4fb8-85fa-f2cebc49732c",
        "https://firebasestorage.googleapis.com/v0/b/walzee.appspot.com/o/funonoffical%40gmail%2F256thumbnail?alt=media&token=03286d15-0163-4a33-a929-e9c38053c68d",
        "https://firebasestorage.googleapis.com/v0/b/walzee.appspot.com/o/funonoffical%40gmail%2F264thumbnail?alt=media&token=62fd4fbd-3c7b-4abc-8c94-86a4992234e1",
        "https://firebasestorage.googleapis.com/v0/b/walzee.appspot.com/o/adarshdevaiah30%40gmail%2F3?alt=media&token=e1a65cc5-3ea0-4a50-b7cb-fe0d8aa40e84",
        "https://firebasestorage.googleapis.com/v0/b/walzee.appspot.com/o/funonstatus%40gmail%2F318?alt=media&token=8b4759b7-4c64-459e-b62e-44a1a15972f5",
        "https://firebasestorage.googleapis.com/v0/b/walzee.appspot.com/o/funonstatus%40gmail%2F326?alt=media&token=71d5a661-5061-48fd-ac7a-3b6acd1e88c2",
        "https://firebasestorage.googleapis.com/v0/b/walzee.appspot.com/o/adarshdevaiah30%40gmail%2F347?alt=media&token=66c314c8-5880-4016-90ec-c841f16cd93c",
        // Add more URLs here
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().moveTaskToBack(false)
        }

        callback.isEnabled = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        wallpaperList = arrayListOf()
        categoryList = arrayListOf()
        creatorList = arrayListOf()
        bannerList = arrayListOf()
        ringtoneList = arrayListOf()
        liveWallpaperList = arrayListOf()
        trendingWallpaperList = arrayListOf()

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageSlider.setSliderAdapter(SliderAdapter(context, bannerList))

        binding.rvWallpapersFirstFragment.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)

        imageAdapter = ImageAdapter(imageUrls)
        binding.rvWallpapersFirstFragment.adapter = imageAdapter



//        binding.rvWallpapersFirstFragment.adapter =
//            context?.let { WallpaperRecyclerAdapter(it, wallpaperList) }

        binding.rvCreators.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvCreators.adapter = CreatorRecyclerAdapter(context, creatorList)

        binding.rvLiveWallpapers.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvLiveWallpapers.adapter = LiveWallpaperRecyclerAdapter(context, liveWallpaperList)

        binding.rvCategories.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        binding.rvCategories.adapter = CategoryRecyclerAdapter(
            context,
            categoryList,
            findNavController()
        )

        binding.rvTrendingWallpapers.layoutManager =
            GridLayoutManager(context, 3)
        binding.rvTrendingWallpapers.adapter =
            TrendingWallpapersRecyclerAdapter(context, trendingWallpaperList)


        binding.rvRingtone.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvRingtone.adapter = RingtoneRecyclerAdapter(
            context,
            ringtoneList,
            findNavController()
        )

        binding.chipWallpaperExplore.setOnClickListener {
            findNavController().navigate(
                R.id.action_FirstFragment_to_exploreFragment,
                bundleOf("type" to 1)
            )
        }

        binding.chipRingtoneExplore.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_ringtoneFragment)
        }

        binding.chipLiveWallpaperExplore.setOnClickListener {
            findNavController().navigate(
                R.id.action_FirstFragment_to_exploreFragment,
                bundleOf("type" to 2)
            )

        }

//        requireActivity().findViewById<ConstraintLayout>(R.id.include)
//            ?.findViewById<HorizontalScrollView>(R.id.horizontalScrollView)!!.visibility =
//            View.GONE

        binding.lottieLoadingDiamond.visibility = View.VISIBLE
        binding.constraintLayout.visibility = View.INVISIBLE

        lifecycleScope.launch(Dispatchers.IO) {
            getWallpapers()
            Category().getCategories(
                categoryList,
                binding.rvCategories,
                view.findViewById(R.id.lottie_loading_diamond)
            )

            getCreators()
            getBanner()
            Ringtone().getRingtones(
                ringtoneList,
                binding.rvRingtone,
                view.findViewById(R.id.lottie_loading_diamond)
            )
            getLiveWallpapers()
            getTrendingWallpaperList()
        }
    }

    private fun getTrendingWallpaperList() {
        val docRef =
            db.collection("wallpapers").orderBy("downloads").limit(42)
        docRef.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val wallpaper = document.toObject<WallpaperDataClass>()
                trendingWallpaperList.add(
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
            binding.rvTrendingWallpapers.adapter?.notifyItemInserted(0)
        }

    }

    private fun getLiveWallpapers() {
        val docRef =
            db.collection("live wallpapers").whereEqualTo("creatorVerified", true).limit(10)
        docRef.get().addOnSuccessListener { documents ->
//
//            if (documents.isEmpty) {
////                binding.tvNewQuestionsComingSoon.visibility = View.VISIBLE
//            }
            for (document in documents) {
                val wallpaper = document.toObject<WallpaperDataClass>()
                liveWallpaperList.add(
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
//            Toast.makeText(context, wallpaperList.toString(), Toast.LENGTH_SHORT).show()
            binding.rvLiveWallpapers.adapter?.notifyItemInserted(0)

//            requireActivity().findViewById<CircularProgressIndicator>(R.id.circularProgressIndicator2).visibility =
//                View.GONE

            binding.lottieLoadingDiamond.visibility = View.GONE
            binding.constraintLayout.visibility = View.VISIBLE
//            activity?.findViewById<ConstraintLayout>(R.id.include)
//                ?.findViewById<HorizontalScrollView>(R.id.horizontalScrollView)!!.visibility =
//                View.VISIBLE

//            requireActivity().findViewById<ConstraintLayout>(R.id.include)
//                .findViewById<HorizontalScrollView>(R.id.horizontalScrollView).visibility =
//                View.VISIBLE
        }

    }

    private fun getBanner() {
        val docRef = db.collection("banner")
//        for (i in 1..3) {
//            docRef.document(i.toString()).set(
//                SliderDataClass(
//                    "https://firebasestorage.googleapis.com/v0/b/walzee.appspot.com/o/banners%2Flucas-kapla-wQLAGv4_OYs-unsplash%20(1).webp?alt=media&token=1ad38059-6484-4330-8a61-106e8177065b",
//                    "https://wallzee.net/"
//                )
//            ).addOnCompleteListener {
//                Toast.makeText(context, i.toString(), Toast.LENGTH_SHORT).show()
//            }
//        }

        docRef.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val banner = document.toObject<SliderDataClass>()
                bannerList.add(
                    SliderDataClass(
                        banner.imageUrl,
                        banner.link
                    )
                )
            }
            binding.imageSlider.sliderAdapter.notifyDataSetChanged()
        }
    }

    private fun getCreators() {
        val docRef = db.collection("creators").limit(10)

//        for (i in 1..10) {
//            docRef.document(i.toString()).set(
//                CreatorDataClass(
//                    "creator",
//                    "",
//                    "creator@gmail.com",
//                    "https://firebasestorage.googleapis.com/v0/b/walzee.appspot.com/o/categories%2Fcategoryimage.webp?alt=media&token=5a1d06a5-5acc-43fc-85d0-f98c4620cdc4",
//                    1000,
//                    100
//                )
//            )
//        }
        docRef.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val creator = document.toObject<CreatorDataClass>()
                creatorList.add(
                    CreatorDataClass(
                        creator.creatorName,
                        creator.creatorLinks,
                        creator.creatorEmail,
                        creator.creatorProfileUrl,
                        creator.followers,
                        creator.followingList
                    )
                )
            }
            binding.rvCreators.adapter?.notifyItemInserted(0)
        }
    }


    private fun getWallpapers() {
        val docRef =
            db.collection("wallpapers").orderBy("dateUploaded")
                .limit(20)
        docRef.get().addOnSuccessListener { documents ->
//
//            if (documents.isEmpty) {
////                binding.tvNewQuestionsComingSoon.visibility = View.VISIBLE
//            }
            for (document in documents) {

                val wallpaper = document.toObject<WallpaperDataClass>()
                if (wallpaper.isCreatorVerified)
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
                            true
                        )
                    )
            }
//            Toast.makeText(context, wallpaperList.toString(), Toast.LENGTH_SHORT).show()
            binding.rvWallpapersFirstFragment.adapter?.notifyItemInserted(0)
        }

    }

}