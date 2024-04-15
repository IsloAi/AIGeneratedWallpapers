package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.menuFragments
import ApiCategoriesNameAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.CommonAdsListenerAdapter
import com.bmik.android.sdk.listener.keep.IKLoadNativeAdListener
import com.bmik.android.sdk.tracking.SDKTrackingController
import com.bmik.android.sdk.widgets.IkmNativeAdView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentCategoryBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.LiveCategoriesHorizontalAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.StringCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatNameResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.GetLiveWallpaperByCategoryViewmodel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryFragment : Fragment() {
   private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var myActivity : MainActivity

    val catlist = ArrayList<CatNameResponse?>()

    private lateinit var firebaseAnalytics: FirebaseAnalytics


    val catListViewmodel: MyViewModel by activityViewModels()

    private val myViewModel: GetLiveWallpaperByCategoryViewmodel by activityViewModels()

    private var adapter: LiveCategoriesHorizontalAdapter? = null

    var isNavigationInProgress = false

    val TAG = "CATEGORIES"
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

        updateUIWithFetchedData()
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
        },myActivity,"")

        SDKBaseController.getInstance().loadIkmNativeAdView(requireContext(),"mainscr_cate_tab_scroll_view","mainscr_cate_tab_scroll_view",object :
            IKLoadNativeAdListener {
            override fun onAdFailedToLoad(errorCode: Int) {
                Log.e(TAG, "onAdFailedToLoad: "+errorCode )

            }

            override fun onAdLoaded(adsResult: IkmNativeAdView?) {
                if (isAdded && view!= null){
                    adapter?.nativeAdView = adsResult
                    binding.recyclerviewAll.adapter = adapter
                }

            }

        })
        binding.recyclerviewAll.adapter = adapter

        myActivity.myCatNameViewModel.wallpaper.observe(viewLifecycleOwner) { wallpapersList ->
            Log.e("TAG", "onCustomCreateView: no data exists" )
                if (wallpapersList?.size!! > 0){
                    Log.e("TAG", "onCustomCreateView: data exists" )
                    lifecycleScope.launch(Dispatchers.IO) {
                        AdConfig.categoryOrder += "Neon lights"

                        val sortedCategories = sortWallpaperCategories(wallpapersList, AdConfig.categoryOrder)

                        Log.e("TAG", "onCustomCreateView: "+sortedCategories )

                        val list = addNullValueInsideArray(sortedCategories)

                        withContext(Dispatchers.Main){
                            adapter.updateData(newData = list)
                        }
                    }

                }
        }


        binding.more.setOnClickListener {
            findNavController().navigate(R.id.liveWallpaperCategoriesFragment)
        }
    }


    private fun updateUIWithFetchedData() {
        val gson = Gson()
        val categoryList: ArrayList<CatNameResponse> = gson.fromJson(categoriesJson, object : TypeToken<ArrayList<CatNameResponse>>() {}.type)


        adapter = LiveCategoriesHorizontalAdapter(categoryList, object : StringCallback {
                override fun getStringCall(string: String) {
                    myViewModel.getMostUsed(string)

                    if (isAdded){
                        sendTracking("categorymainscr_click",Pair("categorymainscr", "$string Live"))
                    }

                    SDKBaseController.getInstance().showInterstitialAds(
                        requireActivity(),
                        "mainscr_cate_tab_click_item",
                        "mainscr_cate_tab_click_item",
                        showLoading = true,
                        adsListener = object : CommonAdsListenerAdapter() {
                            override fun onAdsShowFail(errorCode: Int) {
                                Log.e("********ADS", "onAdsShowFail: $errorCode")

//                                setFragment(string)
                                findNavController().navigate(R.id.liveWallpapersFromCategoryFragment)
                                //do something
                            }

                            override fun onAdsDismiss() {
                                findNavController().navigate(R.id.liveWallpapersFromCategoryFragment)
                            }
                        }
                    )

                }
            }, myActivity)

        binding.recyclerviewTrending.adapter = adapter
    }

    val categoriesJson = """
[
    {
        "cat_name": "Heteroclite",
        "img_url": "http://edecator.com/wallpaperApp/Livecategoryimages/65f28a9b7cd7f_Others.jpg"
    },
    {
        "cat_name": "Love",
        "img_url": "http://edecator.com/wallpaperApp/Livecategoryimages/65f171774585e_Love-8.jpg"
    },
    {
        "cat_name": "Space",
        "img_url": "http://edecator.com/wallpaperApp/Livecategoryimages/65f170f90444d_Space-2.jpg"
    },
    {
        "cat_name": "Naruto",
        "img_url": "http://edecator.com/wallpaperApp/Livecategoryimages/65f1701bbc604_Naruto.jpg"
    },
    {
        "cat_name": "Nature",
        "img_url": "http://edecator.com/wallpaperApp/Livecategoryimages/65f16eacc3109_Nature.jpg"
    },
    {
        "cat_name": "Tech",
        "img_url": "http://edecator.com/wallpaperApp/Livecategoryimages/65f15d4ce4285_Tech.jpg"
    },
    {
        "cat_name": "Robotic",
        "img_url": "http://edecator.com/wallpaperApp/Livecategoryimages/65f159494fce7_Robot.jpg"
    },
    {
        "cat_name": "Roro'Noa Zoro",
        "img_url": "http://edecator.com/wallpaperApp/Livecategoryimages/65f1546f1cbd1_Roronoa-Zoro.jpg"
    },
    {
        "cat_name": "Anime",
        "img_url": "http://edecator.com/wallpaperApp/Livecategoryimages/65f1530aa7599_256.jpg"
    },
    {
        "cat_name": "Dragon Ball Z",
        "img_url": "http://edecator.com/wallpaperApp/Livecategoryimages/65f151c571610_Dragon-Ball-Z.jpg"
    },
    {
        "cat_name": "Cars",
        "img_url": "http://edecator.com/wallpaperApp/Livecategoryimages/65f14b5595fdc_Car-1.jpg"
    }
]
"""

    fun sortWallpaperCategories(categories: List<CatNameResponse>, order: List<String>): List<CatNameResponse> {
        // Create a map to store the order of categories based on their names
        Log.e("TAG", "sortWallpaperCategories: "+categories )
        Log.e("TAG", "sortWallpaperCategories: "+order )
        order.forEach {
            Log.e("TAG", "sortWallpaperCategories: "+it )
        }
        val orderMap = order.withIndex().associate { it.value.trim() to it.index }

        // Sort the categories based on the order specified in the map
        return categories.sortedWith(compareBy { orderMap[it.cat_name] ?: Int.MAX_VALUE })
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
        sendTracking("categorymainscr_click",Pair("categorymainscr", name))
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
            sendTracking("screen_active",Pair("action_type", "Tab"), Pair("action_name", "MainScr_CateTab_View"))
        }

        if (isAdded){
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Categories Screen")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, javaClass.simpleName)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }
    }

    private fun sendTracking(
        eventName: String,
        vararg param: Pair<String, String?>
    )
    {
        SDKTrackingController.trackingAllApp(requireContext(), eventName, *param)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

