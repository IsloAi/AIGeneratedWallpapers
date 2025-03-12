package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads

import android.app.Application
import android.util.Log
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkSettings
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.ConnectivityListener
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import dagger.hilt.android.HiltAndroidApp
import org.json.JSONException
import org.json.JSONObject


@HiltAndroidApp
class MyApp : Application() {

    private var connectivityListener: ConnectivityListener? = null

    // Method to set the listener
    fun setConnectivityListener(listener: ConnectivityListener) {
        connectivityListener = listener
    }

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(applicationContext)
        innitRemoteConfig()
        MobileAds.initialize(
            this
        ) { }

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
                Log.d("RemoteConfig", "Fetched JSON: $jsonString")

                // Parse JSON to extract values
                parseAdConfig(jsonString)

            } else {
                Log.e("RemoteConfig", "Failed to fetch Remote Config")
            }
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
                AppLovinSdk.getInstance(this@MyApp).setMediationProvider("max")
                val sdk = AppLovinSdk.getInstance(applovinSdkKey, AppLovinSdkSettings(this), this)
                sdk.initializeSdk()

                AdConfig.applovinSdkKey = applovinSdkKey
                AdConfig.applovinAndroidBanner = applovinBannerId
                AdConfig.applovinAndroidInterstitial = applovinInterstitialId
                AdConfig.applovinAndroidReward = applovinRewardedId
                AdConfig.admobAndroidAppOpen = admobAppOpen
                AdConfig.admobAndroidNative = admobNative
            }

        } catch (e: JSONException) {
            Log.e("RemoteConfig", "JSON Parsing Error: ${e.message}")
        }
    }

    private var adEventListener: AdEventListener? = null

    fun registerAdEventListener(listener: AdEventListener) {
        adEventListener = listener
    }

    fun unregisterAdEventListener() {
        adEventListener = null
    }

}

