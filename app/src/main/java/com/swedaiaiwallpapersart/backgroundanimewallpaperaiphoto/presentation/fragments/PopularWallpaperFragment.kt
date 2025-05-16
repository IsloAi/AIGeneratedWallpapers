package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.google.firebase.analytics.FirebaseAnalytics
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentPopularWallpaperBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.AdClickCounter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MaxInterstitialAds
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.SingleDatabaseResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.activity.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.adapters.ApiCategoriesListHorizontalAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.adapters.MostUsedWallpaperAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.adapters.PopularSliderAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.Constants
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.Constants.Companion.checkAppOpen
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.PositionCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.viewmodels.DataFromRoomViewmodel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class PopularWallpaperFragment() : Fragment() {

    private var _binding: FragmentPopularWallpaperBinding? = null
    private val binding get() = _binding!!

    private lateinit var welcomeAdapter: PopularSliderAdapter

    @Inject
    lateinit var appDatabase: AppDatabase
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var myActivity: MainActivity
    private var mostUsedWallpaperAdapter: MostUsedWallpaperAdapter? = null
    private var adapter: ApiCategoriesListHorizontalAdapter? = null
    private val roomDataViewmodel: DataFromRoomViewmodel by viewModels()
    private var cachedMostDownloaded = ArrayList<SingleDatabaseResponse?>()
    var isNavigationInProgress = false
    private var dataSet = false
    var externalOpen = false
    var oldPosition = 0

    companion object {
        var hasToNavigate = false
        var wallFromPopular = false
    }

    var startIndex = 0

    /*private var cachedCatResponses: ArrayList<CatResponse?> = ArrayList()
    val catListViewmodel: MyViewModel by activityViewModels()
    private var addedItems: ArrayList<CatResponse?>? = ArrayList()
    val orignalList = arrayListOf<CatResponse?>()
    private lateinit var myActivity: MainActivity
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val viewModel: MostDownloadedViewmodel by activityViewModels()
    var isLoadingMore = false
    var datasetTrending = false
    val TAG = "PopularTab"
    */

    private val fragmentScope: CoroutineScope by lazy { MainScope() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPopularWallpaperBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        myActivity = activity as MainActivity
        initMostUsedRV()


        updateUIWithFetchedData()
        setEvents()
    }

    private fun updateUIWithFetchedData() {
        lifecycleScope.launchWhenStarted {
            roomDataViewmodel.fetchTrendingWallpapers()
            roomDataViewmodel.trendingWallpapers.collect { wallpapers ->
                // Update your UI with wallpapers
                Log.d("Popular", "updateUIWithFetchedData:wallpapers Trending ${wallpapers.size} ")
                adapter?.updateData(ArrayList(wallpapers.shuffled().map { it }))
            }
        }
        lifecycleScope.launchWhenStarted {
            roomDataViewmodel.fetchPopularWallpapers()
            roomDataViewmodel.popularWallpapers.collect { wallpapers ->
                // Update your UI with wallpapers
                Log.d("Popular", "updateUIWithFetchedData:wallpapers Popular ${wallpapers.size} ")
                val list = arrayListOf<SingleDatabaseResponse>()
                wallpapers.forEach { item ->
                    val single = SingleDatabaseResponse(
                        item.id,
                        item.cat_name,
                        item.image_name,
                        item.hd_image_url,
                        item.compressed_image_url,
                        item.likes,
                        item.liked,
                        item.size,
                        item.Tags,
                        item.capacity,
                        false, // locked by default
                    )
                    if (!list.contains(single)) {
                        list.add(single)
                    }
                }
                // Unlock 20% randomly
                val unlockCount = (list.size * 0.2).toInt()
                list.shuffled().take(unlockCount).forEach { it.unlocked = true }
                if (view != null) {
                    lifecycleScope.launch {
                        if (view != null) {
                            lifecycleScope.launch {
                                if (!dataSet) {
                                    val rawList = if (AdConfig.ISPAIDUSER) {
                                        list
                                    } else {
                                        addNullValueInsideArray(list)
                                    }

                                    cachedMostDownloaded.clear()
                                    cachedMostDownloaded.addAll(rawList)

                                    lifecycleScope.launch(Dispatchers.Main) {
                                        mostUsedWallpaperAdapter?.updateData(cachedMostDownloaded)
                                    }
                                    dataSet = true
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initMostUsedRV() {
        val list = ArrayList<SingleDatabaseResponse?>()
        val layoutManager = GridLayoutManager(requireContext(), 3)
        mostUsedWallpaperAdapter = MostUsedWallpaperAdapter(list, object : PositionCallback {
            override fun getPosition(position: Int) {
                Log.e("Popular", "getPosition: clicked")
                if (!isNavigationInProgress) {
                    hasToNavigate = true
                    externalOpen = true
                    /*val allItems = mostUsedWallpaperAdapter?.getAllItems()
                    if (addedItems?.isNotEmpty() == true) {
                        addedItems?.clear()
                    }
                    isNavigationInProgress = true
                    addedItems = allItems*/

                    oldPosition = position

                    if (isAdded) {
                        navigateToDestination(cachedMostDownloaded, position)
                    }
                }
            }

            override fun getFavorites(position: Int) {
                //
            }

        }, myActivity)
        mostUsedWallpaperAdapter!!.setCoroutineScope(fragmentScope)
        binding.recyclerviewMostUsed.adapter = mostUsedWallpaperAdapter
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (list[position] == null) {
                    3 // full width for ad
                } else {
                    1
                }
            }
        }
        binding.recyclerviewMostUsed.layoutManager = layoutManager
        binding.recyclerviewMostUsed.addItemDecoration(RvItemDecore(3, 5, false, 10000))
        binding.recyclerviewMostUsed.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })

        val list2 = ArrayList<SingleDatabaseResponse?>()
        adapter = ApiCategoriesListHorizontalAdapter(list2, object : PositionCallback {
            override fun getPosition(position: Int) {
                if (!isNavigationInProgress) {
                    isNavigationInProgress = true
                    val allItems = adapter?.getAllItems()
                    if (isAdded) {
                        navigateToDestination(allItems!!, position)
                    }
                }
            }

            override fun getFavorites(position: Int) {}
        }, myActivity)
        binding.recyclerviewTrending.adapter = adapter
        binding.recyclerviewTrending.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // Set the boolean to true when the RecyclerView is scrolled
                if (dy != 0 || dx != 0) {
                    Constants.checkInter = false
                    checkAppOpen = false
                }
            }
        })
    }

    private fun setEvents() {
        binding.refresh.setOnRefreshListener {
            lifecycleScope.launch {
                val newData = cachedMostDownloaded.filterNotNull()

                val nullAdd = if (AdConfig.ISPAIDUSER) {
                    newData.shuffled()
                } else {
                    addNullValueInsideArray(newData.shuffled())
                }
                cachedMostDownloaded.clear()
                cachedMostDownloaded = nullAdd as ArrayList<SingleDatabaseResponse?>

                mostUsedWallpaperAdapter?.updateData(cachedMostDownloaded)
                binding.refresh.isRefreshing = false
            }
        }

        binding.more.setOnClickListener {
            setFragment("Trending")
        }
    }

    private suspend fun addNullValueInsideArray(data: List<SingleDatabaseResponse?>): ArrayList<SingleDatabaseResponse?> {
        return withContext(Dispatchers.IO) {
            Log.e("TAG", "addNullValueInsideArray: DataSize = ${data.size}")
            val newData = arrayListOf<SingleDatabaseResponse?>()
            for (i in data.indices) {
                newData.add(data[i]) // Add the current item
                if ((i + 1) % 15 == 0) { // Add null every 15th item
                    newData.add(null)
                    Log.e(
                        "TAG", "addNullValueInsideArray: Added null at position ${newData.size - 1}"
                    )
                }
            }
            Log.e("TAG", "addNullValueInsideArray: Final size = ${newData.size}")
            newData
        }

    }

    private fun populateOnbaordingItems() {
        val welcomeItems: MutableList<Int> = ArrayList()
        welcomeItems.add(0)
        //welcomeItems.add(2)
        welcomeItems.add(3)

        welcomeAdapter =
            PopularSliderAdapter(welcomeItems, object : PopularSliderAdapter.joinButtons {
                override fun clickEvent(position: Int) {
                    when (position) {
                        0 -> {
                            (requireParentFragment() as HomeTabsFragment).navigateTOTabs("Category")
                        }

                        1 -> {
                            //catListViewmodel.getAllCreations("4K")
                            setFragment("4K")
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
                        requireContext(), R.drawable.banner_slider_indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(), R.drawable.banner_slider_indicator_inactive
                    )
                )
            }
        }
    }

    private fun navigateToDestination(
        arrayList: ArrayList<SingleDatabaseResponse?>,
        position: Int
    ) {
        Log.e("Popular", "navigateToDestination: arrayList: $arrayList")
        try {
            val countOfNulls = arrayList.subList(0, position).count { it == null }
            val sharedViewModel: SharedViewModel by activityViewModels()
            sharedViewModel.clearData()
            sharedViewModel.setData(arrayList.filterNotNull(), position - countOfNulls)
            if (AdConfig.ISPAIDUSER) {
                lifecycleScope.launch(Dispatchers.Main) {
                    Bundle().apply {
                        putString("from", "trending")
                        putString("wall", "popular")
                        putInt("position", position - countOfNulls)
                        Log.e("Popular", "navigateToDestination: inside bundle")

                        requireParentFragment().findNavController()
                            .navigate(R.id.wallpaperViewFragment, this)
                    }
                }
            } else {
                if (AdClickCounter.shouldShowAd()) {
                    MaxInterstitialAds.showInterstitialAd(
                        requireActivity(),
                        object : MaxAdListener {
                            override fun onAdLoaded(p0: MaxAd) {}

                            override fun onAdDisplayed(p0: MaxAd) {}

                            override fun onAdHidden(p0: MaxAd) {
                                lifecycleScope.launch(Dispatchers.Main) {
                                    Bundle().apply {
                                        putString("from", "trending")
                                        putString("wall", "popular")
                                        putInt("position", position - countOfNulls)
                                        Log.e("Popular", "navigateToDestination: inside bundle")

                                        requireParentFragment().findNavController()
                                            .navigate(R.id.wallpaperViewFragment, this)
                                    }
                                }
                            }

                            override fun onAdClicked(p0: MaxAd) {}

                            override fun onAdLoadFailed(p0: String, p1: MaxError) {}

                            override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {}
                        })
                } else {
                    lifecycleScope.launch(Dispatchers.Main) {
                        Bundle().apply {
                            putString("from", "trending")
                            putString("wall", "popular")
                            putInt("position", position - countOfNulls)
                            Log.e("Popular", "navigateToDestination: inside bundle")

                            requireParentFragment().findNavController()
                                .navigate(R.id.wallpaperViewFragment, this)
                        }
                    }
                    AdClickCounter.increment()
                }
            }
            isNavigationInProgress = false
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    override fun onResume() {
        super.onResume()
        if (isAdded) {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Popular Screen")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, javaClass.simpleName)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }
        mostUsedWallpaperAdapter?.updateData(cachedMostDownloaded)
    }

}