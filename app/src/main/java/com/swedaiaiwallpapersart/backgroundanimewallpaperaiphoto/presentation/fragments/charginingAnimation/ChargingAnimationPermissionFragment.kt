package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.fragments.charginingAnimation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentChargingAnimationPermissionBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.ChargingAnimModel

class ChargingAnimationPermissionFragment : Fragment() {

    private var _binding: FragmentChargingAnimationPermissionBinding? = null
    private val binding get() = _binding!!

    //val sharedViewModel: SharedViewModel by activityViewModels()

    private var livewallpaper: ChargingAnimModel? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChargingAnimationPermissionBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        setEvents()
    }

    private fun initObservers() {
        /*sharedViewModel.chargingAnimationResponseList.observe(viewLifecycleOwner) { wallpaper ->
            if (wallpaper.isNotEmpty()) {

                Log.e("TAG", "initObservers: $wallpaper")

                livewallpaper = wallpaper[0]
            }
        }
        sharedViewModel.liveAdPosition.observe(viewLifecycleOwner) {
        }*/
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
        binding.mySwitch.setOnCheckedChangeListener { _, b ->
            if (b) {
                requestDrawOverlaysPermission(requireActivity())
            }
        }

        backHandle()
    }

    private fun requestDrawOverlaysPermission(activity: Activity) {

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
                binding.mySwitch.isChecked = false
                Toast.makeText(
                    requireContext(),
                    "Please grant permission to continue",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun isDrawOverlaysPermissionGranted(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}