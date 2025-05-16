package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads

object AdClickCounter {
    private var clickCount = 0

    fun increment(): Int {
        clickCount++
        return clickCount
    }

    fun shouldShowAd(): Boolean {
        if (clickCount >= 2) {
            reset()
            return true
        } else return false
    }

    fun reset() {
        clickCount = 0
    }
}