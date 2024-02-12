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
import com.bmik.android.sdk.listener.CustomSDKAdsListenerAdapter
import com.bmik.android.sdk.widgets.IkmWidgetAdLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding
.CatNameListBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.StaggeredNativeLayoutBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.StringCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatNameResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import java.lang.NullPointerException

class ApiCategoriesNameAdapter(
    private val arrayList: ArrayList<CatNameResponse?>,
    private val stringCallback: StringCallback,
    private val myActivity: MainActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val VIEW_TYPE_CONTAINER1 = 0
    private val VIEW_TYPE_NATIVE_AD = 1
    var context: Context? = null

    private var lastClickTime = 0L
    private val debounceThreshold = 2000L // 1 second


    private val firstAdLineThreshold = if (AdConfig.firstAdLineCategoryArt != 0) AdConfig.firstAdLineCategoryArt else 4
    val firstLine = firstAdLineThreshold * 3

    private val lineCount = if (AdConfig.lineCountCategoryArt != 0) AdConfig.lineCountCategoryArt else 5
    val lineC = lineCount * 3
    private val statusAd =  AdConfig.adStatusCategoryArt


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

        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        return when (viewType) {
            VIEW_TYPE_CONTAINER1 -> {
                val binding =  CatNameListBinding.inflate(inflater, parent, false)
                ViewHolderContainerItem(binding)
            }
            VIEW_TYPE_NATIVE_AD -> {
                val binding = StaggeredNativeLayoutBinding.inflate(inflater,parent,false)
                ViewHolderContainer3(binding)

            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = arrayList[position]

        when (holder.itemViewType) {
            VIEW_TYPE_CONTAINER1 -> {
                try {
                    Log.e("TAG", "onBindViewHolder: "+model )
                    val viewHolderContainer1 = holder as ViewHolderContainerItem
                    viewHolderContainer1.bind(model!!)
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

    override fun getItemCount(): Int = arrayList.size



    override fun getItemViewType(position: Int): Int {
        return if ((position + 1) == (firstLine + 1)){
            VIEW_TYPE_NATIVE_AD
        }else if (position + 1 > firstLine +1 && ((position +1) - (firstLine+1)) % (lineC+1) == 0){
            VIEW_TYPE_NATIVE_AD
        }  else {
            VIEW_TYPE_CONTAINER1
        }
    }

    inner class ViewHolderContainerItem(private val binding: CatNameListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: CatNameResponse) {
            binding.loading.visibility = View.VISIBLE
            binding.loading.setAnimation(R.raw.main_loading_animation)
            binding.catName.text = model.cat_name
            Glide.with(context!!)
                .load(model.img_url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d("onLoadFailed", "onLoadFailed: ")
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
                        Log.d("onLoadFailed", "onResourceReady: ")
                        return false
                    }
                })
                .into(binding.catIconImage)

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
        fun bind(holder: RecyclerView.ViewHolder){
            loadad(holder,binding)
        }
    }


    fun loadad(holder: RecyclerView.ViewHolder, binding: StaggeredNativeLayoutBinding){
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
        binding.adsView.loadAd(myActivity,"onboardscr_bottom","onboardscr_bottom",
            object : CustomSDKAdsListenerAdapter() {
                override fun onAdsLoadFail() {
                    super.onAdsLoadFail()
                    Log.e("TAG", "onAdsLoadFail: native failded " )
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
                    Log.e("TAG", "onAdsLoaded: native loaded" )
                }
            }
        )
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
        notifyDataSetChanged()

    }
}
