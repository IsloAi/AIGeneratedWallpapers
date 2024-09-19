package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.notiWidget

import android.app.KeyguardManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.notiWidget.models.NotificationConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class NotificationWidgetService : Service() {
    private lateinit var job: Job
    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: View
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var screenLockReceiver: ScreenLockReceiver

    private val checkOverlayRunnable = object : Runnable {
        override fun run() {
            checkAndShowOverlay()
            handler.postDelayed(this, 1000) // Check every second
        }
    }

    override fun onCreate() {
        super.onCreate()
        job = Job()
        val scope = CoroutineScope(Dispatchers.IO + job)
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        startForegroundService()
        // Initialize the BroadcastReceiver
        screenLockReceiver = ScreenLockReceiver(
            onLock = {
                Log.d("NotificationWidgetService", "Device locked")
                handler.post(checkOverlayRunnable) // Start checking every second when locked
            },
            onUnlock = {
                Log.d("NotificationWidgetService", "Device unlocked")
                handler.removeCallbacks(checkOverlayRunnable) // Stop checking when unlocked
                removeOverlay()
            }
        )
        registerReceiver(screenLockReceiver, IntentFilter(Intent.ACTION_SCREEN_OFF))
        registerReceiver(screenLockReceiver, IntentFilter(Intent.ACTION_USER_PRESENT))

        // Initial check
        scope.launch {
            checkAndShowOverlay()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        handler.removeCallbacks(checkOverlayRunnable) // Stop the runnable when service is destroyed
        unregisterReceiver(screenLockReceiver) // Unregister the receiver
        removeOverlay() // Ensure the overlay is removed
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun removeOverlay() {
        if (::overlayView.isInitialized) {
            try {
                if (overlayView.windowToken != null) {
                    windowManager.removeView(overlayView)
                    Log.d("NotificationWidgetService", "Overlay removed")
                }
            } catch (e: IllegalArgumentException) {
                Log.e("NotificationWidgetService", "Error removing overlay: ${e.message}", e)
            }
        }
    }

    private fun checkAndShowOverlay() {
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
                val currentDate = MySharePreference.getStoredDate(this@NotificationWidgetService)
                val currentDay = MySharePreference.getStoredDay(this@NotificationWidgetService)
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
                        calendar.set(Calendar.HOUR_OF_DAY, timeArray[0].toInt())
                        calendar.set(Calendar.MINUTE, timeArray[1].toInt())
                        calendar.set(Calendar.SECOND, 0)

                        if (currentTimeInMillis >= calendar.timeInMillis && isDeviceLocked(this@NotificationWidgetService)) {
                            MySharePreference.updateDayIfDateChanged(this@NotificationWidgetService)
                            val intent =
                                Intent(
                                    this@NotificationWidgetService,
                                    NotiWidgetActivity::class.java
                                )
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                            intent.putExtra("notification_config", notificationConfig)
                            startActivity(intent)
                            break
                        }
                    }
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private fun startForegroundService() {
        val notificationId = 123
        val channelId = "notification_widget_service_channel"
        val channelName = "Silent Notifications"

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_MIN
            ).apply {
                description = "Channel for silent notifications"
                enableLights(false)
                enableVibration(false)
                setSound(null, null)
                setShowBadge(false)
            }
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle(null)
            .setContentText(null)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOngoing(true)
            .setSilent(true)
            .setVibrate(null)
            .setSound(null)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .setAutoCancel(false)
            .build()

        startForeground(notificationId, notification)
    }

    private fun isDeviceLocked(context: Context): Boolean {
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        return keyguardManager.isKeyguardLocked
    }
}
