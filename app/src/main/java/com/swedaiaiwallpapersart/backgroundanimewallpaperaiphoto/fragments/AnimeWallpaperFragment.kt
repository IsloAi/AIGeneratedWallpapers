package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments

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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.DialogCongratulationsBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentAnimeWallpaperBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.ApiCategoriesListAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.AdEventListener
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MyApp
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.PositionCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants.Companion.checkAppOpen
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.AnimeViewmodel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AnimeWallpaperFragment : Fragment(), AdEventListener {

    private var _binding: FragmentAnimeWallpaperBinding? = null
    private val binding get() = _binding!!
    private lateinit var myActivity: MainActivity
    var adapter: ApiCategoriesListAdapter? = null
    val sharedViewModel: SharedViewModel by activityViewModels()
    private var cachedCatResponses: ArrayList<CatResponse?> = ArrayList()
    private var addedItems: ArrayList<CatResponse?>? = ArrayList()
    var dataset = false
    var oldPosition = 0

    var adcount = 0
    var totalADs = 0
    var externalOpen = false

    var startIndex = 0
    var isNavigationInProgress = false
    val myViewModel: AnimeViewmodel by activityViewModels()

    val TAG = "ANIME"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAnimeWallpaperBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreateViewCalling()

    }

    private fun onCreateViewCalling() {

        myActivity = activity as MainActivity
        binding.progressBar.visibility = View.GONE
        binding.progressBar.setAnimation(R.raw.main_loading_animation)
        binding.recyclerviewAll.layoutManager = GridLayoutManager(requireContext(), 3)

        binding.recyclerviewAll.addItemDecoration(RvItemDecore(3, 5, false, 10000))

        val list = ArrayList<CatResponse?>()
        adapter = ApiCategoriesListAdapter(list, object : PositionCallback {
            override fun getPosition(position: Int) {

                if (!isNavigationInProgress) {

                    hasToNavigateAnime = true

                    isNavigationInProgress = true
                    externalOpen = true
                    val allItems = adapter?.getAllItems()
                    if (addedItems?.isNotEmpty() == true) {
                        addedItems?.clear()
                    }


                    addedItems = allItems

                    oldPosition = position

                    if (AdConfig.ISPAIDUSER) {
                        navigateToDestination(allItems!!, position)
                    }
                }
            }

            override fun getFavorites(position: Int) {
            }
        }, myActivity, "category")

        adapter!!.setCoroutineScope(fragmentScope)

        binding.recyclerviewAll.adapter = adapter


        binding.recyclerviewAll.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)/*val layoutManager = recyclerView.layoutManager as GridLayoutManager
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

                }*/
            }
        })

        binding.swipeLayout.setOnRefreshListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val newData = cachedCatResponses.filterNotNull().shuffled()
                val nullAdd = addNullValueInsideArray(newData.shuffled())
                cachedCatResponses.clear()
                cachedCatResponses = nullAdd
                withContext(Dispatchers.Main) {
                    adapter?.updateMoreData(cachedCatResponses)
                    //startIndex += 30

                    binding.swipeLayout.isRefreshing = false
                }

            }


        }
    }

    override fun onStart() {
        super.onStart()
        (myActivity.application as MyApp).registerAdEventListener(this)

    }

    private fun loadData() {
        Log.d(TAG, "onCreateCustom:  home on create")

        myViewModel.catWallpapers.observe(viewLifecycleOwner) { result ->
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
                                    item.unlocked,
                                    item.size,
                                    item.Tags,
                                    item.capacity
                                )
                                if (!tempList.contains(model)) {
                                    tempList.add(model)
                                }
                            }
                            val list = addNullValueInsideArray(tempList.shuffled())
                            cachedCatResponses = list
                            Log.e(TAG, "initMostDownloadedData: $cachedCatResponses")
                            withContext(Dispatchers.Main) {
                                adapter?.updateMoreData(cachedCatResponses)
                                dataset = true
                            }
                        }
                    }
                }

                is Response.Error -> {

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
                startIndex1, endIndex.coerceAtMost(cachedCatResponses.size)
            )
            return ArrayList(subList)
        }
    }

    override fun onResume() {
        super.onResume()
        myViewModel.getAllCreations("Anime")
        if (wallFromAnime) {
            if (isAdded) {
                congratulationsDialog()
            }
        }

        loadData()

        if (dataset) {

            Log.e(TAG, "onResume: Data set $dataset")
            Log.e(TAG, "onResume: Data set ${addedItems?.size}")

            if (addedItems?.isEmpty() == true) {
                Log.e(TAG, "onResume: empty added items" + cachedCatResponses.size)


            }
            adapter?.updateMoreData(addedItems!!)

            binding.recyclerviewAll.layoutManager?.scrollToPosition(oldPosition)

        }

        lifecycleScope.launch(Dispatchers.Main) {
            delay(1500)
            if (!WallpaperViewFragment.isNavigated && hasToNavigateAnime) {
                navigateToDestination(addedItems!!, oldPosition)
            }
        }

    }

    override fun onPause() {
        super.onPause()
        val allItems = adapter?.getAllItems()
        Log.e(TAG, "onPause: " + allItems?.size)
        if (allItems?.isNotEmpty() == true) {
            addedItems = allItems
        }

        Log.e(TAG, "onPause: " + addedItems?.size)


    }

    suspend fun addNullValueInsideArray(data: List<CatResponse?>): ArrayList<CatResponse?> {
        return withContext(Dispatchers.IO) {
            val firstAdLineThreshold =
                if (AdConfig.firstAdLineViewListWallSRC != 0) AdConfig.firstAdLineViewListWallSRC else 4
            val firstLine = firstAdLineThreshold * 3

            val lineCount =
                if (AdConfig.lineCountViewListWallSRC != 0) AdConfig.lineCountViewListWallSRC else 5
            val lineC = lineCount * 3
            val newData = arrayListOf<CatResponse?>()

            for (i in data.indices) {
                if (i > firstLine && (i - firstLine) % (lineC + 1) == 0) {
                    newData.add(null)
                    totalADs++
                } else if (i == firstLine) {
                    newData.add(null)
                    totalADs++
                }
                newData.add(data[i])

            }
            newData
        }
    }

    private val fragmentScope: CoroutineScope by lazy { MainScope() }

    private fun navigateToDestination(arrayList: ArrayList<CatResponse?>, position: Int) {

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
            putString("from", "category")
            putString("wall", "anime")
            putInt("position", position - countOfNulls)
            findNavController().navigate(R.id.wallpaperViewFragment, this)
        }
        isNavigationInProgress = false

    }

    companion object {
        var hasToNavigateAnime = false
        var wallFromAnime = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
            wallFromAnime = false
            dialog.dismiss()
        }

        dialog.show()
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