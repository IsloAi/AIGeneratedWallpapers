package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.NativeSliderLayoutBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.SlideItemContainerBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.FullViewImage
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.activity.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.AdConfig
import kotlinx.coroutines.CoroutineScope

class WallpaperApiSliderAdapter(
    private val arrayList: ArrayList<CatResponse?>,
    private val fullViewImage: FullViewImage,
    private val mActivity: MainActivity,
    private val from: String,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var context: Context? = null

    private val VIEW_TYPE_CONTAINER1 = 0
    private val VIEW_TYPE_NATIVE_AD = 1

    private val firstAdLineThreshold =
        if (AdConfig.firstAdLineTrending != 0) AdConfig.firstAdLineTrending else 4

    private val lineCount = if (AdConfig.lineCountTrending != 0) AdConfig.lineCountTrending else 5
    private val statusAd = AdConfig.adStatusTrending

    private var coroutineScope: CoroutineScope? = null

    fun setCoroutineScope(scope: CoroutineScope) {
        coroutineScope = scope
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        return when (viewType) {
            VIEW_TYPE_CONTAINER1 -> {
                val binding = SlideItemContainerBinding.inflate(inflater, parent, false)
                ViewHolderContainer1(binding)
            }

            VIEW_TYPE_NATIVE_AD -> {
                val binding = NativeSliderLayoutBinding.inflate(inflater, parent, false)
                ViewHolderContainer3(binding)
            }

            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (arrayList[position]) {
            null -> {
                VIEW_TYPE_NATIVE_AD
            }

            else -> {
                VIEW_TYPE_CONTAINER1
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = arrayList[position]
        when (holder.itemViewType) {
            VIEW_TYPE_CONTAINER1 -> {
                val viewHolderContainer1 = holder as ViewHolderContainer1
                try {
                    viewHolderContainer1.bind(arrayList, position)
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }

            VIEW_TYPE_NATIVE_AD -> {
                val viewHolder = holder as ViewHolderContainer3
                try {
                    viewHolder.bind(holder)
                } catch (_: NullPointerException) {

                }
            }
        }
        Log.d("tracingImageId", "free: list ${arrayList}")
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolderContainer1(val binding: SlideItemContainerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(arrayList: ArrayList<CatResponse?>, position: Int) {

            val model = arrayList[position]
            Log.d("WallpaperPreview", "bind:model= $model")
            dataSet(
                model!!,
                binding.imageSlide,
                binding.progressBar,
                binding.blurView,
                adapterPosition,
                binding.noDataIMG
            )
        }
    }

    inner class ViewHolderContainer3(private val binding: NativeSliderLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(holder: RecyclerView.ViewHolder) {
            if (AdConfig.globalBigNativeAdView != null) {
                // Detach globalNativeAdView from its previous parent if it has one
                AdConfig.globalBigNativeAdView?.parent?.let { parent ->
                    (parent as ViewGroup).removeView(AdConfig.globalBigNativeAdView)
                }
                binding.sliderNative.removeAllViews()
                binding.sliderNative.addView(AdConfig.globalBigNativeAdView)
            } else {
                // maybe show a placeholder or hide the view
                binding.sliderNative.visibility = View.GONE
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun dataSet(
        model: CatResponse, imageSlide: AppCompatImageView, progressBar: LottieAnimationView,
        blurView: ConstraintLayout, adapterPosition: Int, noData: ImageView
    ) {
        progressBar.visibility = VISIBLE
        progressBar.setAnimation(R.raw.main_loading_animation)
        if (model.unlockimges == true) {
            blurView.visibility = INVISIBLE
        } else {
            if (AdConfig.ISPAIDUSER) {
                blurView.visibility = INVISIBLE
            } else {
                blurView.visibility = VISIBLE
            }
        }

        imageSlide.setOnClickListener {
            Log.d(
                "modelTracingNow",
                "dataSet: model else condition  ${model.unlockimges}  imageId  ${model.id}"
            )
            fullViewImage.getFullImageUrl(model)
        }

        var url: String? = ""
        if (from == "Vip") {
            url = AdConfig.BASE_URL_DATA + "/rewardwallpaper/hd/" + model.hd_image_url
        } else {
            url = AdConfig.BASE_URL_DATA + "/staticwallpaper/hd/" + model.hd_image_url
        }

        Log.d("Wallpaper", "dataSet:url = $url ")
        Glide.with(context!!).load(url).diskCacheStrategy(DiskCacheStrategy.ALL)
            .listener(object :
                RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = View.GONE
                    noData.visibility = View.VISIBLE

                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = INVISIBLE
                    noData.visibility = View.GONE
                    return false
                }
            }).into(imageSlide)

    }

    interface posCallback {
        fun positionCallback(isNull: String)
    }
}