package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.StaggeredNativeLayoutBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.WallpaperRowBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.NativeAdManager
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.PositionCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import kotlinx.coroutines.CoroutineScope

class ApiCategoriesListAdapter(
    var arrayList: ArrayList<CatResponse?>,
    var positionCallback: PositionCallback,
    private val myActivity: MainActivity,
    private val from: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var lastClickTime = 0L
    private val debounceThreshold = 2000L // 1 second
    private val context: Context? get() = myActivity.applicationContext
    private val VIEW_TYPE_CONTAINER1 = 0
    private val VIEW_TYPE_NATIVE_AD = 1

    var row = 0

    private val tracking = when (from) {
        "trending" -> "mainscr_trending_tab_scroll_view"
        "category" -> "categoryscr_scroll_view"
        "search" -> "searchscr_scroll_view"
        "Vip" -> "rewardscr"
        else -> "mainscr_sub_cate_tab_click_item"
    }
    private val firstAdLineThreshold = AdConfig.firstAdLineViewListWallSRC.takeIf { it != 0 } ?: 4
    private val firstline = firstAdLineThreshold * 3
    private val lineCount = AdConfig.lineCountViewListWallSRC.takeIf { it != 0 } ?: 5
    private val lineC = lineCount * 3
    private val statusAd = AdConfig.adStatusViewListWallSRC

    private var coroutineScope: CoroutineScope? = null

    fun setCoroutineScope(scope: CoroutineScope) {
        coroutineScope = scope
    }

    inner class ViewHolderContainer1(private val binding: WallpaperRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: CatResponse) {
            setAllData(adapterPosition, model, binding)
        }
    }

    inner class ViewHolderContainer3(private val binding: StaggeredNativeLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            val nativeAdView = NativeAdManager(
                context!!,
                AdConfig.admobAndroidNative,
                R.layout.native_layout_small
            )
            nativeAdView.loadNativeAd(binding.NativeAd)
        }
    }

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

    override fun getItemCount() = arrayList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_CONTAINER1 -> {
                val binding = WallpaperRowBinding.inflate(inflater, parent, false)
                ViewHolderContainer1(binding)
            }

            VIEW_TYPE_NATIVE_AD -> {
                val binding = StaggeredNativeLayoutBinding.inflate(inflater, parent, false)
                ViewHolderContainer3(binding)
            }

            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            if (position >= arrayList.size) return
            when (holder.itemViewType) {
                VIEW_TYPE_CONTAINER1 -> {
                    val viewHolderContainer1 = holder as ViewHolderContainer1
                    arrayList[position]?.let { viewHolderContainer1.bind(it) }
                }

                VIEW_TYPE_NATIVE_AD -> {
                    val viewHolderContainer3 = holder as ViewHolderContainer3
                    viewHolderContainer3.bind()
                }
            }
        } catch (e: Exception) {
            Log.d("usmanTAG", "onBindViewHolder: error ${e.localizedMessage}")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (arrayList[position] == null) {
            VIEW_TYPE_NATIVE_AD
        } else {
            VIEW_TYPE_CONTAINER1
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setAllData(
        adapterPosition: Int, model: CatResponse, binding: WallpaperRowBinding
    ) {
        val animationView = binding.loading
        val wallpaperMainImage = binding.wallpaper
        val errorImg = binding.errorImage
        val iapItem = binding.iapInd

        animationView.visibility = VISIBLE
        animationView.setAnimation(R.raw.loading_upload_image)

        if (model.unlockimges == false) {
            if (AdConfig.ISPAIDUSER) {
                iapItem.visibility = View.GONE
            } else {
                iapItem.visibility = View.VISIBLE
            }
        } else {
            iapItem.visibility = View.GONE
        }

        val url = if (from == "Vip") {
            AdConfig.BASE_URL_DATA + "/rewardwallpaper/hd/" + model.hd_image_url + "?class=custom"
        } else {
            AdConfig.BASE_URL_DATA + "/staticwallpaper/hd/" + model.hd_image_url + "?class=custom"
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

    /*fun loadad(binding: StaggeredNativeLayoutBinding) {
        coroutineScope?.launch(Dispatchers.Main) {
            adLayout?.titleView = adLayout?.findViewById(R.id.custom_headline)
            adLayout?.bodyView = adLayout?.findViewById(R.id.custom_body)
            adLayout?.callToActionView = adLayout?.findViewById(R.id.custom_call_to_action)
            adLayout?.iconView = adLayout?.findViewById(R.id.custom_app_icon)
            adLayout?.mediaView = adLayout?.findViewById(R.id.custom_media)

            if (nativeAdView == null) {
                withContext(Dispatchers.IO) {  // Load ad on a background thread
                    IKSdkController.loadNativeDisplayAd(
                        tracking,
                        object : IKLoadDisplayAdViewListener {
                            override fun onAdLoaded(adObject: IkmDisplayWidgetAdView?) {
                                nativeAdView = adObject
                            }

                            override fun onAdLoadFail(error: IKAdError) {
                                Log.e("LIVE_WALL_SCREEN_ADAPTER", "onAdFailedToLoad: $error")
                            }
                        })
                }
            }

            nativeAdView?.let {
                binding.adsView.showWithDisplayAdView(R.layout.shimmer_loading_native,
                    adLayout!!,
                    tracking,
                    it,
                    object : IKShowWidgetAdListener {
                        override fun onAdShowFail(error: IKAdError) {
                            Log.e("TAG", "onAdsLoadFail: native failed")
                            binding.adsView.visibility =
                                if (statusAd == 0 || !isNetworkAvailable()) {
                                    View.GONE
                                } else {
                                    View.VISIBLE
                                }
                        }

                        override fun onAdShowed() {
                            binding.adsView.visibility = View.VISIBLE
                            Log.e("TAG", "onAdsLoaded: native loaded")
                        }
                    })
            }
        }

    }*/

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            myActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(
                NetworkCapabilities.TRANSPORT_CELLULAR
            ))
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo != null && networkInfo.isConnected
        }
    }

    fun getFirstImageUrl(): String {
        return arrayList[0]?.hd_image_url!!
    }

    fun updateMoreData(list: ArrayList<CatResponse?>) {
        val startPosition = arrayList.size
        val currentItems = arrayList.toHashSet()
        val newItems = ArrayList<CatResponse?>()

        for (item in list) {
            if (!currentItems.contains(item)) {
                newItems.add(item)
                currentItems.add(item) // Track new items to avoid duplicates within the new list
            }
        }

        if (newItems.isNotEmpty()) {
            try {
                arrayList.addAll(newItems.distinct())
                notifyItemRangeInserted(startPosition, newItems.size)
            } catch (e: IndexOutOfBoundsException) {
                e.printStackTrace()
            }
        } else {
            Log.e("********new Data", "updateMoreData: no new items to add")
        }
    }

    fun updateData(list: ArrayList<CatResponse?>) {
        val diffCallback = CatResponseDiffCallback(arrayList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        arrayList.clear()
        arrayList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

    class CatResponseDiffCallback(
        private val oldList: List<CatResponse?>, private val newList: List<CatResponse?>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition]?.id == newList[newItemPosition]?.id  // Adjust comparison based on your data

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition] == newList[newItemPosition]
    }

    fun getAllItems(): ArrayList<CatResponse?> {
        return arrayList
    }

    fun addNewData() {
        try {
            arrayList.clear()
            notifyDataSetChanged()
        } catch (e: IndexOutOfBoundsException) {
            Log.d("usmanTAG", "addNewData: error ${e.localizedMessage} ")
        }
    }
}
