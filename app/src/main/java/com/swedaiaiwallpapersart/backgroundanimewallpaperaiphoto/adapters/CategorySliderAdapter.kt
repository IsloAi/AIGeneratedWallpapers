package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.StringCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatNameResponse
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding
.CatContainerBinding

class CategorySliderAdapter(
    private val arrayList: ArrayList<CatNameResponse>,
    private val viewPager2: ViewPager2,
    private val stringCallback: StringCallback
) : RecyclerView.Adapter<CategorySliderAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): ViewHolder {
        val binding = CatContainerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hits = arrayList[position]
        val context = holder.binding.imageSlide.context
        holder.binding.title.text = hits.cat_name
        Glide.with(context).load(hits.img_url).diskCacheStrategy(DiskCacheStrategy.ALL)
            .listener(object:
                RequestListener<Drawable> {


                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

            }).into(holder.binding.imageSlide)

        if (position == arrayList.size - 1) {
            viewPager2.post(runable)
        }
        holder.binding.imageSlide.setOnClickListener {
            stringCallback.getStringCall(hits.cat_name!!)
        }
    }

    override fun getItemCount() = arrayList.size

    class ViewHolder(val binding: CatContainerBinding) : RecyclerView.ViewHolder(binding.root)

    private val runable = Runnable {
        arrayList.addAll(arrayList)
        notifyDataSetChanged()
    }
}