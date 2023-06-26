package com.adarsh.wallzee.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.adarsh.wallzee.MainActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback

class GenerateAd {

    private var rewardedInterstitialAd: RewardedInterstitialAd? = null

    fun rewardedAds(context: Context): RewardedInterstitialAd? {
        RewardedInterstitialAd.load(
            context, "ca-app-pub-3940256099942544/5354046379",
            AdRequest.Builder().build(), object : RewardedInterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedInterstitialAd) {
//                    Toast.makeText(context, "Ad was loaded.", Toast.LENGTH_SHORT).show()
                    rewardedInterstitialAd = ad
                }
            })

        return rewardedInterstitialAd
    }

    fun bannerAds(mainActivity: Activity, adManagerAdView: AdManagerAdView) {
        val adRequest = AdManagerAdRequest.Builder().build()
        adManagerAdView.loadAd(adRequest)
    }
}