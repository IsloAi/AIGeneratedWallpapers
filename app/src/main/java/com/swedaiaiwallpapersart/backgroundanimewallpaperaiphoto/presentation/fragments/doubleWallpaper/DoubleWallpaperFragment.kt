package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.fragments.doubleWallpaper

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentDoubleWallpaperBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.DoubleWallpaperAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.DoubleWallModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.activity.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.Constants
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.Constants.Companion.checkAppOpen
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.DownloadCallbackDouble
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.viewmodels.DataFromRoomViewmodel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class DoubleWallpaperFragment : Fragment() {
    private var _binding: FragmentDoubleWallpaperBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var myActivity: MainActivity
    val viewmodel: DataFromRoomViewmodel by viewModels()
    var adapter: DoubleWallpaperAdapter? = null
    private val fragmentScope: CoroutineScope by lazy { MainScope() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoubleWallpaperBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        myActivity = activity as MainActivity
        val layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvDouble.layoutManager = layoutManager
        binding.rvDouble.addItemDecoration(RvItemDecore(2, 5, false, 10000))

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

    private fun loadData() {
        Log.d("functionCallingTest", "onCreateCustom:  home on create")
        val list = ArrayList<DoubleWallModel>()
        lifecycleScope.launch {
            viewmodel.fetchAllDoubleWallpapers()
            viewmodel.doubleWallpapers.collect { wallpapers ->
                Log.d("Popular", "updateUIWithFetchedData:wallpapers ${wallpapers.size} ")
                wallpapers.forEach { item ->
                    val single = DoubleWallModel(
                        item.id,
                        item.hd_url1,
                        item.compress_url1,
                        item.size1,
                        item.hd_url2,
                        item.compress_url2,
                        item.size2,
                        item.downloaded
                    )
                    if (!list.contains(single)) {
                        list.add(single)
                    }
                }
                //Unlock 20% randomly BUT SINCE DB DOES NOT HAVE THE FIELD OF UNLOCK
                //val unlockCount = (list.size * 0.2).toInt()
                //list.shuffled().take(unlockCount).forEach { it.unlocked = true }
                Log.d("Double", "loadData:list:$list ")
                val data = if (AdConfig.ISPAIDUSER) {
                    list
                } else {
                    list?.let { addNullValueInsideArray(it) }
                }

                withContext(Dispatchers.Main) {
                    data.let { adapter?.updateData(it as ArrayList<DoubleWallModel?>) }
                    adapter!!.setCoroutineScope(fragmentScope)
                }
            }
        }
    }

    private suspend fun addNullValueInsideArray(data: List<DoubleWallModel?>): ArrayList<DoubleWallModel?> {

        return withContext(Dispatchers.IO) {
            val newData = arrayListOf<DoubleWallModel?>()

            for (i in data.indices) {
                newData.add(data[i]) // Add the current item

                if ((i + 1) % 12 == 0) { // Add null every 15th item
                    newData.add(null)
                    Log.e(
                        "DoubleWall",
                        "addNullValueInsideArray: Added null at position ${newData.size - 1}"
                    )
                }
            }
            Log.e("DoubleWall", "addNullValueInsideArray: Final size = ${newData.size}")
            newData
        }
    }

    private fun navigateToDestination(arrayList: ArrayList<DoubleWallModel?>, position: Int) {
        val countOfNulls = arrayList.subList(0, position).count { it == null }
        /*val sharedViewModel: DoubleSharedViewmodel by activityViewModels()
        sharedViewModel.setDoubleWalls(arrayList.filterNotNull())*/
        if (AdConfig.ISPAIDUSER) {
            Bundle().apply {
                Log.e("DoubleWall", "navigateToDestination: inside bundle")
                putString("from", "trending")
                putString("wall", "home")
                putInt("position", position - countOfNulls)
                findNavController().navigate(R.id.doubleWallpaperSliderFragment, this)
            }
        } else {
            /*if (AdClickCounter.shouldShowAd()) {
                MaxInterstitialAds.showInterstitialAd(requireActivity(), object : MaxAdListener {
                    override fun onAdLoaded(p0: MaxAd) {}

                    override fun onAdDisplayed(p0: MaxAd) {}

                    override fun onAdHidden(p0: MaxAd) {
                        Bundle().apply {
                            Log.e("DoubleWall", "navigateToDestination: inside bundle")
                            putString("from", "trending")
                            putString("wall", "home")
                            putInt("position", position - countOfNulls)
                            findNavController().navigate(
                                R.id.doubleWallpaperSliderFragment,
                                this
                            )
                        }
                    }

                    override fun onAdClicked(p0: MaxAd) {

                    }

                    override fun onAdLoadFailed(p0: String, p1: MaxError) {

                    }

                    override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {

                    }
                })
            } else {
                AdClickCounter.increment()
                Bundle().apply {
                    Log.e("DoubleWall", "navigateToDestination: inside bundle")
                    putString("from", "trending")
                    putString("wall", "home")
                    putInt("position", position - countOfNulls)
                    findNavController().navigate(R.id.doubleWallpaperSliderFragment, this)
                }
            }*/
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        loadData()
        adapter!!.setCoroutineScope(fragmentScope)
        if (isAdded) {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Live WallPapers Screen")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, javaClass.simpleName)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }
    }

}