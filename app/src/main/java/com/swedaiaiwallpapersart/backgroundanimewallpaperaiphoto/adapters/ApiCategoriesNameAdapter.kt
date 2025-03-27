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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.CatNameListBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.StaggeredNativeLayoutBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.StringCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatNameResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig

class ApiCategoriesNameAdapter(
    private val arrayList: ArrayList<CatNameResponse?>,
    private val stringCallback: StringCallback,
    private val myActivity: MainActivity,
    private val from: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_CONTAINER1 = 0
    private val VIEW_TYPE_NATIVE_AD = 1
    private val debounceThreshold = 2000L // 2 seconds

    private val firstAdLineThreshold =
        if (AdConfig.firstAdLineCategoryArt != 0) AdConfig.firstAdLineCategoryArt else 4
    private val firstLine = firstAdLineThreshold * 3

    private val lineCount =
        if (AdConfig.lineCountCategoryArt != 0) AdConfig.lineCountCategoryArt else 5
    private val lineC = lineCount * 3
    private val statusAd = AdConfig.adStatusCategoryArt

    private var lastClickTime = 0L
    private var context: Context? = null

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
            VIEW_TYPE_CONTAINER1 -> ViewHolderContainerItem(
                CatNameListBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )

            VIEW_TYPE_NATIVE_AD -> ViewHolderContainer3(
                StaggeredNativeLayoutBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )

            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        Log.d("Adapter", "Binding position: $position, Adjusted Position: $position")

        when (holder.itemViewType) {
            VIEW_TYPE_CONTAINER1 -> {
                val model = arrayList[position]
                Log.d("Adapter", "Category Name: ${model?.cat_name}")
                if (model != null) {
                    (holder as ViewHolderContainerItem).bind(model)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position >= arrayList.size) {
            VIEW_TYPE_CONTAINER1
        } else if (arrayList[position] == null) {
            VIEW_TYPE_CONTAINER1
        } else {
            VIEW_TYPE_CONTAINER1
        }
    }

    inner class ViewHolderContainerItem(private val binding: CatNameListBinding) :
        RecyclerView.ViewHolder(binding.root) {
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

    inner class ViewHolderContainer3(binding: StaggeredNativeLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {}
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

    fun updateData(newData: List<CatNameResponse?>) {
        arrayList.clear()

        // Remove duplicate categories
        val uniqueData = newData.distinctBy { it?.cat_name }
        arrayList.addAll(uniqueData)

        Log.d("Adapter", "updateData: ${arrayList.size} items added")
        notifyDataSetChanged()
    }

}