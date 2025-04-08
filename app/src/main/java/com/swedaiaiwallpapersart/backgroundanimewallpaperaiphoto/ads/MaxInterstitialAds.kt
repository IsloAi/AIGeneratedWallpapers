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

    private var interstitialAd: MaxInterstitialAd? = null
    var clickCounter = 0  // Counter to track clicks
    private var adShowThreshold = 3  // Show ad after 3 clicks
    var willIntAdShow: Boolean = false

    fun loadInterstitialAd(context: Context) {
        interstitialAd = MaxInterstitialAd(AdConfig.applovinAndroidInterstitial, context)
        interstitialAd?.setListener(object : MaxAdListener {
            override fun onAdLoaded(ad: MaxAd) {
                Log.d("AppLovin", "Interstitial Loaded")
            }

            override fun onAdDisplayed(ad: MaxAd) {}

            override fun onAdHidden(ad: MaxAd) {
                clickCounter = 0  // Reset counter after showing ad
                interstitialAd?.loadAd()  // Load the next ad
            }

            override fun onAdClicked(ad: MaxAd) {}

            override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
                Log.e("AppLovin", "Interstitial Load Failed: ${error.message}")
            }

            override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {}
        })
        interstitialAd?.loadAd()
    }

    fun showInterstitial(context: Activity, listener: MaxAdListener, listener2: MaxAD) {
        clickCounter++  // Increment click count
        Log.d("AppLovin", "Click Count: $clickCounter")

        if (clickCounter < adShowThreshold) {
            willIntAdShow = false
            Log.d("AppLovin", "Threshold not reached, skipping ad")
            listener2.adNotReady("Ad Not Ready")
            return
        }

        willIntAdShow = true
        Log.d("AppLovin", "Threshold reached, showing ad")

        if (interstitialAd?.isReady == true) {
            interstitialAd?.setListener(listener)
            interstitialAd?.showAd(context)
        } else {
            Log.d("AppLovin", "Interstitial Ad Not Ready Yet")
            listener2.adNotReady("Ad Not Ready")
            clickCounter = 0  // Reset counter if ad not ready
        }
    }

}