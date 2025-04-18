package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxReward
import com.applovin.mediation.MaxRewardedAdListener
import com.applovin.mediation.ads.MaxRewardedAd
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig


object MaxRewardAds {

    private const val MAX_REWARDED_ADS = 5
    private const val MIN_REWARDED_THRESHOLD = 3
    private val rewardedAdPool = mutableListOf<MaxRewardedAd>()
    private val loadedAds = mutableListOf<MaxAd>()
    private var isLoading = false

    fun preloadRewardedAds(context: Context) {
        if (isLoading || rewardedAdPool.size >= MAX_REWARDED_ADS) return
        isLoading = true

        val rewardedAd = MaxRewardedAd.getInstance(AdConfig.applovinAndroidReward, context)
        rewardedAd.setListener(object : MaxRewardedAdListener {
            override fun onAdLoaded(ad: MaxAd) {
                rewardedAdPool.add(rewardedAd)
                loadedAds.add(ad)
                isLoading = false
                Log.d("AppLovin", "Rewarded Ad Preloaded. Pool size: ${rewardedAdPool.size}")
                preloadRewardedAds(context) // Load next if needed
            }

            override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
                isLoading = false
                Log.e("AppLovin", "Rewarded Ad Load Failed: ${error.message}")
            }

            override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {}
            override fun onUserRewarded(p0: MaxAd, p1: MaxReward) {}

            override fun onAdDisplayed(ad: MaxAd) {}
            override fun onAdHidden(ad: MaxAd) {
                loadedAds.remove(ad)
                preloadRewardedAds(context)
            }

            override fun onAdClicked(ad: MaxAd) {}
        })

        rewardedAd.loadAd()
    }

    fun showRewardedAd(activity: Activity, listener: MaxRewardedAdListener): Boolean {
        val ad = rewardedAdPool.firstOrNull()
        return if (ad?.isReady == true) {
            ad.setListener(listener)
            ad.showAd(activity)
            rewardedAdPool.remove(ad)
            true
        } else {
            Log.d("AppLovin", "No Rewarded Ad Available")
            false
        }
    }

    fun checkAndPreload(context: Context) {
        if (rewardedAdPool.size < MIN_REWARDED_THRESHOLD) {
            preloadRewardedAds(context)
        }
    }

    fun clearAds() {
        rewardedAdPool.clear()
        loadedAds.clear()
    }
}