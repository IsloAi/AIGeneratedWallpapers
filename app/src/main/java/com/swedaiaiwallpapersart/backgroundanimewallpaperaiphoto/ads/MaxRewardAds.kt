package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxReward
import com.applovin.mediation.MaxRewardedAdListener
import com.applovin.mediation.ads.MaxRewardedAd

object MaxRewardAds {
    private var rewardedAd: MaxRewardedAd? = null

    fun loadRewardAds(context: Context, adId: String) {
        rewardedAd = MaxRewardedAd.getInstance(adId, context)
        rewardedAd?.setListener(object : MaxRewardedAdListener {
            override fun onAdLoaded(ad: MaxAd) {
                Log.d("AppLovin", "Rewarded Ad Loaded")
            }

            override fun onAdDisplayed(ad: MaxAd) {}

            override fun onAdHidden(ad: MaxAd) {
                // Load next rewarded ad after closing
                rewardedAd?.loadAd()
            }

            override fun onAdClicked(ad: MaxAd) {}

            override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
                Log.e("AppLovin", "Rewarded Ad Load Failed: ${error.message}")
            }

            override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {}

            override fun onUserRewarded(ad: MaxAd, reward: MaxReward) {}
        })
        rewardedAd?.loadAd()
    }

    fun showRewardAd(context: Activity, listener: MaxRewardedAdListener) {
        if (rewardedAd?.isReady == true) {
            rewardedAd?.setListener(listener)
            rewardedAd?.showAd(context)
        }
    }
}