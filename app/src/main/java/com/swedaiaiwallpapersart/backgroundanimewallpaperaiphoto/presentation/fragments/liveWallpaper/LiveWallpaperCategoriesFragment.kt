package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.fragments.liveWallpaper

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentLiveWallpaperCategoriesBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.CategoryApiModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.activity.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.adapters.ApiCategoriesNameAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.Constants
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.StringCallback

class LiveWallpaperCategoriesFragment : Fragment() {

    private var _binding: FragmentLiveWallpaperCategoriesBinding? = null
    private val binding get() = _binding!!
    private lateinit var myActivity: MainActivity
    private var categoriesJson = ""

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
        val categoryList: ArrayList<CategoryApiModel?> =
            gson.fromJson(categoriesJson, object : TypeToken<ArrayList<CategoryApiModel>>() {}.type)
        Log.d("LiveWallpaperCategory", "onViewCreated: categoryList: $categoryList")
        binding.recyclerviewAll.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerviewAll.addItemDecoration(RvItemDecore(3, 5, false, 10000))
        val adapter = ApiCategoriesNameAdapter(categoryList, object : StringCallback {
            override fun getStringCall(string: String) {
                if (isAdded) {
                    Bundle().apply {
                        putString("from", "LiveCategory")
                        putString("wall", string)
                        findNavController().navigate(R.id.liveWallpapersFromCategoryFragment, this)
                    }

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

    private fun populateLiveCat() {
        categoriesJson = """
[
  {
    "cat_name": "Faces",
    "img_url": "https://4kwallpaper-zone.b-cdn.net/livecategoryimages/6721ed0151a46_smilies.jpg"
  },
  {
    "cat_name": "Heteroclite",
    "img_url": "https://4kwallpaper-zone.b-cdn.net/livecategoryimages/6721d0d10d47d_hetroclite 2.jpg"
  },
  {
    "cat_name": "Love",
    "img_url": "https://4kwallpaper-zone.b-cdn.net/livecategoryimages/6721cfdcd8c61_Love.jpg"
  },
  {
    "cat_name": "Space",
    "img_url": "https://4kwallpaper-zone.b-cdn.net/livecategoryimages/6721d0ea7123b_space.jpg"
  },

  {
    "cat_name": "Nature",
    "img_url": "https://4kwallpaper-zone.b-cdn.net/livecategoryimages/6721d07753306_nature.jpg"
  },
  {
    "cat_name": "Tech",
    "img_url": "https://4kwallpaper-zone.b-cdn.net/livecategoryimages/6721c8c43449f_tech 2.jpg"
  },
  {
    "cat_name": "Robotic",
    "img_url": "https://4kwallpaper-zone.b-cdn.net/livecategoryimages/67206957122f7_robotic 2.jpg"
  },

  {
    "cat_name": "Cars",
    "img_url": "https://4kwallpaper-zone.b-cdn.net/livecategoryimages/672067d7adc91_cars.jpg"
  }
]
"""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}