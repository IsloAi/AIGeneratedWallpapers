package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import com.airbnb.lottie.LottieAnimationView
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.debug.R
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.GemsTextUpdate
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import retrofit2.Retrofit

class MyDialogs {
    @SuppressLint("SetTextI18n")
    fun exitPopup(context: Context, activity: Activity, myActivity: MainActivity,) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.exit)
        val width = WindowManager.LayoutParams.MATCH_PARENT
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.setLayout(width, height)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        val title = dialog.findViewById<TextView>(R.id.texttop)
        val btnNo = dialog.findViewById<Button>(R.id.btnNo)
        val btnYes = dialog.findViewById<Button>(R.id.btnYes)
        title!!.text = context.getString(R.string.are_you_sure_you_want_to_exit)
        btnYes!!.setOnClickListener {
            activity.finish()
            dialog.dismiss()
        }
        btnNo!!.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
    fun getWallpaperPopup(
        context: Context,
        model: CatResponse,
        navController: NavController,
        actionId: Int,
        gemsTextUpdate: GemsTextUpdate,
        lockButton: ImageView,
        diamondIcon: ImageView,
        gemsView:TextView,
        myViewModel: MyViewModel?,
    ) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.perchase_gems)
        val width = WindowManager.LayoutParams.MATCH_PARENT
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.setLayout(width, height)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
        dialog.setCancelable(false)
        val title = dialog.findViewById<TextView>(R.id.unLockTextView)
        val totalGemsTextView = dialog.findViewById<TextView>(R.id.totalGems)
        val getMoreGems = dialog.findViewById<TextView>(R.id.getMoreGems)
        val buttonGetWallpaper = dialog.findViewById<ConstraintLayout>(R.id.buttonGetWallpaper)
        val totalGems = MySharePreference.getGemsValue(context)
        title!!.text = "To Unlock this wallpaper\nUse ${model.gems} Gems"
        var gems = model.gems!!
        if (totalGems != null) {
            if(totalGems<gems){
               buttonGetWallpaper.alpha = 0.3f
                title.text = "To Unlock this wallpaper you need ${model.gems} Gems, but you have only ${totalGems} gems,first get more gems"
                buttonGetWallpaper.isClickable = false
            }
        }
        buttonGetWallpaper!!.setOnClickListener {
            if (totalGems != null) {
                if(totalGems>=gems){
                  val leftGems = totalGems-gems
                    val postData = PostDataOnServer()
                    postData.unLocking(MySharePreference.getDeviceID(context)!!,model,context,leftGems,gemsTextUpdate,dialog,lockButton,diamondIcon,gemsView)
                }
            }
        }
        getMoreGems.setOnClickListener {
            navController.navigate(actionId)
            myViewModel?.clear()
            dialog.dismiss()
        }
        dialog.findViewById<ImageView>(R.id.closePopup).setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    fun getWallpaperPopup(
        context: Context,
        model: CatResponse,
        navController: NavController,
        actionId: Int,
        gemsTextUpdate: GemsTextUpdate,
        lockButton: ImageView,
        diamondIcon: ImageView,
        gemsView:TextView
    ) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.perchase_gems)
        val width = WindowManager.LayoutParams.MATCH_PARENT
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.setLayout(width, height)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
        dialog.setCancelable(false)
        val title = dialog.findViewById<TextView>(R.id.unLockTextView)
        val totalGemsTextView = dialog.findViewById<TextView>(R.id.totalGems)
        val getMoreGems = dialog.findViewById<TextView>(R.id.getMoreGems)
        val buttonGetWallpaper = dialog.findViewById<ConstraintLayout>(R.id.buttonGetWallpaper)
        val totalGems = MySharePreference.getGemsValue(context)
        title!!.text = "To Unlock this wallpaper\nUse ${model.gems} Gems"
        var gems = model.gems!!
        if (totalGems != null) {
            if(totalGems<gems){
                buttonGetWallpaper.alpha = 0.3f
                title.text = "To Unlock this wallpaper you need ${model.gems} Gems, but you have only ${totalGems} gems,first get more gems"
                buttonGetWallpaper.isClickable = false
            }
        }
        buttonGetWallpaper!!.setOnClickListener {
            if (totalGems != null) {
                if(totalGems>=gems){
                    val leftGems = totalGems-gems
                    val postData = PostDataOnServer()
                    postData.unLocking(MySharePreference.getDeviceID(context)!!,model,context,leftGems,gemsTextUpdate,dialog,lockButton,diamondIcon,gemsView)
                }
            }
        }
        getMoreGems.setOnClickListener {
            navController.navigate(actionId)
            dialog.dismiss()
        }
        dialog.findViewById<ImageView>(R.id.closePopup).setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    fun getWallpaperPopup(
        context: Context,
        model: CatResponse,
        navController: NavController,
        id: Int,
        instance: Retrofit,
        totalGemsView: TextView,
        layout:ConstraintLayout
    ) {
        val postData = PostDataOnServer()
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.perchase_gems)
        val width = WindowManager.LayoutParams.MATCH_PARENT
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.setLayout(width, height)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
        dialog.setCancelable(false)
        val title = dialog.findViewById<TextView>(R.id.unLockTextView)
        val totalGemsTextView = dialog.findViewById<TextView>(R.id.totalGems)
        val getMoreGems = dialog.findViewById<TextView>(R.id.getMoreGems)
        val buttonGetWallpaper = dialog.findViewById<ConstraintLayout>(R.id.buttonGetWallpaper)
        val totalGems = MySharePreference.getGemsValue(context)
        title!!.text = "To Unlock this wallpaper\nUse ${model.gems} Gems"
        totalGemsTextView.text ="You have total ${totalGems} Gems"

        if (totalGems != null) {
            if(totalGems < model.gems!!){
                buttonGetWallpaper.alpha = 0.3f
                title.text = "To Unlock this wallpaper you need ${model.gems} Gems, but you have only ${totalGems} gems,first get more gems"
                buttonGetWallpaper.isClickable = false
            }
        }

        buttonGetWallpaper!!.setOnClickListener {
            if (totalGems != null) {
                val gems = model.gems!!
                if(totalGems>=gems){
                    val leftGems = totalGems-gems
                    postData.unLocking(MySharePreference.getDeviceID(context)!!,model,context,leftGems,totalGemsView,dialog,layout)
                    //postData.gemsPostData(context,MySharePreference.getUserID(context)!!,instance,leftGems,PostDataOnServer.isPlan,layout,model)
//                    totalGemsView.text = leftGems.toString()
                }
            } }
        getMoreGems.setOnClickListener {
            navController.navigate(id)
            dialog.dismiss()
        }
        dialog.findViewById<ImageView>(R.id.closePopup).setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    fun waiting(dialog: Dialog) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.loading_ad)
        val width = WindowManager.LayoutParams.WRAP_CONTENT
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.setLayout(width, height)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.findViewById<TextView>(R.id.title).text = "Please wait..."
        dialog.show()
    }

}