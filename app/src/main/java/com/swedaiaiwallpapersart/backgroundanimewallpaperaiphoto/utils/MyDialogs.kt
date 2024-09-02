package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ikame.android.sdk.data.dto.pub.IKAdError
import com.ikame.android.sdk.listener.pub.IKShowWidgetAdListener
import com.ikame.android.sdk.widgets.IkmWidgetAdLayout
import com.ikame.android.sdk.widgets.IkmWidgetAdView
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.SplashOnFragment.Companion.exit

class MyDialogs {
    @SuppressLint("SetTextI18n")
    fun exitPopup(context: Context, activity: Activity, myActivity: MainActivity,) {

        val bottomSheetDialog = BottomSheetDialog(context)
        bottomSheetDialog.setContentView(R.layout.exit)

        val title = bottomSheetDialog.findViewById<TextView>(R.id.texttop)
        val btnNo = bottomSheetDialog.findViewById<Button>(R.id.btnNo)
        val btnYes = bottomSheetDialog.findViewById<Button>(R.id.btnYes)
        val adsView = bottomSheetDialog.findViewById<IkmWidgetAdView>(R.id.adsView)

        if (AdConfig.ISPAIDUSER){
            adsView?.visibility = View.GONE
        }else{

            val adLayout = LayoutInflater.from(activity).inflate(
                R.layout.native_dialog_layout,
                null, false
            ) as? IkmWidgetAdLayout
            adLayout?.titleView = adLayout?.findViewById(R.id.custom_headline)
            adLayout?.bodyView = adLayout?.findViewById(R.id.custom_body)
            adLayout?.callToActionView = adLayout?.findViewById(R.id.custom_call_to_action)
            adLayout?.iconView = adLayout?.findViewById(R.id.custom_app_icon)
            adLayout?.mediaView = adLayout?.findViewById(R.id.custom_media)
            adsView?.loadAd(R.layout.shimmer_loading_native, adLayout!!,"exitapp_bottom",
                object : IKShowWidgetAdListener {
                    override fun onAdShowFail(error: IKAdError) {
                        Log.e("TAG", "onAdsLoadFail: native failded " )
                    }

                    override fun onAdShowed() {

                    }
                }
            )
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