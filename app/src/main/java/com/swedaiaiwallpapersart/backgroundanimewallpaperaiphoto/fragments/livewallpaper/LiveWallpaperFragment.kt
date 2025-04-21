package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.livewallpaper

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
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
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.google.firebase.analytics.FirebaseAnalytics
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentLiveWallpaperBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.LiveWallpaperAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.AdClickCounter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.AdEventListener
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MaxInterstitialAds
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MyApp
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.downloadCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.LiveWallpaperModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.BlurView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants.Companion.checkAppOpen
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants.Companion.checkInter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.LiveWallpaperViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

class LiveWallpaperFragment : Fragment(), AdEventListener {

    private var _binding: FragmentLiveWallpaperBinding? = null
    private val binding get() = _binding!!
    private val myViewModel: LiveWallpaperViewModel by activityViewModels()
    private val statusAd = AdConfig.adStatusViewListWallSRC
    private var liveList = ArrayList<LiveWallpaperModel>()

    val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var myActivity: MainActivity
    var adapter: LiveWallpaperAdapter? = null

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    val TAG = "LIVE_WALL_SCREEN"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLiveWallpaperBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        myActivity = activity as MainActivity

        myViewModel.getAllTrendingWallpapers()

        binding.liveReccyclerview.layoutManager = GridLayoutManager(requireContext(), 3)
        (binding.liveReccyclerview.layoutManager as GridLayoutManager).isItemPrefetchEnabled = false
        binding.liveReccyclerview.addItemDecoration(RvItemDecore(3, 5, false, 10000))
        updateUIWithFetchedData()
        adapter!!.setCoroutineScope(fragmentScope)
        binding.refreshLive.setOnRefreshListener {
            if (liveList.isNotEmpty()) {
                val listWithNulls = if (AdConfig.ISPAIDUSER) {
                    liveList.shuffled()
                } else {
                    addNullValueInsideArray(liveList.shuffled())
                }
                listWithNulls.let { adapter?.updateData(it as ArrayList<LiveWallpaperModel?>) }
                binding.refreshLive.isRefreshing = false
            }
        }
    }

    override fun onStart() {
        super.onStart()
        (myActivity.application as MyApp).registerAdEventListener(this)

    }

    private fun loadData() {
        myViewModel.liveWallsFromDB.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Response.Success -> {
                    val list = result.data
                    if (list != null) {
                        liveList = ArrayList(list)  // Safe way to cast to ArrayList
                        Log.d("LIVE", "loadData: ${liveList.size}")
                    } else {
                        Log.d("LIVE", "loadData: result.data was null")
                    }
                    val listNullable = if (AdConfig.ISPAIDUSER) {
                        list
                    } else {
                        addNullValueInsideArray(list!!)
                    }

                    listNullable?.let { adapter?.updateData(it as ArrayList<LiveWallpaperModel?>) }
                    adapter!!.setCoroutineScope(fragmentScope)

                }

                is Response.Loading -> {
                    Log.e(TAG, "loadData: Loading")
                }

                is Response.Error -> {
                    Log.e(TAG, "loadData: response error")

                }

                is Response.Processing -> {
                    Log.e(TAG, "loadData: processing")
                }

                else -> {}
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
        Log.e(TAG, "onResume: ")
        if (isAdded) {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Live WallPapers Screen")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, javaClass.simpleName)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }
    }

    private fun updateUIWithFetchedData() {

        val list = ArrayList<LiveWallpaperModel?>()
        adapter = LiveWallpaperAdapter(list, object :
            downloadCallback {
            override fun getPosition(position: Int, model: LiveWallpaperModel) {
                val newPosition = position + 1

                sharedViewModel.setAdPosition(newPosition)
                setDownloadAbleWallpaperAndNavigate(model, false)
            }
        }, myActivity)

        binding.liveReccyclerview.adapter = adapter
        binding.liveReccyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // Set the boolean to true when the RecyclerView is scrolled
                if (dy != 0 || dx != 0) {
                    checkInter = false
                    checkAppOpen = false
                }
            }
        })
    }

    private fun setDownloadAbleWallpaperAndNavigate(model: LiveWallpaperModel, adShowd: Boolean) {
        BlurView.filePath = ""
        sharedViewModel.clearLiveWallpaper()
        sharedViewModel.setLiveWallpaper(listOf(model))
        if (isAdded) {
            if (AdConfig.ISPAIDUSER) {
                Bundle().apply {
                    putBoolean("adShowed", adShowd)
                    DownloadLiveWallpaperFragment.shouldObserveLiveWallpapers = true
                    DownloadLiveWallpaperFragment.shouldObserveFavorites = false
                    findNavController().navigate(R.id.downloadLiveWallpaperFragment, this)
                }
            } else {
                if (AdClickCounter.shouldShowAd()) {
                    MaxInterstitialAds.showInterstitialAd(
                        requireActivity(),
                        object : MaxAdListener {
                            override fun onAdLoaded(p0: MaxAd) {}

                            override fun onAdDisplayed(p0: MaxAd) {}

                            override fun onAdHidden(p0: MaxAd) {
                                Bundle().apply {
                                    putBoolean("adShowed", adShowd)
                                    DownloadLiveWallpaperFragment.shouldObserveLiveWallpapers = true
                                    DownloadLiveWallpaperFragment.shouldObserveFavorites = false
                                    findNavController().navigate(
                                        R.id.downloadLiveWallpaperFragment,
                                        this
                                    )
                                }
                            }

                            override fun onAdClicked(p0: MaxAd) {}

                            override fun onAdLoadFailed(p0: String, p1: MaxError) {}

                            override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {}
                        })
                } else {
                    AdClickCounter.increment()
                    Bundle().apply {
                        putBoolean("adShowed", adShowd)
                        DownloadLiveWallpaperFragment.shouldObserveLiveWallpapers = true
                        DownloadLiveWallpaperFragment.shouldObserveFavorites = false
                        findNavController().navigate(R.id.downloadLiveWallpaperFragment, this)
                    }
                }
            }
        }
    }

    private val fragmentScope: CoroutineScope by lazy { MainScope() }

    private fun addNullValueInsideArray(data: List<LiveWallpaperModel?>): ArrayList<LiveWallpaperModel?> {
        val modifiedList = ArrayList<LiveWallpaperModel?>()

        for (i in data.indices) {
            modifiedList.add(data[i]) // Add the current item

            // After every 15 items, add a null value (excluding the last item)
            if ((i + 1) % 15 == 0) {
                modifiedList.add(null)
            }
        }

        return modifiedList
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            myActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo != null && networkInfo.isConnected
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (myActivity.application as MyApp).unregisterAdEventListener()
        _binding = null
    }

    override fun onAdDismiss() {
        checkAppOpen = true
    }

    override fun onAdLoading() {}

    override fun onAdsShowTimeout() {}

    override fun onShowAdComplete() {}

    override fun onShowAdFail() {}

}
