package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
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
        if (AdConfig.globalNativeAdView != null) {
            // Detach globalNativeAdView from its previous parent if it has one
            AdConfig.globalNativeAdView?.parent?.let { parent ->
                (parent as ViewGroup).removeView(AdConfig.globalNativeAdView)
            }
            btn?.removeAllViews()
            btn?.addView(AdConfig.globalNativeAdView)
        } else {
            // maybe show a placeholder or hide the view
            btn?.visibility = View.GONE
        }

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