package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.NativeAdManager
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
        val nativead = NativeAdManager(context, AdConfig.admobAndroidNative)
        if (btn != null) {
            nativead.loadNativeAd(btn)
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