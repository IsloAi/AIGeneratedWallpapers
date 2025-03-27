package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.batteryanimation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentChargingAnimationBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.ChargingAnimationAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.AdEventListener
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MaxAD
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MaxInterstitialAds
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MyApp
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.ChargingAnimModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.BlurView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants.Companion.checkAppOpen
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.BatteryAnimationViewmodel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ChargingAnimationFragment : Fragment(), AdEventListener {
    private var _binding: FragmentChargingAnimationBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    val sharedViewModel: BatteryAnimationViewmodel by activityViewModels()

    val chargingAnimationViewmodel: ChargingAnimationViewmodel by activityViewModels()

    private lateinit var myActivity: MainActivity
    var adapter: ChargingAnimationAdapter? = null

    val TAG = "ChargingAnimation"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChargingAnimationBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        myActivity = activity as MainActivity

        val layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerviewAll.layoutManager = layoutManager
        binding.recyclerviewAll.addItemDecoration(RvItemDecore(3, 5, false, 10000))

        updateUIWithFetchedData()
        adapter!!.setCoroutineScope(fragmentScope)

    }

    private fun loadData() {
        Log.d("functionCallingTest", "onCreateCustom:  home on create")
        chargingAnimationViewmodel.chargingAnimList.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Response.Success -> {
                    Log.e(TAG, "ChargingAnimation: " + result.data)
                    lifecycleScope.launch(Dispatchers.IO) {
                        val list = result.data
                        val data = if (AdConfig.ISPAIDUSER) {
                            list as ArrayList<ChargingAnimModel?>
                        } else {
                            list?.let { addNullValueInsideArray(it.shuffled()) }
                        }
                        withContext(Dispatchers.Main) {
                            data?.let { adapter?.updateMoreData(it) }
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

    override fun onStart() {
        super.onStart()
        (myActivity.application as MyApp).registerAdEventListener(this)

    }

    override fun onResume() {
        super.onResume()
        loadData()

        if (isAdded) {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Live WallPapers Screen")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, javaClass.simpleName)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }
    }

    private fun updateUIWithFetchedData() {

        val list = ArrayList<ChargingAnimModel?>()
        adapter =
            ChargingAnimationAdapter(list, object : ChargingAnimationAdapter.downloadCallback {
                override fun getPosition(position: Int, model: ChargingAnimModel) {
                    val newPosition = position + 1

                    Log.e(TAG, "getPosition: " + model)

                    Log.e(TAG, "getPosition: " + position)

                    sharedViewModel.setChargingAdPosition(newPosition)
                    Log.e(TAG, "getPosition:$position odd ")


                    setPathandNavigate(model, false)
                }
            }, myActivity)

        binding.recyclerviewAll.adapter = adapter
        binding.recyclerviewAll.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

    private fun setPathandNavigate(model: ChargingAnimModel, adShowd: Boolean) {
        BlurView.filePathBattery = ""
        sharedViewModel.clearChargeAnimation()
        sharedViewModel.setchargingAnimation(listOf(model))
        if (isAdded) {
            MaxInterstitialAds.showInterstitial(requireActivity(), object : MaxAdListener {
                override fun onAdLoaded(p0: MaxAd) {
                    MaxInterstitialAds.showInterstitial(
                        requireActivity(),
                        object : MaxAdListener {
                            override fun onAdLoaded(p0: MaxAd) {}

                            override fun onAdDisplayed(p0: MaxAd) {}

                            override fun onAdHidden(p0: MaxAd) {}

                            override fun onAdClicked(p0: MaxAd) {}

                            override fun onAdLoadFailed(p0: String, p1: MaxError) {}

                            override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {}
                        },
                        object : MaxAD {
                            override fun adNotReady(type: String) {}
                        })
                }

                override fun onAdDisplayed(p0: MaxAd) {}

                override fun onAdHidden(p0: MaxAd) {
                    Toast.makeText(requireContext(), "AD not Available", Toast.LENGTH_SHORT)
                    Bundle().apply {
                        putBoolean("adShowed", adShowd)
                        findNavController().navigate(R.id.downloadBatteryAnimation, this)
                    }
                }

                override fun onAdClicked(p0: MaxAd) {}

                override fun onAdLoadFailed(p0: String, p1: MaxError) {
                    Toast.makeText(requireContext(), "AD not Available", Toast.LENGTH_SHORT)
                    Bundle().apply {
                        putBoolean("adShowed", adShowd)
                        findNavController().navigate(R.id.downloadBatteryAnimation, this)
                    }
                }

                override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
                    Toast.makeText(requireContext(), "AD not Available", Toast.LENGTH_SHORT)
                    Bundle().apply {
                        putBoolean("adShowed", adShowd)
                        findNavController().navigate(R.id.downloadBatteryAnimation, this)
                    }
                }
            }, object : MaxAD {
                override fun adNotReady(type: String) {
                    Toast.makeText(requireContext(), "AD not Available", Toast.LENGTH_SHORT).show()
                    Bundle().apply {
                        putBoolean("adShowed", adShowd)
                        findNavController().navigate(R.id.downloadBatteryAnimation, this)
                    }
                }
            })
        }
    }

    private val fragmentScope: CoroutineScope by lazy { MainScope() }

    private suspend fun addNullValueInsideArray(data: List<ChargingAnimModel?>): ArrayList<ChargingAnimModel?> {
        return withContext(Dispatchers.IO) {
            val firstAdLineThreshold =
                if (AdConfig.firstAdLineViewListWallSRC != 0) AdConfig.firstAdLineViewListWallSRC else 4
            val firstLine = firstAdLineThreshold * 3

            val lineCount =
                if (AdConfig.lineCountViewListWallSRC != 0) AdConfig.lineCountViewListWallSRC else 5
            val lineC = lineCount * 3
            val newData = arrayListOf<ChargingAnimModel?>()

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
            newData
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