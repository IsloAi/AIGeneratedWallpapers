package com.example.hdwallpaper.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.ViewPagerImageClick
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.SlideItemContainerBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.FullViewImage


class WallpaperApiSliderAdapter(
    private val arrayList: ArrayList<CatResponse>,
    private val viewPager2: ViewPager2,
    private val viewPagerImageClick: ViewPagerImageClick,
    private val fullViewImage: FullViewImage
    ) : RecyclerView.Adapter<WallpaperApiSliderAdapter.ViewHolder>() {
    var context :Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        val binding = SlideItemContainerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        context = parent.context
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(arrayList,position)
        Log.d("tracingImageId", "free: list ${arrayList}")
    }
    override fun getItemCount(): Int {
        return arrayList.size
    }
    inner class  ViewHolder(val binding:SlideItemContainerBinding ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(arrayList: ArrayList<CatResponse>, position: Int) {
            val model = arrayList[position]
            dataSet(model,binding.imageSlide,binding.progressBar,binding.gemsTextView,binding.blurView,adapterPosition)
        } }
    @SuppressLint("SuspiciousIndentation")
    private fun dataSet(model: CatResponse, imageSlide: AppCompatImageView, progressBar: LottieAnimationView,
                        gemsTextView: TextView, blurView: ConstraintLayout, adapterPosition: Int) {
        progressBar.visibility = VISIBLE
        progressBar.setAnimation(R.raw.main_loading_animation)
        gemsTextView.text = model.gems.toString()
        if(model.gems==0 || model.unlockimges==true){
          blurView.visibility = INVISIBLE
        }else{ blurView.visibility = VISIBLE }

        imageSlide.setOnClickListener {
            Log.d("modelTracingNow", "dataSet: model else condition  ${model.unlockimges}  imageId  ${model.id}")
            if(model.gems !=0 && model.unlockimges==false) {
                viewPagerImageClick.getImagePosition(adapterPosition, blurView)
            }else{
                fullViewImage.getFullImageUrl(model.hd_image_url!!)
            }
        }
        Glide.with(context!!).load(model.hd_image_url).diskCacheStrategy(DiskCacheStrategy.ALL)
            .listener(object:
                RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = VISIBLE
                    progressBar.setAnimation(R.raw.no_data_image_found)
                    return false
                }
                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?,
                                             dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                 progressBar.visibility = INVISIBLE
                    return false
                }
            }).into(imageSlide)

        if (adapterPosition == arrayList.size - 1) {
            viewPager2.post(runable)
        }

    }

    private val runable = Runnable {
        arrayList.addAll(arrayList)
        notifyDataSetChanged()
    }
}