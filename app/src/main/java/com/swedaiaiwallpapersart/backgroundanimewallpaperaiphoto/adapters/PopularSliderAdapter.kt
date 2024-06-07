package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.ListItemPopularSliderBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.PositionCallback


class PopularSliderAdapter(welcomeItems: List<Int>,var positionCallback: joinButtons) :
    RecyclerView.Adapter<PopularSliderAdapter.SliderViewHolder>() {
    private val welcomeItems: List<Int>

    init {
        this.welcomeItems = welcomeItems
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val binding = ListItemPopularSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SliderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        val item = welcomeItems[position] // Caches both original & resized image

        when (position) {
//            0 -> {
//                holder.binding.bannerInfoleft.visibility = View.GONE
//                holder.binding.bannerInforight.visibility = View.VISIBLE
//
//                holder.binding.heading.text = "Generative AI"
//
//                holder.binding.foregroundImage.setImageResource(R.drawable.banner_gen_ai_foreground)
//                Glide.with(holder.itemView.context)
//                    .load(R.drawable.banner_gen_ai_image)
//                    .into(holder.binding.onBoardImg)
//
//            }
            0 -> {
                holder.binding.bannerInfoleft.visibility = View.VISIBLE
                holder.binding.bannerInforight.visibility = View.GONE
                holder.binding.headingleft.text = "Category"
                holder.binding.foregroundImage.setImageResource(R.drawable.banner_categoreis_foreground)
                Glide.with(holder.itemView.context)
                    .load(R.drawable.banner_category_image)
                    .into(holder.binding.onBoardImg)


            }
            1 -> {
                holder.binding.bannerInfoleft.visibility = View.GONE
                holder.binding.bannerInforight.visibility = View.VISIBLE
                holder.binding.heading.text = "Anime"
                holder.binding.foregroundImage.setImageResource(R.drawable.banner_gen_ai_foreground)
                Glide.with(holder.itemView.context)
                    .load(R.drawable.banner_anime_image)
                    .into(holder.binding.onBoardImg)

            }
            2 -> {
                holder.binding.bannerInfoleft.visibility = View.VISIBLE
                holder.binding.bannerInforight.visibility = View.GONE
                holder.binding.headingleft.text = "AI Wallpaper"
                holder.binding.foregroundImage.setImageResource(R.drawable.banner_categoreis_foreground)
                Glide.with(holder.itemView.context)
                    .load(R.drawable.banner_ai_wallpaper_image)
                    .into(holder.binding.onBoardImg)

            }
        }

        with(holder){
            binding.navigateLeft.setOnClickListener {
                positionCallback.clickEvent(position)

            }

            binding.navigateRight.setOnClickListener {
                positionCallback.clickEvent(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return welcomeItems.size
    }

    inner class SliderViewHolder(val binding: ListItemPopularSliderBinding) : RecyclerView.ViewHolder(binding.root)


    interface joinButtons{
        fun clickEvent(position:Int)
    }
}