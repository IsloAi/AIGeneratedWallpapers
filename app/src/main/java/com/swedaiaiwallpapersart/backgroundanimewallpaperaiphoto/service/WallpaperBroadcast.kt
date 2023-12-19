package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

internal class WallpaperBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val service = Intent(context, LiveWallpaperService::class.java)
        context.startService(service)
    }
}