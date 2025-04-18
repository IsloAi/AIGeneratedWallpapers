package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.menuFragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
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
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.SaveStateViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.ApiCategoriesListAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.AdEventListener
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MaxInterstitialAds
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MyApp
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.HomeTabsFragment.Companion.navigationInProgress
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.WallpaperViewFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.PositionCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants.Companion.checkAppOpen
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class HomeFragment : Fragment(), AdEventListener {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    //private  val myViewModel: MyHomeViewModel by activityViewModels()
    private var navController: NavController? = null
    private var cachedCatResponses: ArrayList<CatResponse?> = ArrayList()
    private lateinit var myActivity: MainActivity
    private lateinit var adapter: ApiCategoriesListAdapter
    private val viewModel: SaveStateViewModel by viewModels()

    private var isFirstLoad = true

    var isLoadingMore = false
    val TAG = "HOMEFRAG"

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    var dataset = false

    var startIndex = 0
    var oldPosition = 0

    val myViewModel: MyViewModel by activityViewModels()

    var isNavigationInProgress = false

    var externalOpen = false

    private var addedItems: ArrayList<CatResponse?>? = ArrayList()

    //var checkAppOpen = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        (myActivity.application as MyApp).registerAdEventListener(this)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myActivity = activity as MainActivity
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
                    cachedCatResponses = nullAdd as ArrayList<CatResponse?>
                    adapter.updateData(nullAdd as ArrayList<CatResponse?>)
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
                /*val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                val totalItemCount = adapter.itemCount
                Log.e(TAG, "onScrolled: insdie scroll listener")
                if (lastVisibleItemPosition + 10 >= totalItemCount) {

                    isLoadingMore = true
                    val nextItems = getItems(startIndex, 30)
                    if (nextItems.isNotEmpty()) {
                        Log.e(TAG, "onScrolled: inside 3 coondition")
                        adapter.updateData(nextItems)
                        startIndex += 30 // Update startIndex for the next batch of data
                    } else {
                        Log.e(TAG, "onScrolled: inside 4 coondition")
                    }

                }*/


            }
        })
    }

    private fun loadData() {

        myViewModel.getAllCreations("Car")

        myViewModel.catWallpapers.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Response.Loading -> {

                }

                is Response.Success -> {
                    if (view != null) {

                        lifecycleScope.launch(Dispatchers.IO) {
                            if (!dataset) {
                                val tempList = ArrayList<CatResponse>()
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
                                        item.unlocked,
                                        item.size,
                                        item.Tags,
                                        item.capacity
                                    )
                                    if (!tempList.contains(model)) {
                                        tempList.add(model)
                                    }
                                }
                                val list = if (AdConfig.ISPAIDUSER) {
                                    tempList.shuffled() as ArrayList<CatResponse?>
                                } else {
                                    addNullValueInsideArray(tempList.shuffled())
                                }
                                cachedCatResponses = list
                                withContext(Dispatchers.Main) {
                                    adapter.updateData(cachedCatResponses)
                                    startIndex += 30
                                }
                                dataset = true
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
        if (startIndex1 >= cachedCatResponses.size) {
            return arrayListOf()
        } else {
            isLoadingMore = false
            val subList = cachedCatResponses.subList(
                startIndex1,
                endIndex.coerceAtMost(cachedCatResponses.size)
            )
            return ArrayList(subList)
        }
    }

    private suspend fun addNullValueInsideArray(data: List<CatResponse?>): ArrayList<CatResponse?> {
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

    private fun setAdapter() {

        val list = ArrayList<CatResponse?>()
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
                            navigateToDestination(allItems, position)
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

    private fun navigateToDestination(arrayList: ArrayList<CatResponse?>, position: Int) {
        Log.e(TAG, "navigateToDestination: inside")


        viewModel.setCatList(arrayList.filterNotNull() as ArrayList<CatResponse>)
        viewModel.setData(false)

        val countOfNulls = arrayList.subList(0, position).count { it == null }
        val sharedViewModel: SharedViewModel by activityViewModels()


        sharedViewModel.clearData()

        sharedViewModel.setData(arrayList.filterNotNull(), position - countOfNulls)

        if (AdConfig.ISPAIDUSER) {
            Bundle().apply {
                Log.e(TAG, "navigateToDestination: inside bundle")
                putString("from", "trending")
                putString("wall", "home")
                putInt("position", position - countOfNulls)
                findNavController().navigate(R.id.wallpaperViewFragment, this)
                navigationInProgress = false
            }
        } else {
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
//        var getReward = dialog?.findViewById<LinearLayout>(R.id.buttonGetReward)


        bindingDialog.continueBtn.setOnClickListener {
            wallFromHome = false
            dialog.dismiss()
        }

        dialog.show()
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
//            Log.e(TAG, "onResume: Data set ${addedItems?.size}")

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

    companion object {
        var hasToNavigateHome = false
        var wallFromHome = false
    }

    override fun onPause() {
        super.onPause()
        Log.e(TAG, "onPause: ")

        if (!externalOpen) {
            val allItems = adapter.getAllItems()
            if (addedItems?.isNotEmpty() == true) {
                addedItems?.clear()
            }

            addedItems?.addAll(allItems)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        isFirstLoad = true
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