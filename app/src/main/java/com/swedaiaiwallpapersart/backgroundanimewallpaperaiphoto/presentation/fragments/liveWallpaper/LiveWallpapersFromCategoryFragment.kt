package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.livewallpaper

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentLiveWallpapersFromCategoryBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.LiveWallpaperModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.activity.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.adapters.LiveWallpaperAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.BlurView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.Constants
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.Constants.Companion.checkAppOpen
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.downloadCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LiveWallpapersFromCategoryFragment : Fragment() {

    private var _binding: FragmentLiveWallpapersFromCategoryBinding? = null
    private val binding get() = _binding!!
    //private val myViewModel: GetLiveWallpaperByCategoryViewmodel by activityViewModels()
    //val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var myActivity: MainActivity
    var adapter: LiveWallpaperAdapter? = null

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    val TAG = "LIVE_WALL_SCREEN"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLiveWallpapersFromCategoryBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        val from = arguments?.getString("from")
        val name = arguments?.getString("wall")

        binding.catTitle.text = name
        myActivity = activity as MainActivity
        binding.liveReccyclerview.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.liveReccyclerview.addItemDecoration(RvItemDecore(3, 5, false, 10000))
        updateUIWithFetchedData()
        adapter!!.setCoroutineScope(fragmentScope)

        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
            Constants.checkInter = false
            checkAppOpen = false
        }

    }

    fun initObservers() {
        /*myViewModel.liveWallpapers.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Response.Error -> {
                    Log.e(TAG, "loadData: Loading")
                }

                Response.Loading -> {
                    Log.e(TAG, "loadData: response error")

                }

                is Response.Processing -> {
                    Log.e(TAG, "loadData: processing")
                }

                is Response.Success -> {
                    lifecycleScope.launch(Dispatchers.IO) {
                        Log.e(TAG, "initObservers: " + result.data)

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
            }

        }*/
    }

    private fun updateUIWithFetchedData() {

        val list = ArrayList<LiveWallpaperModel?>()
        adapter = LiveWallpaperAdapter(list, object :
            downloadCallback {
            override fun getPosition(position: Int, model: LiveWallpaperModel) {
                val newPosition = position + 1

                Log.e(TAG, "getPosition: $model")

                Log.e(TAG, "getPosition: $position")

                //sharedViewModel.setAdPosition(newPosition)
                Log.e(TAG, "getPosition:$position odd ")
                if (AdConfig.ISPAIDUSER) {
                    setDownloadAbleWallpaperAndNavigate(model, true)
                } else {
                    /*if (isAdded) {
                        Constants.checkInter = false
                        MaxInterstitialAds.showInterstitialAd(requireActivity(),
                            object : MaxAdListener {
                                override fun onAdLoaded(p0: MaxAd) {}

                                override fun onAdDisplayed(p0: MaxAd) {}

                                override fun onAdHidden(p0: MaxAd) {
                                    setDownloadAbleWallpaperAndNavigate(model, true)
                                }

                                override fun onAdClicked(p0: MaxAd) {}

                                override fun onAdLoadFailed(p0: String, p1: MaxError) {}

                                override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {}
                            })
                    }*/
                }
            }
        }, myActivity)

        binding.liveReccyclerview.adapter = adapter
    }

    private fun showInterAd(model: LiveWallpaperModel) {
    }


    private fun setDownloadAbleWallpaperAndNavigate(model: LiveWallpaperModel, adShowd: Boolean) {
        BlurView.filePath = ""
        /*sharedViewModel.clearLiveWallpaper()
        sharedViewModel.setLiveWallpaper(listOf(model))*/
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

    override fun onResume() {
        super.onResume()

        initObservers()

        if (isAdded) {
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

}