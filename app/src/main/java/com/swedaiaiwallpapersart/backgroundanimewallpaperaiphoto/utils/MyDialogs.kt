package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MaxNativeAd
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.SplashOnFragment.Companion.exit

class MyDialogs {
    @SuppressLint("SetTextI18n")
    fun exitPopup(context: Context, activity: Activity, myActivity: MainActivity) {

        val bottomSheetDialog = BottomSheetDialog(context)
        bottomSheetDialog.setContentView(R.layout.exit)

        val title = bottomSheetDialog.findViewById<TextView>(R.id.texttop)
        val btnNo = bottomSheetDialog.findViewById<Button>(R.id.btnNo)
        val btnYes = bottomSheetDialog.findViewById<Button>(R.id.btnYes)
        val btn = bottomSheetDialog.findViewById<FrameLayout>(R.id.nativeADExit)
        MaxNativeAd.createNativeAdLoader(
            context!!,
            AdConfig.applovinAndroidNativeManual,
            object : MaxNativeAdListener() {
                override fun onNativeAdLoaded(adView: MaxNativeAdView?, ad: MaxAd) {
                    btn?.removeAllViews()
                    adView?.let {
                        btn?.addView(it)
                    }
                }

                override fun onNativeAdLoadFailed(adUnitId: String, error: MaxError) {
                    // Handle failure (optional retry logic)
                }

                override fun onNativeAdClicked(ad: MaxAd) {
                    // Handle click
                }

                override fun onNativeAdExpired(ad: MaxAd) {
                    // Ad expired - reload if needed
                }
            }
        )

        MaxNativeAd.loadNativeAd(R.layout.max_native_small, context!!)

        title!!.text = context.getString(R.string.are_you_sure_you_want_to_exit)
        btnYes!!.setOnClickListener {
            bottomSheetDialog.dismiss()
            activity.finishAffinity()
        }
        btnNo!!.setOnClickListener {
            exit = false
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }
}