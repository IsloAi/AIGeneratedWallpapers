package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.menuFragments

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.ikame.android.sdk.data.dto.pub.IKAdError
import com.ikame.android.sdk.format.intertial.IKInterstitialAd
import com.ikame.android.sdk.listener.pub.IKLoadAdListener
import com.ikame.android.sdk.listener.pub.IKShowAdListener
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentFavouriteBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.FavouriteLiveAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.FavouriteStaticAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.livewallpaper.DownloadLiveWallpaperFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.FavouriteListIGEntity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.RoomViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.ViewModelFactory
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.FavouriteDownloadCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.PositionCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.downloadCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.FavouriteLiveModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.LiveWallpaperModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.BlurView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyFavouriteViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.LiveWallpaperViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class FavouriteFragment : Fragment() {

    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!
    private val favouriteViewModel: MyFavouriteViewModel by activityViewModels()
    private lateinit var wallpapers: MutableList<CatResponse>
    private var cachedCatResponses: ArrayList<CatResponse>? = ArrayList()
    private lateinit var myActivity: MainActivity
    private lateinit var roomViewModel: RoomViewModel
    private var cachedIGList: ArrayList<FavouriteListIGEntity>? = ArrayList()
    private var isLoadedData = false
    private val liveWallpaperViewModel: LiveWallpaperViewModel by activityViewModels()
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    val interAd = IKInterstitialAd()
    val sharedViewModel: SharedViewModel by activityViewModels()
    val TAG = "FAVORITES"

    @Inject
    lateinit var appDatabase: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        wallpapers = mutableListOf()
        interAd.attachLifecycle(this.lifecycle)
        // Load ad with a specific screen ID, considered as a unitId
        interAd.loadAd("mainscr_favorite_tab_click_item", object : IKLoadAdListener {
            override fun onAdLoaded() {
                // Ad loaded successfully
            }

            override fun onAdLoadFail(error: IKAdError) {
                // Handle ad load failure
            }
        })
        onCreateViewCalling()

        binding.StaticWallpaper.setOnClickListener {
            selector(binding.StaticWallpaper, binding.live, binding.aiWallpaper)
            loadDataFromRoomDB()
            binding.selfCreationRecyclerView.visibility = VISIBLE
            binding.liveRecyclerview.visibility = GONE
            binding.emptySupport.visibility = GONE
            MySharePreference.setFavouriteSaveState(requireContext(), 2)
            binding.progressBar.visibility = INVISIBLE

        }

        binding.live.setOnClickListener {
            selector(binding.live, binding.StaticWallpaper, binding.aiWallpaper)
            MySharePreference.setFavouriteSaveState(requireContext(), 3)
            initObservers()
            binding.liveRecyclerview.visibility = VISIBLE
            binding.selfCreationRecyclerView.visibility = GONE
            binding.progressBar.visibility = INVISIBLE
        }

        binding.addToFav.setOnClickListener {

            sharedViewModel.selectTab(1)
            findNavController().popBackStack(R.id.homeTabsFragment, false)


        }

        /*binding.addToFavAI.setOnClickListener {
            sharedViewModel.selectTab(5)
            findNavController().popBackStack(R.id.homeTabsFragment, false)
        }*/

        loadDataFromRoomDB()
    }

    private fun onCreateViewCalling() {

        val roomDatabase = AppDatabase.getInstance(requireContext())
        roomViewModel =
            ViewModelProvider(this, ViewModelFactory(roomDatabase, 0))[RoomViewModel::class.java]
        myActivity = activity as MainActivity
        //viewVisible()

        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
            Constants.checkInter = false
            Constants.checkAppOpen = false
        }
    }

    private fun loadData() {

    }

    /*
        private fun viewVisible() {
            binding.errorMessage.visibility = INVISIBLE
            binding.selfCreationRecyclerView.visibility = VISIBLE
            binding.switchLayout.visibility = VISIBLE
            binding.progressBar.visibility = VISIBLE
            binding.progressBar.setAnimation(R.raw.main_loading_animation)
            binding.selfCreationRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
            binding.selfCreationRecyclerView.addItemDecoration(RvItemDecore(2, 20, false, 10000))
            binding.liveRecyclerview.layoutManager = GridLayoutManager(requireContext(), 3)
            binding.liveRecyclerview.addItemDecoration(RvItemDecore(3, 5, false, 10000))

          if (MySharePreference.getFavouriteSaveState(requireContext()) == 1) {
                binding.selfCreationRecyclerView.visibility = VISIBLE
                selector(binding.aiWallpaper, binding.StaticWallpaper, binding.live)
                binding.emptySupportAI.visibility = View.GONE
                binding.liveRecyclerview.visibility = View.GONE


                if (!isLoadedData) {
                    binding.progressBar.visibility = VISIBLE
                }

            } else if (MySharePreference.getFavouriteSaveState(requireContext()) == 2) {
                binding.selfCreationRecyclerView.visibility = VISIBLE
                selector(binding.StaticWallpaper, binding.aiWallpaper, binding.live)
                binding.progressBar.visibility = INVISIBLE
                binding.emptySupport.visibility = View.GONE
                binding.liveRecyclerview.visibility = View.GONE
            } else if (MySharePreference.getFavouriteSaveState(requireContext()) == 3) {
                initObservers()
                binding.selfCreationRecyclerView.visibility = INVISIBLE
                binding.liveRecyclerview.visibility = VISIBLE
                selector(binding.live, binding.StaticWallpaper, binding.aiWallpaper)
                binding.progressBar.visibility = INVISIBLE
                binding.emptySupport.visibility = View.GONE
                binding.emptySupportAI.visibility = View.GONE
            }

            binding.aiWallpaper.setOnClickListener {
                //loadData()
                selector(binding.aiWallpaper, binding.StaticWallpaper, binding.live)
                binding.selfCreationRecyclerView.visibility = VISIBLE
                binding.emptySupportAI.visibility = View.GONE
                binding.liveRecyclerview.visibility = INVISIBLE
                if (cachedCatResponses?.isEmpty() == true) {
                    binding.emptySupport.visibility = VISIBLE
                }
                MySharePreference.setFavouriteSaveState(requireContext(), 1)
                if (!isLoadedData) {
                    binding.progressBar.visibility = VISIBLE
                }
            }

        }

        private fun addNullValueInsideArray(data: List<CatResponse?>): ArrayList<CatResponse?> {

            val firstAdLineThreshold =
                if (AdConfig.firstAdLineViewListWallSRC != 0) AdConfig.firstAdLineViewListWallSRC else 4
            val firstLine = firstAdLineThreshold * 2

            val lineCount =
                if (AdConfig.lineCountViewListWallSRC != 0) AdConfig.lineCountViewListWallSRC else 5
            val lineC = lineCount * 2
            val newData = arrayListOf<CatResponse?>()

            for (i in data.indices) {
                if (i > firstLine && (i - firstLine) % (lineC + 1) == 0) {
                    newData.add(null)
                } else if (i == firstLine) {
                    newData.add(null)
                }
                newData.add(data[i])

            }

            return newData
        }
    */

    fun initObservers() {
        var favourites: List<FavouriteLiveModel>
        lifecycleScope.launch(Dispatchers.IO) {
            favourites = ArrayList()
            favourites = appDatabase.liveWallpaperDao().getAllFavouriteWallpapers()
            Log.d("LIVEADAPTER", "LIVE: favorites: $favourites")
            withContext(Dispatchers.Main) {
                updateUIWithFetchedDataLive(favourites)
            }
        }

        /*liveWallpaperViewModel.liveWallsFromDB.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Response.Success -> {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val filtered = result.data?.filter { it.liked }
                        Log.d(TAG, "initObservers: filtered: $filtered")

                        val list = filtered?.let { addNullValueInsideArrayLive(it) }
                        Log.d("LiveWallpaper", "list: ${list?.size} ")
                        list?.let { updateUIWithFetchedDataLive(it) }
                    }
                }

                is Response.Loading -> {
                    Log.e("TAG", "loadData: Loading")
                }

                is Response.Error -> {
                    Log.e("TAG", "loadData: response error")
                    MySharePreference.getDeviceID(requireContext())
                        ?.let { liveWallpaperViewModel.getMostUsed("1", "500", it) }
                }

                is Response.Processing -> {
                    Log.e("TAG", "loadData: processing")
                }
            }
        }*/
    }

    private fun updateUIWithFetchedDataLive(list: List<FavouriteLiveModel>) {
        if (list.isEmpty()) {
            binding.emptySupport.visibility = VISIBLE
            binding.noFavImg.visibility = VISIBLE
        } else {
            binding.emptySupport.visibility = GONE
            binding.noFavImg.visibility = GONE

            val adapter =
                FavouriteLiveAdapter(list, myActivity, object : FavouriteDownloadCallback {
                    override fun getPosition(position: Int, model: FavouriteLiveModel) {
                        BlurView.filePath = ""
                        sharedViewModel.clearLiveWallpaper()
                        sharedViewModel.setFavLiveWallpaper(listOf(model))
                        DownloadLiveWallpaperFragment.shouldObserveFavorites = true
                        DownloadLiveWallpaperFragment.shouldObserveLiveWallpapers = false
                        // Create the action using Safe Args
                        findNavController().navigate(R.id.downloadLiveWallpaperFragment)
                    }
                })
            binding.liveRecyclerview.layoutManager = GridLayoutManager(requireContext(), 2)
            binding.liveRecyclerview.adapter = adapter
            binding.liveRecyclerview.addItemDecoration(RvItemDecore(2, 20, false, 10000))
        }

        //binding.liveRecyclerview.adapter = adapter
        /*LiveWallpaperAdapter(catResponses, object : downloadCallback {
        override fun getPosition(position: Int, model: LiveWallpaperModel) {
            BlurView.filePath = ""
            sharedViewModel.clearLiveWallpaper()
            sharedViewModel.setLiveWallpaper(listOf(model))
            findNavController().navigate(R.id.downloadLiveWallpaperFragment)
        }
    }, myActivity)

    IKSdkController.loadNativeDisplayAd("mainscr_live_tab_scroll",
        object : IKLoadDisplayAdViewListener {
            override fun onAdLoaded(adObject: IkmDisplayWidgetAdView?) {
                // Update UI on the main thread using coroutines
                CoroutineScope(Dispatchers.Main).launch {
                    if (isAdded && view != null) {
                        adapter.nativeAdView = adObject
                        binding.liveRecyclerview.adapter = adapter
                    }
                }
            }

            override fun onAdLoadFail(error: IKAdError) {
                // Handle ad load failure
            }
        })

    // Update the RecyclerView adapter on the main thread
    CoroutineScope(Dispatchers.Main).launch {
        binding.liveRecyclerview.adapter = adapter
    }*/
    }

    private fun addNullValueInsideArrayLive(data: List<LiveWallpaperModel?>): ArrayList<LiveWallpaperModel?> {

        val firstAdLineThreshold =
            if (AdConfig.firstAdLineViewListWallSRC != 0) AdConfig.firstAdLineViewListWallSRC else 4
        val firstLine = firstAdLineThreshold * 3

        val lineCount =
            if (AdConfig.lineCountViewListWallSRC != 0) AdConfig.lineCountViewListWallSRC else 5
        val lineC = lineCount * 3
        val newData = arrayListOf<LiveWallpaperModel?>()

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




        return newData
    }

    private fun selector(selector: TextView, unSelector: TextView, unselector1: TextView) {
        selector.setBackgroundResource(R.drawable.text_selector)
        unSelector.setBackgroundResource(0)
        unselector1.setBackgroundResource(0)
        selector.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        unSelector.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        unselector1.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

    }

    private val fragmentScope: CoroutineScope by lazy { MainScope() }

    private fun updateUIWithFetchedData(catResponses: List<CatResponse>) {
        //val nonNullCatResponses = catResponses.filterNotNull()
        val adapter =
            FavouriteStaticAdapter(
                catResponses,
                myActivity,
                "favorites",
                object : PositionCallback {
                    override fun getPosition(position: Int) {

                        interAd.showAd(requireActivity(),
                            "mainscr_favorite_tab_click_item",
                            adListener = object : IKShowAdListener {
                                override fun onAdsShowFail(error: IKAdError) {
                                    if (isAdded) {
                                        navigateToDestination(ArrayList(catResponses), position)
                                    }
                                }

                                override fun onAdsDismiss() {
                                    if (isAdded) {
                                        navigateToDestination(ArrayList(catResponses), position)
                                    }
                                }
                            })
                    }

                    override fun getFavorites(position: Int) {

                    }
                })
        //adapter.setCoroutineScope(fragmentScope)

        binding.selfCreationRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.selfCreationRecyclerView.adapter = adapter
        binding.selfCreationRecyclerView.addItemDecoration(RvItemDecore(2, 20, false, 10000))
    }

    private fun navigateToDestination(arrayList: ArrayList<CatResponse?>, position: Int) {
        val gson = Gson()
        val arrayListJson = gson.toJson(arrayList.filterNotNull())

        val countOfNulls = arrayList.subList(0, position).count { it == null }

        sharedViewModel.clearData()
        sharedViewModel.setData(arrayList.filterNotNull(), position - countOfNulls)
        Bundle().apply {
            putInt("position", position - countOfNulls)
            putString("from", "trending")
            putString("wall", "home")
            findNavController().navigate(R.id.wallpaperViewFragment, this)
        }
    }

    /*override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        fragmentScope.cancel()
    }*/

    private fun loadDataFromRoomDB() {
        binding.emptySupport.visibility = GONE
        val deviceId = MySharePreference.getDeviceID(requireContext())
        // Trigger data load in the ViewModel
        favouriteViewModel.loadFavourites(deviceId!!)

        // Observe the data from the ViewModel
        lifecycleScope.launch(Dispatchers.IO) {
            favouriteViewModel.favourites.collect { response ->
                when (response) {
                    is Response.Processing<*> -> {
                        // Show loading state (e.g., a progress bar)
                    }

                    is Response.Success<*> -> {
                        val data = response.data as? ArrayList<String> ?: ArrayList()
                        Log.d(TAG, "loadDataFromRoomDB: Data = $data")

                        // Create a new list to hold wallpapers
                        val newWallpapers =
                            mutableListOf<CatResponse>() // Replace WallpaperType with your actual type

                        data.forEach { id ->
                            val wallpaper =
                                appDatabase.wallpapersDao().getFavouritesByWallpaperId(id)
                            if (wallpaper != null) {
                                newWallpapers.add(wallpaper) // Add to the new list
                                Log.d(TAG, "LD: Wallpaper in Room = $wallpaper")
                            } else {
                                Log.d(TAG, "LD: No wallpaper found for ID $id")
                            }
                        }

                        // Now update the wallpapers list
                        wallpapers.clear() // Clear the existing list
                        wallpapers.addAll(newWallpapers) // Add all from the new list

                        Log.d(TAG, "LD: All wallpapers = $wallpapers")
                    }

                    is Response.Error -> {
                        Log.d(TAG, "Fav Frag Error: ${response.message}")
                        // Update the UI with the retrieved data
                    }

                    Response.Loading -> {


                    }

                }//End of when
            }//End of viewmodel scope
        }//End of LifeScope
        Handler().postDelayed({
            updateUIWithFetchedData(wallpapers)
        }, 1000)


    }//End of function

    override fun onResume() {
        super.onResume()
        if (isAdded) {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Favorites Screen")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, javaClass.simpleName)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }
    }

    override fun onPause() {
        super.onPause()
        // Make sure to release the surface if it exists

    }
}