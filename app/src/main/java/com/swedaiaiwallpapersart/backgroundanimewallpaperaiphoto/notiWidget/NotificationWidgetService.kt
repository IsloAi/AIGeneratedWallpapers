package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.notiWidget

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.notiWidget.models.NotificationConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.URL

@AndroidEntryPoint
class NotificationWidgetService : Service() {
    private lateinit var job: Job
    override fun onCreate() {
        super.onCreate()
        job = Job()
        val scope = CoroutineScope(Dispatchers.IO + job)

        // Launch a coroutine to call initObservers
        scope.launch {
            initObservers()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showNotification()
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private fun showNotification() {
        // Create a custom notification layout
        val remoteViews = RemoteViews(packageName, R.layout.layout_noti_widget)

        val notificationBuilder = NotificationCompat.Builder(this, "Background progress")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setColor(ContextCompat.getColor(this, R.color.white))
            .setContent(remoteViews)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // Ensure it shows on the lock screen
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val notificationManager = NotificationManagerCompat.from(this)

        // Create the notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "Background progress",
                "Channel Name",
                NotificationManager.IMPORTANCE_HIGH // High importance to ensure visibility on lock screen
            )
            channel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(channel)
        }

        // Show the notification
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(1, notificationBuilder)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                1,
                notificationBuilder,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(1, notificationBuilder)
        }
    }

    private fun scheduleNotification(triggerTimeInMillis: Long) {
        val intent = Intent(this, NotificationWidgetService::class.java)
        val pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            triggerTimeInMillis,
            pendingIntent
        )
    }

    private suspend fun showNotification(notificationModel: NotificationConfig) {
        // Create an intent for action (if any)

        // Fetch the image if there is one
        val bitmap = notificationModel.notification_data[0].image["en"]?.let {
            withContext(Dispatchers.IO) {
                val url = URL(it)
                val inputStream: InputStream = url.openConnection().getInputStream()
                BitmapFactory.decodeStream(inputStream)
            }
        }

        // Create the notification
        val builder = NotificationCompat.Builder(this, "your_channel_id")
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle(notificationModel.notification_data[0].title["en"])
            .setContentText(notificationModel.notification_data[0].message["en"])
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // If there is an image, show it
        if (bitmap != null) {
            builder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
        }

        // Show the notification
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(this).notify(1, builder.build())
            return
        }
    }
    private suspend fun initObservers() {
        val gson = Gson()
        val notificationConfig = gson.fromJson(AdConfig.Noti_Widget, NotificationConfig::class.java)
        showNotification(notificationConfig)
    }
}