package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.fragmentsIG

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding
.CatListPromptWordItemBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding
.ListItemMyCreationsBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.interfaces.GetbackOfID
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.GetResponseIGEntity


class CreationsAdapter(private val arrayList: List<GetResponseIGEntity>,
                       var isSelectionMode:Boolean,
                       private val creationSelectionInterface: CreationSelectionInterface
): RecyclerView.Adapter<CreationsAdapter.ViewHolder>() {

    private val selectedItems = mutableListOf<Int>()

    private val selectedList = ArrayList<GetResponseIGEntity>()
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
        val isSelected = selectedItems.contains(position)
        if(model.output != null){
            Glide.with(context!!).load(model.output?.get(0)).into(holder.binding.wallpaper)
        }
        if (isSelectionMode){
            holder.binding.selectImage.visibility = View.VISIBLE
        }else{
            holder.binding.selectImage.visibility = View.GONE
        }


        if (isSelected){
            holder.binding.selectImage.setImageResource(R.drawable.creation_selected)
        }else{
            holder.binding.selectImage.setImageResource(R.drawable.creation_unselected)
        }

        holder.itemView.setOnClickListener {
            if (isSelectionMode){
                toggleSelection(position,model)
                creationSelectionInterface.setOnClick(position,model)
                creationSelectionInterface.viewMyCreations(position,selectedList)
            }else{
                creationSelectionInterface.setOnClick(position,model)
                creationSelectionInterface.viewMyCreations(model.id,selectedList)
            }
        }



    }


    fun getSelectedlist():ArrayList<GetResponseIGEntity>{
        return selectedList
    }


    private fun toggleSelection(position: Int,getResponseIGEntity: GetResponseIGEntity) {
        if (selectedItems.contains(position)) {
            if (selectedList.contains(getResponseIGEntity)){
                selectedList.remove(getResponseIGEntity)
            }
            selectedItems.remove(position)
        } else {
            selectedList.add(getResponseIGEntity)
            selectedItems.add(position)
        }
        notifyItemChanged(position)
    }

    fun updateSelectionMode(selection:Boolean){
        isSelectionMode = selection
        selectedItems.clear()
        notifyDataSetChanged()
    }


    fun selectAll() {
        selectedItems.clear()
        selectedList.addAll(arrayList)
        selectedItems.addAll(arrayList.indices)
        notifyDataSetChanged()
    }

    // Method to unselect all items
    fun unselectAll() {
        selectedList.clear()
        selectedItems.clear()
        notifyDataSetChanged()
    }
    override fun getItemCount() = arrayList.size

    interface CreationSelectionInterface {
        fun setOnClick(id:Int,getResponseIGEntity: GetResponseIGEntity)

        fun viewMyCreations(id:Int,list: ArrayList<GetResponseIGEntity>)
    }
}