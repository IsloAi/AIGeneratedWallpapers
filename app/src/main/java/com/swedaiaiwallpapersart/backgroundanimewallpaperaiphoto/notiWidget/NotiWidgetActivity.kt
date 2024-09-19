package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.notiWidget

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.PixelFormat
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.ActivityNotiWidgetBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.notiWidget.models.NotificationConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class NotiWidgetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotiWidgetBinding
    private lateinit var notificationConfig: NotificationConfig
    private var languageCode:String = "en"

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

        languageCode = MySharePreference.getLanguage(this)?.takeIf { it.isNotEmpty() } ?: "en"

        notificationConfig = intent.getSerializableExtra("notification_config") as NotificationConfig

        binding.title.text = notificationConfig.notification_data[0].title[languageCode]
        binding.message.text = notificationConfig.notification_data[0].message[languageCode]

        // Load image and set it to ImageView
        loadNotificationImage(notificationConfig.notification_data[0].image[languageCode])

        // Set click listeners for buttons
        binding.close.setOnClickListener {
            finish() // Close activity
        }

        binding.actionButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        loadNotificationImage(notificationConfig.notification_data[0].image[languageCode])

        turnOnScreen()
    }
    private fun loadNotificationImage(url: String?) {

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val inputStream = URL(url).openStream()
                val bitmap = BitmapFactory.decodeStream(inputStream)

                // Switch back to the main thread to update the UI
                withContext(Dispatchers.Main) {
                    binding.image.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace() // Handle exceptions like failed network requests
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }
    }
    private fun turnOnScreen() {
        val powerManager = applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "NotificationWidgetService::WakeLock"
        )

        wakeLock.acquire(5 * 1000L) // Acquire wake lock for 5 seconds

        // Schedule wake lock release after 5 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            if (wakeLock.isHeld) {
                wakeLock.release()  // Release the wake lock
            }
        }, 5000L)  // 5000 milliseconds = 5 seconds
    }

}