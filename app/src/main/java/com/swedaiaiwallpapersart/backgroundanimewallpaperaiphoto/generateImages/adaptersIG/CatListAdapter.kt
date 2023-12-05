package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.adaptersIG

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding
.CatListPromptWordItemBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.interfaces.GetbackNameOfCat
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.models.CatListModelIG

class CatListAdapter( private val arrayList: ArrayList<CatListModelIG>,
    private val getbackNameOfCat: GetbackNameOfCat):RecyclerView.Adapter<CatListAdapter.ViewHolder>() {
    class ViewHolder(val binding: CatListPromptWordItemBinding):RecyclerView.ViewHolder(binding.root)
    private var selectedItemPosition = RecyclerView.NO_POSITION
    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val binding = CatListPromptWordItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }
    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val model = arrayList[position]
        val mainContainer = holder.binding.mainContainer

        Glide.with(holder.itemView.context)
            .load(model.image)
            .into(holder.binding.imageViewOfList)
        holder.binding.title.text = model.title
        if (selectedItemPosition == position) {
            mainContainer.setBackgroundResource(R.drawable.plan_slected)
        } else {
            mainContainer.setBackgroundResource(R.drawable.plan_slected_white)
        }
        mainContainer.setOnClickListener {
            getbackNameOfCat.getName(model.title)
            val previousSelected = selectedItemPosition
            selectedItemPosition = position
            notifyItemChanged(previousSelected)
            notifyItemChanged(selectedItemPosition)
        }
    }
    override fun getItemCount() = arrayList.size
}