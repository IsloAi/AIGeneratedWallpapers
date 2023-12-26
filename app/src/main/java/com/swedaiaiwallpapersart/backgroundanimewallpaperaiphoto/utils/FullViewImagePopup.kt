package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils

import android.R
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FullViewWallpaperBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse

class FullViewImagePopup {
    companion object{


         fun openFullViewWallpaper(context: Context,image: CatResponse) {
            val dialog = Dialog(context, R.style.Theme_Black_NoTitleBar_Fullscreen)
             val binding = FullViewWallpaperBinding.inflate(LayoutInflater.from(context))
             dialog.setContentView(binding.root)
            dialog.show()

             binding.fullViewImage.isEnabled = false
             Glide.with(context)
                 .load(image.hd_image_url)
                 .listener(object : RequestListener<Drawable> {
                     override fun onLoadFailed(
                         e: GlideException?,
                         model: Any?,
                         target: Target<Drawable>,
                         isFirstResource: Boolean
                     ): Boolean {
                         return false
                     }

                     override fun onResourceReady(
                         resource: Drawable,
                         model: Any,
                         target: Target<Drawable>?,
                         dataSource: DataSource,
                         isFirstResource: Boolean
                     ): Boolean {
                         binding.fullViewImage.isEnabled = true

                         return false
                     }
                 })
                 .into(binding.fullViewImage)
             binding.closeButton.setOnClickListener {
                dialog.dismiss()
            }
        }
    }
}