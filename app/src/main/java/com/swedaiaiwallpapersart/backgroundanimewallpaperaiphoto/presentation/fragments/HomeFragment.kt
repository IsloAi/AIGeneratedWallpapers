package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.google.firebase.analytics.FirebaseAnalytics
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.DialogCongratulationsBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentHomeBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.AdClickCounter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MaxInterstitialAds
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.SingleDatabaseResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.activity.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.adapters.ApiCategoriesListAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.fragments.HomeTabsFragment.Companion.navigationInProgress
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.PositionCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.viewmodels.DataFromRoomViewmodel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var myActivity: MainActivity
    private var navController: NavController? = null
    private lateinit var adapter: ApiCategoriesListAdapter
    var isNavigationInProgress = false
    var externalOpen = false
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private var addedItems: ArrayList<SingleDatabaseResponse?>? = ArrayList()
    private var cachedCatResponses: ArrayList<SingleDatabaseResponse?> = ArrayList()
    var oldPosition = 0
    val TAG = "HOMEFRAG"
    var dataset = false
    private val myViewModel: DataFromRoomViewmodel by viewModels()
    val sharedViewModel: SharedViewModel by activityViewModels()

    companion object {
        var hasToNavigateHome = false
        var wallFromHome = false
    }

    /*
    private val viewModel: SaveStateViewModel by viewModels()
    private var isFirstLoad = true
    var isLoadingMore = false
    var startIndex = 0
    */

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myActivity = activity as MainActivity
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        onCreatingCalling()
        setEvents()
    }

    private fun setEvents() {
        binding.swipeLayout.setOnRefreshListener {
            val newData = cachedCatResponses.filterNotNull().shuffled()
            lifecycleScope.launch(Dispatchers.IO) {
                val nullAdd = if (AdConfig.ISPAIDUSER) {
                    newData
                } else {
                    addNullValueInsideArray(newData.shuffled())
                }
                withContext(Dispatchers.Main) {
                    cachedCatResponses.clear()
                    cachedCatResponses = nullAdd as ArrayList<SingleDatabaseResponse?>
                    adapter.updateData(nullAdd as ArrayList<SingleDatabaseResponse?>)
                    binding.recyclerviewAll.scrollToPosition(0) // <-- this is what you need
                    binding.swipeLayout.isRefreshing = false
                }
            }
        }
    }

    private fun onCreatingCalling() {
        Log.d("TraceLogingHomaeHHH", "onCreatingCalling   ")
        navController = findNavController()
        val layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerviewAll.layoutManager = layoutManager
        binding.recyclerviewAll.addItemDecoration(RvItemDecore(3, 5, false, 10000))
        setAdapter()
        binding.recyclerviewAll.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
            }
        })
    }

    private val fragmentScope: CoroutineScope by lazy { MainScope() }

    private fun setAdapter() {
        val list = ArrayList<SingleDatabaseResponse?>()
        adapter = ApiCategoriesListAdapter(list, object :
            PositionCallback {
            override fun getPosition(position: Int) {
                if (!navigationInProgress) {
                    if (!isNavigationInProgress) {
                        hasToNavigateHome = true
                        externalOpen = true
                        val allItems = adapter.getAllItems()
                        if (addedItems?.isNotEmpty() == true) {
                            addedItems?.clear()
                        }
                        isNavigationInProgress = true
                        addedItems = allItems
                        oldPosition = position
                        if (isAdded) {
                            navigateToDestination(cachedCatResponses, position)
                        }
                    }
                }
            }

            override fun getFavorites(position: Int) {
                //
            }
        }, myActivity, "trending")
        adapter.setCoroutineScope(fragmentScope)

        if (binding.recyclerviewAll.adapter == null) {
            binding.recyclerviewAll.adapter = adapter
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

    private fun navigateToDestination(
        arrayList: ArrayList<SingleDatabaseResponse?>,
        position: Int
    ) {
        /*viewModel.setCatList(arrayList.filterNotNull() as ArrayList<CatResponse>)
        viewModel.setData(false)*/
        val countOfNulls = arrayList.subList(0, position).count { it == null }
        sharedViewModel.clearData()
        Log.e(TAG, "navigateToDestination: arrayList: ${arrayList.filterNotNull()}")
        sharedViewModel.setData(arrayList.filterNotNull(), position - countOfNulls)
        if (AdConfig.ISPAIDUSER) {
            Bundle().apply {
                Log.e(TAG, "navigateToDestination: inside bundle")
                putString("from", "Car")
                putString("wall", "home")
                putInt("position", position - countOfNulls)
                findNavController().navigate(R.id.wallpaperViewFragment, this)
                navigationInProgress = false
            }
        } else {
            if (AdClickCounter.shouldShowAd()) {
                MaxInterstitialAds.showInterstitialAd(requireActivity(), object : MaxAdListener {
                    override fun onAdLoaded(p0: MaxAd) {

                    }

                    override fun onAdDisplayed(p0: MaxAd) {}

                    override fun onAdHidden(p0: MaxAd) {
                        Bundle().apply {
                            Log.e(TAG, "navigateToDestination: inside bundle")
                            putString("from", "trending")
                            putString("wall", "home")
                            putInt("position", position - countOfNulls)
                            findNavController().navigate(R.id.wallpaperViewFragment, this)
                            navigationInProgress = false
                        }
                    }

                    override fun onAdClicked(p0: MaxAd) {}

                    override fun onAdLoadFailed(p0: String, p1: MaxError) {}

                    override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {}
                })
            } else {
                Bundle().apply {
                    Log.e(TAG, "navigateToDestination: inside bundle")
                    putString("from", "trending")
                    putString("wall", "home")
                    putInt("position", position - countOfNulls)
                    findNavController().navigate(R.id.wallpaperViewFragment, this)
                    navigationInProgress = false
                }
                AdClickCounter.increment()
            }
        }
        isNavigationInProgress = false
    }

    override fun onResume() {
        super.onResume()

        if (wallFromHome) {
            if (isAdded) {
                congratulationsDialog()
            }
        }
        loadData()
        if (dataset) {
            Log.e(TAG, "onResume: Data set $dataset")
            if (addedItems?.isEmpty() == true) {
                Log.e(TAG, "onResume: " + cachedCatResponses.size)
            }
            adapter.updateData(addedItems!!)
            binding.recyclerviewAll.layoutManager?.scrollToPosition(oldPosition)
        }

        if (isAdded) {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Trending Screen")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, javaClass.simpleName)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }
        lifecycleScope.launch(Dispatchers.Main) {
            delay(1500)
            if (!WallpaperViewFragment.isNavigated && hasToNavigateHome) {
                if (isAdded) {
                    navigateToDestination(addedItems!!, oldPosition)
                }
            }
        }

    }

    private fun loadData() {
        lifecycleScope.launch {
            myViewModel.fetchCarWallpapers()
            myViewModel.carWallpapers.collect { response ->
                if (response != null) {
                    cachedCatResponses = ArrayList(response)
                    val list = arrayListOf<SingleDatabaseResponse>()
                    response.forEach { item ->
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

                    Log.e("TAG", "loadData: ${list.size}")
                    adapter.updateData(ArrayList(list))
                }
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
        //var getReward = dialog?.findViewById<LinearLayout>(R.id.buttonGetReward)


        bindingDialog.continueBtn.setOnClickListener {
            wallFromHome = false
            dialog.dismiss()
        }

        dialog.show()
    }
}