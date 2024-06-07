package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.doublewallpaper

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
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentDoubleWallpaperBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.ChargingAnimationAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.DoubleWallpaperAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.ChargingAnimModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.DoubleWallModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.HomeTabsFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.batteryanimation.ChargingAnimationViewmodel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.DownloadCallbackDouble
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.BlurView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.DoubeWallpaperViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.DoubleSharedViewmodel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class DoubleWallpaperFragment : Fragment() {
    private var _binding:FragmentDoubleWallpaperBinding ?= null

    val doubleWallpaperViewmodel: DoubeWallpaperViewModel by activityViewModels()
    private val binding get() = _binding!!

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private lateinit var myActivity : MainActivity
    var adapter: DoubleWallpaperAdapter?= null

    val TAG = "DoubleWallpaper"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoubleWallpaperBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        myActivity = activity as MainActivity

        val layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvDouble.layoutManager = layoutManager
        binding.rvDouble.addItemDecoration(RvItemDecore(2,5,false,10000))

        updateUIWithFetchedData()
        adapter!!.setCoroutineScope(fragmentScope)

    }

    private fun loadData() {
        Log.d("functionCallingTest", "onCreateCustom:  home on create")
        doubleWallpaperViewmodel.doubleWallList.observe(viewLifecycleOwner){result->
            when(result){
                is Response.Success -> {

                    Log.e(TAG, "ChargingAnimation: "+result.data )
                    lifecycleScope.launch(Dispatchers.IO) {
                        val list = result.data?.let { addNullValueInsideArray(it.shuffled()) }

                        withContext(Dispatchers.Main){
                            list?.let { adapter?.updateMoreData(it) }
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

        val list = ArrayList<DoubleWallModel?>()
        adapter = DoubleWallpaperAdapter(list, object :
            DownloadCallbackDouble {
            override fun getPosition(position: Int, model: DoubleWallModel) {
                val newPosition = position + 1

                Log.e(TAG, "getPosition: "+model )

                Log.e(TAG, "getPosition: "+position )

                val allItems = adapter?.getAllItems()

//                sharedViewModel.setChargingAdPosition(newPosition)
                Log.e(TAG, "getPosition:$position odd " )

                navigateToDestination(allItems!!,position)

//                SDKBaseController.getInstance().showInterstitialAds(
//                    requireActivity(),
//                    "mainscr_sub_cate_tab_click_item",
//                    "mainscr_sub_cate_tab_click_item",
//                    showLoading = true,
//                    adsListener = object : CommonAdsListenerAdapter() {
//                        override fun onAdsShowFail(errorCode: Int) {
//                            Log.e("********ADS", "onAdsShowFail: "+errorCode )
//                            navigateToDestination(allItems!!,position)
//                            //do something
//                        }
//
//                        override fun onAdsDismiss() {
//                            Log.e("********ADS", "onAdsDismiss: " )
//                            navigateToDestination(allItems!!,position)
//
////                                    navigateToDestination(allItems,position)
//                        }
//
//                    }
//                )
            }
        },myActivity)

        binding.rvDouble.adapter = adapter
    }



    private val fragmentScope: CoroutineScope by lazy { MainScope() }

    private suspend fun addNullValueInsideArray(data: List<DoubleWallModel?>): ArrayList<DoubleWallModel?>{

        return withContext(Dispatchers.IO){
            val firstAdLineThreshold = if (AdConfig.firstAdLineViewListWallSRC != 0) AdConfig.firstAdLineViewListWallSRC else 4
            val firstLine = firstAdLineThreshold * 3

            val lineCount = if (AdConfig.lineCountViewListWallSRC != 0) AdConfig.lineCountViewListWallSRC else 5
            val lineC = lineCount * 3
            val newData = arrayListOf<DoubleWallModel?>()

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


    private fun navigateToDestination(arrayList: ArrayList<DoubleWallModel?>, position:Int) {
        Log.e(TAG, "navigateToDestination: inside", )

        val countOfNulls = arrayList.subList(0, position).count { it == null }
//        val sharedViewModel: SharedViewModel by activityViewModels()
        val sharedViewModel: DoubleSharedViewmodel by activityViewModels()


        sharedViewModel.setDoubleWalls(arrayList.filterNotNull())



        Bundle().apply {
            Log.e(TAG, "navigateToDestination: inside bundle", )

            putString("from","trending")
            putString("wall","home")
            putInt("position",position - countOfNulls)
            findNavController().navigate(R.id.doubleWallpaperSliderFragment,this)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}