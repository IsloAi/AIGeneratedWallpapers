package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding
.ListItemOnboadingBinding


class OnboardingAdapter(welcomeItems: List<Int>) :
    RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {
    private val welcomeItems: List<Int>

    init {
        this.welcomeItems = welcomeItems
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val binding = ListItemOnboadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OnboardingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        val item = welcomeItems[position]
        when (position) {
            0 -> {
                holder.binding.onBoardImg.setImageResource(R.drawable.onboading_item1)
            }
            1 -> {
                holder.binding.onBoardImg.setImageResource(R.drawable.onboarding_item2)

            }
            2 -> {
                holder.binding.onBoardImg.setImageResource(R.drawable.onboarding_item3)
            }
        }
    }

    override fun getItemCount(): Int {
        return welcomeItems.size
    }

    inner class OnboardingViewHolder(val binding: ListItemOnboadingBinding) : RecyclerView.ViewHolder(binding.root)
}