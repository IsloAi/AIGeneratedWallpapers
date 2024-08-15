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
import com.google.firebase.analytics.FirebaseAnalytics
import com.ikame.android.sdk.IKSdkController
import com.ikame.android.sdk.data.dto.pub.IKAdError
import com.ikame.android.sdk.format.intertial.IKInterstitialAd
import com.ikame.android.sdk.listener.pub.IKLoadAdListener
import com.ikame.android.sdk.listener.pub.IKLoadDisplayAdViewListener
import com.ikame.android.sdk.listener.pub.IKShowAdListener
import com.ikame.android.sdk.tracking.IKTrackingHelper
import com.ikame.android.sdk.widgets.IkmDisplayWidgetAdView
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.DialogCongratulationsBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentPopularWallpaperBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.ApicategoriesListHorizontalAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.MostUsedWallpaperAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.PopularSliderAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.AdEventListener
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MyApp
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.WallpaperViewFragment.Companion.isNavigated
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.PositionCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
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
class PopularWallpaperFragment () : Fragment(),AdEventListener {

    private var _binding: FragmentPopularWallpaperBinding? = null
    private val binding get() = _binding!!

    private lateinit var welcomeAdapter: PopularSliderAdapter

    @Inject
    lateinit var appDatabase: AppDatabase

    var startIndex = 0

    val catListViewmodel: MyViewModel by activityViewModels()

    companion object{
        var hasToNavigate = false
        var wallFromPopular = false
    }

//    var checkAppOpen = false

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

    val interAd = IKInterstitialAd()


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

        interAd.attachLifecycle(this.lifecycle)
// Load ad with a specific screen ID, considered as a unitId
        interAd.loadAd("mainscr_all_tab_click_item", object : IKLoadAdListener {
            override fun onAdLoaded() {
                // Ad loaded successfully
            }
            override fun onAdLoadFail(error: IKAdError) {
                // Handle ad load failure
            }
        })

        interAd.loadAd("mainscr_cate_tab_click_item", object : IKLoadAdListener {
            override fun onAdLoaded() {
                // Ad loaded successfully
            }
            override fun onAdLoadFail(error: IKAdError) {
                // Handle ad load failure
            }
        })

        interAd.loadAd("mainscr_trending_tab_click_item", object : IKLoadAdListener {
            override fun onAdLoaded() {
                // Ad loaded successfully
            }
            override fun onAdLoadFail(error: IKAdError) {
                // Handle ad load failure
            }
        })


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

    private fun setEvents(){
        binding.refresh.setOnRefreshListener {

            lifecycleScope.launch {
                val newData = cachedMostDownloaded.filterNotNull()
                val nullAdd = if (AdConfig.ISPAIDUSER){
                    newData as ArrayList<CatResponse?>
                }else{
                    addNullValueInsideArray(newData.shuffled())
                }
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
                Log.e(TAG, "getPosition: clicked" )
                if (!isNavigationInProgress){
                    hasToNavigate = true
                    externalOpen = true
                    val allItems = mostUsedWallpaperAdapter?.getAllItems()
                    if (addedItems?.isNotEmpty() == true){
                        addedItems?.clear()
                    }
                    isNavigationInProgress = true


                    addedItems = allItems

                    oldPosition = position

                    if (AdConfig.ISPAIDUSER){
                        if (isAdded){
                            navigateToDestination(allItems!!, position)
                        }
                    }else{
                        var shouldShowInterAd = true

                        if (AdConfig.avoidPolicyRepeatingInter == 1 && Constants.checkInter) {
                            if (isAdded) {
                                Constants.checkInter = false
                                navigateToDestination(allItems!!, position)
                                shouldShowInterAd = false // Skip showing the ad for this action
                            }
                        }

                        if (AdConfig.avoidPolicyOpenAdInter == 1 && checkAppOpen) {
                            if (isAdded) {
                                checkAppOpen = false
                                navigateToDestination(allItems!!, position)
                                Log.e(TAG, "app open showed")
                                shouldShowInterAd = false // Skip showing the ad for this action
                            }
                        }

                        if (shouldShowInterAd) {
                            showInterAdForAllItems(allItems, position)
                        }
//                        if (AdConfig.avoidPolicyOpenAdInter == 1 && checkAppOpen){
//                            if (isAdded){
//                                checkAppOpen = false
//                                navigateToDestination(allItems!!, position)
//                                Log.e(TAG, "app open showed: ", )
//                            }
//                        }else{
//                            showInterAdForAllItems(allItems, position)
//                        }
                    }


                }


            }

            override fun getFavorites(position: Int) {
                //
            }

        }, myActivity)


        mostUsedWallpaperAdapter!!.setCoroutineScope(fragmentScope)

        IKSdkController.loadNativeDisplayAd("mainscr_all_tab_scroll", object : IKLoadDisplayAdViewListener {
            override fun onAdLoaded(adObject: IkmDisplayWidgetAdView?) {
                if (isAdded && view!= null){
                    mostUsedWallpaperAdapter?.nativeAdView = adObject
                    binding.recyclerviewMostUsed.adapter = mostUsedWallpaperAdapter
                }
            }

            override fun onAdLoadFail(error: IKAdError) {
                // Handle ad load failure with view object
            }
        })

        binding.recyclerviewMostUsed.adapter = mostUsedWallpaperAdapter

        binding.recyclerviewMostUsed.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                Constants.checkInter = false
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

                }


            }
        })
    }

    private fun showInterAdForAllItems(
        allItems: ArrayList<CatResponse?>?,
        position: Int
    ) {
        interAd.showAd(
            requireActivity(),
            "mainscr_all_tab_click_item",
            adListener = object : IKShowAdListener {
                override fun onAdsShowFail(error: IKAdError) {
                    if (isAdded) {
                        navigateToDestination(allItems!!, position)
                    }
                }

                override fun onAdsDismiss() {
                    Constants.checkInter = true
                    // Handle ad dismissal
                }
            }
        )
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

                                        val newList = if (AdConfig.ISPAIDUSER){
                                            list.shuffled() as ArrayList<CatResponse?>
                                        }else{
                                            addNullValueInsideArray(list.shuffled())
                                        }

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


//        appDatabase.wallpapersDao()?.getAllWallpapersLive()?.observe(viewLifecycleOwner) {
//            if (it.isNotEmpty()) {
//                it?.let { data ->
//                    cachedMostDownloaded = arrayListOf()
//                    data.forEachIndexed { _, genericResponseModel ->
//                        cachedMostDownloaded.add(genericResponseModel)
//
//                    }
//                }
//            }
//        }



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

        if (wallFromPopular){
            if (isAdded){
                congratulationsDialog()
            }
        }


        if (isAdded){
            sendTracking("screen_active",Pair("action_type", "Tab"), Pair("action_name", "MainScr_PopTab_View"))
        }

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

        lifecycleScope.launch(Dispatchers.Main) {
            delay(1500)
            if (!isNavigated && hasToNavigate){
                navigateToDestination(addedItems!!,oldPosition)
            }
        }



    }


    private fun sendTracking(
        eventName: String,
        vararg param: Pair<String, String?>
    )
    {
        IKTrackingHelper.sendTracking( eventName, *param)
    }


    private fun congratulationsDialog() {
        val dialog = Dialog(requireContext())
        val bindingDialog = DialogCongratulationsBinding.inflate(LayoutInflater.from(requireContext()))
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

        welcomeAdapter =
            PopularSliderAdapter(welcomeItems, object : PopularSliderAdapter.joinButtons {
                override fun clickEvent(position: Int) {
                    when (position) {
                        0 -> {
                            (requireParentFragment() as HomeTabsFragment).navigateTOTabs("Category")
                        }

                        1 -> {
                            (requireParentFragment() as HomeTabsFragment).navigateTOTabs("Anime")
                        }

                        2 -> {
                            catListViewmodel.getAllCreations("4K")
                            if (AdConfig.ISPAIDUSER){
                                setFragment("4K")
                            }else{
                                interAd.showAd(
                                    requireActivity(),
                                    "mainscr_cate_tab_click_item",
                                    adListener = object : IKShowAdListener {
                                        override fun onAdsShowFail(error: IKAdError) {
                                            if (isAdded){
                                                setFragment("4K")
                                            }
                                        }
                                        override fun onAdsDismiss() {
                                            setFragment("4K")
                                        }
                                    }
                                )
                            }
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

                                Log.e(TAG, "initTrendingData: "+cachedCatResponses )


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

                        if (AdConfig.ISPAIDUSER){
                            if (isAdded){
                                navigateToDestination(allItems!!, position)
                            }
                        }else{
                            var shouldShowInterAd = true

                            if (AdConfig.avoidPolicyRepeatingInter == 1 && Constants.checkInter) {
                                if (isAdded) {
                                    Constants.checkInter = false
                                    navigateToDestination(allItems!!, position)
                                    shouldShowInterAd = false // Skip showing the ad for this action
                                }
                            }

                            if (AdConfig.avoidPolicyOpenAdInter == 1 && checkAppOpen) {
                                if (isAdded) {
                                    checkAppOpen = false
                                    navigateToDestination(allItems!!, position)
                                    Log.e(TAG, "app open showed")
                                    shouldShowInterAd = false // Skip showing the ad for this action
                                }
                            }

                            if (shouldShowInterAd) {
                                showInterAdForHorizontalList(allItems, position)
                            }
//                            if (AdConfig.avoidPolicyOpenAdInter == 1 && checkAppOpen){
//                                    if (isAdded){
//                                        checkAppOpen = false
//                                        navigateToDestination(allItems!!, position)
//                                        Log.e(TAG, "app open showed: ", )
//                                    }
//
//                            }else{
//                                showInterAdForHorizontalList(allItems, position)
//                            }
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

    private fun showInterAdForHorizontalList(
        allItems: ArrayList<CatResponse?>?,
        position: Int
    ) {
        interAd.showAd(
            requireActivity(),
            "mainscr_trending_tab_click_item",
            adListener = object : IKShowAdListener {
                override fun onAdsShowFail(error: IKAdError) {
                    if (isAdded) {
                        if (isAdded) {
                            navigateToDestination(allItems!!, position)
                        }
                    }
                }

                override fun onAdsDismiss() {
                    if (isAdded) {
                        Constants.checkInter = true
                        navigateToDestination(allItems!!, position)
                    }
                }
            }
        )
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
        Log.e(TAG, "navigateToDestination: " )

        try {
            val countOfNulls = arrayList.subList(0, position).count { it == null }
            val sharedViewModel: SharedViewModel by activityViewModels()

            Log.e(TAG, "navigateToDestination: ", )


            sharedViewModel.clearData()

            sharedViewModel.setData(arrayList.filterNotNull(), position - countOfNulls)

            lifecycleScope.launch(Dispatchers.Main) {

                Bundle().apply {
                    putString("from", "trending")
                    putString("wall","popular")
                    putInt("position", position - countOfNulls)
                    Log.e(TAG, "navigateToDestination: inside bundle", )

                    requireParentFragment().findNavController().navigate(R.id.wallpaperViewFragment, this)
                }
            }



            isNavigationInProgress = false
        }catch (e:IndexOutOfBoundsException){
            e.printStackTrace()
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    override fun onAdDismiss() {
        checkAppOpen = true
        Log.e(TAG, "app open dismissed: ", )
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