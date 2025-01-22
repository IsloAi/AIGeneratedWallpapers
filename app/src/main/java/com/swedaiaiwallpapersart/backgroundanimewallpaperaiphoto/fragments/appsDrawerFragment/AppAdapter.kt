package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.appsDrawerFragment

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R

//Created on 10/1/2025 by U5M4N-K071N
/*It's the fear of the bug unseen 
that makes coding harder to begin.*/

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class AppAdapter(var context: Context ,var list:List<AppInfo>) : RecyclerView.Adapter<AppAdapter.Viewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_apps, parent, false)
        return Viewholder(view)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val appLabel: String = list[position].label.toString()
        val appPackage: String = list[position].packageName.toString()
        val appIcon: Drawable = list[position].icon

        val textView: TextView = holder.name
        textView.text = appLabel
        val imageView: ImageView = holder.image
        imageView.setImageDrawable(appIcon)

        holder.itemView.setOnClickListener {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(appPackage)
            context.startActivity(launchIntent)
        }

    }
    fun updateList(newList: List<AppInfo>) {
        list = newList
        notifyDataSetChanged()  // Notify that data has changed
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image = itemView.findViewById<ImageView>(R.id.app_icon)
        var name = itemView.findViewById<TextView>(R.id.tv_app_name)

    }

}