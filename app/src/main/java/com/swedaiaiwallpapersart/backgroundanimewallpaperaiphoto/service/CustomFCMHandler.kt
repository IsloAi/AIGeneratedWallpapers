package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.service

import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.RemoteMessage
import com.ikame.android.sdk.core.fcm.BaseIkFirebaseMessagingService
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.SplashOnFragment

class CustomFCMHandler : BaseIkFirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
    }

    override fun handleIntentSdk(intent: Intent) {
        super.handleIntentSdk(intent)
        // Extract data from the intent
        val feature = intent.getStringExtra("ik_notify_feature")

        when (feature) {
            "live_wallpaper_tab" -> openLiveWallpaperTab()
            "tab_popular" -> openPopularScreen()
            "tab_double" -> openDoubleScreen()
            "tab_car" -> openCarScreen()
            "tab_charging" -> openChargingScreen()
            else -> {
                // Handle other cases or log unhandled features
            }
        }
    }

    override fun splashActivityClass(): Class<*>? {
        return SplashOnFragment::class.java
    }

    private fun openLiveWallpaperTab() {
        Log.d("USMAN", "openLiveWallpaperTab: will open live tab")
    }

    private fun openPopularScreen() {
        Log.d("USMAN", "openPopularWallpaperTab: will open popular tab")
    }

    private fun openDoubleScreen() {
        Log.d("USMAN", "openDoubleWallpaperTab: will open Double tab")
    }

    private fun openCarScreen() {
        Log.d("USMAN", "openCarWallpaperTab: will open Car tab")
    }

    private fun openChargingScreen() {
        Log.d("USMAN", "openChargingWallpaperTab: will open Charging tab")
    }

}