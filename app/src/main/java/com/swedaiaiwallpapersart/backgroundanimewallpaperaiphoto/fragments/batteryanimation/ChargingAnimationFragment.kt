package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.batteryanimation

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
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.CommonAdsListenerAdapter
import com.bmik.android.sdk.tracking.SDKTrackingController
import com.google.firebase.analytics.FirebaseAnalytics
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentChargingAnimationBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.ChargingAnimationAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.LiveWallpaperAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.ChargingAnimModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.downloadCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.LiveWallpaperModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.BlurView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.BatteryAnimationViewmodel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.LiveWallpaperViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ChargingAnimationFragment : Fragment() {
    private var _binding:FragmentChargingAnimationBinding ?= null
    private val binding get() = _binding!!

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    val sharedViewModel: BatteryAnimationViewmodel by activityViewModels()

    val chargingAnimationViewmodel:ChargingAnimationViewmodel by activityViewModels()

    private lateinit var myActivity : MainActivity
    var adapter: ChargingAnimationAdapter?= null

    val TAG = "ChargingAnimation"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChargingAnimationBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        myActivity = activity as MainActivity

        val layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerviewAll.layoutManager = layoutManager
        binding.recyclerviewAll.addItemDecoration(RvItemDecore(3,5,false,10000))

        updateUIWithFetchedData()
        adapter!!.setCoroutineScope(fragmentScope)

    }

    private fun loadData() {
        Log.d("functionCallingTest", "onCreateCustom:  home on create")
        chargingAnimationViewmodel.chargingAnimList.observe(viewLifecycleOwner){result->
            when(result){
                is Response.Success -> {

                    Log.e(TAG, "ChargingAnimation: "+result.data )
                    lifecycleScope.launch(Dispatchers.IO) {
                        val list = result.data

                        val data = if (AdConfig.ISPAIDUSER){
                            list as ArrayList<ChargingAnimModel?>
                        }else{
                            list?.let { addNullValueInsideArray(it.shuffled()) }
                        }

                        withContext(Dispatchers.Main){
                            data?.let { adapter?.updateMoreData(it) }
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

        if (isAdded){
            sendTracking("screen_active",Pair("action_type", "Tab"), Pair("action_name", "MainScr_ChargingTab_View"))
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

        val list = ArrayList<ChargingAnimModel?>()
        adapter = ChargingAnimationAdapter(list, object :
            ChargingAnimationAdapter.downloadCallback {
            override fun getPosition(position: Int, model: ChargingAnimModel) {
                val newPosition = position + 1

                Log.e(TAG, "getPosition: "+model )

                Log.e(TAG, "getPosition: "+position )

                sharedViewModel.setChargingAdPosition(newPosition)
                    Log.e(TAG, "getPosition:$position odd " )

                if (AdConfig.ISPAIDUSER){
                    setPathandNavigate(model,false)
                }else{
                    SDKBaseController.getInstance().showInterstitialAds(
                        requireActivity(),
                        "mainscr_live_tab_click_item",
                        "mainscr_live_tab_click_item",
                        showLoading = true,
                        adsListener = object : CommonAdsListenerAdapter() {
                            override fun onAdsShowFail(errorCode: Int) {
                                Log.e("********ADS", "onAdsShowFail: " + errorCode)
                                setPathandNavigate(model,false)
                            }

                            override fun onAdsDismiss() {
                                Log.e("TAG", "onAdsDismiss: ", )
                                setPathandNavigate(model,true)

                            }

                            override fun onAdsShowTimeout() {
                                super.onAdsShowTimeout()
                                Log.e(TAG, "onAdsShowTimeout: " )
                                setPathandNavigate(model,false)
                            }
                        }
                    )
                }

            }
        },myActivity)

        binding.recyclerviewAll.adapter = adapter
    }

    private fun setPathandNavigate(model: ChargingAnimModel,adShowd:Boolean) {
        BlurView.filePathBattery = ""
        sharedViewModel.clearChargeAnimation()
        sharedViewModel.setchargingAnimation(listOf(model))
        if (isAdded) {
            Bundle().apply {
                putBoolean("adShowed",adShowd)
                findNavController().navigate(R.id.downloadBatteryAnimation,this)
            }
        }
    }


    private val fragmentScope: CoroutineScope by lazy { MainScope() }

    private suspend fun addNullValueInsideArray(data: List<ChargingAnimModel?>): ArrayList<ChargingAnimModel?>{

        return withContext(Dispatchers.IO){
            val firstAdLineThreshold = if (AdConfig.firstAdLineViewListWallSRC != 0) AdConfig.firstAdLineViewListWallSRC else 4
            val firstLine = firstAdLineThreshold * 3

            val lineCount = if (AdConfig.lineCountViewListWallSRC != 0) AdConfig.lineCountViewListWallSRC else 5
            val lineC = lineCount * 3
            val newData = arrayListOf<ChargingAnimModel?>()

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
        _binding = null
    }
}