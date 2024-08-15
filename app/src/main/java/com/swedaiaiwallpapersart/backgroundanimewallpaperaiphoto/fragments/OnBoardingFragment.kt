package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.core.os.BuildCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.analytics.FirebaseAnalytics
import com.ikame.android.sdk.IKSdkController
import com.ikame.android.sdk.tracking.IKTrackingHelper
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentOnBoardingBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.OnboardingPagerAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.welcome.WelcomeFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.welcome.WelcomeFragment3
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.welcome.WelcomeFragment4
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.welcome.welcomeFragment2
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.LocaleManager
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class OnBoardingFragment : Fragment() {

    private var _binding: FragmentOnBoardingBinding?= null
    private val binding get() = _binding!!

    lateinit var welcomeAdapter: OnboardingPagerAdapter

    private lateinit var firebaseAnalytics: FirebaseAnalytics

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

        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        backHandle()

        populateOnbaordingItems()



        if (isAdded){
            sendTracking("screen_active",Pair("action_type", "screen"), Pair("action_name", "OnboardingScr1_View"))
        }



        binding.onboardingViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
//                setCurrentIndicator(position)
                if (AdConfig.ISPAIDUSER){
                    when (position) {
                        0 -> {
                            if (isAdded){
                                sendTracking("screen_active",Pair("action_type", "screen"), Pair("action_name", "OnboardingScr1_View"))
                            }
                            binding.skipBtn.visibility = View.VISIBLE
                        }
                        1 -> {
                            if (isAdded){
                                sendTracking("screen_active",Pair("action_type", "screen"), Pair("action_name", "OnboardingScr2_View"))
                            }
                            binding.skipBtn.visibility = View.VISIBLE

                        }
                        2 -> {
                            if (isAdded){
                                sendTracking("screen_active",Pair("action_type", "screen"), Pair("action_name", "OnboardingScr3_View"))
                            }
                            binding.skipBtn.visibility = View.GONE
                        }
                    }
                }else{
                    if (AdConfig.onboarding_Full_Native != 1){
                        when (position) {
                            0 -> {
                                if (isAdded){
                                    sendTracking("screen_active",Pair("action_type", "screen"), Pair("action_name", "OnboardingScr1_View"))
                                }
                                binding.skipBtn.visibility = View.VISIBLE
                            }
                            1 -> {
                                if (isAdded){
                                    sendTracking("screen_active",Pair("action_type", "screen"), Pair("action_name", "OnboardingScr2_View"))
                                }
                                binding.skipBtn.visibility = View.VISIBLE

                            }
                            2 -> {
                                if (isAdded){
                                    sendTracking("screen_active",Pair("action_type", "screen"), Pair("action_name", "OnboardingScr3_View"))
                                }
                                binding.skipBtn.visibility = View.GONE
                            }
                        }
                    }else{
                        when (position) {
                            0 -> {
                                if (isAdded){
                                    sendTracking("screen_active",Pair("action_type", "screen"), Pair("action_name", "OnboardingScr1_View"))
                                }
                                binding.nextBtn.visibility = View.VISIBLE
                                binding.skipBtn.visibility = View.VISIBLE
                                IKSdkController.setEnableShowResumeAds(true)
                            }
                            1 -> {
                                if (isAdded){
                                    sendTracking("screen_active",Pair("action_type", "screen"), Pair("action_name", "OnboardingScr2_View"))
                                }
                                binding.nextBtn.visibility = View.VISIBLE
                                binding.skipBtn.visibility = View.VISIBLE
                                IKSdkController.setEnableShowResumeAds(true)

                            }
                            2 -> {
                                IKSdkController.setEnableShowResumeAds(false)
                                binding.nextBtn.visibility = View.GONE
                                binding.skipBtn.visibility = View.GONE
                            }

                            3 -> {
                                if (isAdded){
                                    sendTracking("screen_active",Pair("action_type", "screen"), Pair("action_name", "OnboardingScr3_View"))
                                }
                                binding.nextBtn.visibility = View.VISIBLE
                                binding.skipBtn.visibility = View.GONE
                                IKSdkController.setEnableShowResumeAds(true)
                            }
                        }
                    }
                }


            }
        })

        binding.skipBtn.setOnClickListener {
            MySharePreference.setOnboarding(requireContext(),true)
//            if (findNavController().currentDestination?.id != R.id.homeTabsFragment) {
//                findNavController().navigate(R.id.action_onBoardingFragment_to_homeTabsFragment)
//            }

            binding.onboardingViewPager.setCurrentItem(welcomeAdapter.itemCount-1, true)
        }


        binding.nextBtn.setOnClickListener {
            MySharePreference.setOnboarding(requireContext(),true)
            val currentItem = binding.onboardingViewPager.currentItem
            val lastItemIndex = (binding.onboardingViewPager.adapter?.itemCount ?: 0) - 1
            Log.e("TAG", "onViewCreated: "+currentItem )


                if (isAdded){
                    sendTracking("click_button",Pair("action_type", "button"), Pair("action_name", "OnboardingScr1_Next_Click"))
                }


            if (currentItem < lastItemIndex) {
                // Move to the next item
                binding.onboardingViewPager.setCurrentItem(currentItem + 1, true)
            } else {
                lifecycleScope.launch {
                    delay(100)  // Short delay before navigating
                    if (findNavController().currentDestination?.id != R.id.homeTabsFragment) {
                        findNavController().navigate(R.id.action_onBoardingFragment_to_homeTabsFragment)
                    }
                }
            }
        }
    }

    private fun backHandle(){
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isAdded){
                    sendTracking("click_button",Pair("action_type", "button"), Pair("action_name", "Sytem_BackButton_Click"))
                }
                when (binding.onboardingViewPager.currentItem) {
                    3 -> {
                        binding.onboardingViewPager.currentItem =2
                    }
                    2 -> {
                        binding.onboardingViewPager.currentItem =1
                    }
                    1 -> {
                        binding.onboardingViewPager.currentItem =0
                    }
                    0 -> {
                        findNavController().navigate(R.id.action_onBoardingFragment_to_homeTabsFragment)
                    }
                }
            }
        })

        if (BuildCompat.isAtLeastT()) {
            requireActivity().onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                if (isAdded){
                    sendTracking("click_button",Pair("action_type", "button"), Pair("action_name", "Sytem_BackButton_Click"))
                }
                when (binding.onboardingViewPager.currentItem) {
                    3 -> {
                        binding.onboardingViewPager.currentItem =2
                    }
                    2 -> {
                        binding.onboardingViewPager.currentItem =1
                    }
                    1 -> {
                        binding.onboardingViewPager.currentItem =0
                    }
                    0 -> {
                        findNavController().navigate(R.id.action_onBoardingFragment_to_homeTabsFragment)
                    }
                }
            }
        }
    }


    private fun sendTracking(
        eventName: String,
        vararg param: Pair<String, String?>
    )
    {
        IKTrackingHelper.sendTracking( eventName, *param)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    override fun onResume() {
        super.onResume()
        if (isAdded){
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Onboarding Screen")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, javaClass.simpleName)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }
    }



    private fun populateOnbaordingItems() {
        welcomeAdapter= OnboardingPagerAdapter(activity as MainActivity)

        welcomeAdapter.addFragment(WelcomeFragment(),"1")
        welcomeAdapter.addFragment(welcomeFragment2(),"2")
        if (AdConfig.onboarding_Full_Native == 1 && !AdConfig.ISPAIDUSER){

            welcomeAdapter.addFragment(WelcomeFragment3(),"4")
        }

        welcomeAdapter.addFragment(WelcomeFragment4(),"3")

        binding.onboardingViewPager.adapter=welcomeAdapter
        binding.onboardingViewPager.offscreenPageLimit = 1
    }


}