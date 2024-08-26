package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.BatteryAnimationActivity


open class BroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_POWER_CONNECTED) {
            Log.e("TAG", "onReceive: connected$action")
            val intent1 = Intent(context, BatteryAnimationActivity::class.java)
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context?.startActivity(intent1)
        } else if (action == Intent.ACTION_POWER_DISCONNECTED) {
            Log.e("TAG", "onReceive: disconnected"+action )
            val local = Intent()
            local.setAction("closeAction")
            context!!.sendBroadcast(local)
        }
    }
}