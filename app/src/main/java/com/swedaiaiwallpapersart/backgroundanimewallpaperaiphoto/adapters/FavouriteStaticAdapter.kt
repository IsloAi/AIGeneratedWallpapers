package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.WallpaperRowBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.PositionCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig

//Created on 4-11-24 by U5M4N-K071N
/*It's the fear of the bug unseen 
that makes coding harder to begin.*/

class FavouriteStaticAdapter(
    val list: List<CatResponse>,
    private val myActivity: MainActivity,
    private val from: String, var positionCallback: PositionCallback
) : RecyclerView.Adapter<FavouriteStaticAdapter.ViewHolder>() {


    private val context: Context? get() = myActivity.applicationContext
    private var lastClickTime = 0L
    private val debounceThreshold = 2000L // 1 second

    inner class ViewHolder(val binding: WallpaperRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CatResponse)
        {
            val layoutParams = binding.wallpaper.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(0, 0, 0, 0)
            binding.wallpaper.layoutParams = layoutParams
            // Bind the item data to your views here
            setAllData(adapterPosition, item, binding)
            // Set any additional properties or click listeners as needed
        }
    }

    private fun setAllData(adapterPosition: Int, item: CatResponse, binding: WallpaperRowBinding) {
        val animationView = binding.loading
        val wallpaperMainImage = binding.wallpaper
        val errorImg = binding.errorImage
        val iapItem = binding.iapInd

        animationView.visibility = VISIBLE
        animationView.setAnimation(R.raw.loading_upload_image)

        if (item.unlockimges == false) {
            if (AdConfig.ISPAIDUSER) {
                iapItem.visibility = View.GONE
            } else {
                iapItem.visibility = VISIBLE
            }
        } else {
            iapItem.visibility = View.GONE
        }

        val url = if (from == "Vip") {
            AdConfig.BASE_URL_DATA + "/rewardwallpaper/hd/" + item.hd_image_url + "?class=custom"
        } else {
            AdConfig.BASE_URL_DATA + "/staticwallpaper/hd/" + item.hd_image_url + "?class=custom"
        }
        Glide.with(context!!).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).thumbnail(0.1f)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d("onLoadFailed", "Failed to load: ${e?.message}")
                    animationView.setAnimation(R.raw.no_data_image_found)
                    animationView.visibility = VISIBLE
                    errorImg.visibility = VISIBLE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    animationView.visibility = INVISIBLE
                    errorImg.visibility = View.GONE
                    Log.d("onLoadFailed", "onResourceReady: ")
                    return false
                }
            }).into(wallpaperMainImage)

        wallpaperMainImage.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime >= debounceThreshold) {
                positionCallback.getPosition(adapterPosition)
                lastClickTime = currentTime
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            WallpaperRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])

    }

    override fun getItemCount(): Int {
        Log.d("FAVORITES", "getItemCount:${list.size} ")
        return list.size
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        // Safely cast to GridLayoutManager if available
        val layoutManager = recyclerView.layoutManager as? GridLayoutManager
        if (layoutManager != null) {
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    // Customize span size if needed based on item type or position
                    return 1 // or set based on your requirements
                }
            }
        } else {
            Log.e("ADAPTER", "RecyclerView is not using GridLayoutManager.")
        }
    }
}