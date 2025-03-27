package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.menuFragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentCategoryBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.ApiCategoriesNameAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.LiveCategoriesHorizontalAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.AdEventListener
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MaxAD
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MaxInterstitialAds
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

    private lateinit var myActivity: MainActivity

    val catlist = ArrayList<CatNameResponse?>()

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    val catListViewmodel: MyViewModel by activityViewModels()

    private val myViewModel: GetLiveWallpaperByCategoryViewmodel by activityViewModels()

    private var adapter: LiveCategoriesHorizontalAdapter? = null

    var isNavigationInProgress = false

    val TAG = "CATEGORIES"
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
        binding.recyclerviewAll.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerviewAll.addItemDecoration(RvItemDecore(3, 5, false, 10000))
        val adapter = ApiCategoriesNameAdapter(catlist, object : StringCallback {
            override fun getStringCall(string: String) {
                catListViewmodel.getAllCreations(string)
                setFragment(string)
            }
        }, myActivity, "")

        binding.recyclerviewAll.adapter = adapter

        myActivity.myCatNameViewModel.wallpaper.observe(viewLifecycleOwner) { wallpapersList ->
            Log.e("TAG", "onCustomCreateView: no data exists")
            if (wallpapersList?.size!! > 0) {
                Log.e("TAG", "onCustomCreateView: data exists")

                lifecycleScope.launch(Dispatchers.IO) {
                    Log.e("TAG", "onCustomCreateView: $wallpapersList")
                    val list = wallpapersList.shuffled()
                    withContext(Dispatchers.Main) {
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
        val categoryList: ArrayList<CatNameResponse> =
            gson.fromJson(categoriesJson, object : TypeToken<ArrayList<CatNameResponse>>() {}.type)


        adapter = LiveCategoriesHorizontalAdapter(categoryList, object : StringCallback {
            override fun getStringCall(string: String) {
                if (string == "Roro'Noa Zoro") {
                    myViewModel.getMostUsed("Roro")
                } else {
                    myViewModel.getMostUsed(string)
                }

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

    val categoriesJson = """
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

    fun sortWallpaperCategories(
        categories: List<CatNameResponse>, order: List<String>
    ): List<CatNameResponse> {
        // Create a map to store the order of categories based on their names
        Log.e("TAG", "sortWallpaperCategories: " + categories)
        Log.e("TAG", "sortWallpaperCategories: " + order)
        order.forEach {
            Log.e("TAG", "sortWallpaperCategories: " + it)
        }
        val orderMap = order.withIndex().associate { it.value.trim() to it.index }
        // Sort the categories based on the order specified in the map
        return categories.sortedWith(compareBy { orderMap[it.cat_name] ?: Int.MAX_VALUE })
    }

    private fun setFragment(name: String) {
        Log.d("Categories", "setFragment:string: $name ")
        MaxInterstitialAds.showInterstitial(requireActivity(),
            object : MaxAdListener {
                override fun onAdLoaded(p0: MaxAd) {
                    MaxInterstitialAds.showInterstitial(requireActivity(), object : MaxAdListener {
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
                    }, object : MaxAD {
                        override fun adNotReady(type: String) {}
                    })
                }

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

                override fun onAdLoadFailed(p0: String, p1: MaxError) {
                    Toast.makeText(requireContext(), "Ad not available", Toast.LENGTH_SHORT).show()
                    val bundle = Bundle().apply {
                        putString("name", name)
                        putString("from", "category")
                    }
                    if (findNavController().currentDestination?.id != R.id.listViewFragment) {
                        findNavController().navigate(R.id.listViewFragment, bundle)
                    }
                }

                override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
                    Toast.makeText(requireContext(), "Ad not available", Toast.LENGTH_SHORT).show()
                    val bundle = Bundle().apply {
                        putString("name", name)
                        putString("from", "category")
                    }
                    if (findNavController().currentDestination?.id != R.id.listViewFragment) {
                        findNavController().navigate(R.id.listViewFragment, bundle)
                    }
                }
            },
            object : MaxAD {
                override fun adNotReady(type: String) {
                    Toast.makeText(requireContext(), "Ad not available", Toast.LENGTH_SHORT).show()
                    val bundle = Bundle().apply {
                        putString("name", name)
                        putString("from", "category")
                    }
                    if (findNavController().currentDestination?.id != R.id.listViewFragment) {
                        findNavController().navigate(R.id.listViewFragment, bundle)
                    }
                }
            })

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

    override fun onAdDismiss() {
        checkAppOpen = true
        Log.e(TAG, "app open dismissed: ")
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

