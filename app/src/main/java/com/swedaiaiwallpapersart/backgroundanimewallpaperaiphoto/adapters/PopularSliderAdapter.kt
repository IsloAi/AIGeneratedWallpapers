package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.ListItemPopularSliderBinding


class PopularSliderAdapter(welcomeItems: List<Int>) :
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
        val item = welcomeItems[position]
        when (position) {
            0 -> {
                            
                holder.binding.onBoardImg.setImageResource(R.drawable.banner_gen_ai_image)
            }
            1 -> {
                holder.binding.onBoardImg.setImageResource(R.drawable.banner_category_image)

            }
            2 -> {
                holder.binding.onBoardImg.setImageResource(R.drawable.banner_anime_image)
            }
            3 -> {
                holder.binding.onBoardImg.setImageResource(R.drawable.banner_ai_wallpaper_image)
            }
        }
    }

    override fun getItemCount(): Int {
        return welcomeItems.size
    }

    inner class SliderViewHolder(val binding: ListItemPopularSliderBinding) : RecyclerView.ViewHolder(binding.root)
}