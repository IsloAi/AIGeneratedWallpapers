package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.livewallpaper

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.ikame.android.sdk.IKSdkController
import com.ikame.android.sdk.data.dto.pub.IKAdError
import com.ikame.android.sdk.format.intertial.IKInterstitialAd
import com.ikame.android.sdk.listener.pub.IKLoadAdListener
import com.ikame.android.sdk.listener.pub.IKLoadDisplayAdViewListener
import com.ikame.android.sdk.listener.pub.IKShowAdListener
import com.ikame.android.sdk.tracking.IKTrackingHelper
import com.ikame.android.sdk.widgets.IkmDisplayWidgetAdView
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentLiveWallpaperBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.LiveWallpaperAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.AdEventListener
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MyApp
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.downloadCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.LiveWallpaperModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.BlurView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants.Companion.checkAppOpen
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants.Companion.checkInter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.LiveWallpaperViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LiveWallpaperFragment : Fragment(), AdEventListener {

    private var _binding: FragmentLiveWallpaperBinding? = null
    private val binding get() = _binding!!
    private val myViewModel: LiveWallpaperViewModel by activityViewModels()

    val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var myActivity: MainActivity
    var adapter: LiveWallpaperAdapter? = null

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    val interAd = IKInterstitialAd()
//    var checkAppOpen = false

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

        interAd.attachLifecycle(this.lifecycle)
        // Load ad with a specific screen ID, considered as a unitId
        interAd.loadAd("mainscr_live_tab_click_item", object : IKLoadAdListener {
            override fun onAdLoaded() {
                // Ad loaded successfully
            }

            override fun onAdLoadFail(error: IKAdError) {
                // Handle ad load failure
            }
        })

        binding.liveReccyclerview.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.liveReccyclerview.addItemDecoration(RvItemDecore(3, 5, false, 10000))
        updateUIWithFetchedData()
        adapter!!.setCoroutineScope(fragmentScope)
    }

    override fun onStart() {
        super.onStart()
        (myActivity.application as MyApp).registerAdEventListener(this)

    }


    private fun loadData() {
        Log.d("functionCallingTest", "onCreateCustom:  home on create")
        myViewModel.liveWallsFromDB.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Response.Success -> {
                    lifecycleScope.launch(Dispatchers.IO) {

                        val list = result.data?.shuffled()
                        val listNullable = if (!AdConfig.ISPAIDUSER) {
                            list?.let { addNullValueInsideArray(it) }
                        } else {
                            list as ArrayList<LiveWallpaperModel?>
                        }

                        withContext(Dispatchers.Main) {
                            listNullable?.let { adapter?.updateMoreData(it) }
                            adapter!!.setCoroutineScope(fragmentScope)
                        }
                    }
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
            }

        }
    }

    override fun onResume() {
        super.onResume()
        loadData()

        Log.e(TAG, "onResume: ")

        if (isAdded) {
            sendTracking(
                "screen_active",
                Pair("action_type", "Tab"),
                Pair("action_name", "MainScr_LiveTab_View")
            )
        }


        if (isAdded) {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Live WallPapers Screen")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, javaClass.simpleName)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }
    }

    private fun sendTracking(
        eventName: String,
        vararg param: Pair<String, String?>
    ) {
        IKTrackingHelper.sendTracking(eventName, *param)
    }


    private fun updateUIWithFetchedData() {

        val list = ArrayList<LiveWallpaperModel?>()
        adapter = LiveWallpaperAdapter(list, object :
            downloadCallback {
            override fun getPosition(position: Int, model: LiveWallpaperModel) {
                val newPosition = position + 1

                Log.e(TAG, "getPosition: " + model)

                Log.e(TAG, "getPosition: " + position)

                sharedViewModel.setAdPosition(newPosition)
                Log.e(TAG, "getPosition:$position odd ")

                if (AdConfig.ISPAIDUSER) {
                    setDownloadAbleWallpaperAndNavigate(model, false)
                } else {
//
                    var shouldShowInterAd = true

                    if (AdConfig.avoidPolicyOpenAdInter == 1 && checkAppOpen) {
                        if (isAdded) {
                            checkAppOpen = false
                            setDownloadAbleWallpaperAndNavigate(model, false)
                            Log.e(TAG, "App open condition handled, skipping ad")
                            shouldShowInterAd = false // Skip showing the ad for this action
                        }
                    }

                    if (AdConfig.avoidPolicyRepeatingInter == 1 && Constants.checkInter) {
                        if (isAdded) {
                            Constants.checkInter = false
                            setDownloadAbleWallpaperAndNavigate(model, false)
                            shouldShowInterAd = false
                        }
                    }

                    if (shouldShowInterAd) {
                        showInterAd(model)
                    }


//                    if (AdConfig.avoidPolicyOpenAdInter == 1 && checkAppOpen){
//                        if (isAdded){
//                            checkAppOpen = false
//                            setDownloadAbleWallpaperAndNavigate(model,false)
//                            Log.e(TAG, "app open showed: ", )
//                        }
//                    }else{
//                        showInterAd(model)
//                    }

                }


            }
        }, myActivity)


        IKSdkController.loadNativeDisplayAd("mainscr_live_tab_scroll", object :
            IKLoadDisplayAdViewListener {
            override fun onAdLoaded(adObject: IkmDisplayWidgetAdView?) {
                if (isAdded && view != null) {
                    adapter?.nativeAdView = adObject
                    binding.liveReccyclerview.adapter = adapter
                }
            }

            override fun onAdLoadFail(error: IKAdError) {
                // Handle ad load failure with view object
            }
        })

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

    private fun showInterAd(model: LiveWallpaperModel) {

        interAd.showAd(
            requireActivity(),
            "mainscr_live_tab_click_item",
            adListener = object : IKShowAdListener {
                override fun onAdsShowFail(error: IKAdError) {
                    if (isAdded) {
                        setDownloadAbleWallpaperAndNavigate(model, false)
                    }
                }

                override fun onAdsDismiss() {
                    Constants.checkInter = true
                    setDownloadAbleWallpaperAndNavigate(model, true)
                }
            }
        )
    }

    private fun setDownloadAbleWallpaperAndNavigate(model: LiveWallpaperModel, adShowd: Boolean) {
        BlurView.filePath = ""
        sharedViewModel.clearLiveWallpaper()
        sharedViewModel.setLiveWallpaper(listOf(model))
        if (isAdded) {
            Bundle().apply {
                putBoolean("adShowed", adShowd)
                findNavController().navigate(R.id.downloadLiveWallpaperFragment, this)
            }

        }
    }


    private val fragmentScope: CoroutineScope by lazy { MainScope() }

    private suspend fun addNullValueInsideArray(data: List<LiveWallpaperModel?>): ArrayList<LiveWallpaperModel?> {

        return withContext(Dispatchers.IO) {
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




            newData
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        (myActivity.application as MyApp).unregisterAdEventListener()
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