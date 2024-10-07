package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.notiWidget

import android.app.KeyguardManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.Q
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.ForegroundInfo
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.notiWidget.models.NotificationConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference

class NotificationWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    override fun doWork(): Result {
        Log.d(
            "NotificationScheduler",
            "Notification called from doWork()"
        )
        val config = loadData(applicationContext)
        if (config != null) {
            setForegroundAsync(createForegroundInfo(applicationContext, config))
            createNotificationChannel()
        }else{
            Log.e("NotificationScheduler", "Failed to fetch notification config")
        }
        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "NOTIFICATION_CHANNEL"
            val channel = NotificationChannel(
                channelId,
                "Notification Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for notifications"
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            val notificationManager =
                applicationContext.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createForegroundInfo(context: Context, config: NotificationConfig): ForegroundInfo {
        return if (SDK_INT >= Q) {
            ForegroundInfo(
                15,
                showFullScreenNotification(context, config),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(15, showFullScreenNotification(context, config))
        }
    }

    private fun showFullScreenNotification(
        context: Context,
        notificationConfig: NotificationConfig
    ): Notification {
        val languageCode = MySharePreference.getLanguage(context)
            ?.takeIf { it.isNotEmpty() } ?: "en"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "full_screen_channel"
            val channel = NotificationChannel(
                channelId,
                "Full Screen Notification",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for full-screen notifications"
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }

        val fullScreenIntent = Intent(context, NotiWidgetActivity::class.java)
        fullScreenIntent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            0,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val previousDay = MySharePreference.getStoredDay(context)
        val dayIndex = (previousDay + 1) % 7

        val notificationBuilder = NotificationCompat.Builder(context, "full_screen_channel")
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle(notificationConfig.notification_data[dayIndex].title[languageCode])
            .setContentText(notificationConfig.notification_data[dayIndex].message[languageCode])
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setOngoing(false)
            .setAutoCancel(false)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        wakeScreenAndShowActivity(context, fullScreenIntent)
        notificationManager.notify(1, notificationBuilder.build())

        return notificationBuilder.build()
    }

    private fun wakeScreenAndShowActivity(context: Context, intent: Intent) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        // Wake up the screen
        val isScreenOn = powerManager.isInteractive
        if (!isScreenOn) {
            val wakeLock = powerManager.newWakeLock(
                PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE,
                "MyApp::MyWakelockTag"
            )
            wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/)
        }

        // Check if the keyguard is locked
        if (keyguardManager.isKeyguardLocked) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            context.startActivity(intent)
        }
    }

    private fun loadData(context: Context): NotificationConfig? {
        val gson = Gson()
        val notiConfigJson = MySharePreference.getNotificationWidget(context)
        val config = gson.fromJson(notiConfigJson, NotificationConfig::class.java)

        if (config == null) {
            Log.e("NotificationWidgetService", "Failed to fetch notification config")
            return null
        }
        val previousDay = MySharePreference.getStoredDay(context)
        val dayIndex = (previousDay + 1) % 7
        var dayConfig = config.notification_scheduler.getOrNull(dayIndex)
        if (previousDay == -1) {
            dayConfig = config.notification_scheduler.getOrNull(0)
        }

        // Only show the notification for the active day and time
        if (dayConfig?.is_active == true) {
            return config
        }
        return null
    }
}