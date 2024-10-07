package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.notiWidget

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.ActivityNotiWidgetBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.notiWidget.models.NotificationConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NotiWidgetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotiWidgetBinding
    private var notificationConfig: NotificationConfig? = null
    private var languageCode: String = "en"
    private var handler: Handler? = null
    private val updateInterval: Long = 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            setDimAmount(0.0f)
            addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
        binding = ActivityNotiWidgetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CoroutineScope(Dispatchers.Main).launch {
            loadData()
        }

        binding.close.setOnClickListener {
            finish()
        }

        binding.actionButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        turnOnScreen()
        startUpdatingTimeAndDate()

        hideSystemUI()
    }

    private fun loadNotificationImage(url: String?) {
        if (url.isNullOrEmpty()) {
            Log.e("NotiWidgetActivity", "Image URL is null or empty.")
            return
        }

        Glide.with(this)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .thumbnail(0.1f)
            .into(binding.image)
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }

    private fun turnOnScreen() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "NotificationWidgetService::WakeLock"
        )
        try {
            wakeLock.acquire(5 * 1000L) // Acquire wake lock for 5 seconds
        } catch (e: Exception) {
            Log.e("NotiWidgetActivity", "Failed to acquire wake lock", e)
        } finally {
            Handler(Looper.getMainLooper()).postDelayed({
                if (wakeLock.isHeld) {
                    wakeLock.release()
                }
            }, 1000L)
        }
    }

    private fun startUpdatingTimeAndDate() {
        handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                showFormattedTime()
                showFormattedDate()
                handler?.postDelayed(this, updateInterval) // Re-run every second
            }
        }
        handler?.post(runnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler?.removeCallbacksAndMessages(null)  // Stop the handler to avoid memory leaks
        MySharePreference.updateDayIfDateChanged(this)
    }

    private fun loadData() {
        val gson = Gson()
        val notiConfigJson = MySharePreference.getNotificationWidget(this@NotiWidgetActivity)
        notificationConfig = gson.fromJson(notiConfigJson, NotificationConfig::class.java)
        if (notificationConfig == null) {
            Log.e("NotificationWorker", "Failed to fetch notification config")
            return
        }
        updateUI()
    }

    private fun updateUI() {
        notificationConfig?.let { config ->
            val previousDay = MySharePreference.getStoredDay(this@NotiWidgetActivity)
            val dayIndex = (previousDay + 1) % 7
            if (config.notification_data.isNotEmpty()) {
                showFormattedDate()
                showFormattedTime()
                binding.title.text = config.notification_data[dayIndex].title[languageCode]
                binding.message.text = config.notification_data[dayIndex].message[languageCode]
                binding.actionButton.text =
                    config.notification_data[dayIndex].btn_text[languageCode]
                loadNotificationImage(config.notification_data[dayIndex].image[languageCode])
            } else {
                Log.e("NotiWidgetActivity", "No notification data available.")
            }
        } ?: run {
            Log.e("NotiWidgetActivity", "notificationConfig is not initialized.")
        }
    }

    private fun showFormattedTime() {
        val calendar = Calendar.getInstance()
        val hourFormat = SimpleDateFormat("HH", Locale.getDefault())
        val minuteFormat = SimpleDateFormat("mm", Locale.getDefault())

        binding.hour.text = hourFormat.format(calendar.time)
        binding.minutes.text = minuteFormat.format(calendar.time)
    }

    private fun showFormattedDate() {
        val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
        val currentDate = Date()
        binding.date.text = dateFormat.format(currentDate)
    }

}