package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.getStartedFragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R

//Created on 14-1-2025 by U5M4N-K071N
/*It's the fear of the bug unseen 
that makes coding harder to begin.*/

class pagerAdapter(val context: Context) : RecyclerView.Adapter<pagerAdapter.Viewholder>() {

    val imgs = intArrayOf(R.drawable.device_android_fs, R.drawable.device_android_fs2)

    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.img)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_pager, parent, false)
        return Viewholder(view)
    }

    override fun getItemCount(): Int {
        return imgs.size
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        holder.imageView.setImageResource(imgs[position])
    }
}