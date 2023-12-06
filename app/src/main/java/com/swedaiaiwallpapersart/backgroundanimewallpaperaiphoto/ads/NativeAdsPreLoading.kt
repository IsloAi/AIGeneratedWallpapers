package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads

import android.util.Log
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.keep.IKLoadNativeAdListener
import com.bmik.android.sdk.widgets.IkmNativeAdView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity

object NativeAdsPreLoading {
    var ad:IkmNativeAdView ?= null
    var adLanguage:IkmNativeAdView ?= null
    fun loadOnboardingAds(activity: MainActivity) {

        SDKBaseController.getInstance().loadIkmNativeAdView(activity,"onboardscr_bottom","onboardscr_bottom",object :
            IKLoadNativeAdListener {
            override fun onAdFailedToLoad(errorCode: Int) {
                Log.e("TAG", "onAdFailedToLoad: preload onbarding" )

            }

            override fun onAdLoaded(adsResult: IkmNativeAdView?) {
                Log.e("TAG", "onAdLoaded: preload onbarding", )
                ad = adsResult!!
            }

        })



    }

    fun loadLanguageAds(activity: MainActivity) {

        SDKBaseController.getInstance().loadIkmNativeAdView(activity,"languagescr_bottom","languagescr_bottom",object :
            IKLoadNativeAdListener {
            override fun onAdFailedToLoad(errorCode: Int) {
                Log.e("TAG", "onAdFailedToLoad: preload language" )

            }

            override fun onAdLoaded(adsResult: IkmNativeAdView?) {
                Log.e("TAG", "onAdLoaded: preload language", )
                adLanguage = adsResult!!
            }

        })



    }

    fun getLanguageAd():IkmNativeAdView? {
        return if (adLanguage!=null){
            adLanguage
        }else{
            ad
        }
    }

    fun getOnBoaringAds():IkmNativeAdView? {
        return if (ad!=null){
            ad
        }else{
            adLanguage
        }
    }
}