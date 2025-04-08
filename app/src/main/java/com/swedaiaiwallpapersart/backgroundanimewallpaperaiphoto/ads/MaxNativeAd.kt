package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig

//Created on  by U5M4N-K071N
/*It's the fear of the bug unseen 
that makes coding harder to begin.*/

@SuppressLint("StaticFieldLeak")
object MaxNativeAd {
    private var nativeAdLoader: MaxNativeAdLoader? = null
    private var loadedNativeAd: MaxAd? = null

    fun createNativeAdLoader(context: Context, nativeValue: String, listener: MaxNativeAdListener) {
        nativeAdLoader = MaxNativeAdLoader(nativeValue, context).apply {
            setNativeAdListener(listener)
        }
    }

    fun loadNativeAd(layout: Int, context: Context) {
        nativeAdLoader?.loadAd(createNativeAdView(layout, context))
    }

    fun destroyAd() {
        loadedNativeAd?.let { nativeAdLoader?.destroy(it) }
        nativeAdLoader?.destroy()
        loadedNativeAd = null
        nativeAdLoader = null
    }

    private fun createNativeAdView(layout: Int, context: Context): MaxNativeAdView {
        val binder = MaxNativeAdViewBinder.Builder(layout)
            .setTitleTextViewId(R.id.Max_custom_headline)
            .setBodyTextViewId(R.id.Max_custom_body)
            .setIconImageViewId(R.id.Max_custom_app_icon)
            .setMediaContentViewGroupId(R.id.MaxMediaView)
            .setStarRatingContentViewGroupId(R.id.MaxStarRating)
            .setOptionsContentViewGroupId(R.id.OptionsView)
            .setCallToActionButtonId(R.id.Max_custom_call_to_actionNew)
            .build()
        return MaxNativeAdView(binder, context)
    }
}