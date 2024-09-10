
package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.notiWidget  // Replace with your package name

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import android.os.Handler
import android.os.Looper
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import javax.inject.Inject

@ActivityScoped
class PermissionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val requestCodeDrawOverApps = 123
    private var permissionCheckHandler: Handler? = null

    fun checkDrawOverOtherAppsPermission(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context)
    }

    fun requestDrawOverOtherAppsPermission(activity: Activity, onPermissionGranted: () -> Unit) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(activity)
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_permission, null)
        builder.setView(view)

        val dialog: androidx.appcompat.app.AlertDialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog.setCancelable(false)

        view.findViewById<TextView>(R.id.allowButton)?.setOnClickListener {
            requestPermission(activity)
            dialog.dismiss()
            startPermissionCheck(activity, onPermissionGranted)
        }

        dialog.show()
    }

    private fun requestPermission(activity: Activity) {
        val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        myIntent.addCategory("android.intent.category.DEFAULT")
        myIntent.setData(Uri.parse(String.format("package:%s", activity.packageName)))
        activity.startActivityForResult(myIntent, requestCodeDrawOverApps)
    }

    private fun startPermissionCheck(activity: Activity, onPermissionGranted: () -> Unit) {
        permissionCheckHandler = Handler(Looper.getMainLooper())
        permissionCheckHandler?.post(object : Runnable {
            override fun run() {
                if (checkDrawOverOtherAppsPermission()) {
                    onPermissionGranted()
                    restartMainActivity(activity)
                    permissionCheckHandler?.removeCallbacks(this)
                } else {
                    permissionCheckHandler?.postDelayed(this, 1000)
                }
            }
        })
    }

    private fun restartMainActivity(activity: Activity) {
        val intent = activity.packageManager.getLaunchIntentForPackage(activity.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        intent?.putExtra("restart", true)
        activity.startActivity(intent)
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

}
