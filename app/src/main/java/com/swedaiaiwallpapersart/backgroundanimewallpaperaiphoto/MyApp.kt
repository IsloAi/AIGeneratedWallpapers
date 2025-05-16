package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto

import android.app.Application
import android.net.Uri
import android.os.Build
import android.util.Log
import android.webkit.WebView
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkInitializationConfiguration
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.get
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MaxInterstitialAds
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MaxRewardAds
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.AdConfig
import dagger.hilt.android.HiltAndroidApp
import org.json.JSONException
import org.json.JSONObject


@HiltAndroidApp
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(applicationContext)
        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = true
        FirebaseCrashlytics.getInstance().log("Crashlytics initialized in MyApp")
        innitRemoteConfig()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val processName = getProcessName()
            if (processName != applicationContext.packageName) {
                WebView.setDataDirectorySuffix(processName)
                Log.d("WebViewFix", "Set data dir suffix: $processName")
            } else {
                Log.d("WebViewFix", "Main process, no suffix needed")
            }
        }
        // ✅ Configure test device for AdMob (used via AppLovin mediation)
        val testDeviceIds = listOf("570CE0CD7B1047286A1E326FC0467F70")
        val config = RequestConfiguration.Builder()
            .setTestDeviceIds(testDeviceIds)
            .build()

        MobileAds.setRequestConfiguration(config)
    }

    private fun innitRemoteConfig() {
        // Initialize Firebase Remote Config
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600) // Adjust based on how often updates should happen
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)

        // Fetch the Remote Config data
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val jsonString = remoteConfig.getString("ads") // Get the JSON as a string
                val jsonStringAppInfo =
                    remoteConfig.getString("app_info") // Get the JSON as a string
                val positionTabs = remoteConfig["tablist_156"].asString()
                val languagesOrder = remoteConfig["languages"].asString()
                val baseUrls = remoteConfig["dataUrl"].asString()
                val appFree = remoteConfig["wholeAppFree"].asBoolean()


                //START PUTTING THE DATA INTO THE VARIABLES

                // Parse JSON to extract values
                parseAdConfig(jsonString)
                parseAppInfo(jsonStringAppInfo)
                //URLS
                AdConfig.BASE_URL_DATA = baseUrls
                AdConfig.inAppConfig = appFree
                //TAB NAMES
                val tabNamesArray: Array<String> =
                    positionTabs.replace("{", "")
                        .replace("}", "")
                        .replace("\"", "")
                        .split(", ")
                        .toTypedArray()
                AdConfig.tabPositions = tabNamesArray
                //LANGUAGES
                try {
                    val languagesOrderArray =
                        languagesOrder.split(",").map { it.trim().removeSurrounding("\"") }

                    AdConfig.languagesOrder = languagesOrderArray
                } catch (e: StringIndexOutOfBoundsException) {
                    e.printStackTrace()
                }
            } else {
                Log.e("RemoteConfig", "Failed to fetch Remote Config")
            }
        }
    }

    private fun parseAppInfo(jsonStringAppInfo: String) {
        try {
            val jsonObject = JSONObject(jsonStringAppInfo)

            // Extract values from JSON
            val privacyUri = jsonObject.optString("privacy_url", "")
            val termsOfService = jsonObject.optString("tos_url", "")
            val supportMail = jsonObject.optString("support_mail", "")

            /*Log.d("AdConfig", "AppLovin SDK Key: $privacyUri")
            Log.d("AdConfig", "AppLovin Banner ID: $termsOfService")
            Log.d("AdConfig", "AppLovin Interstitial ID: $supportMail")*/

            AdConfig.appPrivacy = privacyUri
            AdConfig.appTermsOfServices = termsOfService
            AdConfig.appSupportMail = supportMail

            initAppLovinSDK()
        } catch (e: JSONException) {
            Log.e("RemoteConfig", "JSON Parsing Error: ${e.message}")
        }
    }

    private fun parseAdConfig(jsonString: String) {
        try {
            val jsonObject = JSONObject(jsonString)
            // Extract values from JSON
            val applovinSdkKey = jsonObject.optString("applovinSdkKey", "")
            val applovinBannerId = jsonObject.optString("applovinAndroidBanner", "")
            val applovinInterstitialId = jsonObject.optString("applovinAndroidInterstitial", "")
            val applovinRewardedId = jsonObject.optString("applovinAndroidRewarded", "")
            val applovinNativeSmall = jsonObject.optString("applovinAndroidNativeSmall", "")
            val applovinNativeMedium = jsonObject.optString("applovinAndroidNativeMedium", "")
            val applovinNativeManual = jsonObject.optString("applovinAndroidNativeManual", "")
            val admobAppOpen = jsonObject.optString("admobAndroidAppOpen", "")
            val admobNative = jsonObject.optString("admobAndroidNative", "")

            Log.d("AdConfig", "AppLovin SDK Key: $applovinSdkKey")
            Log.d("AdConfig", "AppLovin Banner ID: $applovinBannerId")
            Log.d("AdConfig", "AppLovin Interstitial ID: $applovinInterstitialId")
            Log.d("AdConfig", "AppLovin Rewarded ID: $applovinRewardedId")
            Log.d("AdConfig", "AdMob App Open ID: $admobAppOpen")
            Log.d("AdConfig", "AdMob Native ID: $admobNative")

            // Initialize AppLovin SDK with fetched key
            if (applovinSdkKey.isNotEmpty()) {
                AdConfig.applovinSdkKey = applovinSdkKey
                AdConfig.applovinAndroidBanner = applovinBannerId
                AdConfig.applovinAndroidInterstitial = applovinInterstitialId
                AdConfig.applovinAndroidReward = applovinRewardedId
                AdConfig.applovinAndroidNativeSmall = applovinNativeSmall
                AdConfig.applovinAndroidNativeMedium = applovinNativeMedium
                AdConfig.applovinAndroidNativeManual = applovinNativeManual
                AdConfig.admobAndroidAppOpen = admobAppOpen
                AdConfig.admobAndroidNative = admobNative
            }

        } catch (e: JSONException) {
            Log.e("RemoteConfig", "JSON Parsing Error: ${e.message}")
        }
    }

    private fun initAppLovinSDK() {
        val sdk = AppLovinSdkInitializationConfiguration.builder(AdConfig.applovinSdkKey, this)
            .setMediationProvider(AppLovinMediationProvider.MAX).build()
        val settings = AppLovinSdk.getInstance(this).settings
        settings.termsAndPrivacyPolicyFlowSettings.apply {
            isEnabled = true
            privacyPolicyUri = Uri.parse(AdConfig.appPrivacy)
            termsOfServiceUri = Uri.parse(AdConfig.appTermsOfServices)
        }

        AppLovinSdk.getInstance(this).initialize(sdk) { sdkConfig ->
            // Start loading ads
            MaxInterstitialAds.preloadInterstitials(this@MyApp)
            AdConfig.loadAndCacheNativeAd(this@MyApp)
            AdConfig.loadAndCacheBigNativeAd(this@MyApp)
            AdConfig.loadTemplateNativeAd(this@MyApp)
            MaxRewardAds.preloadRewardedAds(this@MyApp)

        }
        AppLovinSdk.getInstance(this).showMediationDebugger()

    }

}

