package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.adaptersIG

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding
.SlideItemContainerCreationBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.FavouriteListIGEntity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.FullViewImagePopup

class FavouriteWallpaperSliderAdapter(
    private val arrayList: ArrayList<FavouriteListIGEntity>,
    private val viewPager2: ViewPager2,
    ) : RecyclerView.Adapter<FavouriteWallpaperSliderAdapter.ViewHolder>() {
    var context :Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SlideItemContainerCreationBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        context = parent.context
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(arrayList,position)
    }
    override fun getItemCount(): Int {
        return arrayList.size
    }
    inner class  ViewHolder(val binding: SlideItemContainerCreationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(arrayList: ArrayList<FavouriteListIGEntity>, position: Int) {
            val model = arrayList[position]
            dataSet(model,binding.imageSlide,position)
        }
    }
    @SuppressLint("SuspiciousIndentation")
    private fun dataSet(model: FavouriteListIGEntity, imageSlide: AppCompatImageView, position: Int) {
        Log.d("imageLists", "dataSet: string model in adapter $model")
        Glide.with(context!!).load(model.image).into(imageSlide)
        imageSlide.setOnClickListener {
//            FullViewImagePopup.openFullViewWallpaper(context!!,model!!)
        }
//        if (position == arrayList.size - 1) {
//            viewPager2.post(runable) }
    }
    private val runable = Runnable {
        arrayList.addAll(arrayList)
        notifyDataSetChanged()
    }
}