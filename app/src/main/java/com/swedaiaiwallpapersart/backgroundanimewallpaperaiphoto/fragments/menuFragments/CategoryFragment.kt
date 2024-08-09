package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.menuFragments
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.ApiCategoriesNameAdapter
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
import com.ikame.android.sdk.IKSdkController
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ikame.android.sdk.data.dto.pub.IKAdError
import com.ikame.android.sdk.format.intertial.IKInterstitialAd
import com.ikame.android.sdk.listener.pub.IKLoadAdListener
import com.ikame.android.sdk.listener.pub.IKLoadDisplayAdViewListener
import com.ikame.android.sdk.listener.pub.IKShowAdListener
import com.ikame.android.sdk.tracking.IKTrackingHelper
import com.ikame.android.sdk.widgets.IkmDisplayWidgetAdView
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentCategoryBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.LiveCategoriesHorizontalAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.AdEventListener
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MyApp
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.StringCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatNameResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants.Companion.checkAppOpen
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.GetLiveWallpaperByCategoryViewmodel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryFragment : Fragment(), AdEventListener {
   private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var myActivity : MainActivity

    val catlist = ArrayList<CatNameResponse?>()

    private lateinit var firebaseAnalytics: FirebaseAnalytics


    val catListViewmodel: MyViewModel by activityViewModels()

    private val myViewModel: GetLiveWallpaperByCategoryViewmodel by activityViewModels()

    private var adapter: LiveCategoriesHorizontalAdapter? = null

    var isNavigationInProgress = false

    val interAd = IKInterstitialAd()

//    var checkAppOpen = false


    val TAG = "CATEGORIES"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View{
        _binding = FragmentCategoryBinding.inflate(inflater,container,false)

       return  binding.root }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        interAd.attachLifecycle(this.lifecycle)
// Load ad with a specific screen ID, considered as a unitId
        interAd.loadAd("mainscr_cate_tab_click_item", object : IKLoadAdListener {
            override fun onAdLoaded() {
                // Ad loaded successfully
            }
            override fun onAdLoadFail(error: IKAdError) {
                // Handle ad load failure
            }
        })
        onCustomCreateView()
    }

    override fun onStart() {
        super.onStart()
        (myActivity.application as MyApp).registerAdEventListener(this)

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

                if (AdConfig.ISPAIDUSER){
                    setFragment(string)
                }else{
                    var shouldShowInterAd = true

                    if (AdConfig.avoidPolicyRepeatingInter == 1 && Constants.checkInter) {
                        if (isAdded) {
                            Constants.checkInter = false
                            setFragment(string)
                            shouldShowInterAd = false // Skip showing the ad for this action
                        }
                    }

                    if (AdConfig.avoidPolicyOpenAdInter == 1 && checkAppOpen) {
                        if (isAdded) {
                            checkAppOpen = false
                            setFragment(string)
                            Log.e(TAG, "app open showed")
                            shouldShowInterAd = false // Skip showing the ad for this action
                        }
                    }

                    if (shouldShowInterAd) {
                        showInterAd(string) // Show the interstitial ad if no conditions were met
                    }
//                    if (AdConfig.avoidPolicyOpenAdInter == 1 && checkAppOpen){
//                        if (isAdded){
//                            checkAppOpen = false
//                            setFragment(string)
//                            Log.e(TAG, "app open showed: ", )
//                        }
//                    }else{
//                        showInterAd(string)
//                    }


                }



            }
        },myActivity,"")

        IKSdkController.loadNativeDisplayAd("mainscr_cate_tab_scroll_view", object :
            IKLoadDisplayAdViewListener {
            override fun onAdLoaded(adObject: IkmDisplayWidgetAdView?) {
                if (isAdded && view!= null){
                    adapter?.nativeAdView = adObject
                    binding.recyclerviewAll.adapter = adapter
                }
            }

            override fun onAdLoadFail(error: IKAdError) {
                // Handle ad load failure with view object
            }
        })
        binding.recyclerviewAll.adapter = adapter

        myActivity.myCatNameViewModel.wallpaper.observe(viewLifecycleOwner) { wallpapersList ->
            Log.e("TAG", "onCustomCreateView: no data exists" )
                if (wallpapersList?.size!! > 0){
                    Log.e("TAG", "onCustomCreateView: data exists" )
                    lifecycleScope.launch(Dispatchers.IO) {


                        Log.e("TAG", "onCustomCreateView: "+wallpapersList )

                        val list = if (!AdConfig.ISPAIDUSER){
                            wallpapersList.shuffled()
                        }else{
                            addNullValueInsideArray(wallpapersList.shuffled())
                        }

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

    private fun showInterAd(string: String) {
        interAd.showAd(
            requireActivity(),
            "mainscr_cate_tab_click_item",
            adListener = object : IKShowAdListener {
                override fun onAdsShowFail(error: IKAdError) {
                    if (isAdded) {
                        setFragment(string)
                    }
                }

                override fun onAdsDismiss() {
                    if (isAdded) {
                        Constants.checkInter = true
                        setFragment(string)
                    }
                }
            }
        )
    }


    private fun updateUIWithFetchedData() {
        val gson = Gson()
        val categoryList: ArrayList<CatNameResponse> = gson.fromJson(categoriesJson, object : TypeToken<ArrayList<CatNameResponse>>() {}.type)


        adapter = LiveCategoriesHorizontalAdapter(categoryList, object : StringCallback {
                override fun getStringCall(string: String) {
                    if (string == "Roro'Noa Zoro"){
                        myViewModel.getMostUsed("Roro")
                    }else{
                        myViewModel.getMostUsed(string)
                    }

                    if (isAdded){
                        sendTracking("categorymainscr_click",Pair("categorymainscr", "$string Live"))
                    }

                    if (AdConfig.ISPAIDUSER){
                        findNavController().navigate(R.id.liveWallpapersFromCategoryFragment)
                    }else{
                        var shouldShowInterAd = true

                        if (AdConfig.avoidPolicyRepeatingInter == 1 && Constants.checkInter) {
                            if (isAdded) {
                                Constants.checkInter = false
                                findNavController().navigate(R.id.liveWallpapersFromCategoryFragment)
                                shouldShowInterAd = false // Skip showing the ad for this action
                            }
                        }

                        if (AdConfig.avoidPolicyOpenAdInter == 1 && checkAppOpen) {
                            if (isAdded) {
                                checkAppOpen = false
                                findNavController().navigate(R.id.liveWallpapersFromCategoryFragment)
                                Log.e(TAG, "app open showed")
                                shouldShowInterAd = false // Skip showing the ad for this action
                            }
                        }

                        if (shouldShowInterAd) {
                            showIntersAd() // Show the interstitial ad if no conditions were met
                        }
//                        if (AdConfig.avoidPolicyOpenAdInter == 1 && checkAppOpen){
//                            if (isAdded){
//                                checkAppOpen = false
//                                findNavController().navigate(R.id.liveWallpapersFromCategoryFragment)
//                                Log.e(TAG, "app open showed: ", )
//                            }
//                        }else{
//                            showIntersAd()
//                        }


                    }



                }
            }, myActivity)

        binding.recyclerviewTrending.adapter = adapter
    }

    private fun showIntersAd() {
        interAd.showAd(
            requireActivity(),
            "mainscr_cate_tab_click_item",
            adListener = object : IKShowAdListener {
                override fun onAdsShowFail(error: IKAdError) {
                    if (isAdded) {
                        findNavController().navigate(R.id.liveWallpapersFromCategoryFragment)
                    }
                }

                override fun onAdsDismiss() {
                    if (isAdded) {
                        Constants.checkInter = true
                        findNavController().navigate(R.id.liveWallpapersFromCategoryFragment)
                    }
                }
            }
        )
    }

    val categoriesJson = """
[
    {
        "cat_name": "Heteroclite",
        "img_url": "https://4kwallpaper-zone.b-cdn.net/livecategoryimages/65f28a9b7cd7f_Others.jpg"
    },
    {
        "cat_name": "Love",
        "img_url": "https://4kwallpaper-zone.b-cdn.net/livecategoryimages/65f171774585e_Love-8.jpg"
    },
    {
        "cat_name": "Space",
        "img_url": "https://4kwallpaper-zone.b-cdn.net/livecategoryimages/65f170f90444d_Space-2.jpg"
    },
    {
        "cat_name": "Naruto",
        "img_url": "https://4kwallpaper-zone.b-cdn.net/livecategoryimages/65f1701bbc604_Naruto.jpg"
    },
    {
        "cat_name": "Nature",
        "img_url": "https://4kwallpaper-zone.b-cdn.net/livecategoryimages/65f16eacc3109_Nature.jpg"
    },
    {
        "cat_name": "Tech",
        "img_url": "https://4kwallpaper-zone.b-cdn.net/livecategoryimages/65f15d4ce4285_Tech.jpg"
    },
    {
        "cat_name": "Robotic",
        "img_url": "https://4kwallpaper-zone.b-cdn.net/livecategoryimages/65f159494fce7_Robot.jpg"
    },
    {
        "cat_name": "Roro'Noa Zoro",
        "img_url": "https://4kwallpaper-zone.b-cdn.net/livecategoryimages/65f1546f1cbd1_Roronoa-Zoro.jpg"
    },
    {
        "cat_name": "Anime",
        "img_url": "https://4kwallpaper-zone.b-cdn.net/livecategoryimages/65f1530aa7599_256.jpg"
    },
    {
        "cat_name": "Dragon Ball Z",
        "img_url": "https://4kwallpaper-zone.b-cdn.net/livecategoryimages/65f151c571610_Dragon-Ball-Z.jpg"
    },
    {
        "cat_name": "Cars",
        "img_url": "https://4kwallpaper-zone.b-cdn.net/livecategoryimages/65f14b5595fdc_Car-1.jpg"
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
        IKTrackingHelper.sendTracking( eventName, *param)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onAdDismiss() {
        checkAppOpen = true
        Log.e(TAG, "app open dismissed: ", )
    }

    override fun onAdLoading() {

    }

    override fun onAdsShowTimeout() {

    }

    override fun onShowAdComplete() {

    }

    override fun onShowAdFail() {

    }
}

