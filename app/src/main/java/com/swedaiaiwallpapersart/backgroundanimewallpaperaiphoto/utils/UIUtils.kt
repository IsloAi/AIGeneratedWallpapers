package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils

import android.view.View
import android.view.Window
import android.view.WindowInsetsController

object UIUtils {

    fun hideSystemUI(window: Window) {
        val decorView = window.decorView
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                val controller = window.insetsController
                controller?.hide(WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_SWIPE)
            } else {
                val uiOptions = (View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
                decorView.systemUiVisibility = uiOptions
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle the exception, e.g., show a Toast or log the error
        }
    }

    fun showSystemUI(window: Window) {
        val decorView = window.decorView
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                val controller = window.insetsController
                controller?.show(WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_TOUCH)
            } else {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle the exception, e.g., show a Toast or log the error
        }
    }
}