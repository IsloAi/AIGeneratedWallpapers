package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig

object MaxInterstitialAds {

    private const val maxPoolSize: Int = 5

    // List to store preloaded interstitial ads
    private val interstitialAdsList = mutableListOf<MaxInterstitialAd>()
    private var isAdLoading = false

    fun preloadInterstitials(context: Context) {
        // Only preload if the pool size is less than the max pool size and no ad is being loaded
        if (interstitialAdsList.size < maxPoolSize && !isAdLoading) {
            isAdLoading = true  // Mark ad loading in progress

            // Create new ad object
            val ad = MaxInterstitialAd(AdConfig.applovinAndroidInterstitial, context)

            // Set up listener
            ad.setListener(object : MaxAdListener {
                override fun onAdLoaded(loadedAd: MaxAd) {
                    // Add the ad to the list once it's loaded
                    interstitialAdsList.add(ad)
                    Log.d("AppLovin", "Ad preloaded. Pool size: ${interstitialAdsList.size}")
                    isAdLoading = false  // Reset loading state
                    preloadInterstitials(context)  // Preload a new ad
                }

                override fun onAdDisplayed(p0: MaxAd) {
                }

                override fun onAdHidden(ad: MaxAd) {
                    // When the ad is done, remove it and preload a new one
                    interstitialAdsList.remove(ad as MaxInterstitialAd)
                    preloadInterstitials(context)  // Preload a new ad
                }

                override fun onAdClicked(p0: MaxAd) {
                }

                override fun onAdLoadFailed(p0: String, p1: MaxError) {
                    Log.e("AppLovin", "Failed to load ad. Error code: ${p1.code}")
                    isAdLoading = false  // Reset loading state even in case of failure
                }

                override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
                }
            })

            // Load the ad
            ad.loadAd()
        }
    }

    // Function to show an ad from the pool
    fun showInterstitialAd(context: Activity, listener: MaxAdListener) {
        // Show the first available ad from the pool
        if (interstitialAdsList.isNotEmpty()) {
            val adToShow = interstitialAdsList.removeAt(0)  // Remove the first ad
            if (adToShow.isReady) {
                adToShow.setListener(listener)
                preloadInterstitials(context)
                adToShow.showAd(context)
            } else {
                Log.e("AppLovin", "Ad is not ready to be shown.")
            }
        } else {
            Log.e("AppLovin", "No ads available in the pool.")
        }
    }

}