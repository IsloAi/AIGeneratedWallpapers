package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils

import android.app.Activity
import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.os.Build
import android.os.TransactionTooLargeException
import android.util.DisplayMetrics
import java.io.IOException

class MyWallpaperManager(var context: Context, var activity: Activity) {

    fun homeScreen(bitmap:Bitmap) {
        try {
            var newBitmap = bitmap
            val wallpaperManager = WallpaperManager.getInstance(context)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
                val metrics = DisplayMetrics()
                activity.windowManager.defaultDisplay.getMetrics(metrics)
                val heigh = metrics.heightPixels
                val widt = metrics.widthPixels
                newBitmap = Bitmap.createScaledBitmap(newBitmap, widt, heigh, true)
            }
            else {
                val size = Point()
                activity.getWindowManager().getDefaultDisplay().getRealSize(size)
                val w: Int = size.x
                val h: Int = size.y
//            if(!newBitmap.isRecycled){
                newBitmap = Bitmap.createScaledBitmap(newBitmap, w, h, true)
//            }

            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                try {
                    wallpaperManager.setBitmap(newBitmap, null, true, WallpaperManager.FLAG_SYSTEM)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                val metrics = DisplayMetrics()
                activity.windowManager.defaultDisplay.getMetrics(metrics)
                val height = metrics.heightPixels
                val width = metrics.widthPixels
                newBitmap = Bitmap.createScaledBitmap(newBitmap, width, height, true)
                try {
                    wallpaperManager.setBitmap(newBitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }catch (e: TransactionTooLargeException){
            e.printStackTrace()
        }


    }
    fun lockScreen(bit: Bitmap) {
        var bitmap= bit
        val wallpaperManager = WallpaperManager.getInstance(context)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            val metrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(metrics)
            val heigh = metrics.heightPixels
            val widt = metrics.widthPixels
            bitmap = Bitmap.createScaledBitmap(bitmap, widt, heigh, true)
        } else {
            val size = Point()
            activity.getWindowManager().getDefaultDisplay().getRealSize(size)
            val w: Int = size.x
            val h: Int = size.y
            bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    fun homeAndLockScreen(bit:Bitmap) {
        var bitmap = bit
        val wallpaperManager = WallpaperManager.getInstance(context)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            val metrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(metrics)
            val heigh = metrics.heightPixels
            val widt = metrics.widthPixels
            bitmap = Bitmap.createScaledBitmap(bitmap, widt, heigh, true)
        } else {
            val size = Point()
            activity.getWindowManager().getDefaultDisplay().getRealSize(size)
            val w: Int = size.x
            val h: Int = size.y
            bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true)
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM)
                wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
            } else {
                var metrics = DisplayMetrics()
                metrics = DisplayMetrics()
                activity.windowManager.defaultDisplay.getMetrics(metrics)
                val height = metrics.heightPixels
                val width = metrics.widthPixels
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
                wallpaperManager.setBitmap(bitmap)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun doubleWallpaper(bitLock:Bitmap,bitHome:Bitmap) {
        var bitmapLock = bitLock
        var bitmapHome = bitHome
        val wallpaperManager = WallpaperManager.getInstance(context)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            val metrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(metrics)
            val heigh = metrics.heightPixels
            val widt = metrics.widthPixels
            bitmapLock = Bitmap.createScaledBitmap(bitmapLock, widt, heigh, true)
            bitmapHome = Bitmap.createScaledBitmap(bitHome, widt, heigh, true)
        } else {
            val size = Point()
            activity.getWindowManager().getDefaultDisplay().getRealSize(size)
            val w: Int = size.x
            val h: Int = size.y
            bitmapLock = Bitmap.createScaledBitmap(bitmapLock, w, h, true)
            bitmapHome = Bitmap.createScaledBitmap(bitmapHome, w, h, true)
        }
        try {
            wallpaperManager.setBitmap(bitmapHome, null, true, WallpaperManager.FLAG_SYSTEM)
            wallpaperManager.setBitmap(bitmapLock, null, true, WallpaperManager.FLAG_LOCK)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}