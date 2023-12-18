package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.SplashModel
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding
.SplashContainerBinding

class SplashSliderAdapter(
    private val arrayList: ArrayList<SplashModel>,
    private val viewPager2: ViewPager2
) : RecyclerView.Adapter<SplashSliderAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): ViewHolder {
        val binding = SplashContainerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.binding.imageSlide.context
        // Check if the activity is destroyed before starting Glide image loading

        if (!isActivityDestroyed(context)) {
            val hits = arrayList[position]
            Glide.with(context)
                .load(hits.image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
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
                        return false
                    }
                })
                .into(holder.binding.imageSlide)
        }else{
            Toast.makeText(context, "your are exit from app", Toast.LENGTH_SHORT).show()
        }

        if (position == arrayList.size - 1) {
            viewPager2.post(runable)
        }


    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ViewHolder(val binding:SplashContainerBinding) : RecyclerView.ViewHolder(binding.root)

    private val runable = Runnable {
        arrayList.addAll(arrayList)
        notifyDataSetChanged()
    }

//   fun onDestroy(){
//           Glide.with(this).clear(holder.binding.imageSlide)
//   }

    // Function to check if the activity is destroyed
    private fun isActivityDestroyed(context: Context?): Boolean {
        if (context is Activity) {
            return context.isDestroyed || context.isFinishing
        }
        return true
    }

}