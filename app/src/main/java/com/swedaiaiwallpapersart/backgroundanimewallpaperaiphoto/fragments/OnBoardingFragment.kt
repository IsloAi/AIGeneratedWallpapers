package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.os.BuildCompat
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.CustomSDKAdsListenerAdapter
import com.bmik.android.sdk.widgets.IkmWidgetAdLayout
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.ActivityMainBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding
.FragmentOnBoardingBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.OnboardingAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.NativeAdsPreLoading
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.LocaleManager
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import kotlinx.coroutines.launch
import java.util.Locale

class OnBoardingFragment : Fragment() {

    private var _binding: FragmentOnBoardingBinding?= null
    private val binding get() = _binding!!

    lateinit var welcomeAdapter: OnboardingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val lan = MySharePreference.getLanguage(requireContext())
        val context = LocaleManager.setLocale(requireContext(), lan!!)
        val resources = context.resources
        val newLocale = Locale(lan!!)
        val resources1 = getResources()
        val configuration = resources1.configuration
        configuration.setLocale(newLocale)
        configuration.setLayoutDirection(Locale(lan!!));
        resources1.updateConfiguration(configuration, resources.displayMetrics)
        _binding = FragmentOnBoardingBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//        val nativeAd = NativeAdsPreLoading.getOnBoaringAds()
//
//        val adLayout = LayoutInflater.from(activity).inflate(
//            R.layout.layout_custom_admob,
//            null, false
//        ) as? IkmWidgetAdLayout
//        adLayout?.titleView = adLayout?.findViewById(R.id.custom_headline)
//        adLayout?.bodyView = adLayout?.findViewById(R.id.custom_body)
//        adLayout?.callToActionView = adLayout?.findViewById(R.id.custom_call_to_action)
//        adLayout?.iconView = adLayout?.findViewById(R.id.custom_app_icon)
//        adLayout?.mediaView = adLayout?.findViewById(R.id.custom_media)
//
//        binding.adsView.setCustomNativeAdLayout(
//            R.layout.shimmer_loading_native,
//            adLayout!!
//        )
//        binding.adsView.loadAd(requireActivity(),"onboardscr_bottom","onboardscr_bottom",
//            object : CustomSDKAdsListenerAdapter() {
//                override fun onAdsLoadFail() {
//                    super.onAdsLoadFail()
//                    Log.e("TAG", "onAdsLoadFail: native failded " )
//                    binding.adsView.visibility = View.GONE
//                }
//
//                override fun onAdsLoaded() {
//                    super.onAdsLoaded()
//                    if (isAdded && view != null) {
//                        // Modify view visibility here
//                        binding.adsView.visibility = View.VISIBLE
//                    }
//                    Log.e("TAG", "onAdsLoaded: native loaded" )
//                }
//            }
//        )
        backHandle()

        populateOnbaordingItems()
        binding.onboardingViewPager.adapter = welcomeAdapter

        setIndicator()
        setCurrentIndicator(0)

        binding.onboardingViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
                when (position) {
                    0 -> {
                        SDKBaseController.getInstance().preloadNativeAd(requireActivity(),"onboardscr_bottom","onboardscr_bottom")
                        binding.skipBtn.visibility = View.VISIBLE
                        binding.onbTxt1.text = getString(R.string.enchanting_animated_realms)

                        val adLayout = LayoutInflater.from(activity).inflate(
                            R.layout.layout_custom_admob,
                            null, false
                        ) as? IkmWidgetAdLayout
                        adLayout?.titleView = adLayout?.findViewById(R.id.custom_headline)
                        adLayout?.bodyView = adLayout?.findViewById(R.id.custom_body)
                        adLayout?.callToActionView = adLayout?.findViewById(R.id.custom_call_to_action)
                        adLayout?.iconView = adLayout?.findViewById(R.id.custom_app_icon)
                        adLayout?.mediaView = adLayout?.findViewById(R.id.custom_media)

                        binding.adsView.setCustomNativeAdLayout(
                            R.layout.shimmer_loading_native,
                            adLayout!!
                        )

                        binding.adsView.loadAd(requireActivity(),"onboardscr_bottom","onboardscr_bottom",
                            object : CustomSDKAdsListenerAdapter() {
                                override fun onAdsLoadFail() {
                                    super.onAdsLoadFail()
                                    Log.e("TAG", "onAdsLoadFail: native failded " )
//                                    binding.adsView.visibility = View.GONE
                                }

                                override fun onAdsLoaded() {
                                    super.onAdsLoaded()
                                    if (isAdded && view != null) {
                                        // Modify view visibility here
                                        binding.adsView.visibility = View.VISIBLE
                                    }

                                    Log.e("TAG", "onAdsLoaded: native loaded" )
                                }
                            }
                        )
                    }
                    1 -> {
                        SDKBaseController.getInstance().preloadNativeAd(requireActivity(),"onboardscr_bottom","onboardscr_bottom")
                        binding.skipBtn.visibility = View.VISIBLE
                        val adLayout = LayoutInflater.from(activity).inflate(
                            R.layout.layout_custom_admob,
                            null, false
                        ) as? IkmWidgetAdLayout
                        adLayout?.titleView = adLayout?.findViewById(R.id.custom_headline)
                        adLayout?.bodyView = adLayout?.findViewById(R.id.custom_body)
                        adLayout?.callToActionView = adLayout?.findViewById(R.id.custom_call_to_action)
                        adLayout?.iconView = adLayout?.findViewById(R.id.custom_app_icon)
                        adLayout?.mediaView = adLayout?.findViewById(R.id.custom_media)

                        binding.adsView.setCustomNativeAdLayout(
                            R.layout.shimmer_loading_native,
                            adLayout!!
                        )

                        binding.adsView.loadAd(requireActivity(),"onboardscr_bottom","onboardscr_bottom",
                            object : CustomSDKAdsListenerAdapter() {
                                override fun onAdsLoadFail() {
                                    super.onAdsLoadFail()
                                    Log.e("TAG", "onAdsLoadFail: native failded " )
//                                    binding.adsView.visibility = View.GONE
                                }

                                override fun onAdsLoaded() {
                                    super.onAdsLoaded()
                                    if (isAdded && view != null) {
                                        // Modify view visibility here
                                        binding.adsView.visibility = View.VISIBLE
                                    }
                                    Log.e("TAG", "onAdsLoaded: native loaded" )
                                }
                            }
                        )

                        binding.onbTxt1.text = getString(R.string.captivating_animal_kingdoms)
                    }
                    2 -> {
                        val adLayout = LayoutInflater.from(activity).inflate(
                            R.layout.layout_custom_admob,
                            null, false
                        ) as? IkmWidgetAdLayout
                        adLayout?.titleView = adLayout?.findViewById(R.id.custom_headline)
                        adLayout?.bodyView = adLayout?.findViewById(R.id.custom_body)
                        adLayout?.callToActionView = adLayout?.findViewById(R.id.custom_call_to_action)
                        adLayout?.iconView = adLayout?.findViewById(R.id.custom_app_icon)
                        adLayout?.mediaView = adLayout?.findViewById(R.id.custom_media)

                        binding.adsView.setCustomNativeAdLayout(
                            R.layout.shimmer_loading_native,
                            adLayout!!
                        )

                        binding.adsView.loadAd(requireActivity(),"onboardscr_bottom","onboardscr_bottom",
                            object : CustomSDKAdsListenerAdapter() {
                                override fun onAdsLoadFail() {
                                    super.onAdsLoadFail()
                                    Log.e("TAG", "onAdsLoadFail: native failded " )
//                                    binding.adsView.visibility = View.GONE
                                }

                                override fun onAdsLoaded() {
                                    super.onAdsLoaded()
                                    if (isAdded && view != null) {
                                        // Modify view visibility here
                                        binding.adsView.visibility = View.VISIBLE
                                    }
                                    Log.e("TAG", "onAdsLoaded: native loaded" )
                                }
                            }
                        )
                        binding.skipBtn.visibility = View.GONE
                        binding.onbTxt1.text = getString(R.string.device_with_creative_mastery)
                    }
                }
            }
        })

        binding.skipBtn.setOnClickListener {
            MySharePreference.setOnboarding(requireContext(),true)
            findNavController().navigate(R.id.mainFragment)
        }


        binding.nextBtn.setOnClickListener {
            MySharePreference.setOnboarding(requireContext(),true)
            val currentItem = binding.onboardingViewPager.currentItem
            val lastItemIndex = (binding.onboardingViewPager.adapter?.itemCount ?: 0) - 1

            if (currentItem < lastItemIndex) {
                // Move to the next item
                binding.onboardingViewPager.setCurrentItem(currentItem + 1, true)
            } else {
                if (findNavController().currentDestination?.id != R.id.mainFragment) {
                    findNavController().navigate(R.id.action_onBoardingFragment_to_mainFragment)
                }
            }
        }
    }

    private fun backHandle(){
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when (binding.onboardingViewPager.currentItem) {
                    2 -> {
                        binding.onboardingViewPager.currentItem =1
                    }
                    1 -> {
                        binding.onboardingViewPager.currentItem =0
                    }
                    0 -> {
                        findNavController().navigateUp()
                    }
                }
            }
        })

        if (BuildCompat.isAtLeastT()) {
            requireActivity().onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                when (binding.onboardingViewPager.currentItem) {
                    2 -> {
                        binding.onboardingViewPager.currentItem =1
                    }
                    1 -> {
                        binding.onboardingViewPager.currentItem =0
                    }
                    0 -> {
                        findNavController().navigateUp()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }



    private fun populateOnbaordingItems() {
        val welcomeItems: MutableList<Int> = ArrayList<Int>()
        welcomeItems.add(1)
        welcomeItems.add(2)
        welcomeItems.add(3)

        welcomeAdapter = OnboardingAdapter(welcomeItems)
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