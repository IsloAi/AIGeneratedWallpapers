package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.reward

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.ikame.android.sdk.data.dto.pub.IKAdError
import com.ikame.android.sdk.format.rewarded.IKRewardAd
import com.ikame.android.sdk.listener.pub.IKLoadAdListener
import com.ikame.android.sdk.listener.pub.IKShowRewardAdListener
import com.ikame.android.sdk.tracking.IKTrackingHelper
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.DialogLoadingBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentRewardDetailsBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.LayoutWallpaperGiftDialogBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants
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

        Constants.hasShownRewardScreen = true
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
                        if (isAdded){
                            Toast.makeText(requireActivity(), "Ad failed to show. Still loading...", Toast.LENGTH_SHORT).show()
                        }
                            loadRewardAd()
                    }

                    override fun onAdsDismiss() {

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
            override fun onAdLoaded() {}

            override fun onAdLoadFail(error: IKAdError) {}
        })
    }

    private fun showCustomAlertDialog() {
        val dialogBinding = LayoutWallpaperGiftDialogBinding.inflate(layoutInflater)

        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setView(dialogBinding.root)

        val dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.viewWallpapers.setOnClickListener {
            if (isAdded){
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