package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.launcherHomeFragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R

//Created on 14-1-2025 by U5M4N-K071N
/*It's the fear of the bug unseen 
that makes coding harder to begin.*/

class HomePagerAdapter(
    val context: FragmentActivity,
    private val fragments: List<Fragment>
) : RecyclerView.Adapter<HomePagerAdapter.Viewholder>() {

    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fragmentContainer: FrameLayout = itemView.findViewById(R.id.customFragment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_home_pager, parent, false)
        return Viewholder(view)
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val fragment = fragments[position]
        val fragmentManager = context.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        // Check if the fragment is already added
        if (!fragment.isAdded) {
            fragmentTransaction.replace(holder.fragmentContainer.id, fragment).commit()
        }
    }

}