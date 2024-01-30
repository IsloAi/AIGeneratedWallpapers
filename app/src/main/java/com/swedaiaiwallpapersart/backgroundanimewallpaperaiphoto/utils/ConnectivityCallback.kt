package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.ConnectivityListener


class ConnectivityCallback(context: Context,private val connectivityListener: ConnectivityListener) :
    ConnectivityManager.NetworkCallback() {

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        connectivityListener.onNetworkAvailable()
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        connectivityListener.onNetworkLost()
    }

    init {
        // Register the callback
        val networkRequest = NetworkRequest.Builder().apply {
            addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }.build()

        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(this)
        } else {
            connectivityManager.registerNetworkCallback(networkRequest, this)
        }
    }
}
