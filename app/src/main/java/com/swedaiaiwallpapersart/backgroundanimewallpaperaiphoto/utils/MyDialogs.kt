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
//        val dialog = Dialog(context)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setContentView(R.layout.exit)
//        val width = WindowManager.LayoutParams.MATCH_PARENT
//        val height = WindowManager.LayoutParams.WRAP_CONTENT
//        dialog.window!!.setLayout(width, height)
//        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
//        dialog.setCancelable(false)
//        val title = dialog.findViewById<TextView>(R.id.texttop)
//        val btnNo = dialog.findViewById<Button>(R.id.btnNo)
//        val btnYes = dialog.findViewById<Button>(R.id.btnYes)
//        val adsView = dialog.findViewById<IkmWidgetAdView>(R.id.adsView)
//
//        val adLayout = LayoutInflater.from(activity).inflate(
//            R.layout.layout_custom_admob,
//            null, false
//        ) as? IkmWidgetAdLayout
//        adLayout?.titleView = adLayout?.findViewById(R.id.custom_headline)
//        adLayout?.bodyView = adLayout?.findViewById(R.id.custom_body)
//        adLayout?.callToActionView = adLayout?.findViewById(R.id.custom_call_to_action)
//        adLayout?.iconView = adLayout?.findViewById(R.id.custom_app_icon)
//        adLayout?.mediaView = adLayout?.findViewById(R.id.custom_media)
//
//        adsView.setCustomNativeAdLayout(
//            R.layout.shimmer_loading_native,
//            adLayout!!
//        )
//        adsView.loadAd(activity,"exitapp_bottom","exitapp_bottom",
//            object : CustomSDKAdsListenerAdapter() {
//                override fun onAdsLoadFail() {
//                    super.onAdsLoadFail()
//                    Log.e("TAG", "onAdsLoadFail: native failded " )
//                    adsView.visibility = View.GONE
//                }
//
//                override fun onAdsLoaded() {
//                    super.onAdsLoaded()
//                    Log.e("TAG", "onAdsLoaded: native loaded" )
//                }
//            }
//        )
//        title!!.text = context.getString(R.string.are_you_sure_you_want_to_exit)
//        btnYes!!.setOnClickListener {
//            activity.finish()
//            dialog.dismiss()
//        }
//        btnNo!!.setOnClickListener {
//            dialog.dismiss()
//        }
//        dialog.show()

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
            activity.finishAffinity()
            bottomSheetDialog.dismiss()
        }
        btnNo!!.setOnClickListener {
            bottomSheetDialog.dismiss()
            exit = false
        }
        bottomSheetDialog.show()
    }
//    fun getWallpaperPopup(
//        context: Context,
//        model: CatResponse,
//        navController: NavController,
//        actionId: Int,
//        gemsTextUpdate: GemsTextUpdate,
//        lockButton: ImageView,
//        diamondIcon: ImageView,
//        gemsView:TextView,
//        myViewModel: MyViewModel?,
//        activity: Activity
//    ) {
//        val dialog = Dialog(context)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setContentView(R.layout.perchase_gems)
//        val width = WindowManager.LayoutParams.MATCH_PARENT
//        val height = WindowManager.LayoutParams.WRAP_CONTENT
//        dialog.window!!.setLayout(width, height)
//        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
//        dialog.show()
//        dialog.setCancelable(false)
//        val title = dialog.findViewById<TextView>(R.id.totalGems)
//        val buttonGetWallpaper = dialog.findViewById<ConstraintLayout>(R.id.buttonGetWallpaper)
//        val totalGems = MySharePreference.getGemsValue(context)
//
//        title!!.text = "To Unlock this wallpaper Gems"
//        buttonGetWallpaper!!.setOnClickListener {
//            IKSdkController.getInstance().showRewardedAds(activity,"viewlistwallscr_item_vip_reward","viewlistwallscr_item_vip_reward",object:
//                CustomSDKRewardedAdsListener {
//                override fun onAdsDismiss() {
//                    Log.e("********ADS", "onAdsDismiss: ", )
//
//                }
//
//                override fun onAdsRewarded() {
//                    Log.e("********ADS", "onAdsRewarded: ", )
//                    val postData = PostDataOnServer()
//                    postData.unLocking(MySharePreference.getDeviceID(context)!!,model,context,0,gemsTextUpdate,dialog,lockButton,diamondIcon,gemsView)
//
//                }
//
//                override fun onAdsShowFail(errorCode: Int) {
//                    Log.e("********ADS", "onAdsShowFail: ", )
//                    val postData = PostDataOnServer()
//                    postData.unLocking(MySharePreference.getDeviceID(context)!!,model,context,0,gemsTextUpdate,dialog,lockButton,diamondIcon,gemsView)
//
//
//                }
//
//            })
//
//
//
//        }
////        getMoreGems.setOnClickListener {
////            navController.navigate(actionId)
////            myViewModel?.clear()
////            dialog.dismiss()
////        }
//        dialog.findViewById<ImageView>(R.id.closePopup).setOnClickListener { dialog.dismiss() }
//        dialog.show()
//    }
//
//    fun getWallpaperPopup(
//        context: Context,
//        model: CatResponse,
//        navController: NavController,
//        actionId: Int,
//        gemsTextUpdate: GemsTextUpdate,
//        lockButton: ImageView,
//        diamondIcon: ImageView,
//        gemsView:TextView,
//        activity: Activity
//    ) {
//        val dialog = Dialog(context)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setContentView(R.layout.perchase_gems)
//        val width = WindowManager.LayoutParams.MATCH_PARENT
//        val height = WindowManager.LayoutParams.WRAP_CONTENT
//        dialog.window!!.setLayout(width, height)
//        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
//        dialog.show()
//        dialog.setCancelable(false)
//        val title = dialog.findViewById<TextView>(R.id.totalGems)
//        val buttonGetWallpaper = dialog.findViewById<ConstraintLayout>(R.id.buttonGetWallpaper)
//        title!!.text = "To Unlock this wallpaper"
//        buttonGetWallpaper!!.setOnClickListener {
//
//
//            IKSdkController.getInstance().showRewardedAds(activity,"viewlistwallscr_item_vip_reward","viewlistwallscr_item_vip_reward",object:
//                CustomSDKRewardedAdsListener {
//                override fun onAdsDismiss() {
//                    Log.e("********ADS", "onAdsDismiss: ", )
//
//                }
//
//                override fun onAdsRewarded() {
//                    Log.e("********ADS", "onAdsRewarded: ", )
//                    val postData = PostDataOnServer()
//                    postData.unLocking(MySharePreference.getDeviceID(context)!!,model,context,0,gemsTextUpdate,dialog,lockButton,diamondIcon,gemsView)
//
//                }
//
//                override fun onAdsShowFail(errorCode: Int) {
//                    Log.e("********ADS", "onAdsShowFail: ", )
//                    val postData = PostDataOnServer()
//                    postData.unLocking(MySharePreference.getDeviceID(context)!!,model,context,0,gemsTextUpdate,dialog,lockButton,diamondIcon,gemsView)
//
//
//                }
//
//            })
//
//
//            }
//        dialog.findViewById<ImageView>(R.id.closePopup).setOnClickListener { dialog.dismiss() }
//        dialog.show()
//    }
//
//    fun getWallpaperPopup(
//        context: Context,
//        model: CatResponse,
//        navController: NavController,
//        id: Int,
//        instance: Retrofit,
//        totalGemsView: TextView,
//        layout:ConstraintLayout,
//        activity: Activity
//    ) {
//        val postData = PostDataOnServer()
//        val dialog = Dialog(context)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setContentView(R.layout.perchase_gems)
//        val width = WindowManager.LayoutParams.MATCH_PARENT
//        val height = WindowManager.LayoutParams.WRAP_CONTENT
//        dialog.window!!.setLayout(width, height)
//        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
//        dialog.show()
//        dialog.setCancelable(false)
//        val title = dialog.findViewById<TextView>(R.id.totalGems)
//        val buttonGetWallpaper = dialog.findViewById<ConstraintLayout>(R.id.buttonGetWallpaper)
//        title!!.text = "To Unlock this wallpaper"
//
//        buttonGetWallpaper!!.setOnClickListener {
//
//
//            IKSdkController.getInstance().showRewardedAds(activity,"viewlistwallscr_item_vip_reward","viewlistwallscr_item_vip_reward",object:
//                CustomSDKRewardedAdsListener {
//                override fun onAdsDismiss() {
//                    Log.e("********ADS", "onAdsDismiss: ", )
//
//                }
//
//                override fun onAdsRewarded() {
//                    Log.e("********ADS", "onAdsRewarded: ", )
////                    postData.unLocking(MySharePreference.getDeviceID(context)!!,model,context,0,totalGemsView,dialog,layout)
//                }
//
//                override fun onAdsShowFail(errorCode: Int) {
//                    Log.e("********ADS", "onAdsShowFail: ", )
////                    postData.unLocking(MySharePreference.getDeviceID(context)!!,model,context,0,totalGemsView,dialog,layout)
//
//                }
//
//            })
//
//
//
//                    //postData.gemsPostData(context,MySharePreference.getUserID(context)!!,instance,leftGems,PostDataOnServer.isPlan,layout,model)
////                    totalGemsView.text = leftGems.toString()
//
//             }
//        dialog.findViewById<ImageView>(R.id.closePopup).setOnClickListener { dialog.dismiss() }
//        dialog.show()
//    }
//
//    fun waiting(dialog: Dialog) {
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setContentView(R.layout.loading_ad)
//        val width = WindowManager.LayoutParams.WRAP_CONTENT
//        val height = WindowManager.LayoutParams.WRAP_CONTENT
//        dialog.window!!.setLayout(width, height)
//        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
//        dialog.setCancelable(false)
//        dialog.findViewById<TextView>(R.id.title).text = "Please wait..."
//        dialog.show()
//    }

}