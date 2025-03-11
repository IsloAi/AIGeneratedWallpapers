package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.livewallpaper

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentLiveWallpaperCategoriesBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.ApiCategoriesNameAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.AdEventListener
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MyApp
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.StringCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatNameResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants.Companion.checkAppOpen
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.GetLiveWallpaperByCategoryViewmodel

class LiveWallpaperCategoriesFragment : Fragment(), AdEventListener {

    private var _binding: FragmentLiveWallpaperCategoriesBinding? = null
    private val binding get() = _binding!!
    private lateinit var myActivity: MainActivity
    private var categoriesJson = ""
    private val myViewModel: GetLiveWallpaperByCategoryViewmodel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLiveWallpaperCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myActivity = activity as MainActivity
        val gson = Gson()
        populateLiveCat()
        val categoryList: ArrayList<CatNameResponse?> =
            gson.fromJson(categoriesJson, object : TypeToken<ArrayList<CatNameResponse>>() {}.type)

        Log.d("LiveWallpaperCategory", "onViewCreated: categoryList: $categoryList")

        binding.recyclerviewAll.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerviewAll.addItemDecoration(RvItemDecore(3, 5, false, 10000))
        val adapter = ApiCategoriesNameAdapter(categoryList, object : StringCallback {
            override fun getStringCall(string: String) {
                if (string == "Roro'Noa Zoro") {
                    myViewModel.getMostUsed("Roro")

                } else {

                    myViewModel.getMostUsed(string)
                }

                if (isAdded) {
                    findNavController().navigate(R.id.liveWallpapersFromCategoryFragment)
                }
            }
        }, myActivity, "live")
        binding.recyclerviewAll.adapter = adapter

        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
            Constants.checkInter = false
            Constants.checkAppOpen = false
        }

    }

    override fun onStart() {
        super.onStart()
        (myActivity.application as MyApp).registerAdEventListener(this)

    }

    private fun populateLiveCat() {
        categoriesJson = """
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
        "cat_name": "Cars",
        "img_url": "https://4kwallpaper-zone.b-cdn.net/livecategoryimages/65f14b5595fdc_Car-1.jpg"
    }
]
"""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAdDismiss() {
        checkAppOpen = true
        Log.e("TAG", "app open dismissed: ")
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