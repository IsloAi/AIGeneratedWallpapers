package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.reward

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxReward
import com.applovin.mediation.MaxRewardedAdListener
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentRewardDetailsBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.LayoutWallpaperGiftDialogBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MaxRewardAds
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.RewardedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RewardDetailsFragment : Fragment() {
    private var _binding: FragmentRewardDetailsBinding? = null
    private val binding get() = _binding!!
    private val rewardedViewModel: RewardedViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRewardDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEvents()
        rewardedViewModel.getAllWallpapers()
    }

    private fun setEvents() {
        Constants.hasShownRewardScreen = true

        binding.close.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.watchAd.setOnClickListener {
            if (isAdded) {
                MySharePreference.setVIPGiftBool(requireActivity(), true)
                MySharePreference.setVIPGiftDate(requireActivity())
                MaxRewardAds.showRewardAd(requireActivity(), object : MaxRewardedAdListener {
                    override fun onAdLoaded(p0: MaxAd) {
                        Log.d("Reward", "onAdLoaded: Loaded ")
                        MaxRewardAds.showRewardAd(
                            requireActivity(),
                            object : MaxRewardedAdListener {
                                override fun onAdLoaded(p0: MaxAd) {
                                }

                                override fun onAdDisplayed(p0: MaxAd) {
                                }

                                override fun onAdHidden(p0: MaxAd) {
                                    showCustomAlertDialog()
                                    MaxRewardAds.loadRewardAds(
                                        requireActivity(),
                                        AdConfig.applovinAndroidReward
                                    )
                                }

                                override fun onAdClicked(p0: MaxAd) {}

                                override fun onAdLoadFailed(p0: String, p1: MaxError) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Ad is not available",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    MaxRewardAds.loadRewardAds(
                                        requireActivity(),
                                        AdConfig.applovinAndroidReward
                                    )
                                }

                                override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
                                }

                                override fun onUserRewarded(p0: MaxAd, p1: MaxReward) {
                                    showCustomAlertDialog()
                                }
                            })
                    }

                    override fun onAdDisplayed(p0: MaxAd) {
                    }

                    override fun onAdHidden(p0: MaxAd) {
                        MaxRewardAds.loadRewardAds(requireContext(), AdConfig.applovinAndroidReward)
                    }

                    override fun onAdClicked(p0: MaxAd) {}

                    override fun onAdLoadFailed(p0: String, p1: MaxError) {
                        Toast.makeText(requireContext(), "Ad is not available", Toast.LENGTH_SHORT)
                            .show()
                        MaxRewardAds.loadRewardAds(requireContext(), AdConfig.applovinAndroidReward)
                    }

                    override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
                        MaxRewardAds.loadRewardAds(requireContext(), AdConfig.applovinAndroidReward)
                    }

                    override fun onUserRewarded(p0: MaxAd, p1: MaxReward) {
                        showCustomAlertDialog()
                    }
                })
            }
        }
    }

    private fun showCustomAlertDialog() {
        val dialogBinding = LayoutWallpaperGiftDialogBinding.inflate(layoutInflater)
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setView(dialogBinding.root)
        val dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogBinding.viewWallpapers.setOnClickListener {
            if (isAdded) {
                val bundle = Bundle().apply {
                    putString("name", "Vip")
                    putString("from", "Vip")
                }

                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.rewardDetailsFragment, true)
                    .setLaunchSingleTop(true)
                    .build()
                findNavController().navigate(R.id.listViewFragment, bundle, navOptions)

                dialog.dismiss()
            }

        }
        dialogBinding.close.setOnClickListener {
            findNavController().navigateUp()
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}