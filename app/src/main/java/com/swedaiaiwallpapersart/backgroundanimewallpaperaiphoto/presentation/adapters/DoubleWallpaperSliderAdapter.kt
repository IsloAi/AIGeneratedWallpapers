package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.ListItemDoubleSliderBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.NativeSliderLayoutBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.DoubleWallModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.activity.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.AdConfig
import kotlinx.coroutines.CoroutineScope

class DoubleWallpaperSliderAdapter(
    private val arrayList: ArrayList<DoubleWallModel?>,
    private val mActivity: MainActivity
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
                val binding = ListItemDoubleSliderBinding.inflate(inflater, parent, false)
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
        when (arrayList[position]) {
            null -> return VIEW_TYPE_NATIVE_AD
            else -> return VIEW_TYPE_CONTAINER1
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
                val viewHolderContainer3 = holder as ViewHolderContainer3
                viewHolderContainer3.bind(viewHolderContainer3)
            }
        }
        Log.d("tracingImageId", "free: list ${arrayList}")
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolderContainer1(val binding: ListItemDoubleSliderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(arrayList: ArrayList<DoubleWallModel?>, position: Int) {


            val model = arrayList[position]
            dataSet(
                model!!,
                binding.imageSlideLock,
                binding.imageSlideHome,
                binding.progressLock,
                binding.progressHome
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
        model: DoubleWallModel,
        imageSlide: AppCompatImageView,
        imageSlidehome: AppCompatImageView,
        progressLock: ProgressBar,
        progressHome: ProgressBar
    ) {

        progressLock.visibility = View.VISIBLE
        progressHome.visibility = View.VISIBLE

        Glide.with(context!!)
            .load(AdConfig.BASE_URL_DATA + "/doublewallpaper/" + model.compress_url1)
            .into(imageSlide)
        Glide.with(context!!)
            .load(AdConfig.BASE_URL_DATA + "/doublewallpaper/" + model.compress_url2)
            .into(imageSlidehome)
        Glide.with(context!!).load(AdConfig.BASE_URL_DATA + "/doublewallpaper/" + model.hd_url1)
            .diskCacheStrategy(
                DiskCacheStrategy.ALL
            )
            .listener(object :
                RequestListener<Drawable> {
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
                    progressLock.visibility = View.GONE
                    return false
                }
            }).into(imageSlide)

        Glide.with(context!!).load(AdConfig.BASE_URL_DATA + "/doublewallpaper/" + model.hd_url2)
            .diskCacheStrategy(
                DiskCacheStrategy.ALL
            )
            .listener(object :
                RequestListener<Drawable> {
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
                    progressHome.visibility = View.GONE
                    return false
                }
            }).into(imageSlidehome)

    }

    private val runable = Runnable {
        arrayList.addAll(arrayList)
        notifyDataSetChanged()
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            mActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo != null && networkInfo.isConnected
        }
    }

}