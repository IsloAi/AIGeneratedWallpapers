package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.ActivityBatteryAnimationBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class BatteryAnimationActivity : AppCompatActivity() {
    lateinit var binding:ActivityBatteryAnimationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBatteryAnimationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

       binding.time.text = getCurrentDateTime("time")
       binding.date.text = getCurrentDate()

        updateBatteryPercentage()
        updateTime()

        setLottieAnimationFromFile(this, MySharePreference.getAnimationPath(this)!!,binding.animationView)

        this.registerReceiver(mBatInfoReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        val filter = IntentFilter()
        filter.addAction("closeAction")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(receiver, filter, RECEIVER_NOT_EXPORTED)
        }else{
            registerReceiver(receiver, filter)
        }

        openActivityOnLockScreen()

    }

    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val month = SimpleDateFormat("MMM", Locale.getDefault()).format(calendar.time)
        val year = calendar.get(Calendar.YEAR)

        return when (dayOfMonth) {
            1, 21, 31 -> "${getOrdinal(dayOfMonth)} $month $year"
            2, 22 -> "${dayOfMonth}nd $month $year"
            3, 23 -> "${dayOfMonth}rd $month $year"
            else -> "${dayOfMonth}th $month $year"
        }
    }

    private fun getOrdinal(n: Int): String {
        return when (n % 10) {
            1 -> "${n}st"
            2 -> "${n}nd"
            3 -> "${n}rd"
            else -> "${n}th"
        }
    }


    private fun updateBatteryPercentage() {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            applicationContext.registerReceiver(null, ifilter)
        }

        val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1

        val batteryPct = (level * 100 / scale).toString() + "%"
        binding.batteryPercentage.text = batteryPct
    }

    var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            this@BatteryAnimationActivity.finishAndRemoveTask()
        }
    }

    private fun updateTime() {
        val handler = Handler()
        handler.post(object : Runnable {
            override fun run() {
                val currentTime = getCurrentDateTime("time")
                binding.time.text = currentTime
                handler.postDelayed(this, 1000)
            }
        })
    }

    fun setLottieAnimationFromFile(context: Context, filePath: String, lottieAnimationView: LottieAnimationView) {
        try {
        val fileInputStream: FileInputStream = FileInputStream(File(filePath))

        // Load the Lottie composition from the file input stream
        val compositionTask = LottieCompositionFactory.fromJsonInputStream(fileInputStream, null)
        compositionTask.addListener { composition ->
            if (composition != null) {
                // Set the loaded composition to the LottieAnimationView
                lottieAnimationView.setComposition(composition)
                lottieAnimationView.repeatCount = LottieDrawable.INFINITE // Adjust as needed
                lottieAnimationView.playAnimation()
            } else {
                // Handle error loading composition
                Log.e("LottieAnimation", "Failed to load Lottie composition from file")
            }
            fileInputStream.close() // Close the file input stream after loading
        }

        } catch (e: Exception) {
            Log.e("LottieAnimation", "Error loading Lottie animation from file: ${e.message}")
        }
    }


    private val mBatInfoReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val batteryPct = level * 100 / scale
            binding.batteryPercentage.text = "$batteryPct%"
        }
    }

    fun getCurrentDateTime(type:String): String {
        val calendar = Calendar.getInstance()
        val dateFormat:SimpleDateFormat
        if (type == "time"){
            dateFormat = SimpleDateFormat("HH:mm")
        }else{
            dateFormat = SimpleDateFormat("dd-mm-yyyy")
        }

        return dateFormat.format(calendar.time)
    }

    private fun openActivityOnLockScreen() {
        val win = window
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        win.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        )
    }
}