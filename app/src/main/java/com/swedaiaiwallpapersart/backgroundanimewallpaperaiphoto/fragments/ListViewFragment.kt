package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.CommonAdsListenerAdapter
import com.bmik.android.sdk.listener.CustomSDKAdsListenerAdapter
import com.google.gson.Gson
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentListViewBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.ApiCategoriesListAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.PositionCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

class ListViewFragment : Fragment() {
    private var _binding: FragmentListViewBinding? = null
    private val binding get() = _binding!!
     val myViewModel: MyViewModel by activityViewModels()
    private var name = ""
    private var from = ""
    private var isLogin = true
    private lateinit var myActivity : MainActivity
    var isNavigationInProgress = false

    val sharedViewModel: SharedViewModel by activityViewModels()

    val orignalList = arrayListOf<CatResponse?>()
    var adapter:ApiCategoriesListAdapter ?= null

    private var cachedCatResponses: ArrayList<CatResponse?> = ArrayList()
    private var addedItems: ArrayList<CatResponse?>? = ArrayList()
    var dataset = false
    var oldPosition = 0

    var adcount = 0
    var totalADs = 0
    var externalOpen = false

    var startIndex = 0

    val TAG = "LISTVIEWCAT"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentListViewBinding.inflate(inflater,container,false)
        onCreateViewCalling()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SDKBaseController.getInstance()
            .loadBannerAds(
                requireActivity(),
                binding.adsWidget as? ViewGroup,
                "home_banner",
                " home_banner_tracking", object : CustomSDKAdsListenerAdapter() {
                    override fun onAdsLoaded() {
                        super.onAdsLoaded()
                        Log.e("*******ADS", "onAdsLoaded: Banner loaded", )
                    }

                    override fun onAdsLoadFail() {
                        super.onAdsLoadFail()
                        Log.e("*******ADS", "onAdsLoaded: Banner failed", )
                    }
                }
            )
    }
    private fun onCreateViewCalling(){
        myActivity = activity as MainActivity
        binding.progressBar.visibility = View.GONE
        binding.progressBar.setAnimation(R.raw.main_loading_animation)
         name = arguments?.getString("name").toString()
         from = arguments?.getString("from").toString()
        Log.d("tracingNameCategory", "onViewCreated: name $name")
        binding.catTitle.text = name
        binding.recyclerviewAll.layoutManager = GridLayoutManager(requireContext(), 3)

        binding.recyclerviewAll.addItemDecoration(RvItemDecore(3,5,false,10000))

        val list = ArrayList<CatResponse?>()
         adapter = ApiCategoriesListAdapter(list, object :
            PositionCallback {
            override fun getPosition(position: Int) {

                if (!isNavigationInProgress){

                    isNavigationInProgress = true
                externalOpen = true
                val allItems = adapter?.getAllItems()
                if (addedItems?.isNotEmpty() == true) {
                    addedItems?.clear()
                }


                addedItems = allItems

                oldPosition = position

                SDKBaseController.getInstance().showInterstitialAds(
                    requireActivity(),
                    "categoryscr_fantasy_click_item",
                    "categoryscr_fantasy_click_item",
                    showLoading = true,
                    adsListener = object : CommonAdsListenerAdapter() {
                        override fun onAdsShowFail(errorCode: Int) {
                            navigateToDestination(allItems!!, position)
                            Log.e("********ADS", "onAdsShowFail: " + errorCode)
                            //do something
                        }

                        override fun onAdsDismiss() {
                            navigateToDestination(allItems!!, position)
                        }
                    }
                )

            }



            }

            override fun getFavorites(position: Int) {
            }
        },myActivity,"category")


        adapter!!.setCoroutineScope(fragmentScope)
        binding.recyclerviewAll.adapter = adapter


        binding.recyclerviewAll.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                val totalItemCount = adapter!!.itemCount
                Log.e(TAG, "onScrolled: insdie scroll listener")
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
        }

        binding.swipeLayout.setOnRefreshListener {

            val newData = cachedCatResponses.filterNotNull()
            val nullAdd = addNullValueInsideArray(newData.shuffled())

            cachedCatResponses.clear()
            cachedCatResponses = nullAdd
            val initialItems = getItems(0, 30)
            startIndex = 0
            adapter?.addNewData()
            Log.e(TAG, "initMostDownloadedData: " + initialItems)
            adapter?.updateMoreData(initialItems)
            startIndex += 30



            binding.swipeLayout.isRefreshing = false

        }
    }
    private fun loadData() {
        Log.d("functionCallingTest", "onCreateCustom:  home on create")
        // Observe the LiveData in the ViewModel and update the UI accordingly
        myViewModel.getWallpapers().observe(viewLifecycleOwner) { catResponses ->
            if (catResponses != null) {

                if (!dataset) {
                    val list = addNullValueInsideArray(catResponses.shuffled())

                    cachedCatResponses = list

                    val initialItems = getItems(0, 30)

                    Log.e(TAG, "initMostDownloadedData: " + initialItems)

                        adapter?.updateMoreData(initialItems)
                        startIndex += 30
                    dataset = true
                }

                Log.e(TAG, "loadData: "+catResponses )
            }else{
                Log.e(TAG, "loadData: "+catResponses )
            }
        }
    }

    fun getItems(startIndex1: Int, chunkSize: Int): ArrayList<CatResponse?> {
        val endIndex = startIndex1 + chunkSize
        if (startIndex1 >= cachedCatResponses.size) {
            return arrayListOf()
        } else {
            val subList = cachedCatResponses.subList(
                startIndex1,
                endIndex.coerceAtMost(cachedCatResponses.size)
            )
            return ArrayList(subList)
        }
    }

    override fun onResume() {
        super.onResume()

        loadData()

        if (dataset){

            Log.e(TAG, "onResume: Data set $dataset")
            Log.e(TAG, "onResume: Data set ${addedItems?.size}")

            if (addedItems?.isEmpty() == true){
                Log.e(TAG, "onResume: "+cachedCatResponses.size )


            }
            adapter?.updateMoreData(addedItems!!)

            binding.recyclerviewAll.layoutManager?.scrollToPosition(oldPosition)

        }

    }

    override fun onPause() {
        super.onPause()

        if (!externalOpen){
            val allItems = adapter?.getAllItems()
            if (addedItems?.isNotEmpty() == true){
                addedItems?.clear()
            }

            addedItems = allItems
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
                    totalADs++
                    Log.e("******NULL", "addNullValueInsideArray adcount: "+adcount )
                    Log.e("******NULL", "addNullValueInsideArray adcount: "+totalADs )

                Log.e("******NULL", "addNullValueInsideArray: null "+i )

            }else if (i == firstLine){
                newData.add(null)
                totalADs++
                Log.e("******NULL", "addNullValueInsideArray adcount: "+adcount )
                Log.e("******NULL", "addNullValueInsideArray adcount: "+totalADs )

                Log.e("******NULL", "addNullValueInsideArray: null first "+i )
            }
            Log.e("******NULL", "addNullValueInsideArray: not null "+i )
            newData.add(data[i])

        }
        Log.e("******NULL", "addNullValueInsideArray:size "+newData.size )




        return newData
    }
    private val fragmentScope: CoroutineScope by lazy { MainScope() }
    private fun navigateToDestination(arrayList: ArrayList<CatResponse?>, position:Int) {
        var newPosition = position
        var totalAdsCount = 0
        val firstAdLineThreshold = if (AdConfig.firstAdLineViewListWallSRC != 0) AdConfig.firstAdLineViewListWallSRC else 4
        val firstLine = firstAdLineThreshold * 2

        val lineCount = if (AdConfig.lineCountViewListWallSRC != 0) AdConfig.lineCountViewListWallSRC else 5
        val lineC = lineCount * 2

        for (i in arrayList.indices){
            if (i > firstLine && (i - firstLine) % (lineC + 1)  == 0) {
                if (i <= newPosition){
                    totalAdsCount++

                }
            }else if (i == firstLine){
                totalAdsCount++
            }


        }


        val gson = Gson()
        val arrayListJson = gson.toJson(arrayList.filterNotNull())
//        val countOfNulls = arrayList.count { it == null }
        newPosition = if (position == firstLine){
            position - totalAdsCount
        }else if (position < firstLine){
            position
        }else{
            position - totalAdsCount
        }

        val countOfNulls = arrayList.subList(0, position).count { it == null }

        sharedViewModel.clearData()

        sharedViewModel.setData(arrayList.filterNotNull(), position - countOfNulls)
        val bundle =  Bundle().apply {
            putString("from",from)
            putInt("position",position - countOfNulls)
        }
        findNavController().navigate(R.id.action_listViewFragment_to_wallpaperViewFragment,bundle)

        isNavigationInProgress = false

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        fragmentScope.cancel()
    }

}