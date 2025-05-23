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
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.lottie.LottieAnimationView
import com.bmik.android.sdk.listener.CustomSDKAdsListenerAdapter
import com.bmik.android.sdk.widgets.IkmWidgetAdLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.ViewPagerImageClick
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.NativeSliderLayoutBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding
.SlideItemContainerBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.FullViewImage
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.NullPointerException


class WallpaperApiSliderAdapter(
    private val arrayList: ArrayList<CatResponse?>,
    private val viewPager2: ViewPager2,
    private val from:String,
    private val viewPagerImageClick: ViewPagerImageClick,
    private val fullViewImage: FullViewImage,
    private val mActivity:MainActivity
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var context :Context? = null

    private val VIEW_TYPE_CONTAINER1 = 0
    private val VIEW_TYPE_NATIVE_AD = 1

    private val firstAdLineThreshold = if (AdConfig.firstAdLineTrending != 0) AdConfig.firstAdLineTrending else 4

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
                val binding = NativeSliderLayoutBinding.inflate(inflater,parent,false)
                ViewHolderContainer3(binding)

            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (!isNetworkAvailable() && statusAd == 0) {
            // If network is not available, display View Container
            VIEW_TYPE_CONTAINER1
        } else {
            when {
                (position == firstAdLineThreshold) -> {
                    return VIEW_TYPE_NATIVE_AD // First ad
                }
                position > firstAdLineThreshold && (position - firstAdLineThreshold) % lineCount == 0 -> {
                    return VIEW_TYPE_NATIVE_AD // Subsequent ads

                }
                else -> {
                    return VIEW_TYPE_CONTAINER1
                }
            }
        }

    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        val model = arrayList[position]
        when (holder.itemViewType) {
            VIEW_TYPE_CONTAINER1 -> {
                val viewHolderContainer1 = holder as ViewHolderContainer1
                try {
                    viewHolderContainer1.bind(arrayList,position)
                }catch (e:NullPointerException){
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
    inner class  ViewHolderContainer1(val binding: SlideItemContainerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(arrayList: ArrayList<CatResponse?>, position: Int) {



            val model = arrayList[position]
            dataSet(model!!,binding.imageSlide,binding.progressBar,binding.gemsTextView,binding.blurView,adapterPosition,binding.noDataIMG)
        } }

    inner class ViewHolderContainer3(private val binding: NativeSliderLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(holder: RecyclerView.ViewHolder){
            loadad(holder,binding)
        }
    }
    @SuppressLint("SuspiciousIndentation")
    private fun dataSet(model: CatResponse, imageSlide: AppCompatImageView, progressBar: LottieAnimationView,
                        gemsTextView: TextView, blurView: ConstraintLayout, adapterPosition: Int,noData:ImageView) {
        progressBar.visibility = VISIBLE
        progressBar.setAnimation(R.raw.main_loading_animation)
        gemsTextView.text = model.gems.toString()
        if(model.gems==0 || model.unlockimges==true){
          blurView.visibility = INVISIBLE
        }else{
            blurView.visibility = INVISIBLE
        }

        imageSlide.setOnClickListener {
            Log.d("modelTracingNow", "dataSet: model else condition  ${model.unlockimges}  imageId  ${model.id}")
//            if(model.gems !=0 && model.unlockimges==false) {
//                viewPagerImageClick.getImagePosition(adapterPosition, blurView)
//            }else{
                fullViewImage.getFullImageUrl(model)
//            }
        }
        Glide.with(context!!).load(model.hd_image_url).diskCacheStrategy(DiskCacheStrategy.ALL)
            .listener(object:
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

//        if (adapterPosition == arrayList.size - 1) {
//            viewPager2.post(runable)
//        }

    }

    private val runable = Runnable {
        arrayList.addAll(arrayList)
        notifyDataSetChanged()
    }


    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = mActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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

    fun loadad(holder: RecyclerView.ViewHolder, binding: NativeSliderLayoutBinding){

        coroutineScope?.launch(Dispatchers.Main) {
            val adLayout = LayoutInflater.from(holder.itemView.context).inflate(
                R.layout.custom_native_slider_layout,
                null, false
            ) as? IkmWidgetAdLayout
            adLayout?.titleView = adLayout?.findViewById(R.id.custom_headline)
            adLayout?.bodyView = adLayout?.findViewById(R.id.custom_body)
            adLayout?.callToActionView = adLayout?.findViewById(R.id.custom_call_to_action)
            adLayout?.iconView = adLayout?.findViewById(R.id.custom_app_icon)
            adLayout?.mediaView = adLayout?.findViewById(R.id.custom_media)

            binding.adsView.setCustomNativeAdLayout(
                R.layout.slider_native_shimmer,
                adLayout!!
            )
            withContext(this.coroutineContext) {
                binding.adsView.loadAd(mActivity,"onboardscr_bottom","onboardscr_bottom",
                    object : CustomSDKAdsListenerAdapter() {
                        override fun onAdsLoadFail() {
                            super.onAdsLoadFail()
                            Log.e("TAG", "onAdsLoadFail: native failded " )
                            if (statusAd == 0){
                                binding.adsView.visibility = View.GONE
                            }else{
                                if (isNetworkAvailable()){
                                    loadad(holder,binding)
                                    binding.adsView.visibility = VISIBLE
                                }else{
                                    binding.adsView.visibility = View.GONE
                                }
                            }

                        }

                        override fun onAdsLoaded() {
                            super.onAdsLoaded()
                            Log.e("TAG", "onAdsLoaded: native loaded" )
                        }
                    }
                )
            }

        }

    }
}