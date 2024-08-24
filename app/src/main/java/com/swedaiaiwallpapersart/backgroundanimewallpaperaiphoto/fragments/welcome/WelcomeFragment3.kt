package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.welcome

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.ikame.android.sdk.IKSdkController
import com.ikame.android.sdk.data.dto.pub.IKAdError
import com.ikame.android.sdk.listener.pub.IKLoadAdListener
import com.ikame.android.sdk.listener.pub.IKShowWidgetAdListener
import com.ikame.android.sdk.widgets.IkmWidgetAdLayout
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentWelcome3Binding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.ViewPagerCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.w3c.dom.Text

class WelcomeFragment3 : Fragment() {

    private var _binding: FragmentWelcome3Binding? = null
    private val binding get() = _binding!!
    private var scrollJob: Job? = null
    var viewPagerCallback: ViewPagerCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ViewPagerCallback) {
            viewPagerCallback = context
        } else {
            Log.e("WelcomeFragment3", "Parent fragment is not implementing ViewPagerCallback")
        }
    }

    override fun onDetach() {
        super.onDetach()
        viewPagerCallback = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWelcome3Binding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        IKSdkController.preloadNativeAd("onboardscr_fullscreen", object : IKLoadAdListener {
            override fun onAdLoaded() {
                // Ad loaded successfully
            }

            override fun onAdLoadFail(error: IKAdError) {
                Log.e("WelcomeFragment3", "Ad failed to load: ${error.message}")
                viewPagerCallback?.swipe()
            }
        })

        binding.adsView.attachLifecycle(this.lifecycle)
        val adLayout = LayoutInflater.from(activity).inflate(
            R.layout.layout_image_native_full,
            null, false
        ) as? IkmWidgetAdLayout
        adLayout?.titleView = adLayout?.findViewById(R.id.custom_headline)
        adLayout?.bodyView = adLayout?.findViewById(R.id.custom_body)
        adLayout?.callToActionView = adLayout?.findViewById(R.id.custom_call_to_action)
        adLayout?.iconView = adLayout?.findViewById(R.id.custom_app_icon)
        adLayout?.mediaView = adLayout?.findViewById(R.id.custom_media)
            val title:TextView = adLayout?.findViewById(R.id.title)!!
            val next:ImageView = adLayout.findViewById(R.id.next_btn)!!
            title.text = "Tap button to continue experiencing"
        next.setOnClickListener { viewPagerCallback?.swipe() }
        binding.adsView.loadAd(R.layout.shimmer_loading_native, adLayout,"onboardscr_fullscreen",
            object : IKShowWidgetAdListener {
                override fun onAdShowFail(error: IKAdError) {
                    Log.e("WelcomeFragment3", "Ad failed to show: ${error.message}")
                    if (AdConfig.ISPAIDUSER) {
                        binding.adsView.visibility = View.GONE
                    }
                    viewPagerCallback?.swipe()
                }

                override fun onAdShowed() {

                }
            }
        )
    }

    override fun onPause() {
        super.onPause()
        scrollJob?.cancel()
    }

    override fun onStop() {
        super.onStop()
        scrollJob?.cancel()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        scrollJob?.cancel()
    }

    override fun onResume() {
        super.onResume()
        if (isAdded){
            startAutoScroll()
        }
    }
    private fun startAutoScroll() {
        scrollJob?.cancel()
        if (AdConfig.autoNext) {
            scrollJob = lifecycleScope.launch {
                delay(AdConfig.timeNext)
                viewPagerCallback?.swipe() ?: Log.e("WelcomeFragment3", "viewPagerCallback is null")
            }
        }
    }
}