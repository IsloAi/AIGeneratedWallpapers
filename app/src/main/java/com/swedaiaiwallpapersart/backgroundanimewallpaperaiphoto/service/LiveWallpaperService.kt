package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.service

import android.app.WallpaperManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.util.Log
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.google.firebase.FirebaseApp
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.ForegroundWorker.Companion.TAG

class LiveWallpaperService : WallpaperService() {

    private var engineInstance: WallpaperVideoEngine? = null

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(applicationContext)
    }

    inner class WallpaperVideoEngine : Engine() {

        init {
            engineInstance = this
        }

        private var exoPlayer: ExoPlayer? = null
        private var liveWallBroadcastReceiver: BroadcastReceiver? = null
        private var liveVideoFilePath: String? = null
        private var isReceiverRegistered = false

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)

            FirebaseApp.initializeApp(applicationContext)

            val intentFilter = IntentFilter(VIDEO_PARAMS_CONTROL_ACTION)
            intentFilter.addAction(ACTION_UPDATE_WALLPAPER)

            liveWallBroadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent) {
                    super.onReceive(context, intent)
                    when (intent.action) {
                        KEY_ACTION -> {
                            val action = intent.getBooleanExtra(KEY_ACTION, false)
                            exoPlayer?.volume = if (action) 0f else 1f
                        }

                        ACTION_UPDATE_WALLPAPER -> {
                            restartExoPlayer()
                        }
                    }
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(liveWallBroadcastReceiver, intentFilter, RECEIVER_EXPORTED)
            } else {
                registerReceiver(liveWallBroadcastReceiver, intentFilter)
            }
            isReceiverRegistered = true
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            initializeExoPlayer(holder)
        }

        @OptIn(UnstableApi::class)
        private fun initializeExoPlayer(holder: SurfaceHolder) {
            val renderersFactory = DefaultRenderersFactory(applicationContext)
                .setEnableDecoderFallback(true)
                .setExtensionRendererMode(
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                        DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF
                    } else {
                        DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
                    }
                )

            val loadControl = DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                    DefaultLoadControl.DEFAULT_MIN_BUFFER_MS,
                    DefaultLoadControl.DEFAULT_MAX_BUFFER_MS,
                    DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
                    DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
                )
                .setPrioritizeTimeOverSizeThresholds(true)
                .build()

            val trackSelector = DefaultTrackSelector(applicationContext)
            trackSelector.setParameters(trackSelector.buildUponParameters().setMaxVideoSizeSd())

            exoPlayer = ExoPlayer.Builder(applicationContext)
                .setRenderersFactory(renderersFactory)
                .setLoadControl(loadControl)
                .setTrackSelector(trackSelector)
                .setUseLazyPreparation(true)
                .build()

            exoPlayer?.apply {
                setVideoSurface(holder.surface)

                val videoPath = applicationContext.filesDir.path + "/" + "video.mp4"
                liveVideoFilePath = videoPath
                val mediaItem = MediaItem.fromUri(videoPath)

                setMediaItem(mediaItem)
                repeatMode = Player.REPEAT_MODE_ALL
                videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                    videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
                }

                addListener(object : Player.Listener {
                    override fun onPlayerError(error: PlaybackException) {
                        Log.e("LiveWallpaperService", "ExoPlayer Error: ${error.message}", error)
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_ENDED) {
                            seekTo(0)
                            playWhenReady = true
                        }
                    }
                })

                prepare()
                playWhenReady = true
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            exoPlayer?.playWhenReady = visible
        }

        private fun restartExoPlayer() {
            exoPlayer?.release()
            initializeExoPlayer(surfaceHolder)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            exoPlayer?.release()
            exoPlayer = null
        }

        override fun onDestroy() {
            super.onDestroy()
            exoPlayer?.release()
            exoPlayer = null

            if (isReceiverRegistered && liveWallBroadcastReceiver != null) {
                unregisterReceiver(liveWallBroadcastReceiver)
                isReceiverRegistered = false
                liveWallBroadcastReceiver = null
            }

            engineInstance = null
        }
    }

    override fun onCreateEngine(): Engine {
        return WallpaperVideoEngine()
    }

    companion object {
        private const val ACTION_WALLPAPER_SET_SUCCESS =
            "com.swedaiaiwallpapersart.WALLPAPER_SET_SUCCESS"
        private const val ACTION_WALLPAPER_SET_FAILURE =
            "com.swedaiaiwallpapersart.WALLPAPER_SET_FAILURE"
        const val VIDEO_PARAMS_CONTROL_ACTION =
            "com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto"
        private const val KEY_ACTION = "music"
        const val ACTION_UPDATE_WALLPAPER = "com.swedaiaiwallpapersart.UPDATE_WALLPAPER"

        private fun updateWallpaper(context: Context) {
            Log.d(TAG, "updateWallpaper: SendBroadcast")
            Intent(ACTION_WALLPAPER_SET_SUCCESS).apply { context.sendBroadcast(this) }
            Intent(ACTION_UPDATE_WALLPAPER).apply { context.sendBroadcast(this) }
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

                    Intent(ACTION_WALLPAPER_SET_SUCCESS).apply { context.sendBroadcast(this) }
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(
                            context,
                            "This device doesn't support Live Wallpaper",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    Intent(ACTION_WALLPAPER_SET_FAILURE).apply { context.sendBroadcast(this) }
                }
            } else {
                updateWallpaper(context)
            }
        }
    }
}
