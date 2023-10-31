package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils

import android.R
import android.app.Dialog
import android.content.Context
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
class FullViewImagePopup {
    companion object{
         fun openFullViewWallpaper(context: Context,image: String) {
            val dialog = Dialog(context, R.style.Theme_Black_NoTitleBar_Fullscreen)
            dialog.setContentView(com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R.layout.full_view_wallpaper)
            dialog.show()
            val imageView: ImageView = dialog.findViewById(com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R.id.fullViewImage)
            val closeButton: RelativeLayout = dialog.findViewById(com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R.id.closeButton)
            Glide.with(context).load(image).into(imageView)
            closeButton.setOnClickListener {
                dialog.dismiss()
            }
        }
    }
}