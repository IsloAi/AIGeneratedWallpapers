package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R

//Created on 6/3/25 by U5M4N-K071N
/*It's the fear of the bug unseen 
that makes coding harder to begin.*/

class NativeAdManager(private val context: Context, private val adUnitId: String, val layout: Int) {

    private var nativeAd: NativeAd? = null

    fun loadNativeAd(adContainer: FrameLayout) {
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { ad: NativeAd ->
                if (nativeAd != null) {
                    nativeAd?.destroy()
                }
                nativeAd = ad
                val adView =
                    View.inflate(context, layout, null) as NativeAdView
                populateNativeAdView(ad, adView)
                adContainer.removeAllViews()
                adContainer.addView(adView)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e("NativeAd", "Failed to load native ad: ${error.message}")
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .setRequestCustomMuteThisAd(true)
                    .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
                    .build()
            )
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        adView.headlineView = adView.findViewById(R.id.custom_headline)
        adView.callToActionView = adView.findViewById(R.id.custom_call_to_actionNew)
        adView.iconView = adView.findViewById(R.id.custom_app_icon)
        adView.bodyView = adView.findViewById(R.id.custom_body)
        adView.starRatingView = adView.findViewById(R.id.rating_bar)
        adView.mediaView = adView.findViewById(R.id.mediaView2)

        (adView.headlineView as? TextView)?.text = nativeAd.headline
        (adView.callToActionView as? Button)?.text = nativeAd.callToAction

        Log.d("NativeAd", "populateNativeAdView: ${nativeAd.callToAction} ")
        if (nativeAd.icon != null) {
            (adView.iconView as? ImageView)?.setImageDrawable(nativeAd.icon?.drawable)
        } else {
            adView.iconView?.visibility = View.GONE
        }
        if (nativeAd.starRating != null) {
            (adView.starRatingView as? RatingBar)?.rating = nativeAd.starRating?.toFloat() ?: 0f
        } else {
            adView.starRatingView?.visibility = View.GONE
        }
        if (nativeAd.store != null) {
            (adView.storeView as? TextView)?.text = nativeAd.store
        } else {
            adView.storeView?.visibility = View.GONE
        }
        if (nativeAd.body != null) {
            (adView.bodyView as? TextView)?.text = nativeAd.body
        } else {
            adView.bodyView?.visibility = View.GONE
        }
        if (nativeAd.mediaContent != null) {
            adView.mediaView?.mediaContent = nativeAd.mediaContent
        } else {
            adView.mediaView?.visibility = View.GONE
        }

        adView.setNativeAd(nativeAd)
    }
}