package com.adarsh.wallzee.fullscreen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.adarsh.wallzee.wallpaper.WallpaperDataClass
import com.adarsh.walzee.R
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class FullscreenActivity : AppCompatActivity() {
    private val list: ArrayList<WallpaperDataClass> = arrayListOf()
    private var rewardedInterstitialAd: RewardedInterstitialAd? = null
    private var isLoadingAds = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen)

        MobileAds.initialize(this) {
//            loadRewardedInterstitialAd()
        }

        val wallpaperName = intent.getStringExtra("wallpaperName").toString()
        val wallpaperCategory = intent.getStringExtra("wallpaperCategory").toString()
        val wallpaperImageUrl = intent.getStringExtra("wallpaperImageUrl").toString()
        val wallpaperThumbnailUrl = intent.getStringExtra("wallpaperThumbnailUrl").toString()
        val wallpaperId = intent.getIntExtra("parentKey", 0)
        val downloads = intent.getIntExtra("downloads", 1)
        val profileName = intent.getStringExtra("profileName").toString()
        val dateUploaded = intent.getStringExtra("dateUploaded").toString()
        val imageSize = intent.getIntExtra("wallpaperSize", 100)
        val creatorName = intent.getStringExtra("creatorName").toString()
        val creatorEmail = intent.getStringExtra("creatorEmail").toString()
        val isCreatorVerified = intent.getBooleanExtra("isCreatorVerified", false)
        val type = intent.getIntExtra("contentType", 1)

        val fullscreenViewPager = findViewById<ViewPager2>(R.id.fullscreenViewPager)

        list.add(
            WallpaperDataClass(
                wallpaperId,
                wallpaperThumbnailUrl,
                wallpaperImageUrl,
                wallpaperName,
                wallpaperCategory,
                downloads,
                dateUploaded,
                imageSize,
                creatorName,
                creatorEmail,
                isCreatorVerified
            )
        )

        fullscreenViewPager.adapter =
            FullScreenViewPagerAdapter(list, this, supportFragmentManager, type,lifecycleScope)

        getWallpaperFromCategory(list, wallpaperCategory, fullscreenViewPager, type)
    }

    private fun loadRewardedInterstitialAd() {
        if (rewardedInterstitialAd == null) {
            isLoadingAds = true
            val adRequest = AdRequest.Builder().build()

            // Load an ad.
            RewardedInterstitialAd.load(
                this,
                "ca-app-pub-3940256099942544/5354046379",
                adRequest,
                object : RewardedInterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        super.onAdFailedToLoad(adError)
//                        Toast.makeText(
//                            this@FullscreenActivity,
//                            "onAdFailedToLoad:",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        Log.d(MAIN_ACTIVITY_TAG, "onAdFailedToLoad: ${adError.message}")
                        isLoadingAds = false
                        rewardedInterstitialAd = null
                    }

                    override fun onAdLoaded(rewardedAd: RewardedInterstitialAd) {
                        super.onAdLoaded(rewardedAd)
//                        Log.d(MAIN_ACTIVITY_TAG, "Ad was loaded.")
                        Toast.makeText(
                            this@FullscreenActivity,
                            "Ad was loaded.",
                            Toast.LENGTH_SHORT
                        ).show()
                        adsCallback()

                        rewardedInterstitialAd = rewardedAd
                        isLoadingAds = false
                    }
                }
            )
        }
    }


    private fun getWallpaperFromCategory(
        wallpaperList: java.util.ArrayList<WallpaperDataClass>,
        categoryName: String?,
        fullscreenViewPager: ViewPager2,
        type: Int
    ) {

        val contentType = if (type == 2)
            "live wallpapers"
        else "wallpapers"
        val docRef = Firebase.firestore.collection(contentType)
        docRef.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val wallpaper = document.toObject<WallpaperDataClass>()
                wallpaperList.add(
                    1,
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
            fullscreenViewPager.adapter?.notifyItemRangeRemoved(1, list.size)
            fullscreenViewPager.adapter?.notifyItemRangeInserted(1, list.size)
        }
    }


    fun adsCallback() {
        rewardedInterstitialAd!!.fullScreenContentCallback =
            object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedInterstitialAd = null

                    loadRewardedInterstitialAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    rewardedInterstitialAd = null
                }

                override fun onAdShowedFullScreenContent() {
                }
            }

        rewardedInterstitialAd?.show(this) {
            Toast.makeText(this, "reward gained", Toast.LENGTH_SHORT).show()
        }
    }
}
