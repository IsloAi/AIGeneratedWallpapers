package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bmik.android.sdk.IkmSdkController
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.CommonAdsListenerAdapter
import com.bmik.android.sdk.listener.CustomSDKAdsListenerAdapter
import com.bmik.android.sdk.utils.IkmSdkUtils
import com.google.firebase.analytics.FirebaseAnalytics
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.NewsplashFragmentBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashOnFragment : Fragment() {

    companion object{
        var exit = true
    }

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private var _binding: NewsplashFragmentBinding?= null
    private val binding get() = _binding!!

    private var currentPosition = 0
    private var isVideoPrepared = false

    private lateinit var myActivity : MainActivity

    private var animationJob: Job? = null
    private var animateImages: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = NewsplashFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        IkmSdkController.setEnableShowResumeAds(false)




        myActivity = activity as MainActivity
        SDKBaseController.getInstance()
            .loadBannerAds(
                myActivity,
                binding.adsWidget as? ViewGroup,
                "splashscr_bottom",
                " splashscr_bottom", object : CustomSDKAdsListenerAdapter() {
                    override fun onAdsLoaded() {
                        super.onAdsLoaded()
                        Log.e("*******ADS", "onAdsLoaded: Banner loaded", )
                    }

                    override fun onAdsLoadFail() {
                        super.onAdsLoadFail()
                        Log.e("*******ADS", "onAdsLoaded: Banner failed", )
                    }
                }
            )


        val lan = MySharePreference.getLanguage(requireContext())

        val premium = IkmSdkUtils.isUserIAPAvailable()

        AdConfig.ISPAIDUSER = premium


        if (lan?.isEmpty() == true){
            Log.e("TAG", "onViewCreated: load pre", )
            SDKBaseController.getInstance().preloadNativeAd(requireActivity(),"languagescr_bottom","languagescr_bottom")

        }








        animateLoadingText()
        animateImages()
        lifecycleScope.launch(Dispatchers.Main) {

            val duration = 5000 // 5000 milliseconds = 5 seconds
            val interval = 50 // Adjust the interval for smoother progress

            val steps = duration / interval
            val progressIncrement = 100 / steps

            for (currentProgress in 0..100 step progressIncrement) {
                binding.activeProgress.progress = currentProgress
                delay(interval.toLong())
            }

            delay(3000)

            SDKBaseController.getInstance().showFirstOpenAppAds(myActivity,object:CommonAdsListenerAdapter(){
                override fun onAdReady(priority: Int) {
//                progress.dismiss()
                }

                override fun onAdsDismiss() {
                    if (lan?.isEmpty() == true && isAdded){
                        findNavController().navigate(R.id.localizationFragment)
                    }else{
                        if (isAdded){
                                findNavController().navigate(R.id.action_splashOnFragment_to_homeTabsFragment)
                        }

                    }

                    IkmSdkController.setEnableShowResumeAds(true)

                }

                override fun onAdsShowFail(errorCode: Int) {
                    Log.e("TAG", "onAdsShowFail: $errorCode")
                    if (lan?.isEmpty() == true && isAdded){
                        findNavController().navigate(R.id.localizationFragment)
                    }else{
                        if (isAdded) {
                            findNavController().navigate(R.id.action_splashOnFragment_to_homeTabsFragment)
                        }
                    }

                    IkmSdkController.setEnableShowResumeAds(true)
//                progress.dismiss()
                }

                override fun onAdsShowed(priority: Int) {

//                progress.dismiss()

                }

            })
        }



    }

    private fun animateLoadingText() {
        animationJob = viewLifecycleOwner.lifecycleScope.launch {
            while (isActive) {
                delay(500) // Adjust the delay for animation speed
                withContext(Dispatchers.Main) {
                    animateDots(binding.loadingTxt)
                }
            }
        }
    }

    private fun animateImages() {

        val bgImages = listOf(
            R.drawable.splashnew_bg1,
            R.drawable.splash_bg2,
            R.drawable.splash_bg_3 // Add more images as needed
        )

        val mainImages = listOf(
            R.drawable.splash_image1_new,
            R.drawable.splash_image1_new,
            R.drawable.splash3_image_new // Add more images as needed


        )

        var currentIndex = 0
//        animateImages = viewLifecycleOwner.lifecycleScope.launch {
//            while (isActive) {
//                binding.spalshBgImage.setImageResource(bgImages[currentIndex])
//                binding.splashMainImage.setImageResource(mainImages[currentIndex])
//
//                currentIndex = (currentIndex + 1) % bgImages.size
//                delay(1000) // Wait for 1 second before changing images
//            }
//        }
    }


    private var dotCount = 0

    private fun animateDots(textView: TextView) {
        dotCount = (dotCount + 1) % 4
        val dots = ".".repeat(dotCount)
        textView.text = "Loading$dots"
    }
    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()

        val videoUri: Uri = Uri.parse("android.resource://" + requireContext().packageName + "/" + R.raw.splash_new)
        binding.videoView.setVideoURI(videoUri)
        binding.videoView.start()

        binding.videoView.setOnCompletionListener {
            binding.videoView.start()
        }

        if (isAdded){
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Splash Screen")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, javaClass.simpleName)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }
    }





    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}