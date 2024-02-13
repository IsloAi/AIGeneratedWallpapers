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
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentPopularWallpaperBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.ApicategoriesListHorizontalAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.MostUsedWallpaperAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.PopularSliderAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.PositionCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.MostDownloadImageResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyHomeViewModel
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
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

@AndroidEntryPoint
class PopularWallpaperFragment () : Fragment() {

    private var _binding: FragmentPopularWallpaperBinding? = null
    private val binding get() = _binding!!

    private lateinit var welcomeAdapter: PopularSliderAdapter

    @Inject
    lateinit var appDatabase: AppDatabase

    var startIndex = 0

    val catListViewmodel: MyViewModel by activityViewModels()

    private var cachedCatResponses: ArrayList<CatResponse?> = ArrayList()
    private var addedItems: ArrayList<CatResponse?>? = ArrayList()

    val orignalList = arrayListOf<CatResponse?>()

    private lateinit var myActivity: MainActivity

    private lateinit var firebaseAnalytics: FirebaseAnalytics


    private var mostUsedWallpaperAdapter: MostUsedWallpaperAdapter? = null
    private var adapter: ApicategoriesListHorizontalAdapter? = null


    private val viewModel: MostDownloadedViewmodel by activityViewModels()

    var cachedMostDownloaded = ArrayList<CatResponse?>()


    var isLoadingMore = false

    var dataset = false
    var datasetTrending = false


    var externalOpen = false
    var oldPosition = 0

    val TAG = "POPULARTAB"
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

//        val timer = Timer()
//
//        timer.schedule(object : TimerTask() {
//            override fun run() {
//                lifecycleScope.launch(Dispatchers.Main) {
//                    while (isActive) {
//                        delay(3000)
//                        val itemCount = binding.sliderPager.adapter?.itemCount ?: 0
//                        val nextItem = (binding.sliderPager.currentItem + 1) % itemCount
//                        binding.sliderPager.setCurrentItem(nextItem, true)
//                    }
//                }
//            }
//        }, 3000, 3000)


    }

    private fun setEvents(){
        binding.refresh.setOnRefreshListener {

            lifecycleScope.launch {
                val newData = cachedMostDownloaded.filterNotNull()
                val nullAdd = addNullValueInsideArray(newData.shuffled())

                cachedMostDownloaded.clear()
                cachedMostDownloaded = nullAdd
                val initialItems = getItems(0, 30)
                startIndex = 0

                withContext(Dispatchers.Main){
                    mostUsedWallpaperAdapter?.addNewData()
                    Log.e(TAG, "initMostDownloadedData: " + initialItems)
                    mostUsedWallpaperAdapter?.updateMoreData(initialItems)
                    startIndex += 30



                    binding.refresh.isRefreshing = false
                }

            }



        }

        binding.more.setOnClickListener {
            (requireParentFragment() as HomeTabsFragment).navigateToTrending(1)
        }
    }

    private fun initMostUsedRV() {

        val layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerviewMostUsed.layoutManager = layoutManager
        binding.recyclerviewMostUsed.addItemDecoration(RvItemDecore(3, 5, false, 10000))
        val list = ArrayList<CatResponse?>()
        mostUsedWallpaperAdapter = MostUsedWallpaperAdapter(list, object : PositionCallback {
            override fun getPosition(position: Int) {
                if (!isNavigationInProgress){
                    externalOpen = true
                    val allItems = mostUsedWallpaperAdapter?.getAllItems()
                    if (addedItems?.isNotEmpty() == true){
                        addedItems?.clear()
                    }
                    isNavigationInProgress = true


                    addedItems = allItems

                    oldPosition = position

                    SDKBaseController.getInstance().showInterstitialAds(
                        requireActivity(),
                        "mainscr_all_tab_click_item",
                        "mainscr_all_tab_click_item",
                        showLoading = true,
                        adsListener = object : CommonAdsListenerAdapter() {
                            override fun onAdsShowFail(errorCode: Int) {
                                Log.e("********ADS", "onAdsShowFail: " + errorCode)
                                if (isAdded){
                                    navigateToDestination(allItems!!, position)
                                }

                                //do something
                            }

                            override fun onAdsDismiss() {
                                if (isAdded){
                                    navigateToDestination(allItems!!, position)
                                }
                            }
                        }
                    )
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

            viewModel.allCreations.observe(viewLifecycleOwner){result ->
                when (result) {
                    is Response.Loading -> {
                    }

                    is Response.Success -> {

                        if (!result.data.isNullOrEmpty()) {
                            val list = arrayListOf<CatResponse>()
                            result.data.forEach { item ->
                                val model = CatResponse(item.id,item.image_name,item.cat_name,item.hd_image_url,item.compressed_image_url,null,item.likes,item.liked,item.unlocked,item.size,item.Tags,item.capacity)
                                if (!list.contains(model)){
                                    list.add(model)
                                }

                            }


                            if (view != null) {
                                lifecycleScope.launch {
                                    Log.e(TAG, "initMostDownloadedData: ", )
                                    if (!dataset) {
                                        Log.e(TAG, "initMostDownloadedData: $dataset")
                                        val newList = addNullValueInsideArray(list.shuffled())

                                        cachedMostDownloaded = newList

                                        val initialItems = getItems(0, 30)

                                        Log.e(TAG, "initMostDownloadedData: " + initialItems)

                                        lifecycleScope.launch(Dispatchers.Main) {
                                            mostUsedWallpaperAdapter?.updateMoreData(initialItems)
                                            startIndex += 30
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


     suspend fun addNullValueInsideArray(data: List<CatResponse?>): ArrayList<CatResponse?> {
         return withContext(Dispatchers.IO){
             Log.e(TAG, "addNullValueInsideArray: "+data.size )

             val firstAdLineThreshold =
                 if (AdConfig.firstAdLineMostUsed != 0) AdConfig.firstAdLineMostUsed else 4
             val firstLine = firstAdLineThreshold * 3

             val lineCount =
                 if (AdConfig.lineCountMostUsed != 0) AdConfig.lineCountMostUsed else 5
             val lineC = lineCount * 3
             val newData = arrayListOf<CatResponse?>()

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




              newData
         }

    }


    override fun onResume() {
        super.onResume()

        initMostDownloadedData()

        initTrendingData()

        if (datasetTrending){
            if (cachedCatResponses?.isEmpty() == true){
                Log.e(TAG, "onResume: "+cachedCatResponses.size )


            }
            adapter?.updateMoreData(cachedCatResponses)

        }

        if (dataset){

            Log.e(TAG, "onResume: Data set $dataset")
            Log.e(TAG, "onResume: Data set ${addedItems?.size}")

            if (addedItems?.isEmpty() == true){
                Log.e(TAG, "onResume: "+cachedMostDownloaded.size )


            }
            mostUsedWallpaperAdapter?.updateMoreData(addedItems!!)

            binding.recyclerviewMostUsed.layoutManager?.scrollToPosition(oldPosition)

        }

        if (isAdded){
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Popular Screen")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, javaClass.simpleName)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }

    }


    override fun onPause() {
        super.onPause()

        Log.e(TAG, "onPause: ", )

        if (!externalOpen){

            val allItems = mostUsedWallpaperAdapter?.getAllItems()

            Log.e(TAG, "onPause: all items${allItems?.size}")
            if (addedItems?.isNotEmpty() == true){

                Log.e(TAG, "onPause: cleared", )
                addedItems?.clear()
            }

            allItems?.let { addedItems?.addAll(it) }

            Log.e(TAG, "onPause: "+addedItems?.size )
        }

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
                            catListViewmodel.getAllCreations("Anime")
                            SDKBaseController.getInstance().showInterstitialAds(
                                requireActivity(),
                                "mainscr_cate_tab_click_item",
                                "mainscr_cate_tab_click_item",
                                showLoading = true,
                                adsListener = object : CommonAdsListenerAdapter() {
                                    override fun onAdsShowFail(errorCode: Int) {
                                        Log.e("********ADS", "onAdsShowFail: $errorCode")


                                        setFragment("Anime")
                                        //do something
                                    }

                                    override fun onAdsDismiss() {
                                        setFragment("Anime")
                                    }
                                }
                            )


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
        viewModel.getAllTrendingWallpapers()

        viewModel.trendingWallpapers.observe(viewLifecycleOwner){result ->
            when (result) {
                is Response.Loading -> {

                }

                is Response.Success -> {
                    if (view != null) {

                        lifecycleScope.launch(Dispatchers.IO) {
                            if (!datasetTrending) {

                                val list = result.data?.take(100)

                                list?.forEach {item->
                                    val model = CatResponse(item.id,item.image_name,item.cat_name,item.hd_image_url,item.compressed_image_url,null,item.likes,item.liked,item.unlocked,item.size,item.Tags,item.capacity)
                                    if (!cachedCatResponses.contains(model)){
                                        cachedCatResponses.add(model)
                                    }
                                }
                                Log.e(TAG, "initMostDownloadedData: " + list)

                                withContext(Dispatchers.Main) {
                                    adapter?.updateMoreData(cachedCatResponses)
                                }

                                datasetTrending = true
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

    private fun updateUIWithFetchedData() {

        val list = ArrayList<CatResponse?>()

        adapter =
            ApicategoriesListHorizontalAdapter(list, object :
                PositionCallback {
                override fun getPosition(position: Int) {
                    if (!isNavigationInProgress){
                        isNavigationInProgress = true
                        val allItems = adapter?.getAllItems()
                        SDKBaseController.getInstance().showInterstitialAds(
                            requireActivity(),
                            "mainscr_trending_tab_click_item",
                            "mainscr_trending_tab_click_item",
                            showLoading = true,
                            adsListener = object : CommonAdsListenerAdapter() {
                                override fun onAdsShowFail(errorCode: Int) {
                                    Log.e("********ADS", "onAdsShowFail: " + errorCode)

                                    if (isAdded){
                                        navigateToDestination(allItems!!, position)
                                    }
                                    //do something
                                }

                                override fun onAdsDismiss() {
                                    if (isAdded){
                                        navigateToDestination(allItems!!, position)
                                    }
                                }
                            }
                        )
                    }



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

        isNavigationInProgress = false
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }


}