package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.reward

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ikame.android.sdk.data.dto.pub.IKAdError
import com.ikame.android.sdk.format.rewarded.IKRewardAd
import com.ikame.android.sdk.listener.pub.IKLoadAdListener
import com.ikame.android.sdk.listener.pub.IKShowRewardAdListener
import com.ikame.android.sdk.tracking.IKTrackingHelper
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentRewardDetailsBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.LayoutWallpaperGiftDialogBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.RewardedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RewardDetailsFragment : Fragment() {
    private var _binding: FragmentRewardDetailsBinding? = null
    private val binding get() = _binding!!
    private val rewardAd = IKRewardAd()
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
        loadRewardAd()

        binding.close.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.watchAd.setOnClickListener {
            rewardAd.showAd(
                requireActivity(),
                "rewardscr",
                adListener = object : IKShowRewardAdListener {
                    override fun onAdsRewarded() {
                        if (isAdded){
                            MySharePreference.setVIPGiftBool(requireActivity(),true)
                            MySharePreference.setVIPGiftDate(requireActivity())
                            showCustomAlertDialog()
                        }
                    }

                    override fun onAdsShowFail(error: IKAdError) {
                        if (isAdded) {
                            Toast.makeText(
                                requireContext(),
                                "Ad not available, Try again",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onAdsDismiss() {
                        loadRewardAd()
                    }
                }
            )
        }
    }

    private fun sendTracking(
        eventName: String,
        vararg param: Pair<String, String?>
    ) {
        IKTrackingHelper.sendTracking(eventName, *param)
    }

    private fun loadRewardAd() {
        rewardAd.attachLifecycle(this.lifecycle)
        // Load ad with a specific screen ID, considered as a unitId
        rewardAd.loadAd("rewardscr", object : IKLoadAdListener {
            override fun onAdLoaded() {
                // Ad loaded successfully
            }

            override fun onAdLoadFail(error: IKAdError) {
                // Handle ad load failure
            }
        })
    }

    private fun showCustomAlertDialog() {
        // Inflate the custom layout using view binding
        val dialogBinding = LayoutWallpaperGiftDialogBinding.inflate(layoutInflater)

        // Build the AlertDialog
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setView(dialogBinding.root)

        val dialog = dialogBuilder.create()

        // Set up dialog behavior
        dialogBinding.viewWallpapers.setOnClickListener {
            if (isAdded){
                val bundle = Bundle().apply {
                    putString("name", "Vip")
                    putString("from", "Vip")
                }
                findNavController().navigate(R.id.listViewFragment, bundle)
            }
            dialog.dismiss()
        }

        // Show the dialog
        dialog.show()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}