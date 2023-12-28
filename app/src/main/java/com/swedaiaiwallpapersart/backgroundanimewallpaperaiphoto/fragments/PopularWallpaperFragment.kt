package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.CommonAdsListenerAdapter
import com.google.gson.Gson
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentPopularWallpaperBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.ApicategoriesListHorizontalAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.MostUsedWallpaperAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.PopularSliderAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.PositionCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.MostDownloadImageResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyHomeViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.MostDownloadedViewmodel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PopularWallpaperFragment : Fragment() {

    private var _binding: FragmentPopularWallpaperBinding? = null
    private val binding get() = _binding!!

    private lateinit var welcomeAdapter: PopularSliderAdapter

    var startIndex = 0


    private val myViewModel: MyHomeViewModel by activityViewModels()

    private var cachedCatResponses: ArrayList<CatResponse>? = ArrayList()

    val orignalList = arrayListOf<CatResponse?>()

    private lateinit var myActivity: MainActivity


    private var mostUsedWallpaperAdapter: MostUsedWallpaperAdapter? = null


    private val viewModel: MostDownloadedViewmodel by activityViewModels()

    var cachedMostDownloaded = ArrayList<MostDownloadImageResponse?>()

    var isLoadingMore = false

    var dataset = false

    val TAG = "POPULARTAB"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPopularWallpaperBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myActivity = activity as MainActivity


        populateOnbaordingItems()
        binding.sliderPager.adapter = welcomeAdapter

        setIndicator()
        setCurrentIndicator(0)
        binding.sliderPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)

            }
        })

        binding.more.setOnClickListener {
            (requireParentFragment() as HomeTabsFragment).navigateToTrending(1)
        }


        initMostUsedRV()
    }

    private fun initMostUsedRV() {

        val layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerviewMostUsed.layoutManager = layoutManager
        binding.recyclerviewMostUsed.addItemDecoration(RvItemDecore(3, 5, false, 10000))
        val list = ArrayList<MostDownloadImageResponse?>()
        mostUsedWallpaperAdapter = MostUsedWallpaperAdapter(list, object : PositionCallback {
            override fun getPosition(position: Int) {
                //
            }

            override fun getFavorites(position: Int) {
                //
            }

        }, myActivity)

        binding.recyclerviewMostUsed.adapter = mostUsedWallpaperAdapter

        binding.recyclerviewMostUsed.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                val totalItemCount = mostUsedWallpaperAdapter!!.itemCount
                Log.e(TAG, "onScrolled: insdie scroll listener")
                if (lastVisibleItemPosition + 10 >= totalItemCount) {

                    isLoadingMore = true
                    // End of list reached
                    val nextItems = getItems(startIndex, 30)
                    if (nextItems.isNotEmpty()) {
                        Log.e(TAG, "onScrolled: inside 3 coondition")
                        mostUsedWallpaperAdapter?.updateMoreData(nextItems)
                        startIndex += 30 // Update startIndex for the next batch of data
                    } else {
                        Log.e(TAG, "onScrolled: inside 4 coondition")
                    }

                }


            }
        })
    }


    private fun initMostDownloadedData() {
        viewModel.wallpaperData.observe(viewLifecycleOwner) { wallpapers ->
            if (wallpapers != null) {

                if (view != null) {

                    lifecycleScope.launch(Dispatchers.IO) {
                        if (!dataset) {
                            val list = addNullValueInsideArray(wallpapers.shuffled())

                            cachedMostDownloaded = list

                            val initialItems = getItems(0, 30)

                            Log.e(TAG, "initMostDownloadedData: " + initialItems)

                            withContext(Dispatchers.Main) {
                                mostUsedWallpaperAdapter?.updateMoreData(initialItems)
                                startIndex += 30
                            }

                            dataset = true
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), "No Data Found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getItems(startIndex1: Int, chunkSize: Int): ArrayList<MostDownloadImageResponse?> {
        val endIndex = startIndex1 + chunkSize
        if (startIndex1 >= cachedMostDownloaded.size) {
            return arrayListOf()
        } else {
            isLoadingMore = false
            val subList = cachedMostDownloaded.subList(
                startIndex1,
                endIndex.coerceAtMost(cachedMostDownloaded.size)
            )
            return ArrayList(subList)
        }
    }


    private fun addNullValueInsideArray(data: List<MostDownloadImageResponse?>): ArrayList<MostDownloadImageResponse?> {

        val firstAdLineThreshold =
            if (AdConfig.firstAdLineViewListWallSRC != 0) AdConfig.firstAdLineViewListWallSRC else 4
        val firstLine = firstAdLineThreshold * 3

        val lineCount =
            if (AdConfig.lineCountViewListWallSRC != 0) AdConfig.lineCountViewListWallSRC else 5
        val lineC = lineCount * 3
        val newData = arrayListOf<MostDownloadImageResponse?>()

        for (i in data.indices) {
            if (i > firstLine && (i - firstLine) % (lineC + 1) == 0) {
                newData.add(null)



                Log.e("******NULL", "addNullValueInsideArray: null " + i)

            } else if (i == firstLine) {
                newData.add(null)
                Log.e("******NULL", "addNullValueInsideArray: null first " + i)
            }
            Log.e("******NULL", "addNullValueInsideArray: not null " + i)
            newData.add(data[i])

        }
        Log.e("******NULL", "addNullValueInsideArray:size " + newData.size)




        return newData
    }


    override fun onResume() {
        super.onResume()
        initTrendingData()
        initMostDownloadedData()
    }

    private fun populateOnbaordingItems() {
        val welcomeItems: MutableList<Int> = ArrayList<Int>()
        welcomeItems.add(1)
        welcomeItems.add(2)
        welcomeItems.add(3)
        welcomeItems.add(4)

        welcomeAdapter =
            PopularSliderAdapter(welcomeItems, object : PopularSliderAdapter.joinButtons {
                override fun clickEvent(position: Int) {
                    when (position) {
                        0 -> {
                            (requireParentFragment() as HomeTabsFragment).navigateToTrending(5)
                        }

                        1 -> {
                            (requireParentFragment() as HomeTabsFragment).navigateToTrending(4)
                        }

                        2 -> {
                            setFragment("Anime")
                        }

                        3 -> {
                            (requireParentFragment() as HomeTabsFragment).navigateToTrending(3)
                        }
                    }
                }

            })


    }


    private fun setFragment(name: String) {
        val bundle = Bundle().apply {
            putString("name", name)
            putString("from", "category")

        }
        if (findNavController().currentDestination?.id != R.id.listViewFragment) {

            findNavController().navigate(R.id.listViewFragment, bundle)
        }
    }


    private fun initTrendingData() {
        binding.progressBar.setAnimation(R.raw.main_loading_animation)
        binding.progressBar.visibility = View.GONE
        Log.d("functionCallingTest", "onCreateCustom:  home on create")
        myViewModel.getWallpapers().observe(viewLifecycleOwner) { catResponses ->
            if (catResponses != null) {
                cachedCatResponses = catResponses

                Log.e("TAG", "loadData: " + catResponses)
                if (view != null) {
                    // If the view is available, update the UI
                    orignalList.clear()
                    orignalList.addAll(catResponses)

                    updateUIWithFetchedData(catResponses)
                }
            } else {
                Log.e("TAG", "loadData: $catResponses")
            }
        }
    }

    private fun updateUIWithFetchedData(catResponses: List<CatResponse?>) {


        val adapter =
            ApicategoriesListHorizontalAdapter(catResponses as ArrayList<CatResponse?>, object :
                PositionCallback {
                override fun getPosition(position: Int) {

                    SDKBaseController.getInstance().showInterstitialAds(
                        requireActivity(),
                        "mainscr_trending_tab_click_item",
                        "mainscr_trending_tab_click_item",
                        showLoading = true,
                        adsListener = object : CommonAdsListenerAdapter() {
                            override fun onAdsShowFail(errorCode: Int) {
                                Log.e("********ADS", "onAdsShowFail: " + errorCode)
                                navigateToDestination(catResponses, position)
                                //do something
                            }

                            override fun onAdsDismiss() {
                                navigateToDestination(catResponses, position)
                            }
                        }
                    )


                }

                override fun getFavorites(position: Int) {
                    //
                }
            }, myActivity)

        binding.recyclerviewTrending.adapter = adapter
    }

    private fun setIndicator() {
        val welcomeIndicators = arrayOfNulls<ImageView>(welcomeAdapter.itemCount)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in welcomeIndicators.indices) {
            welcomeIndicators[i] = ImageView(requireContext())
            welcomeIndicators[i]!!.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.banner_slider_indicator_inactive
                )
            )
            welcomeIndicators[i]!!.layoutParams = layoutParams
            binding.layoutOnboardingIndicators.addView(welcomeIndicators[i])
        }
    }


    private fun setCurrentIndicator(index: Int) {
        val childCount = binding.layoutOnboardingIndicators.childCount
        for (i in 0 until childCount) {
            val imageView = binding.layoutOnboardingIndicators.getChildAt(i) as ImageView

            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.banner_slider_indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.banner_slider_indicator_inactive
                    )
                )
            }
        }
    }


    private fun navigateToDestination(arrayList: ArrayList<CatResponse?>, position: Int) {
        val gson = Gson()
        val arrayListJson = gson.toJson(arrayList.filterNotNull())

        val countOfNulls = arrayList.subList(0, position).count { it == null }
        val sharedViewModel: SharedViewModel by activityViewModels()


        sharedViewModel.clearData()

        sharedViewModel.setData(arrayList.filterNotNull(), position - countOfNulls)

        Bundle().apply {
            putString("from", "trending")
            putInt("position", position - countOfNulls)
            requireParentFragment().findNavController().navigate(R.id.wallpaperViewFragment, this)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }


}