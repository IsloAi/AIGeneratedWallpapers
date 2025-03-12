package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.welcome

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
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentWelcome4Binding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.NativeAdManager
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig

class WelcomeFragment4 : Fragment() {
    private var _binding: FragmentWelcome4Binding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWelcome4Binding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(requireContext()).load(R.drawable.onboard_3).into(binding.onBoardImg)

        setIndicator()
        setCurrentIndicator(2)

        val nativeAd = NativeAdManager(requireContext(), AdConfig.admobAndroidNative)
        nativeAd.loadNativeAd(binding.NativeAdOB3)
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

    override fun onDestroyView() {
        super.onDestroyView()
    }
}