package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.PremiumPlanCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.PremiumPlanModel
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding
.PremiumRowItemBinding

class PremiumAdapter(val arrayList: List<PremiumPlanModel>, private val premiumPlanCallback: PremiumPlanCallback):RecyclerView.Adapter<PremiumAdapter.ViewHolder>() {
    class ViewHolder(val binding: PremiumRowItemBinding):RecyclerView.ViewHolder(binding.root)
    private var selectedItemPosition = RecyclerView.NO_POSITION
    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val binding = PremiumRowItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }
    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val model = arrayList[position]
        val mainContainer = holder.binding.mainContainer
        holder.binding.rate.text = model.money
        holder.binding.value.text = model.credits.toString()
        holder.binding.sideBanner.setBackgroundResource(model.background)
        if (selectedItemPosition == position) {
            mainContainer.setBackgroundResource(R.drawable.plan_slected)
        } else {
            mainContainer.setBackgroundResource(R.drawable.plan_slected_white)
        }
        mainContainer.setOnClickListener {
            premiumPlanCallback.getPlan(model.planId)
            val previousSelected = selectedItemPosition
            selectedItemPosition = position
            notifyItemChanged(previousSelected)
            notifyItemChanged(selectedItemPosition)
        }
    }
    override fun getItemCount() = arrayList.size
}