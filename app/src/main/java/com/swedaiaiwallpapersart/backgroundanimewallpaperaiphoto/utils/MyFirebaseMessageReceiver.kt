package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.ikame.android.sdk.core.fcm.BaseIkFirebaseMessagingService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.webHookGenericResponse

class MyFirebaseMessageReceiver : BaseIkFirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Log.d("tracingToken", "Refreshed token: $token")
        MySharePreference.setFireBaseToken(applicationContext,token)
    }

    override fun splashActivityClass(): Class<*>? {
        return MainActivity::class.java
//okay, yeah it's the activity which holds all the fragments. yes.
    // i want activity can start can show first open ad. it ok. right?Yeah that's right
    // oke, well done. tthanks you
    }
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.e("tracingToken", "onMessageReceived: $remoteMessage")
        val data = remoteMessage.data
        val appDatabase = AppDatabase.getInstance(applicationContext)
        Log.e("*******Message", "onMessageReceived: "+data )

        val type = data["type"]
        if (type?.lowercase() == "a") {
            val body = data["body"]
            val webHookGenericResponse = Gson().fromJson(body, webHookGenericResponse::class.java)
            Log.e("TAG", "onMessageReceived: $webHookGenericResponse")
            val oldData = appDatabase.getResponseIGDao()?.getCreationsByIdNotLive(webHookGenericResponse.id)
            val mutableLIst: ArrayList<String> = arrayListOf()
            mutableLIst.addAll(webHookGenericResponse.output)
            oldData?.output = mutableLIst
            if (oldData!=null){
                appDatabase.getResponseIGDao()?.UpdateData(oldData)
            }
            Log.e("TAG", "onMessageReceived: $body")
        }
        if (remoteMessage.notification != null) {
            showNotification(
                remoteMessage.notification?.title,
                remoteMessage.notification?.body
            )
        }
    }
    @SuppressLint("RemoteViewLayout")
    private fun getCustomDesign(title: String?, message: String?): RemoteViews {
        val remoteViews = RemoteViews(applicationContext.packageName, R.layout.notification)
        remoteViews.setTextViewText(R.id.title, title)
        remoteViews.setTextViewText(R.id.message, message)
        remoteViews.setImageViewResource(R.id.icon, R.drawable.app_icon)
        return remoteViews
    }
    private fun showNotification(title: String?, message: String?) {
        val intent = Intent(this, MainActivity::class.java)
        val channel_id = "notification_channel"
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder: NotificationCompat.Builder

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channel_id, "web_app", NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)

            builder = NotificationCompat.Builder(this, channel_id)
        } else {
            builder = NotificationCompat.Builder(this)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            builder.setSmallIcon(R.drawable.app_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)
                .setContent(getCustomDesign(title, message))
        }else{
            builder.setSmallIcon(R.drawable.app_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(message)
        }
        val notificationManagerCompat = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManagerCompat.notify(0, builder.build())
    }
}


