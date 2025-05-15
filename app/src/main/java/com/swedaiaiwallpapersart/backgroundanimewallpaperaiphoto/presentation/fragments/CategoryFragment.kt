package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentCategoryBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.CategoryApiModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.activity.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.adapters.ApiCategoriesNameAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.adapters.LiveCategoriesHorizontalAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.Constants
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.StringCallback

class CategoryFragment : Fragment() {
    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var myActivity: MainActivity
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private var adapter: LiveCategoriesHorizontalAdapter? = null
    private var catList = ArrayList<CategoryApiModel?>()
    /*

    val catListViewmodel: MyViewModel by activityViewModels()
    private val myViewModel: GetLiveWallpaperByCategoryViewmodel by activityViewModels()
    var isNavigationInProgress = false
    val TAG = "CATEGORIES"*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        onCustomCreateView()
    }

    private fun onCustomCreateView() {
        myActivity = activity as MainActivity
        binding.progressBar.visibility = VISIBLE
        binding.progressBar.setAnimation(R.raw.main_loading_animation)

        updateUIWithFetchedData()
        catList = AdConfig.categories as ArrayList<CategoryApiModel?>
        binding.progressBar.visibility = View.GONE
        binding.recyclerviewAll.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerviewAll.addItemDecoration(RvItemDecore(3, 5, false, 10000))
        val adapter = ApiCategoriesNameAdapter(catList, object : StringCallback {
            override fun getStringCall(string: String) {
                setFragment(string)
            }
        }, myActivity, "")

        binding.recyclerviewAll.adapter = adapter
        binding.more.setOnClickListener {
            findNavController().navigate(R.id.liveWallpaperCategoriesFragment)
        }
    }

    private fun updateUIWithFetchedData() {
        val gson = Gson()
        val categoryList: ArrayList<CategoryApiModel> =
            gson.fromJson(categoriesJson, object : TypeToken<ArrayList<CategoryApiModel>>() {}.type)
        adapter = LiveCategoriesHorizontalAdapter(categoryList, object : StringCallback {
            override fun getStringCall(string: String) {
                if (AdConfig.ISPAIDUSER) {
                    findNavController().navigate(R.id.liveWallpapersFromCategoryFragment)
                } else {

                    if (isAdded) {
                        Constants.checkInter = false
                        findNavController().navigate(R.id.liveWallpapersFromCategoryFragment)
                    }

                }


            }
        }, myActivity)

        binding.recyclerviewTrending.adapter = adapter
    }

    private fun setFragment(name: String) {
        Log.d("Categories", "setFragment:string: $name ")
        if (AdConfig.ISPAIDUSER) {
            val bundle = Bundle().apply {
                putString("name", name)
                putString("from", "category")
            }
            if (findNavController().currentDestination?.id != R.id.listViewFragment) {
                findNavController().navigate(R.id.listViewFragment, bundle)
            }
        } else {
            /*if (AdClickCounter.shouldShowAd()) {
                MaxInterstitialAds.showInterstitialAd(requireActivity(), object : MaxAdListener {
                    override fun onAdLoaded(p0: MaxAd) {}

                    override fun onAdDisplayed(p0: MaxAd) {}

                    override fun onAdHidden(p0: MaxAd) {
                        val bundle = Bundle().apply {
                            putString("name", name)
                            putString("from", "category")
                        }
                        if (findNavController().currentDestination?.id != R.id.listViewFragment) {
                            findNavController().navigate(R.id.listViewFragment, bundle)
                        }
                    }

                    override fun onAdClicked(p0: MaxAd) {}

                    override fun onAdLoadFailed(p0: String, p1: MaxError) {}

                    override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {}
                })
            } else {
                AdClickCounter.increment()
                val bundle = Bundle().apply {
                    putString("name", name)
                    putString("from", "category")
                }
                if (findNavController().currentDestination?.id != R.id.listViewFragment) {
                    findNavController().navigate(R.id.listViewFragment, bundle)
                }
            }*/
        }
    }

    override fun onResume() {
        super.onResume()
        if (isAdded) {
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

    private val categoriesJson = """
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

