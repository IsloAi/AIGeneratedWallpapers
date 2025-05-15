package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils

import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.CategoryApiModel

object AdConfig {
    var firstAdLineTrending = 0
    var lineCountTrending = 0
    var adStatusTrending = 1

    var firstAdLineCategoryArt = 0
    var lineCountCategoryArt = 0
    var adStatusCategoryArt = 1

    var firstAdLineViewListWallSRC = 0
    var lineCountViewListWallSRC = 0
    var adStatusViewListWallSRC = 1

    var firstAdLineMostUsed = 0
    var lineCountMostUsed = 0
    var adStatusMostUsed = 1

    /* Added by usman */
    var liveTabScrollType = 0
    var shouldRestoreCache = false

    var showOnboarding = true

    var inAppConfig = true

    var tabPositions = arrayOf("Live", "Popular", "Double", "Category", "Anime", "Car", "Charging")

    var categoryOrder: List<String> = listOf(
        "Sadness",
        "Car",
        "Motor Bike",
        "Fantasy",
        "Animal",
        "New Year",
        "Christmas",
        "Travel",
        "Ocean",
        "Nature",
        "Mountains",
        "Music",
        "Love",
        "Art",
        "Space",
        "Adstract",
        "Tech",
        "Black And White",
        "Architecture",
        "Artistic",
        "Pattern",
        "City",
        "Minimal",
        "Vintage",
        "4K",
        "Anime",
        "Super Heros",
        "IOS",
        "Dark"
    )

    var Noti_Widget: String = ""

//    val HD_ImageUrl = "${BASE_URL_DATA}/images/"
//    val Compressed_Image_url = "${BASE_URL_DATA}/compress/"

    var BASE_URL_DATA: String = ""
    const val BASE_URL = "https://vps.edecator.com/wallpaper_App/V4/"

    var iapScreenType = 2

    var ISPAIDUSER = false
    var Reward_Screen = false

    var regularWallpaperFlow = 0

    var languagesOrder: List<String> = listOf(
        "Spanish",
        "English (US)",
        "English (UK)",
        "French",
        "German",
        "Japanese",
        "Korean",
        "Portuguese",
        "Arabic",
        "Chinese",
        "Italian",
        "Russian",
        "Thai",
        "Turkish",
        "Vietnamese",
        "Hindi",
        "Dutch",
        "Indonesian"
    )
    var categories: List<CategoryApiModel> = emptyList()

    var languageLogicShowNative = 1
    var avoidPolicyOpenAdInter = 0
    var avoidPolicyRepeatingInter = 0
    var autoNext = false
    var timeNext = 5000L

    var onboarding_Full_Native = 0

    var applovinSdkKey: String = ""
    var applovinAndroidBanner: String = "28a977ac3117328e"
    var applovinAndroidInterstitial: String = "2118cd1883ce90b3"
    var applovinAndroidReward: String = "2ffa4cee789785d6"
    var applovinAndroidNativeSmall: String = "b439d281186ccf15"
    var applovinAndroidNativeMedium: String = "3d1478d6bf7dc5f4"
    var applovinAndroidNativeManual: String = "cfdfc558a6a4f862"
    var admobAndroidAppOpen: String = "ca-app-pub-3940256099942544/9257395921"
    var admobAndroidNative: String = "ca-app-pub-3940256099942544/2247696110"

    var appPrivacy: String = ""
    var appTermsOfServices: String = ""
    var appSupportMail: String = ""

    var globalNativeAdView: MaxNativeAdView? = null
    var globalBigNativeAdView: MaxNativeAdView? = null
    var globalTemplateNativeAdView: MaxNativeAdView? = null


    /*    fun loadAndCacheNativeAd(context: Context) {
            MaxNativeAd.createNativeAdLoader(
                context,
                applovinAndroidNativeManual,
                object : MaxNativeAdListener() {
                    override fun onNativeAdLoaded(view: MaxNativeAdView?, p1: MaxAd) {
                        globalNativeAdView = view
                        // You can also cache ad if needed
                    }

                    override fun onNativeAdLoadFailed(p0: String, p1: MaxError) {
                        Log.e("Ad", "Failed to load native ad: ${p1.message}")
                    }
                })
            MaxNativeAd.loadNativeAd(R.layout.max_native_small, context)
        }

        fun loadAndCacheBigNativeAd(context: Context) {
            MaxNativeAd.createNativeAdLoader(
                context,
                applovinAndroidNativeManual,
                object : MaxNativeAdListener() {
                    override fun onNativeAdLoaded(view: MaxNativeAdView?, p1: MaxAd) {
                        globalBigNativeAdView = view
                        // You can also cache ad if needed
                    }

                    override fun onNativeAdLoadFailed(p0: String, p1: MaxError) {
                        Log.e("Ad", "Failed to load native ad: ${p1.message}")
                    }
                })
            MaxNativeAd.loadNativeAd(R.layout.max_native_ad_slider, context)
        }

        fun loadTemplateNativeAd(context: Context) {
            //Max Medium Native Ad
            MaxNativeAd.createTemplateNativeAdLoader(
                context,
                applovinAndroidNativeMedium,
                object : MaxNativeAdListener() {
                    override fun onNativeAdLoaded(p0: MaxNativeAdView?, p1: MaxAd) {
                        super.onNativeAdLoaded(p0, p1)
                        globalTemplateNativeAdView = p0
                    }

                    override fun onNativeAdLoadFailed(p0: String, p1: MaxError) {
                        super.onNativeAdLoadFailed(p0, p1)
                    }

                    override fun onNativeAdClicked(p0: MaxAd) {
                        super.onNativeAdClicked(p0)
                    }

                    override fun onNativeAdExpired(p0: MaxAd) {
                        super.onNativeAdExpired(p0)
                    }
                })
            MaxNativeAd.loadTemplateNativeAd(MaxNativeAdView.MEDIUM_TEMPLATE_1, context)
        }*/

}































