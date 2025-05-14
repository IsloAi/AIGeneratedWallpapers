package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.imageview.ShapeableImageView
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.ListItemTrendingHroizontalBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.SingleDatabaseResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.activity.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.PositionCallback


class ApiCategoriesListHorizontalAdapter(
    var arrayList: ArrayList<SingleDatabaseResponse?>,
    var positionCallback: PositionCallback,
    private val myActivity: MainActivity
) :
    RecyclerView.Adapter<ApiCategoriesListHorizontalAdapter.ViewHolderContainer1>() {

    private var lastClickTime = 0L
    private val debounceThreshold = 2000L // 1 second
    var context: Context? = null


    inner class ViewHolderContainer1(val binding: ListItemTrendingHroizontalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            modela: ArrayList<SingleDatabaseResponse?>,
            holder: RecyclerView.ViewHolder,
            position: Int
        ) {
            val model = modela[position]
            setAllData(
                model!!, adapterPosition, binding.trendingImage
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderContainer1 {
        val binding = ListItemTrendingHroizontalBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolderContainer1(binding)
    }


    override fun getItemCount(): Int {
        return minOf(arrayList.size, 10)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: ViewHolderContainer1, position: Int) {
        val model = arrayList[position]
        context = holder.itemView.context
        setAllData(model!!, position, holder.binding.trendingImage)
    }

    @SuppressLint("SetTextI18n")
    private fun setAllData(
        model: SingleDatabaseResponse,
        position: Int,
        image: ShapeableImageView
    ) {

        Glide.with(context!!)
            .load(AdConfig.BASE_URL_DATA + "/staticwallpaper/hd/" + model.hd_image_url + "?class=custom")
            .diskCacheStrategy(DiskCacheStrategy.DATA).thumbnail(0.1f)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d("onLoadFailed", "onLoadFailed: ")
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d("onLoadFailed", "onResourceReady: ")
                    return false
                }
            }).into(image)
        image.setOnClickListener {
            val currentTime = System.currentTimeMillis()

            if (currentTime - lastClickTime >= debounceThreshold) {
                positionCallback.getPosition(position)
                lastClickTime = currentTime
            }


        }
    }

    fun updateData(list: ArrayList<SingleDatabaseResponse?>) {
        arrayList.clear()
        arrayList.addAll(list)
        notifyDataSetChanged()
    }
    /* fun updateMoreData(list: ArrayList<SingleDatabaseResponse?>) {
         val startPosition = arrayList.size

         for (i in 0 until list.size) {
             if (arrayList.contains(list[i])) {
                 Log.e("********new Data", "updateMoreData: already in list")
             } else {
                 arrayList.add(list[i])
             }
         }
         notifyItemRangeInserted(startPosition, list.size)
     }*/

    fun getAllItems(): ArrayList<SingleDatabaseResponse?> {
        return arrayList
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


}