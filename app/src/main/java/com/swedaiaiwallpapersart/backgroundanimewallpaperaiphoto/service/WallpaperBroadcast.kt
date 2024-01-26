package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

internal class WallpaperBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val service = Intent(context, LiveWallpaperService::class.java)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.startForegroundService(service)
//        } else {
//        }
        context.startService(service)
    }
}