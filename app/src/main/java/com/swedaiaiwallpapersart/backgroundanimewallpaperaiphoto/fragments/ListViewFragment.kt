package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applovin.mediation.ads.MaxAdView
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.DialogCongratulationsBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentListViewBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.SaveStateViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.ApiCategoriesListAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.AdEventListener
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MyApp
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.PositionCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants.Companion.checkAppOpen
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.MostDownloadedViewmodel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.RewardedViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListViewFragment : Fragment(), AdEventListener {
    private var _binding: FragmentListViewBinding? = null
    private val binding get() = _binding!!
    val myViewModel: MyViewModel by activityViewModels()
    private var name = ""
    private var from = ""
    private lateinit var myActivity: MainActivity
    var isNavigationInProgress = false
    private val viewModel: SaveStateViewModel by activityViewModels()
    val sharedViewModel: SharedViewModel by activityViewModels()
    var adapter: ApiCategoriesListAdapter? = null
    private var cachedCatResponses: ArrayList<CatResponse?> = ArrayList()
    private var addedItems: ArrayList<CatResponse?>? = ArrayList()
    var dataset = false
    var oldPosition = 0
    var adcount = 0
    var totalADs = 0
    var externalOpen = false
    var startIndex = 0
    val TAG = "LISTVIEWCAT"
    private lateinit var adView: MaxAdView

    private val mostDownloadedViewmodel: MostDownloadedViewmodel by activityViewModels()
    private val rewardedViewModel: RewardedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListViewBinding.inflate(inflater, container, false)
        onCreateViewCalling()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        (myActivity.application as MyApp).registerAdEventListener(this)
    }

    private fun onCreateViewCalling() {
        myActivity = activity as MainActivity
        binding.progressBar.visibility = View.GONE
        binding.progressBar.setAnimation(R.raw.main_loading_animation)
        name = arguments?.getString("name").toString()
        from = arguments?.getString("from").toString()
        Log.d("tracingNameCategory", "onViewCreated: name $name")

        when (name) {
            "Trending" -> {
                initTrendingData()
            }

            "Vip" -> {
                sharedViewModel.clearData()
                loadRewardedData()
            }

            else -> {
                viewModel.selectedTab.observe(viewLifecycleOwner) {
                    name = name.ifEmpty {
                        it.also { loadData() }
                    }
                }
            }
        }

        if (name == "4K" && from == "category") {
            binding.catTitle.text = "AI Wallpapers"
        } else {
            binding.catTitle.text = name
        }
        binding.recyclerviewAll.layoutManager = GridLayoutManager(requireContext(), 3)

        binding.recyclerviewAll.addItemDecoration(RvItemDecore(3, 5, false, 10000))

        val list = ArrayList<CatResponse?>()
        adapter = ApiCategoriesListAdapter(list, object : PositionCallback {
            override fun getPosition(position: Int) {

                if (!isNavigationInProgress) {

                    hasToNavigateList = true
                    isNavigationInProgress = true
                    externalOpen = true
                    val allItems = adapter?.getAllItems()
                    if (addedItems?.isNotEmpty() == true) {
                        addedItems?.clear()
                    }
                    addedItems = allItems
                    oldPosition = position

                    navigateToDestination(allItems!!, position)

                }
            }

            override fun getFavorites(position: Int) {
            }
        }, myActivity, from)

        adapter!!.setCoroutineScope(fragmentScope)

        binding.recyclerviewAll.adapter = adapter

        binding.recyclerviewAll.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                Constants.checkInter = false
                checkAppOpen = false
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                val totalItemCount = adapter!!.itemCount
                if (lastVisibleItemPosition + 10 >= totalItemCount) {
                    // End of list reached
                    val nextItems = getItems(startIndex, 30)
                    if (nextItems.isNotEmpty()) {
                        Log.e(TAG, "onScrolled: inside 3 coondition")
                        adapter?.updateMoreData(nextItems)
                        startIndex += 30 // Update startIndex for the next batch of data
                    } else {
                        Log.e(TAG, "onScrolled: inside 4 coondition")
                    }

                }


            }
        })

        binding.toolbar.setOnClickListener {
            findNavController().navigateUp()
            Constants.checkInter = false
            checkAppOpen = false
        }

        binding.swipeLayout.setOnRefreshListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val newData = cachedCatResponses.filterNotNull().shuffled()
                val nullAdd = if (AdConfig.ISPAIDUSER) {
                    newData as ArrayList<CatResponse?>
                } else {
                    addNullValueInsideArray(newData.shuffled())
                }

                cachedCatResponses.clear()
                cachedCatResponses = nullAdd
                val initialItems = getItems(0, 30)
                startIndex = 0
                withContext(Dispatchers.Main) {
                    adapter?.addNewData()
                    Log.e(TAG, "initMostDownloadedData: " + initialItems)
                    adapter?.updateMoreData(initialItems)
                    startIndex += 30



                    binding.swipeLayout.isRefreshing = false
                }

            }


        }

    }

    private fun initTrendingData() {
        mostDownloadedViewmodel.trendingWallpapers.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Response.Loading -> {

                }

                is Response.Success -> {
                    if (!dataset) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            var tempList = ArrayList<CatResponse>()
                            result.data?.forEach { item ->
                                val model = CatResponse(
                                    item.id,
                                    item.image_name,
                                    item.cat_name,
                                    item.hd_image_url,
                                    item.compressed_image_url,
                                    null,
                                    item.likes,
                                    item.liked,
                                    false,
                                    item.size,
                                    item.Tags,
                                    item.capacity
                                )
                                if (!tempList.contains(model)) {
                                    tempList.add(model)
                                }
                            }
                            // Unlock 20% randomly
                            val unlockCount = (tempList.size * 0.2).toInt()
                            tempList.shuffled().take(unlockCount).forEach { it.unlockimges = true }

                            val list = if (AdConfig.ISPAIDUSER) {
                                tempList.shuffled() as ArrayList<CatResponse?>
                            } else {
                                addNullValueInsideArray(tempList.shuffled())
                            }
                            cachedCatResponses = list
                            withContext(Dispatchers.Main) {
                                adapter?.updateData(cachedCatResponses)
                                dataset = true
                            }
                        }
                    }
                }
                is Response.Error -> {
                    Log.e("TAG", "error: ${result.message}")
                    Toast.makeText(requireContext(), "${result.message}", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    private fun loadRewardedData() {

        lifecycleScope.launch {
            rewardedViewModel.allWallpapers.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Response.Error -> {

                    }

                    is Response.Success -> {
                        val tempList = ArrayList<CatResponse>()
                        result.data?.forEach { item ->
                            val model = CatResponse(
                                id = item.id,
                                image_name = item.image_name,
                                cat_name = item.cat_name,
                                hd_image_url = item.url,
                                compressed_image_url = null,
                                gems = null,
                                likes = item.likes,
                                liked = item.liked,
                                unlockimges = true,
                                img_size = item.size,
                                Tags = item.Tags,
                                capacity = item.capacity
                            )
                            if (!tempList.contains(model)) {
                                tempList.add(model)
                            }
                        }
                        val processedList = if (AdConfig.ISPAIDUSER) {
                            tempList.shuffled()
                        } else {
                            SharedViewModel().addNullValuesToCatResponseList(tempList.shuffled())
                                .filterNotNull()
                        }
                        sharedViewModel.setCatResponseList(processedList)
                    }

                    else -> {}
                }
            }
        }
        sharedViewModel.catResponseList.observe(viewLifecycleOwner) { catResponses ->

            if (catResponses.isNotEmpty()) {
                cachedCatResponses = ArrayList(catResponses)
                val initialItems = getItems(0, 30)
                adapter?.updateMoreData(initialItems)
                startIndex += 30
                dataset = true
            }
        }
    }

    private fun loadData() {
        myViewModel.fetchWallpapers(requireContext(), name)

        myViewModel.getWallpapers().observe(viewLifecycleOwner) { catResponses ->
            if (catResponses != null) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val list = addNullValueInsideArray(catResponses.shuffled())
                    cachedCatResponses = list
                    withContext(Dispatchers.Main) {
                        //adapter?.updateMoreData(initialItems)
                        adapter?.updateData(list)
                        //startIndex += 30
                        dataset = true
                    }
                }
            } else {
                Log.d("responseOk", "No wallpapers found or error occurred")
            }
        }
    }

    fun getItems(startIndex1: Int, chunkSize: Int): ArrayList<CatResponse?> {
        val endIndex = startIndex1 + chunkSize
        if (startIndex1 >= cachedCatResponses.size) {
            return arrayListOf()
        } else {
            val subList = cachedCatResponses.subList(
                startIndex1, endIndex.coerceAtMost(cachedCatResponses.size)
            )
            return ArrayList(subList)
        }
    }

    override fun onResume() {
        super.onResume()

        createBannerAd()
        if (wallFromList) {
            if (isAdded) {
                congratulationsDialog()
            }
        }
        if (name == "Trending") {
            initTrendingData()
        } else {
            loadData()
        }
        if (dataset) {

            Log.e(TAG, "onResume: Data set $dataset")
            Log.e(TAG, "onResume: Data set ${addedItems?.size}")

            if (addedItems?.isEmpty() == true) {
                Log.e(TAG, "onResume: " + cachedCatResponses.size)
            }
            adapter?.updateMoreData(addedItems!!)
            binding.recyclerviewAll.layoutManager?.scrollToPosition(oldPosition)
        }
        lifecycleScope.launch(Dispatchers.Main) {
            delay(1500)
            if (!WallpaperViewFragment.isNavigated && hasToNavigateList) {
                navigateToDestination(addedItems!!, oldPosition)
            }
        }

    }

    override fun onPause() {
        super.onPause()
        val allItems = adapter?.getAllItems()
        if (allItems?.isNotEmpty() == true) {
            addedItems = allItems
        }
    }

    suspend fun addNullValueInsideArray(data: List<CatResponse?>): ArrayList<CatResponse?> {
        return withContext(Dispatchers.IO) {
            val newData = arrayListOf<CatResponse?>()
            for (i in data.indices) {
                newData.add(data[i]) // Add the current item
                // After every 15 items, add a null value (excluding the last item)
                if ((i + 1) % 15 == 0) {
                    newData.add(null)
                }
            }
            newData
        }
    }

    private val fragmentScope: CoroutineScope by lazy { MainScope() }

    private fun navigateToDestination(arrayList: ArrayList<CatResponse?>, position: Int) {
        /*if (position >= arrayList.size) {
            Log.e(TAG, "navigateToDestination: Position $position out of bounds ${arrayList.size} ")

            addedItems?.clear()
            addedItems = getItems(0, 30)
            adapter?.updateData(addedItems!!)
            isNavigationInProgress = false
            return
        }*/
        val countOfNulls = arrayList.subList(0, position).count { it == null }

        sharedViewModel.clearData()

        sharedViewModel.setData(arrayList.filterNotNull(), position - countOfNulls)
        Bundle().apply {
            putString("from", from)
            putString("wall", "list")
            putInt("position", position - countOfNulls)
            findNavController().navigate(R.id.wallpaperViewFragment, this)
        }
        isNavigationInProgress = false
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
        bindingDialog.continueBtn.setOnClickListener {
            wallFromList = false
            dialog.dismiss()
        }

        dialog.show()
    }

    companion object {
        var hasToNavigateList = false
        var wallFromList = false
    }

    private fun createBannerAd() {
        adView = MaxAdView(AdConfig.applovinAndroidBanner, requireContext())

        // Stretch to the width of the screen for banners to be fully functional
        val width = ViewGroup.LayoutParams.MATCH_PARENT

        // Banner height on phones and tablets is 50 and 90, respectively
        val heightPx = resources.getDimensionPixelSize(R.dimen.banner_height)

        adView.layoutParams = FrameLayout.LayoutParams(width, heightPx)

        // Set background color for banners to be fully functional
        adView.setBackgroundColor(resources.getColor(R.color.new_main_background))

        val rootView = binding.bannerAd
        rootView.addView(adView)

        // Load the ad
        adView?.loadAd()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        fragmentScope.cancel()
    }

    override fun onAdDismiss() {
        checkAppOpen = true
    }

    override fun onAdLoading() {}
    override fun onAdsShowTimeout() {}
    override fun onShowAdComplete() {}
    override fun onShowAdFail() {}
}