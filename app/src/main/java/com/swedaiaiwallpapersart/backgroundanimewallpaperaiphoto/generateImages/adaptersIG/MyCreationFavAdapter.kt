package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.adaptersIG

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding
.WallpaperRow2Binding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding
.WallpaperRowBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.interfaces.GetFavouriteImagePath
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.FavouriteListIGEntity

class MyCreationFavAdapter(
   private var arrayList: List<FavouriteListIGEntity>,
   private val getFavouriteImagePath: GetFavouriteImagePath
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var context: Context? = null
    private val VIEW_TYPE_CONTAINER1 = 0

    inner class ViewHolderContainer1(private val binding: WallpaperRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: FavouriteListIGEntity, position: Int) {
            setData(
                model,
                position,
                binding.wallpaper,
                binding.loading
            )
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
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = arrayList[position]
        when (holder.itemViewType) {
            VIEW_TYPE_CONTAINER1 -> {
                val viewHolderContainer1 = holder as ViewHolderContainer1
                viewHolderContainer1.bind(model,position)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        // Return the appropriate view type based on the position
        return if (position % 4 < 1) VIEW_TYPE_CONTAINER1 else VIEW_TYPE_CONTAINER1
    }
    private fun setData(
        model: FavouriteListIGEntity,
        position: Int,
        wallpaper: ImageView,
        loading: LottieAnimationView,

    ) {
        loading.visibility = INVISIBLE

        Glide.with(context!!).load(model.image).into(wallpaper)
        wallpaper.setOnClickListener {
          getFavouriteImagePath.getImageClick(position,model.prompt,model.imageId)
        }
    }


}
