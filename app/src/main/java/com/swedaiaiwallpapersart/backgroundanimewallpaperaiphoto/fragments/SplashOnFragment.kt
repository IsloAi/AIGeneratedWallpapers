package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.ikame.android.sdk.IKSdkController
import com.ikame.android.sdk.billing.IKBillingController
import com.ikame.android.sdk.data.dto.pub.IKAdError
import com.ikame.android.sdk.data.dto.pub.IKRemoteConfigValue
import com.ikame.android.sdk.listener.pub.IKBillingListener
import com.ikame.android.sdk.listener.pub.IKLoadAdListener
import com.ikame.android.sdk.listener.pub.IKRemoteConfigCallback
import com.ikame.android.sdk.listener.pub.IKShowAdListener
import com.ikame.android.sdk.listener.pub.IKShowWidgetAdListener
import com.ikame.android.sdk.tracking.IKTrackingHelper
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.NewsplashFragmentBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashOnFragment : Fragment() {

    companion object {
        var exit = true
    }

    val TAG = "SPLASH"

    var moveNext = false

    private var hasNavigated = false

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private var _binding: NewsplashFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var myActivity: MainActivity

    private var animationJob: Job? = null

    private var lan: String = ""

    var counter = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NewsplashFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        IKSdkController.setEnableShowResumeAds(false)

        IKSdkController.setOnRemoteConfigDataListener(object : IKRemoteConfigCallback {
            override fun onSuccess(data: HashMap<String, IKRemoteConfigValue>) {}
            override fun onFail() {}
        })
        if (isAdded) {

            sendTracking(
                "screen_active",
                Pair("action_type", "screen"),
                Pair("action_name", "SplashScr_View")
            )
        }

        myActivity = activity as MainActivity

        lan = MySharePreference.getLanguage(requireContext()).toString()

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {

            IKBillingController.reCheckIAP(object : IKBillingListener {
                override fun onBillingFail() {
                    if (isAdded) {
                        AdConfig.ISPAIDUSER = false
                        binding.adsView.visibility = View.VISIBLE
                        binding.adsView.attachLifecycle(lifecycle)
                        binding.adsView.loadAd("splashscr_bottom", object : IKShowWidgetAdListener {
                            override fun onAdShowed() {}
                            override fun onAdShowFail(error: IKAdError) {
                                if (isAdded) {
                                    binding.adsView.visibility = View.GONE
                                }
                            }
                        })

                        IKSdkController.preloadNativeAd(
                            "languagescr_bottom",
                            object : IKLoadAdListener {
                                override fun onAdLoaded() {}
                                override fun onAdLoadFail(error: IKAdError) {}
                            })

                        IKSdkController.preloadNativeAd(
                            "languagescr_bottom2",
                            object : IKLoadAdListener {
                                override fun onAdLoaded() {}
                                override fun onAdLoadFail(error: IKAdError) {}
                            })

                        IKSdkController.preloadNativeAdFullScreen(
                            "onboardscr_fullscreen",
                            object : IKLoadAdListener {
                                override fun onAdLoaded() {}
                                override fun onAdLoadFail(error: IKAdError) {}
                            })

                        viewLifecycleOwner.lifecycleScope.launch {
                            IKSdkController
                                .loadAndShowSplashScreenAd(myActivity, object : IKShowAdListener {
                                    override fun onAdsDismiss() {
                                        if (isAdded) {
                                            moveNext = true
                                            Constants.checkAppOpen = true
                                            navigateToNextScreen()
                                            IKSdkController.setEnableShowResumeAds(true)
                                        }
                                    }

                                    override fun onAdsShowFail(error: IKAdError) {
                                        Log.e(TAG, "onAdsShowFail: $error")
                                        if (isAdded) {
                                            navigateToNextScreen()
                                            IKSdkController.setEnableShowResumeAds(true)
                                        }
                                    }

                                    override fun onAdsShowed() {
                                        counter++
                                        if (isAdded) {
                                            binding.adsView.visibility = View.GONE
                                        }
                                    }
                                })
                        }
                    }
                }

                override fun onBillingSuccess() {
                    if (isAdded) {
                        AdConfig.ISPAIDUSER = true
                        binding.adsView.visibility = View.GONE
                        viewLifecycleOwner.lifecycleScope.launch {
                            if (AdConfig.ISPAIDUSER) {
                                delay(3000)
                            }
                            if (isAdded) {
                                navigateToNextScreen()
                            }
                        }
                    }
                }
            }, false)

            val duration = 2000
            val interval = 20

            val steps = duration / interval
            val progressIncrement = 100 / steps

            for (currentProgress in 0..100 step progressIncrement) {
                if (isAdded) {
                    binding.activeProgress.progress = currentProgress
                }
                delay(interval.toLong())
            }
        }
        animateLoadingText()

    }
    private fun navigateToNextScreen() {
        /*if (lan.isEmpty() && isAdded) {
            hasNavigated = true
            findNavController().navigate(R.id.getStartedFragment)
        } else {*/
            if (isAdded) {
                if (AdConfig.inAppConfig) {
                    hasNavigated = true
                    findNavController().navigate(R.id.getStartedFragment)
                } else {
                    hasNavigated = true
                    if (checkAppDefaultHome()){
                        Toast.makeText(requireContext(), "App is Default Home", Toast.LENGTH_SHORT).show()
                        //findNavController().navigate(R.id.homeTabsFragment)
                        // TODO: take the user to the launcher HomeScreen
                    }else{
                        Toast.makeText(requireContext(), "App is not Default Home", Toast.LENGTH_SHORT).show()
                        // TODO: take the user to the second screen for setting the app to default
                    }
                }
            }
        //}
    }
    /*private fun navigateToNextScreen() {
        if (lan.isEmpty() && isAdded) {
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
    }*/

    fun checkAppDefaultHome(): Boolean {
        val packageManager = requireContext().packageManager
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
        }
        val resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return resolveInfo?.activityInfo?.packageName == requireContext().packageName
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

    override fun onResume() {
        super.onResume()
        Log.e("SPLASH", "onResume: ")

        val videoUri: Uri =
            Uri.parse("android.resource://" + requireContext().packageName + "/" + R.raw.new_splash_video)
        binding.videoView.setVideoURI(videoUri)
        binding.videoView.start()

        binding.videoView.setOnCompletionListener {
            if (isAdded) {
                binding.videoView.start()
            }
        }
        val lan = MySharePreference.getLanguage(requireContext())

        if (counter > 0) {
            if (isAdded) {
                navigateToNextScreen()
            }
        }
        handleAppResume()
        if (isAdded) {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Splash Screen")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, javaClass.simpleName)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }
    }

    private fun handleAppResume() {
        if (moveNext && !hasNavigated) {
            if (isAdded) {
                navigateToNextScreen()
            }
        } else {
            Log.e("TAG", "handleAppResume: ")
        }
    }

    private fun sendTracking(
        eventName: String,
        vararg param: Pair<String, String?>
    ) {
        IKTrackingHelper.sendTracking(eventName, *param)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}