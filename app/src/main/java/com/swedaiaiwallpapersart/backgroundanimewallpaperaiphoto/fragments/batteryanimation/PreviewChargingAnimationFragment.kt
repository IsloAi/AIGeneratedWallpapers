package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.batteryanimation

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
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
import com.ikame.android.sdk.tracking.IKTrackingHelper
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentPreviewChargingAnimationBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.ChargingAnimModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.remote.EndPointsInterface
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.service.ChargingAnimationService
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.BlurView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.BatteryAnimationViewmodel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PreviewChargingAnimationFragment : Fragment() {
    private var _binding:FragmentPreviewChargingAnimationBinding ?= null
    private val binding get() = _binding!!

    val sharedViewModel: BatteryAnimationViewmodel by activityViewModels()

    private var livewallpaper: ChargingAnimModel? = null
    var adPosition = 0

    private lateinit var myActivity : MainActivity

    @Inject
    lateinit var webApiInterface: EndPointsInterface

    @Inject
    lateinit var appDatabase: AppDatabase



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPreviewChargingAnimationBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        myActivity = activity as MainActivity
        if (AdConfig.ISPAIDUSER){
            binding.adsView.visibility = View.GONE
        }else{

            binding.adsView.attachLifecycle(lifecycle)
            binding.adsView.loadAd("searchscr_bottom", object : IKShowWidgetAdListener {
                override fun onAdShowed() {}
                override fun onAdShowFail(error: IKAdError) {
//                    binding.adsView?.visibility = View.GONE
                }

            })
        }

        initObservers()
        setWallpaperOnView()

        setEvents()
    }

    private fun initObservers() {

        sharedViewModel.chargingAnimationResponseList.observe(viewLifecycleOwner) { wallpaper ->
            if (wallpaper.isNotEmpty()) {

                Log.e("TAG", "initObservers: $wallpaper")

                livewallpaper = wallpaper[0]

                setWallpaperOnView()
            }
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
        binding.buttonApplyWallpaper.setOnClickListener {
            Log.e("TAG", "setEvents: clicked" )
            if (isDrawOverlaysPermissionGranted(requireContext())){
                if (isAdded){
                val intent = Intent(requireContext(),ChargingAnimationService::class.java)
                MySharePreference.setAnimationPath(requireContext(),BlurView.filePathBattery)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    Log.e("TAG", "setEvents: service start Q")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
                            var dialog: Dialog = AlertDialog.Builder(requireContext())
                                .setTitle("Foreground Service Required")
                                .setMessage("To continue playing the animation, the app needs to run in the foreground. This means that the app will continue to run even when you close it.")
                                .setPositiveButton("Allow"
                                ) { dialog, which -> // Start the foreground service
                                    requireContext().startForegroundService(intent)
                                    sendTracking("typewallpaper_used",Pair("typewallpaper", "Charging"))
                                    Toast.makeText(requireContext(),"Charging animation Applied Successfully",Toast.LENGTH_SHORT).show()
                                }
                                .setNegativeButton("Cancel"
                                ) { dialog, which -> dialog.dismiss() }
                                .show()


                    }else{
                        sendTracking("typewallpaper_used",Pair("typewallpaper", "Charging"))
                        Toast.makeText(requireContext(),"Charging animation Applied Successfully",Toast.LENGTH_SHORT).show()
                        requireContext().startForegroundService(intent)
                    }

                }else{
                    Log.e("TAG", "setEvents: service start else")
                    requireContext().startService(intent)
                    sendTracking("typewallpaper_used",Pair("typewallpaper", "Charging"))
                    Toast.makeText(requireContext(),"Charging animation Applied Successfully",Toast.LENGTH_SHORT).show()
                }
                }
            }else{

                findNavController().navigate(R.id.chargingAnimationPermissionFragment)
            }
        }

        binding.toolbar.setOnClickListener {
            findNavController().popBackStack(R.id.homeTabsFragment, false)
            Constants.checkInter = false
            Constants.checkAppOpen = false
        }

        backHandle()
    }

    private fun sendTracking(
        eventName: String,
        vararg param: Pair<String, String?>
    )
    {
        IKTrackingHelper.sendTracking( eventName, *param)
    }

    fun isDrawOverlaysPermissionGranted(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }


    override fun onResume() {
        super.onResume()
        setWallpaperOnView()
    }


    private fun setWallpaperOnView() {
        if (isAdded){
            binding.liveWallpaper.setAnimationFromUrl(AdConfig.BASE_URL_DATA + "/animation/"+livewallpaper?.hd_animation)
            binding.liveWallpaper.playAnimation()
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}