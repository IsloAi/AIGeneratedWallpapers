package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.fragments.charginingAnimation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentChargingAnimationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChargingAnimationFragment : Fragment() {
    private var _binding: FragmentChargingAnimationBinding? = null
    private val binding get() = _binding!!

    /*private lateinit var firebaseAnalytics: FirebaseAnalytics

    val sharedViewModel: BatteryAnimationViewmodel by activityViewModels()

    val chargingAnimationViewmodel: ChargingAnimationViewmodel by activityViewModels()

    private lateinit var myActivity: MainActivity
    var adapter: ChargingAnimationAdapter? = null

    val TAG = "ChargingAnimation"*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChargingAnimationBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        myActivity = activity as MainActivity

        val layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerviewAll.layoutManager = layoutManager
        binding.recyclerviewAll.addItemDecoration(RvItemDecore(3, 5, false, 10000))

        updateUIWithFetchedData()
        adapter!!.setCoroutineScope(fragmentScope)*/

    }

    /*   private fun loadData() {
           Log.d("functionCallingTest", "onCreateCustom:  home on create")
           chargingAnimationViewmodel.chargingAnimList.observe(viewLifecycleOwner) { result ->
               when (result) {
                   is Response.Success -> {
                       Log.e(TAG, "ChargingAnimation: " + result.data)
                       lifecycleScope.launch(Dispatchers.IO) {
                           val list = result.data
                           val data = if (AdConfig.ISPAIDUSER) {
                               list
                           } else {
                               list?.let { addNullValueInsideArray(it.shuffled()) }
                           }

                           withContext(Dispatchers.Main) {
                               data?.let { adapter?.updateData(it as ArrayList<ChargingAnimModel?>) }
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
               if (AdConfig.ISPAIDUSER) {
                   Bundle().apply {
                       putBoolean("adShowed", adShowd)
                       findNavController().navigate(R.id.downloadBatteryAnimation, this)
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
                                       findNavController().navigate(
                                           R.id.downloadBatteryAnimation,
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
                           findNavController().navigate(R.id.downloadBatteryAnimation, this)
                       }
                   }
               }
           }
       }

       private val fragmentScope: CoroutineScope by lazy { MainScope() }

       private suspend fun addNullValueInsideArray(data: List<ChargingAnimModel?>): ArrayList<ChargingAnimModel?> {
           return withContext(Dispatchers.IO) {
               val newData = arrayListOf<ChargingAnimModel?>()
               for (i in data.indices) {
                   newData.add(data[i]) // Add the current item
                   // After every 15 items, add a null value (excluding the last item)
                   if ((i + 1) % 15 == 0) {
                       newData.add(null)
                   }
               }
               newData
           }
       }

       override fun onDestroyView() {
           super.onDestroyView()
           _binding = null
       }
   */
}