package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log


class BroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_POWER_CONNECTED) {
            Log.e("TAG", "onReceive: connected$action")
            startChargingAnimation(context!!)
        } else if (action == Intent.ACTION_POWER_DISCONNECTED) {
            Log.e("TAG", "onReceive: disconnected"+action )

            stopChargingAnimation(context!!)
        }
    }

    private fun startChargingAnimation(context: Context) {
        val serviceIntent = Intent(context, ChargingAnimationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            context.startForegroundService(serviceIntent)

        }else{

            context.startService(serviceIntent)
        }
    }

    private fun stopChargingAnimation(context: Context) {
        val serviceIntent = Intent(context, ChargingAnimationService::class.java)
        context.stopService(serviceIntent)
    }
}