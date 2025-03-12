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
    private var adDismissCallback: (() -> Unit)? = null


    fun loadInterstitialAd(context: Context) {
        interstitialAd = MaxInterstitialAd(AdConfig.applovinAndroidInterstitial, context)
        interstitialAd?.setListener(object : MaxAdListener {
            override fun onAdLoaded(ad: MaxAd) {
                Log.d("AppLovin", "Interstitial Loaded")
            }

            override fun onAdDisplayed(ad: MaxAd) {}

            override fun onAdHidden(ad: MaxAd) {
                // Load the next ad after closing
                interstitialAd?.loadAd()
            }

            override fun onAdClicked(ad: MaxAd) {}

            override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
                Log.e("AppLovin", "Interstitial Load Failed: ${error.message}")
            }

            override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {}
        })
        interstitialAd?.loadAd()
    }

    fun showInterstitial(context: Activity, onAdDismissed: () -> Unit) {
        if (interstitialAd?.isReady == true) {
            adDismissCallback = onAdDismissed // Store the callback
            interstitialAd?.showAd(context)
        } else {
            Log.d("AppLovin", "Interstitial Ad Not Ready Yet")
            onAdDismissed() // Immediately execute callback if ad is not available
        }
    }

}