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
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MyApp
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import java.io.File
import java.io.IOException

class LiveWallpaperService : WallpaperService() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(applicationContext)
    }
    internal inner class WallpaperVideoEngine : Engine() {
        private var myMediaPlayer: MediaPlayer? = null
        private var liveWallBroadcastReceiver: BroadcastReceiver? = null
        private var liveVideoFilePath: String? = null
        private var isReceiverRegistered = false
        private val context: Context? = null
        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)

            FirebaseApp.initializeApp(applicationContext)

            val intentFilter = IntentFilter(VIDEO_PARAMS_CONTROL_ACTION)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(object : BroadcastReceiver() {
                    override fun onReceive(context: Context, intent: Intent) {
                        val action = intent.getBooleanExtra(KEY_ACTION, false)
                        if (action) {
                            myMediaPlayer!!.setVolume(0f, 0f)
                        } else {
                            myMediaPlayer!!.setVolume(1.0f, 1.0f)
                        }
                        isReceiverRegistered = true
                    }
                }.also { liveWallBroadcastReceiver = it }, intentFilter, RECEIVER_EXPORTED)
            }else{
                registerReceiver(object : BroadcastReceiver() {
                    override fun onReceive(context: Context, intent: Intent) {
                        val action = intent.getBooleanExtra(KEY_ACTION, false)
                        if (action) {
                            myMediaPlayer!!.setVolume(0f, 0f)
                        } else {
                            myMediaPlayer!!.setVolume(1.0f, 1.0f)
                        }
                        isReceiverRegistered = true
                    }
                }.also { liveWallBroadcastReceiver = it }, intentFilter)
            }
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)

            myMediaPlayer = MediaPlayer()
            myMediaPlayer?.setSurface(holder.surface)
            startPlayer()
        }

        fun startPlayer(){
            try {
            val file = applicationContext.filesDir

//
            val video =  file.path + "/" + "video.mp4"

            Log.e("TAG", "onCreate: $video")

            liveVideoFilePath = video


            myMediaPlayer?.apply {
                setDataSource(liveVideoFilePath)
                isLooping = true
                setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
                prepare()
                start()
            }

//                val file1 = File("$filesDir/unmute")
//                if (file1.exists()) myMediaPlayer!!.setVolume(1.0f, 1.0f) else myMediaPlayer!!.setVolume(
//                    0f,
//                    0f
//                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
            catch (e: IllegalStateException) {
                // Log the exception for debugging
                Log.e("TAG", "IllegalStateException occurred", e)

            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            if (visible) {
                myMediaPlayer?.start()
            } else {
                myMediaPlayer?.pause()
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
            if (isReceiverRegistered){
                unregisterReceiver(liveWallBroadcastReceiver)
                isReceiverRegistered = false
            }

//            unregisterReceiver(wallpaperUpdateReceiver)
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

            try {


                Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
                    putExtra(
                        WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                        ComponentName(context, LiveWallpaperService::class.java)
                    )
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP


                }.also {
//                if (it.resolveActivity(context.packageManager) != null) {
                    context.startActivity(it)
//                }else{
//                    Toast.makeText(context,"this device don't support Live Wallpaper",Toast.LENGTH_SHORT).show()
//                }

                }
                try {
                    WallpaperManager.getInstance(context).clear()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }catch (e:ActivityNotFoundException){
                e.printStackTrace()
                Toast.makeText(context,"this device don't support Live Wallpaper",Toast.LENGTH_SHORT).show()
            }



        }

        fun notifyWallpaperFileUpdated(context: Context) {
            val updateIntent = Intent("com.yourapp.WALLPAPER_FILE_UPDATED")
            context.sendBroadcast(updateIntent)
        }
    }
}