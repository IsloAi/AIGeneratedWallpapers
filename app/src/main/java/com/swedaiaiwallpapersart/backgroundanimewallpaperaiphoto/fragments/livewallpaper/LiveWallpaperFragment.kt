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
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.CommonAdsListenerAdapter
import com.bmik.android.sdk.listener.keep.IKLoadNativeAdListener
import com.bmik.android.sdk.tracking.SDKTrackingController
import com.bmik.android.sdk.widgets.IkmNativeAdView
import com.google.firebase.analytics.FirebaseAnalytics
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentLiveWallpaperBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.LiveWallpaperAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.downloadCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.LiveWallpaperModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.BlurView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.LiveWallpaperViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LiveWallpaperFragment : Fragment() {

    private var _binding:FragmentLiveWallpaperBinding ?= null
    private val binding get() = _binding!!
    private val myViewModel: LiveWallpaperViewModel by activityViewModels()

    val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var myActivity : MainActivity
    var adapter:LiveWallpaperAdapter ?= null

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    val TAG = "LIVE_WALL_SCREEN"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLiveWallpaperBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        myActivity = activity as MainActivity

        myViewModel.getAllTrendingWallpapers()

        binding.liveReccyclerview.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.liveReccyclerview.addItemDecoration(RvItemDecore(3,5,false,10000))
        updateUIWithFetchedData()
        adapter!!.setCoroutineScope(fragmentScope)
    }


    private fun loadData() {
        Log.d("functionCallingTest", "onCreateCustom:  home on create")
        myViewModel.liveWallsFromDB.observe(viewLifecycleOwner){result->
            when(result){
                is Response.Success -> {
                    lifecycleScope.launch(Dispatchers.IO) {

                        val list  = result.data?.shuffled()
                        val listNullable = list?.let { addNullValueInsideArray(it) }

                        withContext(Dispatchers.Main){
                            listNullable?.let { adapter?.updateMoreData(it) }
                            adapter!!.setCoroutineScope(fragmentScope)
                        }
                    }
                }

                is Response.Loading -> {
                    Log.e(TAG, "loadData: Loading" )
                }

                is Response.Error -> {
                    Log.e(TAG, "loadData: response error", )

                }

                is Response.Processing -> {
                    Log.e(TAG, "loadData: processing", )
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        loadData()

        Log.e(TAG, "onResume: " )

        if (isAdded){
            sendTracking("screen_active",Pair("action_type", "Tab"), Pair("action_name", "MainScr_LiveTab_View"))
        }


        if (isAdded){
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Live WallPapers Screen")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, javaClass.simpleName)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }
    }

    private fun sendTracking(
        eventName: String,
        vararg param: Pair<String, String?>
    )
    {
        SDKTrackingController.trackingAllApp(requireContext(), eventName, *param)
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

                setDownloadAbleWallpaperAndNavigate(model,true)

//                    SDKBaseController.getInstance().showInterstitialAds(
//                        requireActivity(),
//                        "mainscr_live_tab_click_item",
//                        "mainscr_live_tab_click_item",
//                        showLoading = true,
//                        adsListener = object : CommonAdsListenerAdapter() {
//                            override fun onAdsShowFail(errorCode: Int) {
//                                Log.e("********ADS", "onAdsShowFail: " + errorCode)
//                                setDownloadAbleWallpaperAndNavigate(model,false)
//
//                                //do something
//                            }
//
//                            override fun onAdsDismiss() {
//                                Log.e("TAG", "onAdsDismiss: ", )
//                                setDownloadAbleWallpaperAndNavigate(model,true)
//
//                            }
//
//                            override fun onAdsShowTimeout() {
//                                super.onAdsShowTimeout()
//                                Log.e(TAG, "onAdsShowTimeout: " )
//                                setDownloadAbleWallpaperAndNavigate(model,false)
//                            }
//                        }
//                    )







            }
        },myActivity)


        SDKBaseController.getInstance().loadIkmNativeAdView(requireContext(),"mainscr_live_tab_scroll","mainscr_live_tab_scroll",object :
            IKLoadNativeAdListener {
            override fun onAdFailedToLoad(errorCode: Int) {
                Log.e(TAG, "onAdFailedToLoad: "+errorCode )

            }

            override fun onAdLoaded(adsResult: IkmNativeAdView?) {
                if (isAdded && view!= null){
                    adapter?.nativeAdView = adsResult
                    binding.liveReccyclerview.adapter = adapter
                }else{
                    Log.e(TAG, "onAdLoaded: View Null", )
                }
            }

        })


        binding.liveReccyclerview.adapter = adapter
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




    override fun onDestroyView() {
        super.onDestroyView()
        _binding =  null
    }
}