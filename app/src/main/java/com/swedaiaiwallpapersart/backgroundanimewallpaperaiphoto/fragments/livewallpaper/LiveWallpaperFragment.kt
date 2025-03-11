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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
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
    private val statusAd = AdConfig.adStatusViewListWallSRC

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
                        Log.d("LIVE", "loadData: ${list?.size} ")
                        val listNullable = if (!AdConfig.ISPAIDUSER) {
                            list?.let { addNullValueInsideArray(it) }
                        } else {
                            if (list != null) {
                                ArrayList(list)
                            } else {
                                ArrayList()
                            }
                        }

                        withContext(Dispatchers.Main) {
                            listNullable?.let { adapter?.updateData(it) }
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
            Bundle().apply {
                putBoolean("adShowed", adShowd)
                DownloadLiveWallpaperFragment.shouldObserveLiveWallpapers = true
                DownloadLiveWallpaperFragment.shouldObserveFavorites = false
                findNavController().navigate(R.id.downloadLiveWallpaperFragment, this)
            }

        }
    }

    private val fragmentScope: CoroutineScope by lazy { MainScope() }

    private suspend fun addNullValueInsideArray(data: List<LiveWallpaperModel?>): ArrayList<LiveWallpaperModel?> {

        return withContext(Dispatchers.IO) {
            when (AdConfig.liveTabScrollType) {
                0 -> {
                    Log.d(TAG, "addNullValueInsideArray: in 0 ")
                    return@withContext ArrayList(data)
                }

                1 -> {
                    Log.d(TAG, "addNullValueInsideArray: in 1 ")
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
                        } else if (i == firstLine) {
                            newData.add(null)
                        }
                        newData.add(data[i])
                    }
                    return@withContext ArrayList(newData)
                }

                2 -> {
                    Log.d(TAG, "addNullValueInsideArray: in 2 ")
                    val newData = arrayListOf<LiveWallpaperModel?>()

                    val positionToInsertNull =
                        if (AdConfig.firstAdLineViewListWallSRC != 0) AdConfig.firstAdLineViewListWallSRC * 3 else 4 * 3

                    for (i in data.indices) {
                        if (i == positionToInsertNull) {
                            newData.add(null) // Add a single null at the specified position.
                        }
                        newData.add(data[i]) // Add the original item to the list.
                    }

                    // If the position exceeds the list size, just append the null at the end.
                    if (positionToInsertNull >= data.size) {
                        newData.add(null)
                    }
                    Log.d(TAG, "addNullValueInsideArray:Array list size ${newData.size} ")
                    return@withContext ArrayList(newData)
                }

                3 -> {
                    Log.d(TAG, "addNullValueInsideArray: in 3 ")
                    return@withContext ArrayList(data)
                }

                else -> {
                    return@withContext ArrayList(data)
                }
            }
        }
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
