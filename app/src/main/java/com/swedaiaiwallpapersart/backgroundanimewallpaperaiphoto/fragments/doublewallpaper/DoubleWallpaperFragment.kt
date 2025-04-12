package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.doublewallpaper

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
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.google.firebase.analytics.FirebaseAnalytics
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentDoubleWallpaperBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.DoubleWallpaperAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.AdEventListener
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MaxAD
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MaxInterstitialAds
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MyApp
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.DoubleWallModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.DownloadCallbackDouble
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants.Companion.checkAppOpen
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.DoubeWallpaperViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.DoubleSharedViewmodel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class DoubleWallpaperFragment : Fragment(), AdEventListener {
    private var _binding: FragmentDoubleWallpaperBinding? = null

    val doubleWallpaperViewmodel: DoubeWallpaperViewModel by activityViewModels()
    private val binding get() = _binding!!

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private lateinit var myActivity: MainActivity
    var adapter: DoubleWallpaperAdapter? = null

    val TAG = "Double"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoubleWallpaperBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        myActivity = activity as MainActivity

        val layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvDouble.layoutManager = layoutManager
        binding.rvDouble.addItemDecoration(RvItemDecore(2, 5, false, 10000))

        //updateUIWithFetchedData()


    }

    override fun onStart() {
        super.onStart()
        (myActivity.application as MyApp).registerAdEventListener(this)

    }

    private fun loadData() {
        Log.d("functionCallingTest", "onCreateCustom:  home on create")
        doubleWallpaperViewmodel.doubleWallList.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Response.Success -> {
                    Log.e(TAG, "loadData: " + result.data)
                    lifecycleScope.launch(Dispatchers.IO) {
                        val list = result.data
                        Log.d("Double", "loadData:list:$list ")
                        val data = if (AdConfig.ISPAIDUSER) {
                            list
                        } else {
                            list?.let { addNullValueInsideArray(it) }
                        }

                        withContext(Dispatchers.Main) {
                            data?.let { adapter?.updateData(it as ArrayList<DoubleWallModel?>) }
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
        updateUIWithFetchedData()
        loadData()
        adapter!!.setCoroutineScope(fragmentScope)
        if (isAdded) {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Live WallPapers Screen")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, javaClass.simpleName)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }
    }

    private fun updateUIWithFetchedData() {

        val list = ArrayList<DoubleWallModel?>()
        adapter = DoubleWallpaperAdapter(list, object :
            DownloadCallbackDouble {
            override fun getPosition(position: Int, model: DoubleWallModel) {

                val newPosition = position + 1

                val allItems = adapter?.getAllItems()

                if (isAdded) {
                    navigateToDestination(allItems!!, position)
                }
            }
        }, myActivity)
        binding.rvDouble.adapter = adapter
        binding.rvDouble.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    // Set the boolean to true when the RecyclerView is scrolled
                    if (dy != 0 || dx != 0) {
                        Constants.checkInter = false
                        checkAppOpen = false
                    }
                }
            })
    }

    private val fragmentScope: CoroutineScope by lazy { MainScope() }

    private suspend fun addNullValueInsideArray(data: List<DoubleWallModel?>): ArrayList<DoubleWallModel?> {

        return withContext(Dispatchers.IO) {
            val newData = arrayListOf<DoubleWallModel?>()

            for (i in data.indices) {
                newData.add(data[i]) // Add the current item

                if ((i + 1) % 12 == 0) { // Add null every 15th item
                    newData.add(null)
                    Log.e(
                        TAG,
                        "addNullValueInsideArray: Added null at position ${newData.size - 1}"
                    )
                }
            }
            Log.e(TAG, "addNullValueInsideArray: Final size = ${newData.size}")



            newData
        }
    }

    private fun navigateToDestination(arrayList: ArrayList<DoubleWallModel?>, position: Int) {
        val countOfNulls = arrayList.subList(0, position).count { it == null }
        val sharedViewModel: DoubleSharedViewmodel by activityViewModels()
        sharedViewModel.setDoubleWalls(arrayList.filterNotNull())
        if (AdConfig.ISPAIDUSER) {
            Bundle().apply {
                Log.e(TAG, "navigateToDestination: inside bundle")
                putString("from", "trending")
                putString("wall", "home")
                putInt("position", position - countOfNulls)
                findNavController().navigate(R.id.doubleWallpaperSliderFragment, this)
            }
        } else {
            MaxInterstitialAds.showInterstitial(requireActivity(),
                object : MaxAdListener {
                    override fun onAdLoaded(p0: MaxAd) {
                        MaxInterstitialAds.showInterstitial(
                            requireActivity(),
                            object : MaxAdListener {
                                override fun onAdLoaded(p0: MaxAd) {}

                                override fun onAdDisplayed(p0: MaxAd) {
                                }

                                override fun onAdHidden(p0: MaxAd) {
                                }

                                override fun onAdClicked(p0: MaxAd) {
                                }

                                override fun onAdLoadFailed(p0: String, p1: MaxError) {
                                }

                                override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
                                }
                            },
                            object : MaxAD {
                                override fun adNotReady(type: String) {}
                            })
                    }

                    override fun onAdDisplayed(p0: MaxAd) {}

                    override fun onAdHidden(p0: MaxAd) {
                        Bundle().apply {
                            Log.e(TAG, "navigateToDestination: inside bundle")
                            putString("from", "trending")
                            putString("wall", "home")
                            putInt("position", position - countOfNulls)
                            findNavController().navigate(R.id.doubleWallpaperSliderFragment, this)
                        }
                        MaxInterstitialAds.loadInterstitialAd(requireContext())
                    }

                    override fun onAdClicked(p0: MaxAd) {}

                    override fun onAdLoadFailed(p0: String, p1: MaxError) {
                        //Toast.makeText(requireContext(), "Ad not available", Toast.LENGTH_SHORT).show()
                        Bundle().apply {
                            Log.e(TAG, "navigateToDestination: inside bundle")
                            putString("from", "trending")
                            putString("wall", "home")
                            putInt("position", position - countOfNulls)
                            findNavController().navigate(R.id.doubleWallpaperSliderFragment, this)
                        }
                    }

                    override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
                        //Toast.makeText(requireContext(), "Ad not available", Toast.LENGTH_SHORT).show()
                        Bundle().apply {
                            Log.e(TAG, "navigateToDestination: inside bundle")
                            putString("from", "trending")
                            putString("wall", "home")
                            putInt("position", position - countOfNulls)
                            findNavController().navigate(R.id.doubleWallpaperSliderFragment, this)
                        }
                    }
                },
                object : MaxAD {
                    override fun adNotReady(type: String) {
                        if (MaxInterstitialAds.willIntAdShow) {
                            /*Toast.makeText(requireContext(), "Ad not available", Toast.LENGTH_SHORT)
                                .show()*/
                            Bundle().apply {
                                Log.e(TAG, "navigateToDestination: inside bundle")
                                putString("from", "trending")
                                putString("wall", "home")
                                putInt("position", position - countOfNulls)
                                findNavController().navigate(
                                    R.id.doubleWallpaperSliderFragment,
                                    this
                                )
                            }
                        } else {
                            Bundle().apply {
                                Log.e(TAG, "navigateToDestination: inside bundle")
                                putString("from", "trending")
                                putString("wall", "home")
                                putInt("position", position - countOfNulls)
                                findNavController().navigate(
                                    R.id.doubleWallpaperSliderFragment,
                                    this
                                )
                            }
                        }

                    }
                })
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
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