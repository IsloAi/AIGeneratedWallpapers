package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.adaptersIG

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding
.PromptListPromptWordItemBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.interfaces.GetPromptDetails
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.models.Prompts

class PromptListAdapter(private val arrayList: ArrayList<Prompts>,
    private val getPromptDetails: GetPromptDetails
    ):RecyclerView.Adapter<PromptListAdapter.ViewHolder>() {
    private var selectedItemPosition = RecyclerView.NO_POSITION
    class ViewHolder(val binding: PromptListPromptWordItemBinding):RecyclerView.ViewHolder(binding.root)

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val binding = PromptListPromptWordItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }
    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val model = arrayList[position]
        val mainContainer = holder.binding.mainContainer
        holder.binding.title.text = model.prompt

        if (selectedItemPosition == position) {
            mainContainer.setBackgroundResource(R.drawable.prompt_selector)
            holder.binding.title.maxLines = 4
            holder.binding.extendButton.visibility = GONE
        } else {
            mainContainer.setBackgroundResource(R.drawable.unselector)
            holder.binding.title.maxLines = 1
            holder.binding.extendButton.visibility = VISIBLE
        }
        mainContainer.setOnClickListener {
//            getPromptDetails.getPrompt(arrayList,mainContainer,position)
          getPromptDetails.getPrompt(model.prompt)
            if (holder.binding.title.maxLines == 4){
                holder.binding.title.maxLines = 1
                holder.binding.extendButton.visibility = VISIBLE
            }else{
                holder.binding.title.maxLines = 4
                holder.binding.extendButton.visibility = GONE
            }
            val previousSelected = selectedItemPosition
            selectedItemPosition = position
            notifyItemChanged(previousSelected)
            notifyItemChanged(selectedItemPosition)
        }
    }
    override fun getItemCount() = arrayList.size
}
