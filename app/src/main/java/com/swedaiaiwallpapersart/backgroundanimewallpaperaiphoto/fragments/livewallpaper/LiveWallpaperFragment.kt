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
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentLiveWallpaperBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.LiveWallpaperAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.HomeTabsFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.downloadCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.LiveWallpaperModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.BlurView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.LiveWallpaperViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class LiveWallpaperFragment : Fragment() {

    private var _binding:FragmentLiveWallpaperBinding ?= null
    private val binding get() = _binding!!
    private val myViewModel: LiveWallpaperViewModel by activityViewModels()

    val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var myActivity : MainActivity
    var adapter:LiveWallpaperAdapter ?= null

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

        myActivity = activity as MainActivity

        binding.liveReccyclerview.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.liveReccyclerview.addItemDecoration(RvItemDecore(3,5,false,10000))
        updateUIWithFetchedData()
        adapter!!.setCoroutineScope(fragmentScope)
    }


    private fun loadData() {
        Log.d("functionCallingTest", "onCreateCustom:  home on create")
        // Observe the LiveData in the ViewModel and update the UI accordingly
        myViewModel.wallpaperData.observe(viewLifecycleOwner) { catResponses ->
            if (catResponses != null) {
                Log.e("TAG", "loadData: "+catResponses )
                if (view != null) {
                    // If the view is available, update the UI
//
                    val list = addNullValueInsideArray(catResponses)
                    adapter?.updateMoreData(list)
                    adapter!!.setCoroutineScope(fragmentScope)
                }
            }else{

            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }


    private fun updateUIWithFetchedData() {

        val list = ArrayList<LiveWallpaperModel?>()
        adapter = LiveWallpaperAdapter(list, object :
            downloadCallback {
            override fun getPosition(position: Int, model: LiveWallpaperModel) {
                val newPosition = position + 1

                Log.e(TAG, "getPosition: "+position )

                sharedViewModel.setAdPosition(newPosition)

                if (newPosition % 2 != 0){
                    Log.e(TAG, "getPosition:$position odd " )

                    SDKBaseController.getInstance().showInterstitialAds(
                        requireActivity(),
                        "mainscr_live_tab_click_item",
                        "mainscr_live_tab_click_item",
                        showLoading = true,
                        adsListener = object : CommonAdsListenerAdapter() {
                            override fun onAdsShowFail(errorCode: Int) {
                                Log.e("********ADS", "onAdsShowFail: " + errorCode)
                                BlurView.filePath = ""
                                sharedViewModel.clearLiveWallpaper()
                                sharedViewModel.setLiveWallpaper(listOf(model))
                                if (isAdded){
                                    findNavController().navigate(R.id.downloadLiveWallpaperFragment)
                                }

                                //do something
                            }

                            override fun onAdsDismiss() {
                                Log.e("TAG", "onAdsDismiss: ", )
                                BlurView.filePath = ""
                                sharedViewModel.clearLiveWallpaper()
                                sharedViewModel.setLiveWallpaper(listOf(model))
                                if (isAdded){
                                    findNavController().navigate(R.id.downloadLiveWallpaperFragment)
                                }

                            }

                            override fun onAdsShowTimeout() {
                                super.onAdsShowTimeout()
                                Log.e(TAG, "onAdsShowTimeout: " )

                                sharedViewModel.clearLiveWallpaper()
                                sharedViewModel.setLiveWallpaper(listOf(model))

                                if (isAdded){
                                    findNavController().navigate(R.id.downloadLiveWallpaperFragment)
                                }
                            }
                        }
                    )
                }else{
                    BlurView.filePath = ""
                    sharedViewModel.clearLiveWallpaper()
                    sharedViewModel.setLiveWallpaper(listOf(model))
                    if (isAdded){
                        findNavController().navigate(R.id.downloadLiveWallpaperFragment)
                    }

                    Log.e(TAG, "getPosition:$position even " )
                }






            }
        },myActivity)

        binding.liveReccyclerview.adapter = adapter
    }



    private val fragmentScope: CoroutineScope by lazy { MainScope() }

    private fun addNullValueInsideArray(data: List<LiveWallpaperModel?>): ArrayList<LiveWallpaperModel?>{

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




        return newData
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding =  null
    }
}