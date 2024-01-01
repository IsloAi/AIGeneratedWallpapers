package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.service

import android.app.WallpaperManager
import android.content.*
import android.media.MediaPlayer
import android.os.Build
import android.os.Environment
import android.service.wallpaper.WallpaperService
import android.util.Log
import android.view.SurfaceHolder
import com.google.firebase.FirebaseApp
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.BlurView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import java.io.File
import java.io.IOException

class LiveWallpaperService : WallpaperService() {
    internal inner class WallpaperVideoEngine : Engine() {
        private var myMediaPlayer: MediaPlayer? = null
        private var liveWallBroadcastReceiver: BroadcastReceiver? = null
        private var liveVideoFilePath: String? = null

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)

            FirebaseApp.initializeApp(applicationContext)

            val file = applicationContext.filesDir

//
            val video =  file.path + "/" + MySharePreference.getFileName(applicationContext)

            Log.e("TAG", "onCreate: $video")

            liveVideoFilePath = video
            val intentFilter = IntentFilter(VIDEO_PARAMS_CONTROL_ACTION)
            registerReceiver(object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val action = intent.getBooleanExtra(KEY_ACTION, false)
                    if (action) {
                        myMediaPlayer!!.setVolume(0f, 0f)
                    } else {
                        myMediaPlayer!!.setVolume(1.0f, 1.0f)
                    }
                }
            }.also { liveWallBroadcastReceiver = it }, intentFilter)
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            myMediaPlayer = MediaPlayer().apply {
                setSurface(holder.surface)
                setDataSource(liveVideoFilePath)
                isLooping = true
                setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
                prepare()
                start()
            }
            try {
                val file = File("$filesDir/unmute")
                if (file.exists()) myMediaPlayer!!.setVolume(1.0f, 1.0f) else myMediaPlayer!!.setVolume(
                    0f,
                    0f
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            if (visible) {
                myMediaPlayer!!.start()
            } else {
                myMediaPlayer!!.pause()
            }
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
            unregisterReceiver(liveWallBroadcastReceiver)
        }
    }

    override fun onCreateEngine(): Engine {
        return WallpaperVideoEngine()
    }

    companion object {
        const val VIDEO_PARAMS_CONTROL_ACTION = "com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto"
        private const val KEY_ACTION = "music"
        private const val ACTION_MUSIC_UNMUTE = false
        private const val ACTION_MUSIC_MUTE = true
        fun muteMusic(context: Context) {
            Intent(VIDEO_PARAMS_CONTROL_ACTION).apply {
                putExtra(KEY_ACTION, ACTION_MUSIC_MUTE)
            }.also { context.sendBroadcast(it) }
        }

        fun unmuteMusic(context: Context) {
            Intent(VIDEO_PARAMS_CONTROL_ACTION).apply {
                putExtra(KEY_ACTION, ACTION_MUSIC_UNMUTE)
            }.also {
                context.sendBroadcast(it)
            }
        }

        fun setToWallPaper(context: Context) {
            Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
                putExtra(
                    WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    ComponentName(context, LiveWallpaperService::class.java)
                )
            }.also {
                context.startActivity(it)
            }
            try {
                WallpaperManager.getInstance(context).clear()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}