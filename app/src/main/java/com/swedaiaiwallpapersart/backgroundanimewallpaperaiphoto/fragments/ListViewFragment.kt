package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ikame.android.sdk.IKSdkController
import com.ikame.android.sdk.data.dto.pub.IKAdError
import com.ikame.android.sdk.format.intertial.IKInterstitialAd
import com.ikame.android.sdk.listener.pub.IKLoadAdListener
import com.ikame.android.sdk.listener.pub.IKLoadDisplayAdViewListener
import com.ikame.android.sdk.listener.pub.IKShowAdListener
import com.ikame.android.sdk.listener.pub.IKShowWidgetAdListener
import com.ikame.android.sdk.widgets.IkmDisplayWidgetAdView
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
    private lateinit var myActivity : MainActivity
    var isNavigationInProgress = false

    private val viewModel: SaveStateViewModel by activityViewModels()

    val sharedViewModel: SharedViewModel by activityViewModels()

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

    private val mostDownloadedViewmodel: MostDownloadedViewmodel by activityViewModels()

    val interAd = IKInterstitialAd()

//    var checkAppOpen = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentListViewBinding.inflate(inflater,container,false)
        onCreateViewCalling()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (AdConfig.ISPAIDUSER){
            binding.adsView.visibility = View.GONE
        }else{
            interAd.attachLifecycle(this.lifecycle)
// Load ad with a specific screen ID, considered as a unitId
            interAd.loadAd("categoryscr_click_item", object : IKLoadAdListener {
                override fun onAdLoaded() {
                    Log.d(TAG, "onAdLoaded: ")
                    // Ad loaded successfully
                }
                override fun onAdLoadFail(error: IKAdError) {
                    Log.d(TAG, "onAdLoadFail1: $error")
                    // Handle ad load failure
                }
            })

            binding.adsView.attachLifecycle(lifecycle)
            binding.adsView.loadAd("categoryscr_bottom", object : IKShowWidgetAdListener {
                override fun onAdShowed() {}
                override fun onAdShowFail(error: IKAdError) {
                    Log.d(TAG, "onAdLoadFail2: $error")
//                    binding.adsView?.visibility = View.GONE
                }

            })
        }

    }

    override fun onStart() {
        super.onStart()
        (myActivity.application as MyApp).registerAdEventListener(this)

    }
    private fun onCreateViewCalling(){
        myActivity = activity as MainActivity
        binding.progressBar.visibility = View.GONE
        binding.progressBar.setAnimation(R.raw.main_loading_animation)
        name = arguments?.getString("name").toString()
        from = arguments?.getString("from").toString()
        Log.d("tracingNameCategory", "onViewCreated: name $name")



        if (name == "Trending"){
            initTrendingData()
        }else{
            viewModel.selectedTab.observe(viewLifecycleOwner){
                Log.e(TAG, "onCreateViewCalling: $name")
                Log.e(TAG, "onCreateViewCalling: $it")
                if (name == ""){
                    name = it
                    loadData()
                }
            }
        }


        binding.catTitle.text = name
        binding.recyclerviewAll.layoutManager = GridLayoutManager(requireContext(), 3)

        binding.recyclerviewAll.addItemDecoration(RvItemDecore(3,5,false,10000))

        val list = ArrayList<CatResponse?>()
        adapter = ApiCategoriesListAdapter(list, object :
            PositionCallback {
            override fun getPosition(position: Int) {

                if (!isNavigationInProgress){

                    hasToNavigateList = true

                    isNavigationInProgress = true
                    externalOpen = true
                    val allItems = adapter?.getAllItems()
                    if (addedItems?.isNotEmpty() == true) {
                        addedItems?.clear()
                    }


                    addedItems = allItems
                    oldPosition = position

                    if (AdConfig.ISPAIDUSER){
                        navigateToDestination(allItems!!, position)
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
                            showInterAd(allItems, position)
                        }
//                        if (AdConfig.avoidPolicyOpenAdInter == 1 && checkAppOpen){
//                            if (isAdded){
//                                checkAppOpen = false
//                                navigateToDestination(allItems!!, position)
//                                Log.e(TAG, "app open showed: ", )
//                            }
//                        }else{
//                            showInterAd(allItems, position)
//                        }
//
//

                    }



                }



            }

            override fun getFavorites(position: Int) {
            }
        },myActivity,"category")


        adapter!!.setCoroutineScope(fragmentScope)

        IKSdkController.loadNativeDisplayAd("categoryscr_scroll_view", object :
            IKLoadDisplayAdViewListener {
            override fun onAdLoaded(adObject: IkmDisplayWidgetAdView?) {
                if (isAdded && view!= null){
                    adapter?.nativeAdView = adObject
                    binding.recyclerviewAll.adapter = adapter
                }
            }

            override fun onAdLoadFail(error: IKAdError) {
                Log.d(TAG, "onAdLoadFail: $error")
                // Handle ad load failure with view object
            }
        })


        binding.recyclerviewAll.adapter = adapter

        binding.recyclerviewAll.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                Constants.checkInter = false
                checkAppOpen = false
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
            Constants.checkInter = false
            checkAppOpen = false
        }

        binding.swipeLayout.setOnRefreshListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val newData = cachedCatResponses.filterNotNull().shuffled()
                val nullAdd = if (AdConfig.ISPAIDUSER){
                    newData as ArrayList<CatResponse?>
                }else{
                    addNullValueInsideArray(newData.shuffled())
                }

                cachedCatResponses.clear()
                cachedCatResponses = nullAdd
                val initialItems = getItems(0, 30)
                startIndex = 0
                withContext(Dispatchers.Main){
                    adapter?.addNewData()
                    Log.e(TAG, "initMostDownloadedData: " + initialItems)
                    adapter?.updateMoreData(initialItems)
                    startIndex += 30



                    binding.swipeLayout.isRefreshing = false
                }

            }



        }
    }

    private fun showInterAd(
        allItems: ArrayList<CatResponse?>?,
        position: Int
    ) {
        interAd.showAd(
            requireActivity(),
            "categoryscr_click_item",
            adListener = object : IKShowAdListener {
                override fun onAdsShowFail(error: IKAdError) {
                    if (isAdded) {
                        //IKAdError(code=8018, message=No screen ID associated with the ad.)
                        Log.d(TAG, "onAdsShowFail: $error")
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

    private fun initTrendingData() {
        mostDownloadedViewmodel.trendingWallpapers.observe(viewLifecycleOwner){result ->
            when (result) {
                is Response.Loading -> {

                }

                is Response.Success -> {
                    if (!dataset) {

                        lifecycleScope.launch(Dispatchers.IO) {
                            var tempList = ArrayList<CatResponse>()

                            result.data?.forEach {item ->
                                val model = CatResponse(item.id,item.image_name,item.cat_name,item.hd_image_url,item.compressed_image_url,null,item.likes,item.liked,item.unlocked,item.size,item.Tags,item.capacity)
                                if (!tempList.contains(model)){
                                    tempList.add(model)
                                }
                            }


                            val list = if (AdConfig.ISPAIDUSER){
                                tempList.shuffled() as ArrayList<CatResponse?>
                            }else{
                                addNullValueInsideArray(tempList.shuffled())
                            }

                            cachedCatResponses = list

                            val initialItems = getItems(0, 30)

                            Log.e(TAG, "initMostDownloadedData: " + initialItems)
                            withContext(Dispatchers.Main){
                                adapter?.updateMoreData(initialItems)
                                startIndex += 30
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
    private fun loadData() {
        Log.d(TAG, "onCreateCustom:  home on create")


        myViewModel.catWallpapers.observe(viewLifecycleOwner){result ->
            when(result){

                is Response.Loading ->{

                }

                is Response.Success ->{
                    if (!dataset) {

                        lifecycleScope.launch(Dispatchers.IO) {
                            var tempList = ArrayList<CatResponse>()

                            result.data?.forEach {item ->
                                val model = CatResponse(item.id,item.image_name,item.cat_name,item.hd_image_url,item.compressed_image_url,null,item.likes,item.liked,item.unlocked,item.size,item.Tags,item.capacity)
                                if (!tempList.contains(model)){
                                    tempList.add(model)
                                }
                            }

                            val list = if (AdConfig.ISPAIDUSER){
                                tempList.shuffled() as ArrayList<CatResponse?>
                            }else{
                                addNullValueInsideArray(tempList.shuffled())
                            }

                            cachedCatResponses = list

                            val initialItems = getItems(0, 30)

                            Log.e(TAG, "initMostDownloadedData: " + initialItems)
                            withContext(Dispatchers.Main){
                                adapter?.updateMoreData(initialItems)
                                startIndex += 30
                                dataset = true
                            }


                        }


                    }
                }

                is Response.Error ->{

                }

                else -> {}
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

        if (wallFromList){
            if (isAdded){
                congratulationsDialog()
            }
        }


        if (name == "Trending"){
            initTrendingData()
        }else{
            loadData()
        }


        if (dataset){

            Log.e(TAG, "onResume: Data set $dataset")
            Log.e(TAG, "onResume: Data set ${addedItems?.size}")

            if (addedItems?.isEmpty() == true){
                Log.e(TAG, "onResume: "+cachedCatResponses.size )


            }
            adapter?.updateMoreData(addedItems!!)

            binding.recyclerviewAll.layoutManager?.scrollToPosition(oldPosition)

        }

        lifecycleScope.launch(Dispatchers.Main) {
            delay(1500)
            if (!WallpaperViewFragment.isNavigated && hasToNavigateList){
                navigateToDestination(addedItems!!,oldPosition)
            }
        }

    }

    override fun onPause() {
        super.onPause()

        val allItems = adapter?.getAllItems()
//            if (addedItems?.isNotEmpty() == true){
//                addedItems?.clear()
//            }
        Log.e(TAG, "onPause: "+allItems?.size )
        if (allItems?.isNotEmpty() == true){
            addedItems = allItems
        }
    }


    suspend fun addNullValueInsideArray(data: List<CatResponse?>): ArrayList<CatResponse?>{

        return withContext(Dispatchers.IO){
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




            newData
        }


    }
    private val fragmentScope: CoroutineScope by lazy { MainScope() }
    private fun navigateToDestination(arrayList: ArrayList<CatResponse?>, position:Int) {

        if (position >= arrayList.size) {
            Log.e(TAG, "navigateToDestination: Position $position out of bounds ${arrayList.size} ")

            addedItems?.clear()
            addedItems = getItems(0,30)
            adapter?.updateData(addedItems!!)
            isNavigationInProgress = false
            return
        }
        val countOfNulls = arrayList.subList(0, position).count { it == null }

        sharedViewModel.clearData()

        sharedViewModel.setData(arrayList.filterNotNull(), position - countOfNulls)
        Bundle().apply {
            putString("from",from)
            putString("wall","list")
            putInt("position",position - countOfNulls)
            findNavController().navigate(R.id.wallpaperViewFragment,this)
        }



        isNavigationInProgress = false

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
            wallFromList = false
            dialog.dismiss()
        }

        dialog.show()
    }

    companion object{
        var hasToNavigateList = false
        var wallFromList = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        fragmentScope.cancel()
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