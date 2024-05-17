package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import com.airbnb.lottie.LottieAnimationView
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.ForegroundWorker.Companion.CHANNEL_ID


class ChargingAnimationService: Service() {
    private var notification: Notification? = null
    private var animationView: LottieAnimationView? = null
    private val CHANNEL_ID = "charging_animation_channel"
    private var broadcastReceiver: BroadcastReceiver? = null


    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()


        notification =  createNotification()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(1, notification!!, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(1, notification)
        }

        registerReceiver()
    }

    private fun registerReceiver() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        }
        broadcastReceiver = BroadcastReceiver().also { receiver ->
            registerReceiver(receiver, filter)
        }
    }



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.e("TAG", "onStartCommand: started" )

        return  START_STICKY;
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "charging_animation_channel"
            val channelName = "Charging Animation Notification"
            val importance = NotificationManager.IMPORTANCE_LOW // Adjust importance as needed
            val channel = NotificationChannel(channelId, channelName, importance)
            channel.description = "Notification for charging animation service"

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun createNotification():Notification {
        val channelId = "charging_animation_channel"
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle("Charging Animation")
            .setContentText("Animation is running...")
            .setPriority(NotificationCompat.PRIORITY_LOW)
        return  builder.build()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun unregisterReceiver() {
        broadcastReceiver?.let {
            unregisterReceiver(it)
            broadcastReceiver = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver()
    }


}