package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.notiWidget

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.notiWidget.models.NotificationConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        showFullScreenNotification(applicationContext)
        return Result.success()
    }
    private fun showFullScreenNotification(context: Context) {
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
        val remoteViews = RemoteViews(context.packageName, R.layout.layout_noti_widget)
        CoroutineScope(Dispatchers.IO).launch {
            val gson = Gson()
            val notificationConfig =
                gson.fromJson(AdConfig.Noti_Widget, NotificationConfig::class.java)

            if (notificationConfig == null) {
                Log.e("NotificationWidgetService", "Failed to fetch notification config")
                return@launch
            }
            for (dayIndex in 0 until 7) {
                val calendar = Calendar.getInstance()
                val currentDate = MySharePreference.getStoredDate(context)
                val currentDay = MySharePreference.getStoredDay(context)
                val currentTimeInMillis = System.currentTimeMillis()
                val todayDate = SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                ).format(Calendar.getInstance().time)

                if (currentDate != todayDate) {
                    val dayConfig = notificationConfig.notification_scheduler.getOrNull(dayIndex)
                    if (dayConfig?.is_active == true && dayIndex == currentDay) {
                        val time = dayConfig.time
                        val timeArray = time.split(":")
                        calendar.set(Calendar.HOUR_OF_DAY, 14)
                        calendar.set(Calendar.MINUTE, 34)
                        calendar.set(Calendar.SECOND, 0)

                        val languageCode = MySharePreference.getLanguage(context)?.takeIf { it.isNotEmpty() } ?: "en"
                        if (currentTimeInMillis >= calendar.timeInMillis) {
                            remoteViews.setTextViewText(R.id.title,notificationConfig.notification_data[0].title[languageCode])
                            remoteViews.setTextViewText(R.id.message,notificationConfig.notification_data[0].message[languageCode])
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val inputStream = URL(notificationConfig.notification_data[0].image[languageCode]).openStream()
                                    val bitmap = BitmapFactory.decodeStream(inputStream)

                                    withContext(Dispatchers.Main) {
                                        remoteViews.setBitmap(R.id.image,"",bitmap)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            break
                        }
                    }
                }
            }
        }

        val notificationBuilder = NotificationCompat.Builder(context, "full_screen_channel")
            .setSmallIcon(R.drawable.app_icon)
            .setCustomContentView(remoteViews)
            .setContentTitle("Full-Screen Notification")
            .setContentText("This is a full-screen notification.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setAutoCancel(true)

        notificationManager.notify(1, notificationBuilder.build())
    }
}