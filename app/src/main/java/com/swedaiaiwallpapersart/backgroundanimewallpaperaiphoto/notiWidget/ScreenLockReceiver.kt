package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.notiWidget

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ScreenLockReceiver(
    private val onLock: () -> Unit,
    private val onUnlock: () -> Unit
) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val keyguard = context?.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        when (intent?.action) {
            Intent.ACTION_SCREEN_OFF -> {
                // If the screen is off, it's typically locked
                if (keyguard.isKeyguardLocked) {
                    onLock()
                }
            }
            Intent.ACTION_USER_PRESENT -> {
                // If the user is present, the device is unlocked
                if (!keyguard.isKeyguardLocked) {
                    onUnlock()
                }
            }
        }
    }
}

