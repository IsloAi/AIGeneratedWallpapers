package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.airbnb.lottie.LottieAnimationView
import com.bmik.android.sdk.listener.CustomSDKAdsListenerAdapter
import com.bmik.android.sdk.widgets.IkmWidgetAdLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.debug.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.debug.databinding.StaggeredNativeBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.debug.databinding.StaggeredNativeLayoutBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.debug.databinding
.WallpaperRow2Binding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.debug.databinding
.WallpaperRowBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.GemsTextUpdate
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.GetLoginDetails
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.PositionCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.PostData
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.RetrofitInstance
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.ApiService
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyDialogs
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ApiCategoriesListAdapter(
    var arrayList: ArrayList<CatResponse>,
    var positionCallback: PositionCallback,
    var navController: NavController,
    private val actionId: Int,
    private val gemsTextUpdate: GemsTextUpdate,
    private val getLoginDetails: GetLoginDetails,
    private val myViewModel: MyViewModel?,
    private val whichClicked: Int,
    private val myActivity: MainActivity
):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var lastClickTime = 0L
    private val debounceThreshold = 2000L // 1 second
         var context:Context? = null
        private val VIEW_TYPE_CONTAINER1 = 0
        private val VIEW_TYPE_CONTAINER2 = 1

    private val VIEW_TYPE_NATIVE_AD = 2

    // Adjust this threshold value as needed
    private val NATIVE_AD_INTERVAL = 10
       private val myDialogs = MyDialogs()
    inner class ViewHolderContainer1(private val binding: WallpaperRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: CatResponse,holder: ViewHolder) {
            setAllData(model,adapterPosition,binding.loading,binding.gemsTextView,binding.likesTextView,binding.setFavouriteButton
            ,binding.lockButton,binding.diamondIcon,binding.wallpaper,holder)
        }
    }
    inner class ViewHolderContainer2(private val binding: WallpaperRow2Binding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: CatResponse,holder: ViewHolder) {
            setAllData(model,adapterPosition,binding.loading,binding.gemsTextView,binding.likesTextView,binding.setFavouriteButton
                ,binding.lockButton,binding.diamondIcon,binding.wallpaper,holder) }
    }

    inner class ViewHolderContainer3(private val binding: StaggeredNativeLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(holder: ViewHolder){
            loadad(holder,binding)
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
            VIEW_TYPE_CONTAINER2 -> {
                val binding = WallpaperRow2Binding.inflate(inflater, parent, false)
                ViewHolderContainer2(binding)
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
                val viewHolderContainer1 = holder as ViewHolderContainer1
                viewHolderContainer1.bind(model,holder)
            }
            VIEW_TYPE_CONTAINER2 -> {
                val viewHolderContainer2 = holder as ViewHolderContainer2
                viewHolderContainer2.bind(model,holder)
            }

            VIEW_TYPE_NATIVE_AD -> {
                val viewHolderContainer3 = holder as ViewHolderContainer3
                viewHolderContainer3.bind(viewHolderContainer3)
            }
        }
    }
    override fun getItemViewType(position: Int): Int {
        // Return the appropriate view type based on the position
        return if (position % (NATIVE_AD_INTERVAL + 1) == 0) {
            // Position is a multiple of NATIVE_AD_INTERVAL, so it's a native ad
            VIEW_TYPE_NATIVE_AD
        } else if (position % 4 < 1) {
            VIEW_TYPE_CONTAINER1
        } else {
            VIEW_TYPE_CONTAINER2
        }
    }
    @SuppressLint("SetTextI18n")
    private fun setAllData(model: CatResponse, position:Int, animationView: LottieAnimationView, gemsView:TextView, likes:TextView, favouriteButton: ImageView
                           , lockButton:ImageView, diamondIcon:ImageView, wallpaperMainImage:ImageView,holder: ViewHolder){
        animationView.visibility = VISIBLE
        animationView.setAnimation(R.raw.loading_upload_image)
        gemsView.text = model.gems.toString()
        likes.text = model.likes.toString()
        if(model.liked==true){
            favouriteButton.setImageResource(R.drawable.heart_red)
        }else{
            favouriteButton.setImageResource(R.drawable.heart_unsel)
        }

        Glide.with(holder.itemView.context)
            .asGif()
            .load(R.raw.gems_animaion)
            .into(diamondIcon)
        if(model.gems==0 || model.unlockimges==true){
           lockButton.visibility = GONE
           diamondIcon.visibility = GONE
            gemsView.visibility = GONE
            Log.d("tracingImageId", "free: category = ${model.cat_name}  , imageId =  ${model.id}")
        }else{
            lockButton.visibility = VISIBLE
            diamondIcon.visibility =GONE
            gemsView.visibility = GONE
            Log.d("tracingImageId", "paid: category = ${model.cat_name}  , imageId = ${model.id}")
        }
        Log.d("nadeemAhmad", "setAllData: ${model.compressed_image_url}")

        Glide.with(context!!).load(model.compressed_image_url).diskCacheStrategy(DiskCacheStrategy.ALL)
            .listener(object:RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    Log.d("onLoadFailed", "onLoadFailed: ")
                    animationView.setAnimation(R.raw.no_data_image_found)
                    return false
                }
                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    animationView.visibility = INVISIBLE
                    Log.d("onLoadFailed", "onResourceReady: ")
                    return false
                }}).into(wallpaperMainImage)
        wallpaperMainImage.setOnClickListener {
            val currentTime = System.currentTimeMillis()

            if (currentTime - lastClickTime >= debounceThreshold) {
                val gems = model.gems
                val isBuy = model.unlockimges
                Log.d("gems", "onBindViewHolder: $gems")

                if (gems != null) {
                    if (gems == 0 || isBuy == true) {
                        positionCallback.getPosition(position)
                    } else {
                        if (whichClicked == 1) {
                            myDialogs.getWallpaperPopup(context!!, model, navController, actionId, gemsTextUpdate, lockButton, diamondIcon, gemsView, myViewModel!!,myActivity)
                        } else {
                            myDialogs.getWallpaperPopup(context!!, model, navController, actionId, gemsTextUpdate, lockButton, diamondIcon, gemsView,myActivity)
                        }
                    }
                }
                lastClickTime = currentTime
            }


        }
        favouriteButton.setOnClickListener{
            favouriteButton.isEnabled = false
                if(arrayList[position].liked==true){
                    arrayList[position].liked = false
                    favouriteButton.setImageResource(R.drawable.heart_unsel)
                    likes.text = (arrayList[position].likes!!-1).toString()
                }else{
                    arrayList[position].liked = true
                    favouriteButton.setImageResource(R.drawable.heart_red)
                    likes.text = (arrayList[position].likes!!+1).toString()
                }
                addFavourite(context!!,model,favouriteButton,likes)

        }
    }

    fun loadad(holder: ViewHolder,binding: StaggeredNativeLayoutBinding){
        val adLayout = LayoutInflater.from(holder.itemView.context).inflate(
            R.layout.staggered_native,
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
        binding.adsView.loadAd(myActivity,"onboardscr_bottom","onboardscr_bottom",
            object : CustomSDKAdsListenerAdapter() {
                override fun onAdsLoadFail() {
                    super.onAdsLoadFail()
                    Log.e("TAG", "onAdsLoadFail: native failded " )
                    binding.adsView.visibility = View.GONE
                }

                override fun onAdsLoaded() {
                    super.onAdsLoaded()
                    Log.e("TAG", "onAdsLoaded: native loaded" )
                }
            }
        )
    }
    @SuppressLint("SuspiciousIndentation")
    private fun addFavourite(
        context: Context,
        model: CatResponse,
        favouriteButton: ImageView,
        likesTextView: TextView){
        val retrofit = RetrofitInstance.getInstance()
        val apiService = retrofit.create(ApiService::class.java)
        val postData = PostData(MySharePreference.getDeviceID(context)!!, model.id.toString())
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.postData(postData).execute()
                  withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val message = response.body()?.string()
                        if (message == "Liked") {
                            val like = model.likes
                            val newLike = like!! + 1
                            model.likes = newLike
                            likesTextView.text = newLike.toString()
                            favouriteButton.setImageResource(R.drawable.heart_red)
                        } else {
                            val like = model.likes
                            val newLike = like!! - 1
                            model.likes = newLike
                            likesTextView.text = newLike.toString()
                            favouriteButton.setImageResource(R.drawable.heart_unsel)
                        }
                        favouriteButton.isEnabled = true
                    } else {
                        favouriteButton.isEnabled = true
                        Toast.makeText(context, "onResponse error", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (t: Throwable) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
                    favouriteButton.isEnabled = true
                }
            }
        }
    }



}
