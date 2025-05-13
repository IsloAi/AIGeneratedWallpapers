package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.fragments.welcome

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentWelcome2Binding


class welcomeFragment2 : Fragment() {
    private var _binding: FragmentWelcome2Binding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcome2Binding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(requireContext()).load(R.drawable.onboard_2).into(binding.onBoardImg)

        setIndicator()
        setCurrentIndicator(1)

        /*val nativeAd = NativeAdManager(
            requireContext(),
            AdConfig.admobAndroidNative,
            R.layout.admob_native_medium
        )
        nativeAd.loadNativeAd(binding.NativeAdOB2)*/
        //Max Medium Native Ad
        /*MaxNativeAd.createTemplateNativeAdLoader(
            requireContext(),
            AdConfig.applovinAndroidNativeMedium,
            object : MaxNativeAdListener() {
                override fun onNativeAdLoaded(p0: MaxNativeAdView?, p1: MaxAd) {
                    super.onNativeAdLoaded(p0, p1)
                    binding.NativeAdOB2.removeAllViews()
                    p0?.let {
                        binding.NativeAdOB2.addView(it)
                        binding.NativeAdOB2.visibility = View.VISIBLE
                    }
                }

                override fun onNativeAdLoadFailed(p0: String, p1: MaxError) {
                    super.onNativeAdLoadFailed(p0, p1)
                }

                override fun onNativeAdClicked(p0: MaxAd) {
                    super.onNativeAdClicked(p0)
                }

                override fun onNativeAdExpired(p0: MaxAd) {
                    super.onNativeAdExpired(p0)
                }
            })
        MaxNativeAd.loadTemplateNativeAd(MaxNativeAdView.MEDIUM_TEMPLATE_1, requireContext())*/

        /*MaxNativeAd.createNativeAdLoader(
            requireContext(),
            AdConfig.applovinAndroidNativeManual,
            object : MaxNativeAdListener() {
                override fun onNativeAdLoaded(adView: MaxNativeAdView?, ad: MaxAd) {
                    binding.NativeAdOB2.removeAllViews()
                    adView?.let {
                        binding.NativeAdOB2.addView(it)
                    }
                }

                override fun onNativeAdLoadFailed(adUnitId: String, error: MaxError) {
                    // Handle failure (optional retry logic)
                }

                override fun onNativeAdClicked(ad: MaxAd) {
                    // Handle click
                }

                override fun onNativeAdExpired(ad: MaxAd) {
                    // Ad expired - reload if needed
                }
            }
        )

        MaxNativeAd.loadNativeAd(R.layout.max_native_medium, requireContext())*/
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }

    private fun setIndicator() {

        val welcomeIndicators = arrayOfNulls<ImageView>(3)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in welcomeIndicators.indices) {
            welcomeIndicators[i] = ImageView(requireContext())
            welcomeIndicators[i]!!.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.onboarding_indicator
                )
            )
            welcomeIndicators[i]!!.layoutParams = layoutParams
            binding.layoutOnboardingIndicators.addView(welcomeIndicators[i])
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val childCount = binding.layoutOnboardingIndicators.childCount
        val isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                Configuration.UI_MODE_NIGHT_YES

        for (i in 0 until childCount) {
            val imageView = binding.layoutOnboardingIndicators.getChildAt(i) as ImageView

            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.onboarding_inactive
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.onboarding_indicator
                    )
                )
            }
        }
    }

}