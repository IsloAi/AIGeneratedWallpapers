package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils

import android.R
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class FullViewImagePopup {
    companion object{
         fun openFullViewWallpaper(context: Context,image: String) {
            val dialog = Dialog(context, R.style.Theme_Black_NoTitleBar_Fullscreen)
            dialog.setContentView(com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.debug.R.layout.full_view_wallpaper)
            dialog.show()
            val imageView: ImageView = dialog.findViewById(com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.debug.R.id.fullViewImage)
            val closeButton: RelativeLayout = dialog.findViewById(com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.debug.R.id.closeButton)
             imageView.isEnabled = false
             Glide.with(context)
                 .load(image)
                 .listener(object : RequestListener<Drawable> {
                     override fun onLoadFailed(
                         e: GlideException?,
                         model: Any?,
                         target: Target<Drawable>?,
                         isFirstResource: Boolean
                     ): Boolean {
                         return false
                     }

                     override fun onResourceReady(
                         resource: Drawable?,
                         model: Any?,
                         target: Target<Drawable>?,
                         dataSource: DataSource?,
                         isFirstResource: Boolean
                     ): Boolean {
                         imageView.isEnabled = true
                         return false
                     }
                 })
                 .into(imageView)
            closeButton.setOnClickListener {
                dialog.dismiss()
            }
        }
    }
}