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
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bmik.android.sdk.listener.CustomSDKAdsListenerAdapter
import com.bmik.android.sdk.widgets.IkmWidgetAdLayout
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
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.PositionCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.MostDownloadImageResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MostUsedWallpaperAdapter(
    var arrayList: ArrayList<CatResponse?>,
    var positionCallback: PositionCallback,
    private val myActivity: MainActivity
):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var lastClickTime = 0L
    private val debounceThreshold = 2000L
    var context: Context? = null
    private val VIEW_TYPE_CONTAINER1 = 0
    private val VIEW_TYPE_NATIVE_AD = 1
    private var lastAdShownPosition = -1

    val TAG = "MOSTUSEDADAPTER"

    var row = 0


    private val firstAdLineThreshold = if (AdConfig.firstAdLineMostUsed != 0) AdConfig.firstAdLineMostUsed else 4

    val firstline = firstAdLineThreshold *3
    private val lineCount = if (AdConfig.lineCountMostUsed != 0) AdConfig.lineCountMostUsed else 5
    val lineC = lineCount*3
    private val statusAd =  AdConfig.adStatusMostUsed

    private var coroutineScope: CoroutineScope? = null

    inner class ViewHolderContainer1(private val binding: WallpaperRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(modela: ArrayList<CatResponse?>, holder: RecyclerView.ViewHolder, position: Int) {
            val model = modela[position]
            Log.e(TAG, "bind: content place", )
            setAllData(
                model!!,adapterPosition,binding.loading,binding.gemsTextView,binding.likesTextView,binding.setFavouriteButton
                ,binding.lockButton,binding.diamondIcon,binding.wallpaper,holder,binding.errorImage)
        }
    }
    inner class ViewHolderContainer3(private val binding: StaggeredNativeLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(holder: RecyclerView.ViewHolder){
            Log.e(TAG, "bind: ad place", )
            loadad(holder,binding)
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
        context = parent.context
        return when (viewType) {
            VIEW_TYPE_CONTAINER1 -> {
                val binding = WallpaperRowBinding.inflate(inflater, parent, false)
                ViewHolderContainer1(binding)
            }
            VIEW_TYPE_NATIVE_AD -> {
                val binding = StaggeredNativeLayoutBinding.inflate(inflater,parent,false)
                ViewHolderContainer3(binding)

            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }
    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = arrayList[position]
        when (holder.itemViewType) {
            VIEW_TYPE_CONTAINER1 -> {
                try {
                    val viewHolderContainer1 = holder as ViewHolderContainer1
                    viewHolderContainer1.bind(arrayList,viewHolderContainer1,position)
                }catch (e: NullPointerException){
                    e.printStackTrace()
                }

            }
            VIEW_TYPE_NATIVE_AD -> {

                val viewHolderContainer3 = holder as ViewHolderContainer3
                viewHolderContainer3.bind(viewHolderContainer3)
            }
        }
    }
    override fun getItemViewType(position: Int): Int {
        row = position / 2
        Log.e("TAG", "getItemViewType: "+row )
        return if ((position + 1) == (firstline + 1)){
            Log.e("TAG", "getItemViewType: "+row )
            lastAdShownPosition = row
            VIEW_TYPE_NATIVE_AD
        }else if (position + 1 > firstline +1 && ((position +1) - (firstline+1)) % (lineC+1) == 0){
            VIEW_TYPE_NATIVE_AD
        }  else {
            VIEW_TYPE_CONTAINER1
        }
    }
    @SuppressLint("SetTextI18n")
    private fun setAllData(model: CatResponse, position:Int, animationView: LottieAnimationView, gemsView: TextView, likes: TextView, favouriteButton: ImageView
                           , lockButton: ImageView, diamondIcon: ImageView, wallpaperMainImage: ImageView, holder: RecyclerView.ViewHolder, error_img: ImageView
    ){
        animationView.visibility = View.VISIBLE
        animationView.setAnimation(R.raw.loading_upload_image)


        Glide.with(holder.itemView.context)
            .asGif()
            .load(R.raw.gems_animaion)
            .into(diamondIcon)

        Log.e("TAG", "*******MostUsed: "+model.compressed_image_url )

        Glide.with(context!!).load(model.compressed_image_url).diskCacheStrategy(DiskCacheStrategy.ALL)
            .listener(object: RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d("onLoadFailed", "onLoadFailed: ")
                    animationView.setAnimation(R.raw.no_data_image_found)
                    animationView.visibility = View.VISIBLE
                    error_img.visibility = View.VISIBLE
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
                    error_img.visibility = View.GONE
                    Log.d("onLoadFailed", "onResourceReady: ")
                    return false
                }
            }).into(wallpaperMainImage)
        wallpaperMainImage.setOnClickListener {
            val currentTime = System.currentTimeMillis()

            if (currentTime - lastClickTime >= debounceThreshold) {
                positionCallback.getPosition(position)
                lastClickTime = currentTime
            }


        }
    }

    fun loadad(holder: RecyclerView.ViewHolder, binding: StaggeredNativeLayoutBinding){

        Log.e(TAG, "loadad: inside methodd" )
        coroutineScope?.launch(Dispatchers.Main) {
            val adLayout = LayoutInflater.from(holder.itemView.context).inflate(
                R.layout.native_dialog_layout,
                null, false
            ) as? IkmWidgetAdLayout
            adLayout?.titleView = adLayout?.findViewById(R.id.custom_headline)
            adLayout?.bodyView = adLayout?.findViewById(R.id.custom_body)
            adLayout?.callToActionView = adLayout?.findViewById(R.id.custom_call_to_action)
            adLayout?.iconView = adLayout?.findViewById(R.id.custom_app_icon)
            adLayout?.mediaView = adLayout?.findViewById(R.id.custom_media)

            binding.adsView.setCustomNativeAdLayout(
                R.layout.shimmer_loading_native,
                adLayout!!
            )

            Log.e(TAG, "loadad: inside layout set" )

            withContext(this.coroutineContext) {
                binding.adsView.loadAd(myActivity,"mainscr_all_tab_scroll","mainscr_all_tab_scroll",
                    object : CustomSDKAdsListenerAdapter() {

                        override fun onAdsLoadFail() {
                            super.onAdsLoadFail()
                            Log.e(TAG, "onAdsLoadFail: native failded " )
                            if (statusAd == 0){
                                binding.adsView.visibility = View.GONE
                            }else{
                                if (isNetworkAvailable()){
                                    loadad(holder,binding)
                                    binding.adsView.visibility = View.VISIBLE
                                }else{
                                    binding.adsView.visibility = View.GONE
                                }
                            }
                        }

                        override fun onAdsLoaded() {
                            super.onAdsLoaded()
                            binding.adsView.visibility = View.VISIBLE
                            Log.e(TAG, "onAdsLoaded: native loaded" )
                        }
                    }
                )
            }
        }






    }

    fun setCoroutineScope(scope: CoroutineScope) {
        coroutineScope = scope
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


    fun addNewData(){
        arrayList.clear()
        notifyDataSetChanged()

    }


    fun updateMoreData(list:ArrayList<CatResponse?>){


        val startPosition = arrayList.size

        for(i in 0 until list.size){
            if (arrayList.contains(list[i])){
                Log.e("********new Data", "updateMoreData: already in list", )
            }else{
                arrayList.add(list[i])
            }
        }
        notifyItemRangeInserted(startPosition, list.size)
    }


    fun getAllItems():ArrayList<CatResponse?>{
        return arrayList
    }



}
