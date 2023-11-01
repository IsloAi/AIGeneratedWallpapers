import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.CatNameListBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.StringCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatNameResponse

class ApiCategoriesNameAdapter(
    private val arrayList: ArrayList<CatNameResponse>,
    private val stringCallback: StringCallback,
) : RecyclerView.Adapter<ApiCategoriesNameAdapter.ViewHolderContainerItem>() {

    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderContainerItem {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        val binding = CatNameListBinding.inflate(inflater, parent, false)
        return ViewHolderContainerItem(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderContainerItem, position: Int) {
        val model = arrayList[position]
        holder.bind(model)
    }

    override fun getItemCount(): Int = arrayList.size

    inner class ViewHolderContainerItem(private val binding: CatNameListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: CatNameResponse) {
            binding.loading.visibility = View.VISIBLE
            binding.loading.setAnimation(R.raw.main_loading_animation)
            binding.catName.text = model.cat_name
            Glide.with(context!!)
                .load(model.img_url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d("onLoadFailed", "onLoadFailed: ")
                        binding.loading.setAnimation(R.raw.no_data_image_found)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.loading.visibility = View.INVISIBLE
                        Log.d("onLoadFailed", "onResourceReady: ")
                        return false
                    }
                })
                .into(binding.catIconImage)

            binding.catIconImage.setOnClickListener {
                stringCallback.getStringCall(model.cat_name!!)
            }
        }
    }

    fun updateData(newData:List<CatNameResponse>){
        arrayList.clear()
        arrayList.addAll(newData)
        notifyDataSetChanged()

    }
}
