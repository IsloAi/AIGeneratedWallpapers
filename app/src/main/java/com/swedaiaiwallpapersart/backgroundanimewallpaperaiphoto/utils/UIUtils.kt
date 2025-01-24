package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import java.io.ByteArrayOutputStream

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

    /*fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }
    fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
    fun bitmapToDrawable(context: Context, bitmap: Bitmap): Drawable {
        return BitmapDrawable(context.resources, bitmap)
    }*/

    fun getAppIcon(packageName: String, context: Context): Drawable? {
        return try {
            context.packageManager.getApplicationIcon(packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }
}