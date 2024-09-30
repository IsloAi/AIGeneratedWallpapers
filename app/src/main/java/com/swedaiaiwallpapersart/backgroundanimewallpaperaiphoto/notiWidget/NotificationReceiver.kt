package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.notiWidget

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.HomeTabsFragment.Companion.ALARM_ACTION

class NotificationReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            Log.d("NotificationReceiver", "onReceive triggered with action: ${intent.action}")
            if (intent.action == ALARM_ACTION) {
                context?.let {
                    showFullScreenNotification(it) // Show full-screen notification here
                }
            }
        }

        private fun showFullScreenNotification(context: Context) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Create a Notification Channel (required for Android 8.0 and above)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelId = "full_screen_channel"
                val channel = NotificationChannel(
                    channelId,
                    "Full Screen Notification",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Channel for full-screen notifications"
                }
                notificationManager.createNotificationChannel(channel)
            }

            // Create an intent for the full-screen activity
            val fullScreenIntent = Intent(context, NotiWidgetActivity::class.java)
            fullScreenIntent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            // Create a pending intent
            val fullScreenPendingIntent = PendingIntent.getActivity(
                context,
                0,
                fullScreenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Create a notification with a full-screen intent
            val notificationBuilder = NotificationCompat.Builder(context, "full_screen_channel")
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle("Full-Screen Notification")
                .setContentText("This is a full-screen notification.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setFullScreenIntent(fullScreenPendingIntent, true)

            notificationManager.notify(1, notificationBuilder.build())
        }
    }
