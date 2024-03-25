package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.batteryanimation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bmik.android.sdk.IkmSdkController
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.CustomSDKAdsListenerAdapter
import com.bmik.android.sdk.widgets.IkmWidgetAdLayout
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentChargingAnimationPermissionBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.ChargingAnimModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.LiveWallpaperModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.BlurView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel

class ChargingAnimationPermissionFragment : Fragment() {

    private var _binding:FragmentChargingAnimationPermissionBinding ?= null
    private val binding get() = _binding!!

    val sharedViewModel: SharedViewModel by activityViewModels()

    private var livewallpaper: ChargingAnimModel? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChargingAnimationPermissionBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadAd()
        initObservers()
        setWallpaperOnView()

        setEvents()
    }

    fun loadAd(){
        val adLayout = LayoutInflater.from(activity).inflate(
            R.layout.large_native_layout,
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

        binding.adsView.loadAd(requireActivity(),"downloadscr_native_bottom","downloadscr_native_bottom",
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

    private fun initObservers() {

        sharedViewModel.chargingAnimationResponseList.observe(viewLifecycleOwner) { wallpaper ->
            if (wallpaper.isNotEmpty()) {

                Log.e("TAG", "initObservers: $wallpaper")

                livewallpaper = wallpaper[0]
            }
        }


        sharedViewModel.liveAdPosition.observe(viewLifecycleOwner) {
        }
    }

    private fun backHandle() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack(R.id.homeTabsFragment, false)
                }
            })
    }


    private fun setEvents() {
        binding.mySwitch.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                requestDrawOverlaysPermission(requireActivity())
            }
        }

        backHandle()
    }

    fun requestDrawOverlaysPermission(activity: Activity) {

        IkmSdkController.setEnableShowResumeAds(false)

        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:${activity.packageName}")
        startActivityForResult(intent, 120)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 120) {
            if (isDrawOverlaysPermissionGranted(requireContext())) {
                findNavController().popBackStack()
            } else {
                binding.mySwitch.isChecked =  false
                Toast.makeText(requireContext(),"Please grant permission to continue", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun isDrawOverlaysPermissionGranted(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

    private fun setWallpaperOnView() {
        if (isAdded){
            binding.liveWallpaper.setMediaController(null)
            binding.liveWallpaper.setVideoPath(BlurView.filePath)
            binding.liveWallpaper.setOnCompletionListener(MediaPlayer.OnCompletionListener {
                binding.liveWallpaper.start()
            })

            binding.liveWallpaper.setOnPreparedListener { mediaPlayer ->
                // Adjust video looping here if needed
                mediaPlayer.isLooping = true
            }

            binding.liveWallpaper.start()
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}