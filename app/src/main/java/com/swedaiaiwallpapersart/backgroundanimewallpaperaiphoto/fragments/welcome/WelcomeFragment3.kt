package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.welcome

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ikame.android.sdk.IKSdkController
import com.ikame.android.sdk.data.dto.pub.IKAdError
import com.ikame.android.sdk.listener.pub.IKLoadAdListener
import com.ikame.android.sdk.listener.pub.IKShowWidgetAdListener
import com.ikame.android.sdk.widgets.IkmWidgetAdLayout
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentWelcome3Binding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig

class WelcomeFragment3 : Fragment() {

    private var _binding: FragmentWelcome3Binding? = null
    private val binding get() = _binding!!
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
        IKSdkController.preloadNativeAd("onboardscr_bottom", object : IKLoadAdListener {
            override fun onAdLoaded() {
                // Ad loaded successfully
            }

            override fun onAdLoadFail(error: IKAdError) {
                Log.e("TAG", "onAdLoadFail: ")
            }
        })

        binding.adsView.attachLifecycle(this.lifecycle)
        val adLayout = LayoutInflater.from(activity).inflate(
            R.layout.native_layout_onboard_latest,
            null, false
        ) as? IkmWidgetAdLayout
        adLayout?.titleView = adLayout?.findViewById(R.id.custom_headline)
        adLayout?.bodyView = adLayout?.findViewById(R.id.custom_body)
        adLayout?.callToActionView = adLayout?.findViewById(R.id.custom_call_to_action)
        adLayout?.iconView = adLayout?.findViewById(R.id.custom_app_icon)
        adLayout?.mediaView = adLayout?.findViewById(R.id.custom_media)
        binding.adsView.loadAd(R.layout.shimmer_loading_native, adLayout!!,"onboardscr_fullscreen",
            object : IKShowWidgetAdListener {
                override fun onAdShowFail(error: IKAdError) {
                    if (AdConfig.ISPAIDUSER){
                        binding.adsView.visibility = View.GONE
                    }

                    Log.e("TAG", "onAdsLoadFail: native failded " )
                }

                override fun onAdShowed() {

                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }

}