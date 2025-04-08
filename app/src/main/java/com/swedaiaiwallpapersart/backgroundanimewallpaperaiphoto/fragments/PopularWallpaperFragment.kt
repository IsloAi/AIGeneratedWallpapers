package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
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
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.google.firebase.analytics.FirebaseAnalytics
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.DialogCongratulationsBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentPopularWallpaperBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.ApicategoriesListHorizontalAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.MostUsedWallpaperAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.PopularSliderAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.AdEventListener
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MaxAD
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MaxInterstitialAds
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MyApp
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.WallpaperViewFragment.Companion.isNavigated
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.PositionCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants.Companion.checkAppOpen
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.MostDownloadedViewmodel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class PopularWallpaperFragment() : Fragment(), AdEventListener {

    private var _binding: FragmentPopularWallpaperBinding? = null
    private val binding get() = _binding!!

    private lateinit var welcomeAdapter: PopularSliderAdapter

    @Inject
    lateinit var appDatabase: AppDatabase

    var startIndex = 0

    val catListViewmodel: MyViewModel by activityViewModels()

    companion object {
        var hasToNavigate = false
        var wallFromPopular = false
    }

    private var cachedCatResponses: ArrayList<CatResponse?> = ArrayList()
    private var addedItems: ArrayList<CatResponse?>? = ArrayList()
    val orignalList = arrayListOf<CatResponse?>()
    private lateinit var myActivity: MainActivity
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private var mostUsedWallpaperAdapter: MostUsedWallpaperAdapter? = null
    private var adapter: ApicategoriesListHorizontalAdapter? = null
    var cachedMostDownloaded = ArrayList<CatResponse?>()

    private val viewModel: MostDownloadedViewmodel by activityViewModels()

    var isLoadingMore = false
    var dataset = false
    var datasetTrending = false
    var externalOpen = false
    var oldPosition = 0
    val TAG = "PopularTab"
    var isNavigationInProgress = false

    private val fragmentScope: CoroutineScope by lazy { MainScope() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPopularWallpaperBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        (myActivity.application as MyApp).registerAdEventListener(this)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

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

        updateUIWithFetchedData()
        setEvents()
        initMostUsedRV()
    }

    private fun setEvents() {
        binding.refresh.setOnRefreshListener {
            lifecycleScope.launch {
                val newData = cachedMostDownloaded.filterNotNull()
                val nullAdd = addNullValueInsideArray(newData.shuffled())
                cachedMostDownloaded.clear()
                cachedMostDownloaded = nullAdd

                mostUsedWallpaperAdapter?.updateData(cachedMostDownloaded)
                binding.refresh.isRefreshing = false
            }
        }

        binding.more.setOnClickListener {
            setFragment("Trending")
        }
    }

    private fun initMostUsedRV() {
        val layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerviewMostUsed.layoutManager = layoutManager
        binding.recyclerviewMostUsed.addItemDecoration(RvItemDecore(3, 5, false, 10000))
        val list = ArrayList<CatResponse?>()
        mostUsedWallpaperAdapter = MostUsedWallpaperAdapter(list, object : PositionCallback {
            override fun getPosition(position: Int) {
                Log.e(TAG, "getPosition: clicked")
                if (!isNavigationInProgress) {
                    hasToNavigate = true
                    externalOpen = true
                    val allItems = mostUsedWallpaperAdapter?.getAllItems()
                    if (addedItems?.isNotEmpty() == true) {
                        addedItems?.clear()
                    }
                    isNavigationInProgress = true
                    addedItems = allItems

                    oldPosition = position

                    if (isAdded) {
                        navigateToDestination(allItems!!, position)
                    }
                }


            }

            override fun getFavorites(position: Int) {
                //
            }

        }, myActivity)

        mostUsedWallpaperAdapter!!.setCoroutineScope(fragmentScope)

        binding.recyclerviewMostUsed.adapter = mostUsedWallpaperAdapter

        binding.recyclerviewMostUsed.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                /*Constants.checkInter = false
                checkAppOpen = false
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

                }*/
            }
        })
    }

    private fun initMostDownloadedData() {
        viewModel.allCreations.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Response.Loading -> {
                }

                is Response.Success -> {
                    if (!result.data.isNullOrEmpty()) {
                        val list = arrayListOf<CatResponse>()
                        result.data.forEach { item ->
                            val model = CatResponse(
                                item.id,
                                item.image_name,
                                item.cat_name,
                                item.hd_image_url,
                                item.compressed_image_url,
                                null,
                                item.likes,
                                item.liked,
                                item.unlocked,
                                item.size,
                                item.Tags,
                                item.capacity
                            )
                            if (!list.contains(model)) {
                                list.add(model)
                            }

                        }

                        if (view != null) {
                            lifecycleScope.launch {
                                if (!dataset) {
                                    Log.e(TAG, "initMostDownloadedData: DataSet= $dataset")
                                    val newList = addNullValueInsideArray(list)
                                    cachedMostDownloaded = newList
                                    Log.e(
                                        TAG,
                                        "initMostDownloadedData:initialItems= $cachedMostDownloaded"
                                    )
                                    lifecycleScope.launch(Dispatchers.Main) {
                                        mostUsedWallpaperAdapter?.updateMoreData(
                                            cachedMostDownloaded
                                        )
                                    }
                                    dataset = true
                                }
                            }
                        }
                    }
                }

                is Response.Error -> {

                    Log.e("TAG", "error: ${result.message}")
                    Toast.makeText(requireContext(), "${result.message}", Toast.LENGTH_SHORT)
                        .show()
                }

                else -> {

                }
            }
        }
    }

    fun getItems(startIndex1: Int, chunkSize: Int): ArrayList<CatResponse?> {
        var modifiedList = arrayListOf<CatResponse?>()
        val endIndex = startIndex1 + chunkSize
        if (startIndex1 >= cachedMostDownloaded.size) {
            return arrayListOf()
        } else {
            isLoadingMore = false
            val subList = cachedMostDownloaded.subList(
                startIndex1,
                endIndex.coerceAtMost(cachedMostDownloaded.size)
            )
            lifecycleScope.launch {
                modifiedList = addNullValueInsideArray(subList) // Add ads in every batch
            }

            return ArrayList(modifiedList)
        }
    }

    suspend fun addNullValueInsideArray(data: List<CatResponse?>): ArrayList<CatResponse?> {
        return withContext(Dispatchers.IO) {
            Log.e(TAG, "addNullValueInsideArray: DataSize = ${data.size}")
            val newData = arrayListOf<CatResponse?>()
            for (i in data.indices) {
                newData.add(data[i]) // Add the current item

                if ((i + 1) % 15 == 0) { // Add null every 15th item
                    newData.add(null)
                    Log.e(
                        TAG,
                        "addNullValueInsideArray: Added null at position ${newData.size - 1}"
                    )
                }
            }
            Log.e(TAG, "addNullValueInsideArray: Final size = ${newData.size}")
            newData
        }

    }

    override fun onResume() {
        super.onResume()
        //load the data first time
        if (wallFromPopular) {
            if (isAdded) {
                congratulationsDialog()
            }
        }
        initMostDownloadedData()
        initTrendingData()
        //when user come back show this data
        if (datasetTrending) {
            adapter?.updateMoreData(cachedCatResponses.shuffled() as ArrayList<CatResponse?>)
        }
        if (dataset) {
            Log.d("PopularTab", "onResume: Data set $dataset")
            if (addedItems?.isEmpty() == true) {
                Log.d(
                    "PopularTab",
                    "onResume:cachedMostDownloaded Size " + cachedMostDownloaded.size
                )
            }
            mostUsedWallpaperAdapter?.updateMoreData(addedItems!!)
            binding.recyclerviewMostUsed.layoutManager?.scrollToPosition(oldPosition)
        }
        if (isAdded) {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Popular Screen")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, javaClass.simpleName)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }
        lifecycleScope.launch(Dispatchers.Main) {
            delay(1500)
            if (!isNavigated && hasToNavigate) {
                navigateToDestination(addedItems!!, oldPosition)
            }
        }
    }

    private fun congratulationsDialog() {
        val dialog = Dialog(requireContext())
        val bindingDialog =
            DialogCongratulationsBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(bindingDialog.root)
        val width = WindowManager.LayoutParams.MATCH_PARENT
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.setLayout(width, height)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
//        var getReward = dialog?.findViewById<LinearLayout>(R.id.buttonGetReward)


        bindingDialog.continueBtn.setOnClickListener {
            wallFromPopular = false
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onPause() {
        super.onPause()

        Log.e(TAG, "onPause: ")

        if (!externalOpen) {

            val allItems = mostUsedWallpaperAdapter?.getAllItems()

            Log.e(TAG, "onPause: all items${allItems?.size}")
            if (addedItems?.isNotEmpty() == true) {

                Log.e(TAG, "onPause: cleared")
                addedItems?.clear()
            }

            allItems?.let { addedItems?.addAll(it) }

            Log.e(TAG, "onPause: " + addedItems?.size)
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

                        /*1 -> {
                            (requireParentFragment() as HomeTabsFragment).navigateTOTabs("Anime")
                        }*/

                        1 -> {
                            catListViewmodel.getAllCreations("4K")
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

    private fun initTrendingData() {
        viewModel.getAllTrendingWallpapers()

        viewModel.trendingWallpapers.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Response.Loading -> {

                }

                is Response.Success -> {
                    if (view != null) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            if (!datasetTrending) {
                                val list = result.data?.take(100)
                                list?.forEach { item ->
                                    val model = CatResponse(
                                        item.id,
                                        item.image_name,
                                        item.cat_name,
                                        item.hd_image_url,
                                        item.compressed_image_url,
                                        null,
                                        item.likes,
                                        item.liked,
                                        item.unlocked,
                                        item.size,
                                        item.Tags,
                                        item.capacity
                                    )
                                    if (!cachedCatResponses.contains(model)) {
                                        cachedCatResponses.add(model)
                                    }
                                }
                                Log.d("PopularTab", "initTrendingData: $cachedCatResponses")
                                withContext(Dispatchers.Main) {
                                    adapter?.updateMoreData(cachedCatResponses.shuffled() as ArrayList<CatResponse?>)
                                }
                                datasetTrending = true
                            }
                        }
                    }
                }

                is Response.Error -> {
                    Log.e("TAG", "error: ${result.message}")
                }

                else -> {
                }
            }

        }
    }

    private fun updateUIWithFetchedData() {
        val list = ArrayList<CatResponse?>()
        adapter =
            ApicategoriesListHorizontalAdapter(list, object :
                PositionCallback {
                override fun getPosition(position: Int) {
                    if (!isNavigationInProgress) {
                        isNavigationInProgress = true
                        val allItems = adapter?.getAllItems()
                        if (isAdded) {
                            navigateToDestination(allItems!!, position)
                        }

                    }


                }

                override fun getFavorites(position: Int) {
                    //
                }
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
        Log.e(TAG, "navigateToDestination: arrayList: $arrayList")

        try {
            val countOfNulls = arrayList.subList(0, position).count { it == null }
            val sharedViewModel: SharedViewModel by activityViewModels()

            sharedViewModel.clearData()
            sharedViewModel.setData(arrayList.filterNotNull(), position - countOfNulls)

            MaxInterstitialAds.loadInterstitialAd(requireContext())
            MaxInterstitialAds.showInterstitial(requireActivity(), object : MaxAdListener {
                override fun onAdLoaded(p0: MaxAd) {
                    MaxInterstitialAds.showInterstitial(requireActivity(), object : MaxAdListener {
                        override fun onAdLoaded(p0: MaxAd) {}

                        override fun onAdDisplayed(p0: MaxAd) {}

                        override fun onAdHidden(p0: MaxAd) {
                            lifecycleScope.launch(Dispatchers.Main) {
                                Bundle().apply {
                                    putString("from", "trending")
                                    putString("wall", "popular")
                                    putInt("position", position - countOfNulls)
                                    Log.e(TAG, "navigateToDestination: inside bundle")

                                    requireParentFragment().findNavController()
                                        .navigate(R.id.wallpaperViewFragment, this)
                                }
                            }
                            MaxInterstitialAds.loadInterstitialAd(requireContext())
                        }

                        override fun onAdClicked(p0: MaxAd) {}

                        override fun onAdLoadFailed(p0: String, p1: MaxError) {}

                        override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {}
                    }, object : MaxAD {
                        override fun adNotReady(type: String) {
                            if (MaxInterstitialAds.willIntAdShow) {
                                /*Toast.makeText(
                                    requireContext(),
                                    "Ad not available",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()*/
                                lifecycleScope.launch(Dispatchers.Main) {
                                    Bundle().apply {
                                        putString("from", "trending")
                                        putString("wall", "popular")
                                        putInt("position", position - countOfNulls)
                                        Log.e(TAG, "navigateToDestination: inside bundle")

                                        requireParentFragment().findNavController()
                                            .navigate(R.id.wallpaperViewFragment, this)
                                    }
                                }
                            } else {
                                lifecycleScope.launch(Dispatchers.Main) {
                                    Bundle().apply {
                                        putString("from", "trending")
                                        putString("wall", "popular")
                                        putInt("position", position - countOfNulls)
                                        Log.e(TAG, "navigateToDestination: inside bundle")

                                        requireParentFragment().findNavController()
                                            .navigate(R.id.wallpaperViewFragment, this)
                                    }
                                }
                            }

                        }
                    })
                }

                override fun onAdDisplayed(p0: MaxAd) {}

                override fun onAdHidden(p0: MaxAd) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        Bundle().apply {
                            putString("from", "trending")
                            putString("wall", "popular")
                            putInt("position", position - countOfNulls)
                            Log.e(TAG, "navigateToDestination: inside bundle")

                            requireParentFragment().findNavController()
                                .navigate(R.id.wallpaperViewFragment, this)
                        }
                    }
                    MaxInterstitialAds.loadInterstitialAd(requireContext())
                }

                override fun onAdClicked(p0: MaxAd) {}

                override fun onAdLoadFailed(p0: String, p1: MaxError) {
                    //Toast.makeText(requireContext(), "Ad not available", Toast.LENGTH_SHORT).show()
                    lifecycleScope.launch(Dispatchers.Main) {
                        Bundle().apply {
                            putString("from", "trending")
                            putString("wall", "popular")
                            putInt("position", position - countOfNulls)
                            Log.e(TAG, "navigateToDestination: inside bundle")

                            requireParentFragment().findNavController()
                                .navigate(R.id.wallpaperViewFragment, this)
                        }
                    }
                }

                override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
                    //Toast.makeText(requireContext(), "Ad not available", Toast.LENGTH_SHORT).show()
                    lifecycleScope.launch(Dispatchers.Main) {
                        Bundle().apply {
                            putString("from", "trending")
                            putString("wall", "popular")
                            putInt("position", position - countOfNulls)
                            Log.e(TAG, "navigateToDestination: inside bundle")

                            requireParentFragment().findNavController()
                                .navigate(R.id.wallpaperViewFragment, this)
                        }
                    }
                }
            }, object : MaxAD {
                override fun adNotReady(type: String) {
                    if (MaxInterstitialAds.willIntAdShow) {
                        //Toast.makeText(requireContext(), "Ad not available", Toast.LENGTH_SHORT) .show()
                        lifecycleScope.launch(Dispatchers.Main) {
                            Bundle().apply {
                                putString("from", "trending")
                                putString("wall", "popular")
                                putInt("position", position - countOfNulls)
                                Log.e(TAG, "navigateToDestination: inside bundle")

                                requireParentFragment().findNavController()
                                    .navigate(R.id.wallpaperViewFragment, this)
                            }
                        }
                    } else {
                        lifecycleScope.launch(Dispatchers.Main) {
                            Bundle().apply {
                                putString("from", "trending")
                                putString("wall", "popular")
                                putInt("position", position - countOfNulls)
                                Log.e(TAG, "navigateToDestination: inside bundle")

                                requireParentFragment().findNavController()
                                    .navigate(R.id.wallpaperViewFragment, this)
                            }
                        }
                    }

                }
            })

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