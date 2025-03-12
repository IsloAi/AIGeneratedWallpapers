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
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.StaggeredNativeLayoutBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.downloadCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.LiveWallpaperModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import kotlinx.coroutines.CoroutineScope

class LiveWallpaperAdapter(
    private var arrayList: ArrayList<LiveWallpaperModel?>,
    private val positionCallback: downloadCallback,
    private val myActivity: MainActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private var lastClickTime = 0L
    private val debounceThreshold = 2000L // 2 seconds
    private val VIEW_TYPE_CONTAINER1 = 0
    private val VIEW_TYPE_NATIVE_AD = 1

    private val firstAdLineThreshold =
        if (AdConfig.firstAdLineViewListWallSRC != 0) AdConfig.firstAdLineViewListWallSRC else 4
    private val firstline = firstAdLineThreshold * 3
    private val lineCount =
        if (AdConfig.lineCountViewListWallSRC != 0) AdConfig.lineCountViewListWallSRC else 5
    private val lineC = lineCount * 3
    private val statusAd = AdConfig.adStatusViewListWallSRC
    private var coroutineScope: CoroutineScope? = null

    fun setCoroutineScope(scope: CoroutineScope) {
        coroutineScope = scope
    }

    inner class ViewHolderContainer1(private val binding: ListItemLiveWallpaperBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: LiveWallpaperModel) {
            setAllData(
                model,
                adapterPosition,
                binding.loading,
                binding.wallpaper,
                binding.errorImage,
                binding.iap
            )
        }
    }

    inner class ViewHolderContainer3(private val binding: StaggeredNativeLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {}
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
        val layoutManager = recyclerView.layoutManager as GridLayoutManager
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (getItemViewType(position) == VIEW_TYPE_NATIVE_AD) {
                    layoutManager.spanCount
                } else {
                    1
                }
            }
        }
    }

    override fun getItemCount(): Int {
        Log.d("LiveWallpaper", "getItemCount: ${arrayList.size} ")
        return arrayList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_CONTAINER1 -> {
                val binding = ListItemLiveWallpaperBinding.inflate(inflater, parent, false)
                ViewHolderContainer1(binding)
            }

            VIEW_TYPE_NATIVE_AD -> {
                val binding = StaggeredNativeLayoutBinding.inflate(inflater, parent, false)
                ViewHolderContainer3(binding)
            }

            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = arrayList[position]
        when (holder.itemViewType) {
            VIEW_TYPE_CONTAINER1 -> {
                val viewHolderContainer1 = holder as ViewHolderContainer1
                model?.let { viewHolderContainer1.bind(it) }
            }

            VIEW_TYPE_NATIVE_AD -> {
                val viewHolderContainer3 = holder as ViewHolderContainer3
                viewHolderContainer3.bind()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE_CONTAINER1
    }

    @SuppressLint("SetTextI18n")
    private fun setAllData(
        model: LiveWallpaperModel,
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

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            myActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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

    fun updateMoreData(list: ArrayList<LiveWallpaperModel?>) {
        try {
            val startPosition = arrayList.size
            arrayList.addAll(list.filter { !arrayList.contains(it) })
            notifyItemRangeInserted(startPosition, list.size)
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

    fun getAllItems(): ArrayList<LiveWallpaperModel?> = arrayList

    fun addNewData() {
        try {
            arrayList.clear()
            notifyDataSetChanged()
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

    fun updateData(list: ArrayList<LiveWallpaperModel?>) {
        try {
            arrayList.clear()
            arrayList.addAll(list)
            notifyDataSetChanged()
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }
}
