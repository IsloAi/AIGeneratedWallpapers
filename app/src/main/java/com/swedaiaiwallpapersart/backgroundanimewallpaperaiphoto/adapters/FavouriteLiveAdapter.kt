package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.ListItemLiveWallpaperBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.FavouriteDownloadCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.FavouriteLiveModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig

//Created on 5-11-24 by U5M4N-K071N
/*It's the fear of the bug unseen 
that makes coding harder to begin.*/

class FavouriteLiveAdapter(
    val list: List<FavouriteLiveModel>,
    private val myActivity: MainActivity,
    var positionCallback: FavouriteDownloadCallback
) : RecyclerView.Adapter<FavouriteLiveAdapter.ViewHolder>() {

    private lateinit var context: Context
    private var lastClickTime = 0L
    private val debounceThreshold = 2000L // 1 second

    inner class ViewHolder(val binding: ListItemLiveWallpaperBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FavouriteLiveModel) {
            // Bind the item data to your views here
            setAllData(
                item,
                adapterPosition,
                binding.loading,
                binding.wallpaper,
                binding.errorImage,
                binding.iap
            )
            // Set any additional properties or click listeners as needed
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setAllData(
        model: FavouriteLiveModel,
        position: Int,
        animationView: LottieAnimationView,
        wallpaperMainImage: ImageView,
        errorImg: ImageView,
        iap: ImageView
    ) {
        animationView.visibility = View.VISIBLE
        animationView.setAnimation(R.raw.loading_upload_image)

        iap.visibility = if (!model.unlocked && !AdConfig.ISPAIDUSER) View.VISIBLE else View.GONE

        Glide.with(context)
            .load("${AdConfig.BASE_URL_DATA}/livewallpaper/${model.thumnail_url}")
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .thumbnail(0.1f)
            .listener(object : RequestListener<Drawable> {

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    animationView.setAnimation(R.raw.no_data_image_found)
                    animationView.visibility = View.VISIBLE
                    errorImg.visibility = View.VISIBLE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    animationView.visibility = View.INVISIBLE
                    errorImg.visibility = View.GONE
                    return false
                }
            }).into(wallpaperMainImage)


        wallpaperMainImage.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime >= debounceThreshold) {
                positionCallback.getPosition(position, model)
                lastClickTime = currentTime
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ListItemLiveWallpaperBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        Log.d("LIVEADAPTER", "getItemCount:${list.size} ")
        return list.size
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
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