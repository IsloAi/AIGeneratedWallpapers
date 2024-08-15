package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.livewallpaper

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.ApiCategoriesNameAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ikame.android.sdk.data.dto.pub.IKAdError
import com.ikame.android.sdk.format.intertial.IKInterstitialAd
import com.ikame.android.sdk.listener.pub.IKLoadAdListener
import com.ikame.android.sdk.listener.pub.IKShowAdListener
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentLiveWallpaperCategoriesBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.AdEventListener
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MyApp
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.StringCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatNameResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants.Companion.checkAppOpen
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.ForegroundWorker.Companion.TAG
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.GetLiveWallpaperByCategoryViewmodel

class LiveWallpaperCategoriesFragment : Fragment(), AdEventListener {

    private var _binding:FragmentLiveWallpaperCategoriesBinding ?= null
    private val binding get() = _binding!!
    private lateinit var myActivity : MainActivity


    private val myViewModel: GetLiveWallpaperByCategoryViewmodel by activityViewModels()

    val interAd = IKInterstitialAd()

//    var checkAppOpen = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLiveWallpaperCategoriesBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myActivity = activity as MainActivity
        val gson = Gson()
        val categoryList: ArrayList<CatNameResponse?> = gson.fromJson(categoriesJson, object : TypeToken<ArrayList<CatNameResponse>>() {}.type)

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
        binding.recyclerviewAll.layoutManager = GridLayoutManager(requireContext(),3)
        binding.recyclerviewAll.addItemDecoration(RvItemDecore(3,5  ,false,10000))
        val adapter = ApiCategoriesNameAdapter(categoryList,object : StringCallback {
            override fun getStringCall(string: String) {
//                catListViewmodel.getAllCreations(string)
                if (string == "Roro'Noa Zoro"){
                    myViewModel.getMostUsed("Roro")

                }else{

                    myViewModel.getMostUsed(string)
                }

                if (AdConfig.ISPAIDUSER){
                    if (isAdded){
                        findNavController().navigate(R.id.liveWallpapersFromCategoryFragment)
                    }
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
                        showInterAd()
                    }
//                    if (AdConfig.avoidPolicyOpenAdInter == 1 && checkAppOpen){
//                        if (isAdded){
//                            checkAppOpen = false
//                            findNavController().navigate(R.id.liveWallpapersFromCategoryFragment)
//                            Log.e("TAG", "app open showed: ", )
//                        }
//                    }else{
//                        showInterAd()
//                    }
                }
            }
        },myActivity,"live")
        binding.recyclerviewAll.adapter = adapter


        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
            Constants.checkInter = false
            Constants.checkAppOpen = false
        }
    }

    private fun showInterAd() {
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
                    Constants.checkInter = true
                    findNavController().navigate(R.id.liveWallpapersFromCategoryFragment)
                }
            }
        )
    }

    override fun onStart() {
        super.onStart()
        (myActivity.application as MyApp).registerAdEventListener(this)

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onAdDismiss() {
        checkAppOpen = true
        Log.e("TAG", "app open dismissed: ", )
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