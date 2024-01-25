package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.menuFragments

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.nfc.tech.MifareUltralight.PAGE_SIZE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.CommonAdsListenerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentHomeBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.SaveStateViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.ApiCategoriesListAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.HomeTabsFragment.Companion.navigationInProgress
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.GemsTextUpdate
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.GetLoginDetails
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.PositionCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.BlurView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyDialogs
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyHomeViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.PostDataOnServer
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
@AndroidEntryPoint
class HomeFragment : Fragment(){
    private var _binding: FragmentHomeBinding?=null
    private val binding get() = _binding!!
    private  val myViewModel: MyHomeViewModel by activityViewModels()
    private var navController: NavController? = null
    private var cachedCatResponses: ArrayList<CatResponse?> = ArrayList()
    private lateinit var myActivity : MainActivity

    private lateinit var adapter:ApiCategoriesListAdapter
    private val viewModel: SaveStateViewModel by viewModels()

    private var isFirstLoad = true

    var isLoadingMore = false
    val TAG = "HOMEFRAG"

    private lateinit var firebaseAnalytics: FirebaseAnalytics


    var dataset = false

    var startIndex = 0
    var oldPosition = 0

    var isNavigationInProgress = false



    var externalOpen = false

    private var addedItems: ArrayList<CatResponse?>? = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View{
        _binding = FragmentHomeBinding.inflate(inflater,container,false)
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myActivity = activity as MainActivity
        SDKBaseController.getInstance(). loadInterstitialAds(myActivity, "mainscr_trending_tab_click_item","mainscr_trending_tab_click_item")

        onCreatingCalling()
        setEvents()
    }


    private fun setEvents(){

        binding.swipeLayout.setOnRefreshListener {

            val newData = cachedCatResponses.filterNotNull()
            val nullAdd = addNullValueInsideArray(newData.shuffled())

            cachedCatResponses.clear()
            cachedCatResponses = nullAdd
            val initialItems = getItems(0, 30)
            startIndex = 0
            adapter.addNewData()
            Log.e(TAG, "initMostDownloadedData: " + initialItems)
            adapter.updateMoreData(initialItems)
            startIndex += 30



            binding.swipeLayout.isRefreshing = false

        }
    }
    private fun onCreatingCalling(){
        Log.d("TraceLogingHomaeHHH", "onCreatingCalling   ")
//        checkDailyReward()

        navController = findNavController()
        binding.progressBar.visibility = View.GONE

        val layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerviewAll.layoutManager = layoutManager
        binding.recyclerviewAll.addItemDecoration(RvItemDecore(3,5,false,10000))


        setAdapter()


        binding.recyclerviewAll.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                val totalItemCount = adapter.itemCount
                Log.e(TAG, "onScrolled: insdie scroll listener")
                if (lastVisibleItemPosition + 10 >= totalItemCount) {

                    isLoadingMore = true
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
    }

    private fun loadData() {


//        myViewModel.wallpaperData.observe(viewLifecycleOwner) { wallpapers ->
//            if (wallpapers != null) {
//
//                if (view != null) {
//
//                    lifecycleScope.launch(Dispatchers.IO) {
//                        if (!dataset) {
//                            val list = addNullValueInsideArray(wallpapers.shuffled())
//
//                            cachedCatResponses = list
//
//                            val initialItems = getItems(0, 30)
//
//                            Log.e(TAG, "initMostDownloadedData: " + initialItems)
//
//                            withContext(Dispatchers.Main) {
//                                adapter.updateMoreData(initialItems)
//                                startIndex += 30
//                            }
//
//                            dataset = true
//                        }
//                    }
//                }
//            } else {
//                Log.e(TAG, "initMostDownloadedData: no Data Found " )
//            }
//        }

        myViewModel.getAllTrendingWallpapers()

        myViewModel.trendingWallpapers.observe(viewLifecycleOwner){result ->
            when (result) {
                is Response.Loading -> {

                }

                is Response.Success -> {
                    if (view != null) {

                        lifecycleScope.launch(Dispatchers.IO) {
                            if (!dataset) {

                                val tempList =  ArrayList<CatResponse>()


                                result.data?.forEach {item ->
                                    val model = CatResponse(item.id,item.image_name,item.cat_name,item.hd_image_url,item.compressed_image_url,null,item.likes,item.liked,null,item.size,item.Tags,item.capacity)
                                    if (!tempList.contains(model)){
                                        tempList.add(model)
                                    }
                                }

                                val list = addNullValueInsideArray(tempList.shuffled())

                                cachedCatResponses = list

                                val initialItems = getItems(0, 30)

                                Log.e(TAG, "initMostDownloadedData: " + initialItems)

                                withContext(Dispatchers.Main) {
                                    adapter.updateMoreData(initialItems)
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





//        myViewModel.getWallpapers().observe(viewLifecycleOwner) { catResponses ->
//            if (catResponses != null) {
//                cachedCatResponses = catResponses
//
//
//                if (view != null) {
//                    // If the view is available, update the UI
//                    orignalList.clear()
//                    orignalList.addAll(catResponses)
//                    binding.retryBtn.visibility = View.GONE
//
//                    if (isFirstLoad) {
//                        isFirstLoad = false // Update the flag after the initial load
//                        binding.retryBtn.visibility = View.GONE
//                        updateUIWithFetchedData(catResponses)
//                        Log.e("********new Data", "loadData new first: "+catResponses.size )
//                        Log.e("********new Data", "loadData new first: "+catResponses )
//                    } else {
//
//                        Log.e("********new Data", "loadData more: "+catResponses.size )
//                        Log.e("********new Data", "loadData more: "+catResponses )
//
//                        val list = addNullValueInsideArray(catResponses)
//                        isLastPage = false
//                        adapter.updateMoreData(list)
//                    }
//
//
//                }
//            }else{
//                myViewModel.fetchWallpapers(requireContext(), binding.progressBar,currentPage.toString())
//
//                isFirstLoad = true
//
//                if (viewModel.getData()){
//                    binding.retryBtn.visibility = View.GONE
//                }else{
//                    binding.retryBtn.visibility = View.GONE
//                }
//
//            }
//        }


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



    private fun addNullValueInsideArray(data: List<CatResponse?>): ArrayList<CatResponse?>{


        val firstAdLineThreshold = if (AdConfig.firstAdLineViewListWallSRC != 0) AdConfig.firstAdLineViewListWallSRC else 4
        val firstLine = firstAdLineThreshold * 3

        val lineCount = if (AdConfig.lineCountViewListWallSRC != 0) AdConfig.lineCountViewListWallSRC else 5
        val lineC = lineCount * 3
        val newData = arrayListOf<CatResponse?>()

        for (i in data.indices){
            if (i > firstLine && (i - firstLine) % (lineC + 1)  == 0) {
                newData.add(null)



                Log.e("******NULL", "addNullValueInsideArray: null "+i )

            }else if (i == firstLine){
                newData.add(null)
                Log.e("******NULL", "addNullValueInsideArray: null first "+i )
            }
            Log.e("******NULL", "addNullValueInsideArray: not null "+i )
            newData.add(data[i])

        }
        Log.e("******NULL", "addNullValueInsideArray:size "+newData.size )




        return newData
    }
    private val fragmentScope: CoroutineScope by lazy { MainScope() }


    private fun setAdapter() {

        val list = ArrayList<CatResponse?>()
        adapter = ApiCategoriesListAdapter(list, object :
            PositionCallback {
            override fun getPosition(position: Int) {
                if (!navigationInProgress){

                    if (!isNavigationInProgress){
                        externalOpen = true
                        val allItems = adapter.getAllItems()
                        if (addedItems?.isNotEmpty() == true){
                            addedItems?.clear()
                        }

                        isNavigationInProgress = true


                        addedItems = allItems

                        oldPosition = position

                        SDKBaseController.getInstance().showInterstitialAds(
                            requireActivity(),
                            "mainscr_trending_tab_click_item",
                            "mainscr_trending_tab_click_item",
                            showLoading = true,
                            adsListener = object : CommonAdsListenerAdapter() {
                                override fun onAdsShowFail(errorCode: Int) {
                                    Log.e("********ADS", "onAdsShowFail: "+errorCode )
                                    navigateToDestination(allItems,position)
                                    //do something
                                }

                                override fun onAdsDismiss() {
                                    Log.e("********ADS", "onAdsDismiss: " )
                                    navigateToDestination(allItems,position)
                                }

                            }
                        )
                    }
                }
            }

            override fun getFavorites(position: Int) {
                //
            }
        },myActivity,"trending")
        adapter.setCoroutineScope(fragmentScope)
        binding.recyclerviewAll.adapter = adapter



    }



    private fun navigateToDestination(arrayList: ArrayList<CatResponse?>, position:Int) {
        Log.e(TAG, "navigateToDestination: inside", )


        viewModel.setCatList(arrayList.filterNotNull() as ArrayList<CatResponse>)
        viewModel.setData(false)
        val gson = Gson()
        val arrayListJson = gson.toJson(arrayList.filterNotNull())

        val countOfNulls = arrayList.subList(0, position).count { it == null }
        val sharedViewModel: SharedViewModel by activityViewModels()


        sharedViewModel.clearData()

        sharedViewModel.setData(arrayList.filterNotNull(), position - countOfNulls)



        Bundle().apply {
            Log.e(TAG, "navigateToDestination: inside bundle", )

            putString("from","trending")
            putInt("position",position - countOfNulls)
            findNavController().navigate(R.id.wallpaperViewFragment,this)
            navigationInProgress = false
        }

        isNavigationInProgress = false

    }

    override fun onResume() {
        super.onResume()

//        dataset = false
//        startIndex = 0

        loadData()
        if (dataset){

            Log.e(TAG, "onResume: Data set $dataset")
//            Log.e(TAG, "onResume: Data set ${addedItems?.size}")

            if (addedItems?.isEmpty() == true){
                Log.e(TAG, "onResume: "+cachedCatResponses.size )


            }
            adapter.updateMoreData(addedItems!!)

            binding.recyclerviewAll.layoutManager?.scrollToPosition(oldPosition)

        }

        if (isAdded){
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Trending")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, javaClass.simpleName)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }

    }

    override fun onPause() {
        super.onPause()
        Log.e(TAG, "onPause: ", )

        if (!externalOpen){
            val allItems = adapter.getAllItems()
            if (addedItems?.isNotEmpty() == true){
                addedItems?.clear()
            }

            addedItems?.addAll(allItems)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        isFirstLoad = true
        _binding =null
        fragmentScope.cancel()
    }

}