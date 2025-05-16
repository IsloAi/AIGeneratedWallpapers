package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applovin.mediation.ads.MaxAdView
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.DialogCongratulationsBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentListViewBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.SingleDatabaseResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.activity.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.adapters.ApiCategoriesListAdapter
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
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ListViewFragment : Fragment() {
    private var _binding: FragmentListViewBinding? = null
    private val binding get() = _binding!!

    //val myViewModel: MyViewModel by activityViewModels()
    private var name = ""
    private var from = ""
    private lateinit var myActivity: MainActivity
    var isNavigationInProgress = false
    val sharedViewModel: SharedViewModel by activityViewModels()

    /*private val viewModel: SaveStateViewModel by activityViewModels()*/
    var adapter: ApiCategoriesListAdapter? = null
    private var cachedCatResponses: ArrayList<SingleDatabaseResponse?> = ArrayList()
    private var addedItems: ArrayList<SingleDatabaseResponse?>? = ArrayList()
    var dataset = false
    var oldPosition = 0
    var adcount = 0
    var totalADs = 0
    var externalOpen = false
    var startIndex = 0
    val TAG = "LISTVIEWCAT"
    private lateinit var adView: MaxAdView

    private val viewmodel: DataFromRoomViewmodel by viewModels()

    /*
    private val rewardedViewModel: RewardedViewModel by activityViewModels()*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListViewBinding.inflate(inflater, container, false)
        onCreateViewCalling()
        return binding.root
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
                loadData(name)
            }
        }

        if (name == "4K" && from == "category") {
            binding.catTitle.text = "AI Wallpapers"
        } else {
            binding.catTitle.text = name
        }
        binding.recyclerviewAll.layoutManager = GridLayoutManager(requireContext(), 3)

        binding.recyclerviewAll.addItemDecoration(RvItemDecore(3, 5, false, 10000))

        val list = ArrayList<SingleDatabaseResponse?>()
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
                cachedCatResponses = nullAdd as ArrayList<SingleDatabaseResponse?>
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
        lifecycleScope.launch {
            viewmodel.fetchTrendingWallpapers()
            viewmodel.trendingWallpapers.collect { wallpapers ->
                if (!dataset) {
                    val list1 = arrayListOf<SingleDatabaseResponse>()
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
                        if (!list1.contains(single)) {
                            list1.add(single)
                        }
                    }
                    // Unlock 20% randomly
                    val unlockCount = (list1.size * 0.2).toInt()
                    list1.shuffled().take(unlockCount).forEach { it.unlocked = true }
                    val list = if (AdConfig.ISPAIDUSER) {
                        list1.shuffled() as ArrayList<CatResponse?>
                    } else {
                        addNullValueInsideArray(list1.shuffled())
                    }
                    cachedCatResponses = list as ArrayList<SingleDatabaseResponse?>
                    withContext(Dispatchers.Main) {
                        adapter?.updateData(cachedCatResponses)
                        dataset = true
                    }
                }
            }
        }
    }

    private fun loadRewardedData() {
        lifecycleScope.launch {
            /*rewardedViewModel.allWallpapers.observe(viewLifecycleOwner) { result ->
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
            }*/
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

    private fun loadData(name: String) {

    }

    fun getItems(startIndex1: Int, chunkSize: Int): ArrayList<SingleDatabaseResponse?> {
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
            loadData(name)
        }
        if (dataset) {
            Log.e(TAG, "onResume: Data set $dataset")

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

    private suspend fun addNullValueInsideArray(data: List<SingleDatabaseResponse?>): ArrayList<SingleDatabaseResponse?> {
        return withContext(Dispatchers.IO) {
            val newData = arrayListOf<SingleDatabaseResponse?>()
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

    private fun navigateToDestination(
        arrayList: ArrayList<SingleDatabaseResponse?>,
        position: Int
    ) {
        if (position >= arrayList.size) {
            Log.e(TAG, "navigateToDestination: Position $position out of bounds ${arrayList.size} ")

            addedItems?.clear()
            addedItems = getItems(0, 30)
            adapter?.updateData(addedItems!!)
            isNavigationInProgress = false
            return
        }
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

}