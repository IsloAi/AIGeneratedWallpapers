package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.fragmentsIG

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.CatListPromptWordItemBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.ListItemMyCreationsBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.interfaces.GetbackOfID
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.GetResponseIGEntity


class CreationsAdapter(private val arrayList: List<GetResponseIGEntity>,
                     private val getbackOfID: GetbackOfID
): RecyclerView.Adapter<CreationsAdapter.ViewHolder>() {
    class ViewHolder(val binding: ListItemMyCreationsBinding): RecyclerView.ViewHolder(binding.root)
    private var context: Context? = null
    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemMyCreationsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        context = parent.context
        return ViewHolder(binding)
    }
    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val model = arrayList[position]
        if(model.future_links != null){
            Glide.with(context!!).load(model.future_links[0]).into(holder.binding.wallpaper)
        }else{
            Glide.with(context!!).load(model.prompt?.get(0)).into(holder.binding.wallpaper)
        }

    }
    override fun getItemCount() = arrayList.size
}