package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.applovin.mediation.ads.MaxAdView
import com.google.firebase.analytics.FirebaseAnalytics
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.NewsplashFragmentBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.activity.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashOnFragment : Fragment() {

    var moveNext = false
    private var hasNavigated = false
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private var _binding: NewsplashFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var myActivity: MainActivity

    private var animationJob: Job? = null

    private var lan: String = ""

    var counter = 0
    private lateinit var adView: MaxAdView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = NewsplashFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        myActivity = activity as MainActivity

        //lan = MySharePreference.getLanguage(requireContext()).toString()

        //createBannerAd()
        //MaxInterstitialAds.loadInterstitialAd(requireContext())
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
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
            //navigateToNextScreen()
        }
        animateLoadingText()
    }

    /*private fun createBannerAd() {
        adView = MaxAdView(AdConfig.applovinAndroidBanner, requireContext())

        // Stretch to the width of the screen for banners to be fully functional
        val width = ViewGroup.LayoutParams.MATCH_PARENT

        // Banner height on phones and tablets is 50 and 90, respectively
        val heightPx = resources.getDimensionPixelSize(R.dimen.banner_height)

        adView.layoutParams = FrameLayout.LayoutParams(width, heightPx)

        // Set background color for banners to be fully functional
        adView.setBackgroundColor(resources.getColor(R.color.new_main_background))

        val rootView = binding.BannerMaxAdView
        rootView.addView(adView)

        // Load the ad
        adView?.loadAd()
    }

    private fun navigateToNextScreen() {
        if (lan.isEmpty() && isAdded) {
            Handler().postDelayed({
                hasNavigated = true
                findNavController().navigate(R.id.localizationFragment)
            }, 4000)
        } else {
            if (isAdded) {
                Handler().postDelayed({
                    hasNavigated = true
                    findNavController().navigate(R.id.homeTabsFragment)
                }, 4000)
            }
        }
    }*/

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

    /*override fun onResume() {
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
        if (isAdded) {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Splash Screen")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, javaClass.simpleName)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }
    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}