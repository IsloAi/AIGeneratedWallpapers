package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.livewallpaper

import ApiCategoriesNameAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.CommonAdsListenerAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentLiveWallpaperCategoriesBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.StringCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatNameResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.GetLiveWallpaperByCategoryViewmodel

class LiveWallpaperCategoriesFragment : Fragment() {

    private var _binding:FragmentLiveWallpaperCategoriesBinding ?= null
    private val binding get() = _binding!!
    private lateinit var myActivity : MainActivity


    private val myViewModel: GetLiveWallpaperByCategoryViewmodel by activityViewModels()

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

        binding.recyclerviewAll.layoutManager = GridLayoutManager(requireContext(),3)
        binding.recyclerviewAll.addItemDecoration(RvItemDecore(3,5  ,false,10000))
        val adapter = ApiCategoriesNameAdapter(categoryList,object : StringCallback {
            override fun getStringCall(string: String) {
//                catListViewmodel.getAllCreations(string)
                myViewModel.getMostUsed(string)

                SDKBaseController.getInstance().showInterstitialAds(
                    requireActivity(),
                    "mainscr_cate_tab_click_item",
                    "mainscr_cate_tab_click_item",
                    showLoading = true,
                    adsListener = object : CommonAdsListenerAdapter() {
                        override fun onAdsShowFail(errorCode: Int) {
                            Log.e("********ADS", "onAdsShowFail: $errorCode")
                            findNavController().navigate(R.id.liveWallpapersFromCategoryFragment)
                            //do something
                        }

                        override fun onAdsDismiss() {
//                            setFragment(string)
                            findNavController().navigate(R.id.liveWallpapersFromCategoryFragment)
                        }
                    }
                )

            }
        },myActivity,"live")
        binding.recyclerviewAll.adapter = adapter


        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    val categoriesJson = """
[
    {
        "cat_name": "Heteroclite",
        "img_url": "https://edecator.com/wallpaperApp/Livecategoryimages/65f28a9b7cd7f_Others.jpg"
    },
    {
        "cat_name": "Love",
        "img_url": "https://edecator.com/wallpaperApp/Livecategoryimages/65f171774585e_Love-8.jpg"
    },
    {
        "cat_name": "Space",
        "img_url": "https://edecator.com/wallpaperApp/Livecategoryimages/65f170f90444d_Space-2.jpg"
    },
    {
        "cat_name": "Naruto",
        "img_url": "https://edecator.com/wallpaperApp/Livecategoryimages/65f1701bbc604_Naruto.jpg"
    },
    {
        "cat_name": "Nature",
        "img_url": "https://edecator.com/wallpaperApp/Livecategoryimages/65f16eacc3109_Nature.jpg"
    },
    {
        "cat_name": "Tech",
        "img_url": "https://edecator.com/wallpaperApp/Livecategoryimages/65f15d4ce4285_Tech.jpg"
    },
    {
        "cat_name": "Robotic",
        "img_url": "https://edecator.com/wallpaperApp/Livecategoryimages/65f159494fce7_Robot.jpg"
    },
    {
        "cat_name": "Roro'Noa Zoro",
        "img_url": "https://edecator.com/wallpaperApp/Livecategoryimages/65f1546f1cbd1_Roronoa-Zoro.jpg"
    },
    {
        "cat_name": "Anime",
        "img_url": "https://edecator.com/wallpaperApp/Livecategoryimages/65f1530aa7599_256.jpg"
    },
    {
        "cat_name": "Dragon Ball Z",
        "img_url": "https://edecator.com/wallpaperApp/Livecategoryimages/65f151c571610_Dragon-Ball-Z.jpg"
    },
    {
        "cat_name": "Cars",
        "img_url": "https://edecator.com/wallpaperApp/Livecategoryimages/65f14b5595fdc_Car-1.jpg"
    }
]
"""

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}