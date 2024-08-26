package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.service

import android.app.WallpaperManager
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Build
import android.service.wallpaper.WallpaperService
import android.util.Log
import android.view.SurfaceHolder
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.WallpaperChangeListener
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.ForegroundWorker.Companion.TAG
import java.io.IOException

class LiveWallpaperService : WallpaperService() {

    private var engineInstance: WallpaperVideoEngine? = null

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(applicationContext)

    }

    internal inner class WallpaperVideoEngine : Engine() {

        init {
            engineInstance = this
        }

        private var myMediaPlayer: MediaPlayer? = null
        private var liveWallBroadcastReceiver: BroadcastReceiver? = null
        private var liveVideoFilePath: String? = null
        private var isReceiverRegistered = false

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)

            FirebaseApp.initializeApp(applicationContext)

            val intentFilter = IntentFilter(VIDEO_PARAMS_CONTROL_ACTION)
            intentFilter.addAction(ACTION_UPDATE_WALLPAPER)

            val broadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    when (intent.action) {
                        KEY_ACTION -> {
                            val action = intent.getBooleanExtra(KEY_ACTION, false)
                            myMediaPlayer?.setVolume(
                                if (action) 0f else 1.0f,
                                if (action) 0f else 1.0f
                            )
                        }

                        ACTION_UPDATE_WALLPAPER -> {
                            restartMediaPlayer()
                        }
                    }
                }
            }
            liveWallBroadcastReceiver = broadcastReceiver

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(broadcastReceiver, intentFilter, RECEIVER_EXPORTED)
            } else {
                registerReceiver(broadcastReceiver, intentFilter)
            }
            isReceiverRegistered = true
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)

            myMediaPlayer = MediaPlayer()
            myMediaPlayer?.setSurface(holder.surface)
            startPlayer()
        }

        private fun startPlayer() {
            try {
                val file = applicationContext.filesDir
                val video = file.path + "/" + "video.mp4"
                Log.e("TAG", "onCreate: $video")
                liveVideoFilePath = video

                myMediaPlayer?.apply {
                    setDataSource(liveVideoFilePath)
                    isLooping = true
                    setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
                    prepare()
                    start()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: IllegalStateException) {
                Log.e("TAG", "IllegalStateException occurred", e)
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            if (visible) {
                try {
                    myMediaPlayer?.start()
                } catch (e: IllegalStateException) {
                    Log.e("LiveWallpaperService", "MediaPlayer is in an illegal state", e)
                    restartMediaPlayer()
                }
            } else {
                myMediaPlayer?.pause()
            }
        }

        private fun restartMediaPlayer() {
            myMediaPlayer?.release()
            myMediaPlayer = MediaPlayer()
            myMediaPlayer?.setSurface(surfaceHolder.surface)
            startPlayer()
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            if (myMediaPlayer!!.isPlaying) myMediaPlayer!!.stop()
            myMediaPlayer?.release()
            myMediaPlayer = null
        }

        override fun onDestroy() {
            super.onDestroy()
            myMediaPlayer?.release()
            myMediaPlayer = null
            if (isReceiverRegistered) {
                unregisterReceiver(liveWallBroadcastReceiver)
                isReceiverRegistered = false
            }
        }
    }

    override fun onCreateEngine(): Engine {
        return WallpaperVideoEngine()
    }

    companion object {

        const val ACTION_WALLPAPER_SET_SUCCESS = "com.swedaiaiwallpapersart.WALLPAPER_SET_SUCCESS"
        const val ACTION_WALLPAPER_SET_FAILURE = "com.swedaiaiwallpapersart.WALLPAPER_SET_FAILURE"
        const val VIDEO_PARAMS_CONTROL_ACTION =
            "com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto"
        private const val KEY_ACTION = "music"
        const val ACTION_UPDATE_WALLPAPER = "com.swedaiaiwallpapersart.UPDATE_WALLPAPER"

        private fun updateWallpaper(context: Context) {
            Log.d(TAG, "updateWallpaper: SendBroadcast")
            Intent(ACTION_WALLPAPER_SET_SUCCESS).apply { context.sendBroadcast(this) }
            Intent(ACTION_UPDATE_WALLPAPER).apply {
                context.sendBroadcast(this)
            }
        }

        fun setToWallPaper(context: Context, isFirst: Boolean) {
            if (isFirst) {
                try {
                    Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
                        putExtra(
                            WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                            ComponentName(context, LiveWallpaperService::class.java)
                        )
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }.also { context.startActivity(it) }

                    // Send success broadcast
                    Intent(ACTION_WALLPAPER_SET_SUCCESS).apply { context.sendBroadcast(this) }
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                    Toast.makeText(
                        context,
                        "This device doesn't support Live Wallpaper",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Send failure broadcast
                    Intent(ACTION_WALLPAPER_SET_FAILURE).apply { context.sendBroadcast(this) }
                }
            } else {
                updateWallpaper(context)
            }
        }

    }
}