package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.adaptersIG

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding
.CatListPromptWordItemBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding
.StyleListPromptWordItemBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.interfaces.GetbackNameOfCat
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.models.CatListModelIG

class StyleListAdapter(private val arrayList: ArrayList<CatListModelIG>):RecyclerView.Adapter<StyleListAdapter.ViewHolder>() {
    class ViewHolder(val binding: StyleListPromptWordItemBinding):RecyclerView.ViewHolder(binding.root)
    private var selectedItemPosition = RecyclerView.NO_POSITION
    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val binding = StyleListPromptWordItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }
    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val model = arrayList[position]
        val mainContainer = holder.binding.mainContainer
        holder.binding.imageViewOfList.setImageResource(model.image)
        holder.binding.title.text = model.title
        if (selectedItemPosition == position) {
            mainContainer.setBackgroundResource(R.drawable.plan_slected)
        } else {
            mainContainer.setBackgroundResource(R.drawable.plan_slected_white)
        }
        mainContainer.setOnClickListener {
            val previousSelected = selectedItemPosition
            selectedItemPosition = position
            notifyItemChanged(previousSelected)
            notifyItemChanged(selectedItemPosition)
        }
    }
    override fun getItemCount() = arrayList.size
}