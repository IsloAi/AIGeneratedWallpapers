package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.AdConfig

object MaxInterstitialAds {
    private const val maxPoolSize = 2

    private val interstitialAdsList = mutableListOf<MaxInterstitialAd>()
    private var isAdLoading = false

    fun preloadInterstitials(context: Context) {
        if (interstitialAdsList.size < maxPoolSize && !isAdLoading) {
            isAdLoading = true
            val ad = MaxInterstitialAd(AdConfig.applovinAndroidInterstitial, context)
            ad.setListener(object : MaxAdListener {
                override fun onAdLoaded(loadedAd: MaxAd) {
                    interstitialAdsList.add(ad)
                    Log.d("AppLovin", "Ad preloaded. Pool size: ${interstitialAdsList.size}")
                    isAdLoading = false

                    // After loading, only continue loading if still below max
                    if (interstitialAdsList.size < maxPoolSize) {
                        preloadInterstitials(context)
                    }
                }

                override fun onAdHidden(ad: MaxAd) {
                    interstitialAdsList.remove(ad as MaxInterstitialAd)
                    Log.d(
                        "AppLovin",
                        "Ad hidden. Pool size after removal: ${interstitialAdsList.size}"
                    )

                    // Only if pool is empty, start preloading 2 new ads
                    if (interstitialAdsList.isEmpty()) {
                        preloadInterstitials(context)
                    }
                }

                override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
                    Log.e("AppLovin", "Failed to load ad. Error code: ${error.code}")
                    isAdLoading = false
                }

                override fun onAdDisplayed(ad: MaxAd) {}
                override fun onAdClicked(ad: MaxAd) {}
                override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {}
            })
            ad.loadAd()
        }
    }

    fun showInterstitialAd(context: Activity, listener: MaxAdListener) {
        if (interstitialAdsList.isNotEmpty()) {
            val adToShow = interstitialAdsList.removeAt(0)
            if (adToShow.isReady) {
                adToShow.setListener(listener)
                adToShow.showAd(context)

                Log.d("AppLovin", "Ad shown. Pool size after showing: ${interstitialAdsList.size}")

                // After removing ad, check pool
                if (interstitialAdsList.isEmpty()) {
                    preloadInterstitials(context)
                }
            } else {
                Log.e("AppLovin", "Ad is not ready to be shown.")
                if (interstitialAdsList.isEmpty()) {
                    preloadInterstitials(context)
                }
            }
        } else {
            Log.e("AppLovin", "No ads available in the pool.")
            preloadInterstitials(context)
        }
    }
}