package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads

interface AdEventListener {
    fun onAdDismiss()
    fun onAdLoading()
    fun onAdsShowTimeout()
    fun onShowAdComplete()
    fun onShowAdFail()
}
