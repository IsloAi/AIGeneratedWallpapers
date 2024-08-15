package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.livewallpaper

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.ikame.android.sdk.IKSdkController
import com.google.firebase.analytics.FirebaseAnalytics
import com.ikame.android.sdk.data.dto.pub.IKAdError
import com.ikame.android.sdk.format.intertial.IKInterstitialAd
import com.ikame.android.sdk.listener.pub.IKLoadAdListener
import com.ikame.android.sdk.listener.pub.IKLoadDisplayAdViewListener
import com.ikame.android.sdk.listener.pub.IKShowAdListener
import com.ikame.android.sdk.listener.pub.IKShowWidgetAdListener
import com.ikame.android.sdk.widgets.IkmDisplayWidgetAdView
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentLiveWallpapersFromCategoryBinding
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
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.GetLiveWallpaperByCategoryViewmodel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LiveWallpapersFromCategoryFragment : Fragment(), AdEventListener {

    private var _binding:FragmentLiveWallpapersFromCategoryBinding ?= null
    private val binding get() = _binding!!


    private val myViewModel: GetLiveWallpaperByCategoryViewmodel by activityViewModels()

    val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var myActivity : MainActivity
    var adapter: LiveWallpaperAdapter?= null

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    val TAG = "LIVE_WALL_SCREEN"

    val interAd = IKInterstitialAd()

//    var checkAppOpen = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLiveWallpapersFromCategoryBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (AdConfig.ISPAIDUSER){
            binding.adsView.visibility = View.GONE
        }else{
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
            binding.adsView.attachLifecycle(lifecycle)
            binding.adsView.loadAd("mainscr_bottom", object : IKShowWidgetAdListener {
                override fun onAdShowed() {}
                override fun onAdShowFail(error: IKAdError) {
//                    binding.adsView?.visibility = View.GONE
                }

            })
        }

        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        myActivity = activity as MainActivity
        binding.liveReccyclerview.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.liveReccyclerview.addItemDecoration(RvItemDecore(3,5,false,10000))
        updateUIWithFetchedData()
        adapter!!.setCoroutineScope(fragmentScope)


        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
            Constants.checkInter = false
            checkAppOpen = false
        }

    }

    fun initObservers(){
        myViewModel.liveWallpapers.observe(viewLifecycleOwner){result ->
            when(result){
                is Response.Error -> {
                    Log.e(TAG, "loadData: Loading" )
                }
                Response.Loading -> {
                    Log.e(TAG, "loadData: response error", )

                }
                is Response.Processing -> {
                    Log.e(TAG, "loadData: processing", )
                }
                is Response.Success -> {
                    lifecycleScope.launch(Dispatchers.IO) {
                        Log.e(TAG, "initObservers: "+result.data )

                        val list  = result.data?.shuffled()
                        val listNullable = if (!AdConfig.ISPAIDUSER){
                            list?.let { addNullValueInsideArray(it) }
                        }else{
                            list as ArrayList<LiveWallpaperModel?>
                        }

                        withContext(Dispatchers.Main){
                            listNullable?.let { adapter?.updateMoreData(it) }
                            adapter!!.setCoroutineScope(fragmentScope)
                        }
                    }
                }
            }

        }
    }



    private fun updateUIWithFetchedData() {

        val list = ArrayList<LiveWallpaperModel?>()
        adapter = LiveWallpaperAdapter(list, object :
            downloadCallback {
            override fun getPosition(position: Int, model: LiveWallpaperModel) {
                val newPosition = position + 1

                Log.e(TAG, "getPosition: "+model )

                Log.e(TAG, "getPosition: "+position )

                sharedViewModel.setAdPosition(newPosition)
                    Log.e(TAG, "getPosition:$position odd " )
                if (AdConfig.ISPAIDUSER){
                    setDownloadAbleWallpaperAndNavigate(model,true)
                }else{
                    var shouldShowInterAd = true

                    if (AdConfig.avoidPolicyRepeatingInter == 1 && Constants.checkInter) {
                        if (isAdded) {
                            Constants.checkInter = false
                            setDownloadAbleWallpaperAndNavigate(model, false)
                            shouldShowInterAd = false // Skip showing the ad for this action
                        }
                    }

                    if (AdConfig.avoidPolicyOpenAdInter == 1 && checkAppOpen) {
                        if (isAdded) {
                            checkAppOpen = false
                            setDownloadAbleWallpaperAndNavigate(model, false)
                            Log.e(TAG, "app open showed")
                            shouldShowInterAd = false // Skip showing the ad for this action
                        }
                    }

                    if (shouldShowInterAd) {
                        showInterAd(model) // Show the interstitial ad if no conditions were met
                    }
//                    if (AdConfig.avoidPolicyOpenAdInter == 1 && checkAppOpen){
//                        if (isAdded){
//                            checkAppOpen = false
//                            setDownloadAbleWallpaperAndNavigate(model,true)
//                            Log.e(TAG, "app open showed: ", )
//                        }
//                    }else{
//                        showInterAd(model)
//                    }
                }
            }
        },myActivity)

        IKSdkController.loadNativeDisplayAd("mainscr_live_tab_scroll", object :
            IKLoadDisplayAdViewListener {
            override fun onAdLoaded(adObject: IkmDisplayWidgetAdView?) {
                if (isAdded && view!= null){
                    adapter?.nativeAdView = adObject
                    binding.liveReccyclerview.adapter = adapter
                }
            }

            override fun onAdLoadFail(error: IKAdError) {
                // Handle ad load failure with view object
            }
        })

        binding.liveReccyclerview.adapter = adapter
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

    override fun onStart() {
        super.onStart()
        (myActivity.application as MyApp).registerAdEventListener(this)

    }




    private fun setDownloadAbleWallpaperAndNavigate(model: LiveWallpaperModel,adShowd:Boolean) {
        BlurView.filePath = ""
        sharedViewModel.clearLiveWallpaper()
        sharedViewModel.setLiveWallpaper(listOf(model))
        if (isAdded) {
            Bundle().apply {
                putBoolean("adShowed",adShowd)
                findNavController().navigate(R.id.downloadLiveWallpaperFragment,this)
            }

        }
    }
    private val fragmentScope: CoroutineScope by lazy { MainScope() }

    private suspend fun addNullValueInsideArray(data: List<LiveWallpaperModel?>): ArrayList<LiveWallpaperModel?>{

        return withContext(Dispatchers.IO){
            val firstAdLineThreshold = if (AdConfig.firstAdLineViewListWallSRC != 0) AdConfig.firstAdLineViewListWallSRC else 4
            val firstLine = firstAdLineThreshold * 3

            val lineCount = if (AdConfig.lineCountViewListWallSRC != 0) AdConfig.lineCountViewListWallSRC else 5
            val lineC = lineCount * 3
            val newData = arrayListOf<LiveWallpaperModel?>()

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




            newData
        }


    }


    override fun onResume() {
        super.onResume()

        initObservers()

        if (isAdded){
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Live WallPapers Screen")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, javaClass.simpleName)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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