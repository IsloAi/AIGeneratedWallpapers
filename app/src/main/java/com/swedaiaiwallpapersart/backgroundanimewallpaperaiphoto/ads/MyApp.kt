package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads

import android.app.Application
import com.google.firebase.FirebaseApp
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.ConnectivityListener
import dagger.hilt.android.HiltAndroidApp

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
    }

    private var adEventListener: AdEventListener? = null

    fun registerAdEventListener(listener: AdEventListener) {
        adEventListener = listener
    }

    fun unregisterAdEventListener() {
        adEventListener = null
    }

}

