package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ikame.android.sdk.IKSdkController
import com.ikame.android.sdk.widgets.IkmWidgetAdLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.ikame.android.sdk.data.dto.pub.IKAdError
import com.ikame.android.sdk.listener.pub.IKLoadDisplayAdViewListener
import com.ikame.android.sdk.listener.pub.IKShowWidgetAdListener
import com.ikame.android.sdk.widgets.IkmDisplayWidgetAdView
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.CatNameListBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.StaggeredNativeLayoutBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.StringCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatNameResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.ForegroundWorker.Companion.TAG

class ApiCategoriesNameAdapter(
    private val arrayList: ArrayList<CatNameResponse?>,
    private val stringCallback: StringCallback,
    private val myActivity: MainActivity,
    private val from: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_CONTAINER1 = 0
    private val VIEW_TYPE_NATIVE_AD = 1
    private val debounceThreshold = 2000L // 2 seconds

    private val firstAdLineThreshold = if (AdConfig.firstAdLineCategoryArt != 0) AdConfig.firstAdLineCategoryArt else 4
    private val firstLine = firstAdLineThreshold * 3

    private val lineCount = if (AdConfig.lineCountCategoryArt != 0) AdConfig.lineCountCategoryArt else 5
    private val lineC = lineCount * 3
    private val statusAd = AdConfig.adStatusCategoryArt

    private var lastClickTime = 0L
    private var context: Context? = null

    var nativeAdView: IkmDisplayWidgetAdView? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val layoutManager = recyclerView.layoutManager as GridLayoutManager
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (getItemViewType(position) == VIEW_TYPE_NATIVE_AD) {
                    layoutManager.spanCount // Make the ad span the full width
                } else {
                    1 // Regular item occupies 1 span
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_CONTAINER1 -> ViewHolderContainerItem(CatNameListBinding.inflate(inflater, parent, false))
            VIEW_TYPE_NATIVE_AD -> ViewHolderContainer3(StaggeredNativeLayoutBinding.inflate(inflater, parent, false))
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val adjustedPosition = calculateAdjustedPosition(position)

        when (holder.itemViewType) {
            VIEW_TYPE_CONTAINER1 -> {
                val model = arrayList[adjustedPosition]
                if (model != null) {
                    (holder as ViewHolderContainerItem).bind(model)
                } else {
                    // Handle null model case (e.g., show a placeholder or do nothing)
                    Log.e("ApiCategoriesNameAdapter", "Null model at position: $adjustedPosition")
                }
            }
            VIEW_TYPE_NATIVE_AD -> {
                (holder as ViewHolderContainer3).bind()
            }
        }
    }

    override fun getItemCount(): Int {
        if (arrayList.isNotEmpty()) {
            return if (AdConfig.ISPAIDUSER) {
                arrayList.size
            } else {
                // Calculate total items including ads
                val adsCount = ((arrayList.size - firstLine - 1) / lineC) + 1
                arrayList.size + adsCount
            }
        }
        return 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (AdConfig.ISPAIDUSER) {
            VIEW_TYPE_CONTAINER1
        } else {
            val actualPosition = position + 1
            if (isAdPosition(actualPosition)) VIEW_TYPE_NATIVE_AD else VIEW_TYPE_CONTAINER1
        }
    }

    private fun isAdPosition(actualPosition: Int): Boolean {
        return actualPosition == firstLine + 1 || (actualPosition > firstLine + 1 && (actualPosition - (firstLine + 1)) % (lineC + 1) == 0)
    }

    private fun calculateAdjustedPosition(position: Int): Int {
        return if (!AdConfig.ISPAIDUSER && getItemViewType(position) == VIEW_TYPE_NATIVE_AD) {
            position - ((position - firstLine) / (lineC + 1) + 1)
        } else if (!AdConfig.ISPAIDUSER) {
            position - (position / (lineC + 1))
        } else {
            position
        }
    }

    inner class ViewHolderContainerItem(private val binding: CatNameListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: CatNameResponse) {
            binding.loading.visibility = View.VISIBLE
            binding.loading.setAnimation(R.raw.main_loading_animation)
            binding.catName.text = model.cat_name

            Glide.with(context!!)
                .load(model.img_url)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d("onLoadFailed", "onLoadFailed: " + e?.message)
                        binding.loading.setAnimation(R.raw.no_data_image_found)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.loading.visibility = View.INVISIBLE
                        return false
                    }
                })
                .into(binding.catIconImage)

            binding.live.visibility = if (from == "live") View.VISIBLE else View.GONE

            binding.catIconImage.setOnClickListener {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTime >= debounceThreshold) {
                    stringCallback.getStringCall(model.cat_name!!)
                    lastClickTime = currentTime
                }
            }
        }
    }

    inner class ViewHolderContainer3(private val binding: StaggeredNativeLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            if (!binding.adsView.isAdLoaded) {
                loadAd(binding)
            }
        }
    }

    private fun loadAd(binding: StaggeredNativeLayoutBinding) {
        val adLayout = LayoutInflater.from(context).inflate(
            R.layout.native_dialog_layout,
            null, false
        ) as? IkmWidgetAdLayout
        adLayout?.apply {
            titleView = findViewById(R.id.custom_headline)
            bodyView = findViewById(R.id.custom_body)
            callToActionView = findViewById(R.id.custom_call_to_action)
            iconView = findViewById(R.id.custom_app_icon)
            mediaView = findViewById(R.id.custom_media)
        }

        IKSdkController.loadNativeDisplayAd("mainscr_cate_tab_scroll_view", object :
            IKLoadDisplayAdViewListener {
            override fun onAdLoaded(adObject: IkmDisplayWidgetAdView?) {
                nativeAdView = adObject
                nativeAdView?.let {
                    binding.adsView.showWithDisplayAdView(
                        R.layout.shimmer_loading_native,
                        adLayout!!,
                        "mainscr_cate_tab_scroll_view",
                        it,
                        object : IKShowWidgetAdListener {
                            override fun onAdShowFail(error: IKAdError) {
                                Log.e("TAG", "onAdsLoadFail: native failed ")
                                binding.adsView.visibility = if (statusAd == 0) View.GONE else View.VISIBLE
                            }

                            override fun onAdShowed() {
                                binding.adsView.visibility = View.VISIBLE
                                Log.e("TAG", "onAdsLoaded: native loaded")
                            }
                        }
                    )
                }
            }

            override fun onAdLoadFail(error: IKAdError) {
                Log.e("LIVE_WALL_SCREEN_ADAPTER", "onAdFailedToLoad: $error")
                if (statusAd == 0) binding.adsView.visibility = View.GONE
            }
        })
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = myActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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

    fun updateData(newData:List<CatNameResponse?>){
        arrayList.clear()
        arrayList.addAll(newData)
        Log.d(TAG, "updateData1223: ${newData.size}")
        notifyDataSetChanged()

    }
}