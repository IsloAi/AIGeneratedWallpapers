package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.adaptersIG

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.debug.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.debug.databinding
.CatListPromptWordItemBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.interfaces.GetbackNameOfCat
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.interfaces.GetbackOfID
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.models.CatListModelIG
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.GetResponseIGEntity

class HistoryAdapter(private val arrayList: List<GetResponseIGEntity>,
    private val getbackOfID: GetbackOfID
):RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    class ViewHolder(val binding: CatListPromptWordItemBinding):RecyclerView.ViewHolder(binding.root)
    private var context:Context? = null
    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val binding = CatListPromptWordItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        context = parent.context
        return ViewHolder(binding)
    }
    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val model = arrayList[position]
        val mainContainer = holder.binding.mainContainer
         holder.binding.title.visibility = GONE
        if(model.future_links != null){
            Glide.with(context!!).load(model.future_links[0]).into(holder.binding.imageViewOfList)
        }else{
            Glide.with(context!!).load(model.prompt?.get(0)).into(holder.binding.imageViewOfList)
        }
        mainContainer.setOnClickListener {
            getbackOfID.getId(model.id)
        }
    }
    override fun getItemCount() = arrayList.size
}