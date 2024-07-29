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
import com.ikame.android.sdk.data.dto.pub.IKAdError
import com.ikame.android.sdk.listener.pub.IKShowWidgetAdListener
import com.ikame.android.sdk.widgets.IkmWidgetAdLayout
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.ListItemLiveWallpaperBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.StaggeredNativeLayoutBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.ChargingAnimModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.downloadCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ChargingAnimationAdapter  (
    var arrayList: ArrayList<ChargingAnimModel?>,
    var positionCallback: downloadCallback,
    private val myActivity: MainActivity
):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var lastClickTime = 0L
    private val debounceThreshold = 2000L // 1 second
    var context: Context? = null
    private val VIEW_TYPE_CONTAINER1 = 0
    private val VIEW_TYPE_NATIVE_AD = 1
    private var lastAdShownPosition = -1

    var row = 0


    private val firstAdLineThreshold = if (AdConfig.firstAdLineViewListWallSRC != 0) AdConfig.firstAdLineViewListWallSRC else 4

    val firstline = firstAdLineThreshold *3
    private val lineCount = if (AdConfig.lineCountViewListWallSRC != 0) AdConfig.lineCountViewListWallSRC else 5
    val lineC = lineCount*3
    private val statusAd =  AdConfig.adStatusViewListWallSRC
    private var coroutineScope: CoroutineScope? = null

    fun setCoroutineScope(scope: CoroutineScope) {
        coroutineScope = scope
    }
    inner class ViewHolderContainer1(private val binding: ListItemLiveWallpaperBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(modela: ArrayList<ChargingAnimModel?>, holder: RecyclerView.ViewHolder, position: Int) {
            val model = modela[position]
            setAllData(
                model!!,adapterPosition,binding.loading,binding.wallpaper,binding.errorImage,binding.iap,binding.animationView)
        }
    }
    inner class ViewHolderContainer3(private val binding: StaggeredNativeLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(holder: RecyclerView.ViewHolder){
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
                val binding = ListItemLiveWallpaperBinding.inflate(inflater, parent, false)
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
        if (AdConfig.ISPAIDUSER){
            return VIEW_TYPE_CONTAINER1
        }else {
            row = position / 2
            Log.e("TAG", "getItemViewType: "+row )
            val adPosition = firstAdLineThreshold + (lineCount * (row - firstAdLineThreshold) / lineCount)
//        (position + 1) % (firstline + 1) == 0
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

    }
    @SuppressLint("SetTextI18n")
    private fun setAllData(model: ChargingAnimModel, position:Int, animationView: LottieAnimationView, wallpaperMainImage: ImageView, error_img: ImageView, iap: ImageView,imageanimationView: LottieAnimationView
    ){
        animationView.visibility = View.VISIBLE
        animationView.setAnimation(R.raw.loading_upload_image)

//        if (!model.unlocked){
//            if (AdConfig.ISPAIDUSER){
//                iap.visibility = View.GONE
//            }else{
//                iap.visibility = View.VISIBLE
//            }
//
//        }else{
//            iap.visibility = View.GONE
//        }
//            imageanimationView.visibility = View.VISIBLE
//            imageanimationView.setAnimationFromUrl(model.hd_animation)
//            imageanimationView.playAnimation()
            Glide.with(context!!).load(AdConfig.BASE_URL_DATA + "/animation/" +model.thumnail).diskCacheStrategy(DiskCacheStrategy.ALL).thumbnail(0.1f)
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
                        Log.d("******loaded", "onResourceReady: ")
                        return false
                    }
                }).into(wallpaperMainImage)


        wallpaperMainImage.setOnClickListener {
            val currentTime = System.currentTimeMillis()

            if (currentTime - lastClickTime >= debounceThreshold) {


                positionCallback.getPosition(position,model)

//                    else {
//                        if (whichClicked == 1) {
//                            myDialogs.getWallpaperPopup(context!!, model, navController, actionId, gemsTextUpdate, lockButton, diamondIcon, gemsView, myViewModel!!,myActivity)
//                        } else {
//                            myDialogs.getWallpaperPopup(context!!, model, navController, actionId, gemsTextUpdate, lockButton, diamondIcon, gemsView,myActivity)
//                        }
//                    }

                lastClickTime = currentTime
            }


        }

    }

    fun loadad(holder: RecyclerView.ViewHolder, binding: StaggeredNativeLayoutBinding){
        Log.e("TAG", "loadad: inside method")
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
            Log.e("TAG", "loadad: inside main scope")

            withContext(this.coroutineContext) {

                binding.adsView.loadAd(R.layout.shimmer_loading_native, adLayout!!,"mainscr_live_tab_scroll",
                    object : IKShowWidgetAdListener {
                        override fun onAdShowFail(error: IKAdError) {
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
                            Log.e("TAG", "onAdsLoadFail: native failded " )
                        }

                        override fun onAdShowed() {
                            binding.adsView.visibility = View.VISIBLE
                        }
                    }
                )
            }


        }

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


    fun updateMoreData(list:ArrayList<ChargingAnimModel?>){
        val startPosition = arrayList.size

        for(i in 0 until list.size){
            if (arrayList.contains(list[i])){
                Log.e("********new Data", "updateMoreData: already in list")
            }else{
                arrayList.add(list[i])
            }
        }
        notifyItemRangeInserted(startPosition, list.size)
    }

    fun getAllItems():ArrayList<ChargingAnimModel?>{
        return arrayList
    }

    fun addNewData(){
        arrayList.clear()
        notifyDataSetChanged()

    }

    fun updateData(list:ArrayList<ChargingAnimModel?>){
        arrayList.clear()
        arrayList.addAll(list)
        notifyDataSetChanged()
    }


    interface downloadCallback {
        fun getPosition(position:Int,model: ChargingAnimModel)
    }
}
