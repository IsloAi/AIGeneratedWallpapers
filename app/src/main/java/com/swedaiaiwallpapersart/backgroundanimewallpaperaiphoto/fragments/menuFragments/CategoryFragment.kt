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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.CommonAdsListenerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.hdwallpaper.Fragments.MainFragment
import com.google.firebase.analytics.FirebaseAnalytics
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
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.RvItemDecore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class CategoryFragment : Fragment() {
   private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var myActivity : MainActivity

    val catlist = ArrayList<CatNameResponse?>()

    private lateinit var firebaseAnalytics: FirebaseAnalytics


    val catListViewmodel: MyViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View{
        _binding = FragmentCategoryBinding.inflate(inflater,container,false)

       return  binding.root }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        onCustomCreateView()
    }
    @SuppressLint("SuspiciousIndentation")
    private fun onCustomCreateView() {
        myActivity = activity as MainActivity
        binding.progressBar.visibility = VISIBLE
        binding.progressBar.setAnimation(R.raw.main_loading_animation)

        binding.progressBar.visibility = View.GONE
        binding.recyclerviewAll.layoutManager = GridLayoutManager(requireContext(),3)
        binding.recyclerviewAll.addItemDecoration(RvItemDecore(3,5  ,false,10000))
        val adapter = ApiCategoriesNameAdapter(catlist,object : StringCallback {
            override fun getStringCall(string: String) {
                catListViewmodel.getAllCreations(string)

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
               lifecycleScope.launch(Dispatchers.IO) {
                   val list = addNullValueInsideArray(wallpapersList)

                   withContext(Dispatchers.Main){
                       adapter.updateData(newData = list)

                   }
               }

           }
        }
    }


    suspend fun addNullValueInsideArray(data: List<CatNameResponse?>): ArrayList<CatNameResponse?>{
        return withContext(Dispatchers.IO){
            val firstAdLineThreshold = if (AdConfig.firstAdLineCategoryArt != 0) AdConfig.firstAdLineCategoryArt else 4
            val firstLine = firstAdLineThreshold * 3

            val lineCount = if (AdConfig.lineCountCategoryArt != 0) AdConfig.lineCountCategoryArt else 5
            val lineC = lineCount * 3
            val newData = arrayListOf<CatNameResponse?>()

            for (i in data.indices){
                if (i > firstLine && (i - firstLine) % (lineC)  == 0) {
                    newData.add(null)
                    Log.e("******NULL", "addNullValueInsideArray: null "+i )

                }else if (i == firstLine){
                    newData.add(null)
                    Log.e("******NULL", "addNullValueInsideArray: null first "+i )
                }
                Log.e("******NULL", "addNullValueInsideArray: not null "+i )
                Log.e("******NULL", "addNullValueInsideArray: "+data[i] )
                newData.add(data[i])

            }
            Log.e("******NULL", "addNullValueInsideArray:size "+newData.size )
             newData
        }


    }


    private fun setFragment(name:String){
       val bundle =  Bundle().apply {
            putString("name",name)
            putString("from","category")

        }
        if (findNavController().currentDestination?.id != R.id.listViewFragment) {

            findNavController().navigate(R.id.listViewFragment, bundle)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isAdded){
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Categories Screen")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, javaClass.simpleName)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

