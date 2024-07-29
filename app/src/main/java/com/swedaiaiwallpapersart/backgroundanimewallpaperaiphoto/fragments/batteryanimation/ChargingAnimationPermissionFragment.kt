package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.batteryanimation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ikame.android.sdk.IKSdkController
import com.ikame.android.sdk.data.dto.pub.IKAdError
import com.ikame.android.sdk.listener.pub.IKShowWidgetAdListener
import com.ikame.android.sdk.widgets.IkmWidgetAdLayout
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentChargingAnimationPermissionBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.ChargingAnimModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
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
        if (AdConfig.ISPAIDUSER){
            binding.adsView.visibility = View.GONE
        }else{
            loadAd()
        }
        initObservers()

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
        binding.adsView.loadAd(R.layout.shimmer_loading_native, adLayout!!,"downloadscr_native_bottom",
            object : IKShowWidgetAdListener {
                override fun onAdShowFail(error: IKAdError) {
                    if (AdConfig.ISPAIDUSER){
                        binding.adsView.visibility = View.GONE
                    }
                    Log.e("TAG", "onAdsLoadFail: native failded " )
                }

                override fun onAdShowed() {
                    if (isAdded && view != null) {
                        // Modify view visibility here
                        binding.adsView.visibility = View.VISIBLE
                    }
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

        IKSdkController.setEnableShowResumeAds(true)

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}