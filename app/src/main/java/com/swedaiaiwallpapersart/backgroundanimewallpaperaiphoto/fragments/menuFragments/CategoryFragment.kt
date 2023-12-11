package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.menuFragments
import ApiCategoriesNameAdapter
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.CommonAdsListenerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.hdwallpaper.Fragments.MainFragment
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentCategoryBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.StringCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatNameResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.BlurView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyCatNameViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyViewModel
import kotlinx.coroutines.launch
import kotlin.random.Random

class CategoryFragment : Fragment() {
   private var _binding: FragmentCategoryBinding? = null

    var adcount = 0
    var totalADs = 0
    private val binding get() = _binding!!
    val myCatNameViewModel: MyCatNameViewModel by viewModels()
    private lateinit var myActivity : MainActivity

    private lateinit var myViewModel: MyViewModel

    val catlist = ArrayList<CatNameResponse?>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View{
        _binding = FragmentCategoryBinding.inflate(inflater,container,false)

       return  binding.root }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadFirstCategory()
        onCustomCreateView()
//        SDKBaseController.getInstance().loadInterstitialAds(requireActivity(),"mainscr_cate_tab_click_item","mainscr_cate_tab_click_item")
    }
    @SuppressLint("SuspiciousIndentation")
    private fun onCustomCreateView() {
        myActivity = activity as MainActivity
        binding.progressBar.visibility = VISIBLE
        binding.gemsText.text = MySharePreference.getGemsValue(requireContext()).toString()
        binding.progressBar.setAnimation(R.raw.main_loading_animation)

        Glide.with(requireContext())
            .asGif()
            .load(R.raw.gems_animaion)
            .into(binding.animationDdd)
        binding.progressBar.visibility = View.GONE
        binding.recyclerviewAll.layoutManager = LinearLayoutManager(requireContext())
        val adapter = ApiCategoriesNameAdapter(catlist,object : StringCallback {
            override fun getStringCall(string: String) {

                SDKBaseController.getInstance().showInterstitialAds(
                    requireActivity(),
                    "mainscr_cate_tab_click_item",
                    "mainscr_cate_tab_click_item",
                    showLoading = true,
                    adsListener = object : CommonAdsListenerAdapter() {
                        override fun onAdsShowFail(errorCode: Int) {
                            Log.e("********ADS", "onAdsShowFail: $errorCode")
                            setFragment(string)
                            //do something
                        }

                        override fun onAdsDismiss() {
                            setFragment(string)
                        }
                    }
                )

            }
        },myActivity)
        binding.recyclerviewAll.adapter = adapter

        myActivity.myCatNameViewModel.wallpaper.observe(viewLifecycleOwner) { wallpapersList ->
            Log.e("TAG", "onCustomCreateView: no data exists" )
           if (wallpapersList?.size!! > 0){
               Log.e("TAG", "onCustomCreateView: data exists" )
               val list = addNullValueInsideArray(wallpapersList)
               adapter.updateData(newData = list)
           }
        }
    }

    private fun loadFirstCategory() {
        myViewModel = ViewModelProvider(this)[MyViewModel::class.java]
        lifecycleScope.launch {
            Log.d("functionCallingTest", "onCreateCustom:  home on create")

            // Observe the LiveData in the ViewModel and update the UI accordingly
            myViewModel.getWallpapers().observe(viewLifecycleOwner) { catResponses ->
                if (catResponses != null) {
                    val randomNumber = Random.nextInt(0, catResponses.size -1)
                    getBitmapFromGlide(catResponses[randomNumber].compressed_image_url!!)
                }
            }
            myViewModel.fetchWallpapers(requireContext(),"IOS",binding.progressBar,true)
        }

    }

    private fun addNullValueInsideArray(data: List<CatNameResponse?>): ArrayList<CatNameResponse?>{

        val firstAdLineThreshold = if (AdConfig.firstAdLineCategoryArt != 0) AdConfig.firstAdLineCategoryArt else 4

        val lineCount = if (AdConfig.lineCountCategoryArt != 0) AdConfig.lineCountCategoryArt else 5
        val newData = arrayListOf<CatNameResponse?>()

            for (i in data.indices){
                if (i > firstAdLineThreshold && (i - firstAdLineThreshold) % (lineCount)  == 0) {
                    newData.add(null)
                    Log.e("******NULL", "addNullValueInsideArray: null "+i )

                }else if (i == firstAdLineThreshold){
                    newData.add(null)
                    Log.e("******NULL", "addNullValueInsideArray: null first "+i )
                }
                Log.e("******NULL", "addNullValueInsideArray: not null "+i )
                Log.e("******NULL", "addNullValueInsideArray: "+data[i] )
                newData.add(data[i])

            }
            Log.e("******NULL", "addNullValueInsideArray:size "+newData.size )
        return newData
    }


    private fun getBitmapFromGlide(url:String){
        Glide.with(requireContext()).asBitmap().load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Log.e("TAG", "onResourceReady: bitmap loaded" )
                    if (isAdded) {
                        val blurImage: Bitmap = BlurView.blurImage(requireContext(), resource!!)!!
                        binding.backImage.setImageBitmap(blurImage)
                    }



                }
                override fun onLoadCleared(placeholder: Drawable?) {
                    Log.e("TAG", "onLoadCleared: cleared" )
                } })
    }


    private fun setFragment(name:String){
       val bundle =  Bundle().apply {
            putString("name",name)
            putString("from","category")

        }
        if (findNavController().currentDestination?.id != R.id.listViewFragment) {

            (requireParentFragment() as MainFragment).findNavController()
                .navigate(R.id.action_mainFragment_to_listViewFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

