package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.fragments.liveWallpaper

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.google.firebase.analytics.FirebaseAnalytics
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentLiveWallpaperBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.AdClickCounter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MaxInterstitialAds
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.LiveWallpaperModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.activity.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.adapters.LiveWallpaperAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.BlurView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.Constants.Companion.checkAppOpen
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.Constants.Companion.checkInter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.downloadCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.viewmodels.DataFromRoomViewmodel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LiveWallpaperFragment : Fragment() {

    private var _binding: FragmentLiveWallpaperBinding? = null
    private val binding get() = _binding!!
    private lateinit var myActivity: MainActivity
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    var adapter: LiveWallpaperAdapter? = null
    private var liveList = ArrayList<LiveWallpaperModel>()
    private val statusAd = AdConfig.adStatusViewListWallSRC
    val sharedViewModel: SharedViewModel by activityViewModels()
    private val myViewModel: DataFromRoomViewmodel by viewModels()

    val TAG = "LIVE_WALL_SCREEN"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLiveWallpaperBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        myActivity = activity as MainActivity
        binding.liveReccyclerview.layoutManager = GridLayoutManager(requireContext(), 3)
        (binding.liveReccyclerview.layoutManager as GridLayoutManager).isItemPrefetchEnabled = false
        binding.liveReccyclerview.addItemDecoration(RvItemDecore(3, 5, false, 10000))
        updateUIWithFetchedData()

    }

    private fun loadData() {
        lifecycleScope.launch {
            myViewModel.fetchAllLiveWallpapers()
            myViewModel.liveWallpapers.collect { wallpapers ->
                // Update your UI with wallpapers
                Log.d("Popular", "updateUIWithFetchedData:wallpapers ${wallpapers.size} ")
                val list = arrayListOf<LiveWallpaperModel>()
                wallpapers.forEach { item ->
                    val single = LiveWallpaperModel(
                        item.id,
                        item.livewallpaper_url,
                        item.thumnail_url,
                        item.videoSize,
                        item.liked,
                        item.downloads,
                        item.catname,
                        item.likes,
                        false
                    )
                    if (!list.contains(single)) {
                        list.add(single)
                    }
                }
                // Unlock 20% randomly
                val unlockCount = (list.size * 0.2).toInt()
                list.shuffled().take(unlockCount).forEach { it.unlocked = true }

                if (view != null) {
                    lifecycleScope.launch {
                        liveList = list
                        val listNullable = if (AdConfig.ISPAIDUSER) {
                            list
                        } else {
                            addNullValueInsideArray(list)
                        }
                        listNullable.let { adapter?.updateData(it as ArrayList<LiveWallpaperModel?>) }
                        adapter!!.setCoroutineScope(fragmentScope)
                    }
                }
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

        adapter!!.setCoroutineScope(fragmentScope)
        binding.refreshLive.setOnRefreshListener {
            lifecycleScope.launch(Dispatchers.Main) {
                if (liveList.isNotEmpty()) {
                    val listWithNulls = if (AdConfig.ISPAIDUSER) {
                        liveList.shuffled()
                    } else {
                        addNullValueInsideArray(liveList.shuffled())
                    }
                    liveList = listWithNulls as ArrayList<LiveWallpaperModel>
                    adapter?.updateData(listWithNulls as ArrayList<LiveWallpaperModel?>)
                } else {
                    Toast.makeText(requireContext(), "No wallpapers to refresh", Toast.LENGTH_SHORT)
                        .show()
                }
                binding.refreshLive.isRefreshing = false
            }
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
