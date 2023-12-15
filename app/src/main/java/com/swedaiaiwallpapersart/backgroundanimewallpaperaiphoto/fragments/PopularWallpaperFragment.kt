package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentPopularWallpaperBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.OnboardingAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.PopularSliderAdapter

class PopularWallpaperFragment : Fragment() {

    private var _binding:FragmentPopularWallpaperBinding ?= null
    private val binding get() = _binding!!

    private lateinit var welcomeAdapter: PopularSliderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPopularWallpaperBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        populateOnbaordingItems()
        binding.sliderPager.adapter = welcomeAdapter

        setIndicator()
        setCurrentIndicator(0)
    }


    private fun populateOnbaordingItems() {
        val welcomeItems: MutableList<Int> = ArrayList<Int>()
        welcomeItems.add(1)
        welcomeItems.add(2)
        welcomeItems.add(3)
        welcomeItems.add(4)

        welcomeAdapter = PopularSliderAdapter(welcomeItems)
    }

    private fun setIndicator() {
        val welcomeIndicators = arrayOfNulls<ImageView>(welcomeAdapter.itemCount)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in welcomeIndicators.indices) {
            welcomeIndicators[i] = ImageView(requireContext())
            welcomeIndicators[i]!!.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.banner_slider_indicator_inactive
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
                        R.drawable.banner_slider_indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.banner_slider_indicator_inactive
                    )
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }


}