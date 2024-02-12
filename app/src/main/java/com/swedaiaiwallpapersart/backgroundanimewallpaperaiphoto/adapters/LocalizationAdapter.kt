package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding
.ListItemLanguagesBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.models.DummyModelLanguages

class LocalizationAdapter(
    private val languages: ArrayList<DummyModelLanguages>,
    private var selectedItemPosition: Int,
    private val clickListener: OnLanguageChangeListener
) : RecyclerView.Adapter<LocalizationAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemLanguagesBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val language = languages[position]
        holder.bindData(language)
        holder.itemView.isSelected = position == selectedItemPosition
        holder.itemView.setOnClickListener {
            selectedItemPosition = holder.adapterPosition
            notifyDataSetChanged()
            clickListener.onLanguageItemClick(language, position)
        }
    }

    override fun getItemCount(): Int {
        return languages.size
    }

    inner class ViewHolder(val binding: ListItemLanguagesBinding) : RecyclerView.ViewHolder(binding.root) {
        var `object`: DummyModelLanguages? = null
        fun bindData(`object`: DummyModelLanguages) {
            binding.languageName.text = `object`.lan_name
            binding.flag.setImageResource(`object`.flag)
            if (adapterPosition == selectedItemPosition) {
                binding.cardLanguages.background = itemView.context.resources.getDrawable(R.drawable.language_selected_bg)
            } else {
                binding.cardLanguages.background = itemView.context.resources.getDrawable(R.drawable.language_unselected_bg)
            }
        }
    }

    interface OnLanguageChangeListener {
        fun onLanguageItemClick(language: DummyModelLanguages?, position: Int)
    }
}