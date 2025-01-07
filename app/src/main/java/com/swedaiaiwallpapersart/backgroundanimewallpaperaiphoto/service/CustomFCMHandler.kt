package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.service

import android.content.Intent
import com.google.firebase.messaging.RemoteMessage
import com.ikame.android.sdk.core.fcm.BaseIkFirebaseMessagingService
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity

class CustomFCMHandler : BaseIkFirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

    }

    override fun handleIntentSdk(intent: Intent) {
        super.handleIntentSdk(intent)
    }

    override fun splashActivityClass(): Class<*>? {
        return MainActivity::class.java
    }


}