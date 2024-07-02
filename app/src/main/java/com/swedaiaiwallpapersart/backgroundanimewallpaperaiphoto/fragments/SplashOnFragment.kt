package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments

import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
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
import com.bmik.android.sdk.model.dto.CommonAdsAction
import com.bmik.android.sdk.tracking.SDKTrackingController
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

    var moveNext = false

    var hasNavigated = false

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private var _binding: NewsplashFragmentBinding?= null
    private val binding get() = _binding!!

    private var currentPosition = 0
    private var isVideoPrepared = false

    private lateinit var myActivity : MainActivity

    private var animationJob: Job? = null
    private var animateImages: Job? = null

    var lan:String = ""

    var counter = 0

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

        SDKBaseController.getInstance().onDataInitSuccessListener = CommonAdsAction {
            //do something
        }
        SDKBaseController.getInstance().onDataGetSuccessListener = {
            //do something
        }
        if (isAdded){

            sendTracking("screen_active",Pair("action_type", "screen"), Pair("action_name", "SplashScr_View"))
        }

        myActivity = activity as MainActivity

        // Load the banner ad
        binding.adsView.loadAd(requireContext(),"splashscr_bottom",
            "splashscr_bottom", object : CustomSDKAdsListenerAdapter() {
                override fun onAdsLoaded() {
                    super.onAdsLoaded()
                    Log.e("*******ADS", "onAdsLoaded: Banner loaded", )
                }

                override fun onAdsLoadFail() {
                    super.onAdsLoadFail()
                    Log.e("*******ADS", "onAdsLoaded: Banner failed", )
                }
            })

        // Get the language preference
        lan = MySharePreference.getLanguage(requireContext()).toString()

        // Check if the user is a premium user
        val premium = IkmSdkUtils.isUserIAPAvailable()
        AdConfig.ISPAIDUSER = premium
        // Preload the native ad if necessary
            Log.e("TAG", "onViewCreated: load pre", )
            SDKBaseController.getInstance().preloadNativeAd(requireActivity(),"languagescr_bottom","languagescr_bottom")
            SDKBaseController.getInstance().preloadNativeAd(requireActivity(),"languagescr_bottom2","languagescr_bottom2")



        SDKBaseController.getInstance().preloadNativeAd(requireActivity(),"onboardscr_fullscreen","onboardscr_fullscreen")

        animateLoadingText()
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {

            val duration = 2000 // 5000 milliseconds = 5 seconds
            val interval = 50 // Adjust the interval for smoother progress

            val steps = duration / interval
            val progressIncrement = 100 / steps

            for (currentProgress in 0..100 step progressIncrement) {
                if (isAdded){

                    binding.activeProgress.progress = currentProgress
                }
                delay(interval.toLong())
            }

            if (counter == 0){
                SDKBaseController.getInstance().showFirstOpenAppAds(myActivity,object:CommonAdsListenerAdapter(){
                    override fun onAdReady(priority: Int) {
                    }

                    override fun onAdsDismiss() {
                        moveNext = true
                        navigateToNextScreen()

                        IkmSdkController.setEnableShowResumeAds(true)

                    }

                    override fun onAdsShowFail(errorCode: Int) {
                        Log.e("TAG", "onAdsShowFail: $errorCode")
//

                        navigateToNextScreen()

                        IkmSdkController.setEnableShowResumeAds(true)
                    }

                    override fun onAdsShowed(priority: Int) {
                        counter++
                        if (isAdded){
                            binding.adsView.visibility = View.GONE
                        }
                    }

                })
            }else{
                navigateToNextScreen()
            }
        }


    }


    private fun navigateToNextScreen() {
        if (lan?.isEmpty() == true && isAdded) {
            hasNavigated = true
            findNavController().navigate(R.id.localizationFragment)
        } else {
            if (isAdded) {
                if (AdConfig.inAppConfig) {
                    hasNavigated = true
                    findNavController().navigate(R.id.localizationFragment)
                } else {
                    hasNavigated = true
                    findNavController().navigate(R.id.homeTabsFragment)
                }
            }
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

        Log.e("SPLASH", "onResume: ", )

        val videoUri: Uri = Uri.parse("android.resource://" + requireContext().packageName + "/" + R.raw.splash_new)
        binding.videoView.setVideoURI(videoUri)
        binding.videoView.start()

        binding.videoView.setOnCompletionListener {
            if (isAdded){
                binding.videoView.start()
            }
        }
        val lan = MySharePreference.getLanguage(requireContext())

        if (counter > 0){
            navigateToNextScreen()
        }
        handleAppResume()


        if (isAdded){
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Splash Screen")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, javaClass.simpleName)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }
    }

    private fun handleAppResume() {
        if (moveNext && !hasNavigated) {
            navigateToNextScreen()
        }else{
            Log.e("TAG", "handleAppResume: ", )
        }
    }

    private fun sendTracking(
        eventName: String,
        vararg param: Pair<String, String?>
    )
    {
        SDKTrackingController.trackingAllApp(requireContext(), eventName, *param)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}