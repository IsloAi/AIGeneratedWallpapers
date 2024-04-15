package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.batteryanimation

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bmik.android.sdk.IkmSdkController
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.CommonAdsListenerAdapter
import com.bmik.android.sdk.listener.CustomSDKAdsListenerAdapter
import com.bmik.android.sdk.listener.CustomSDKRewardedAdsListener
import com.bmik.android.sdk.tracking.SDKTrackingController
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.DialogUnlockOrWatchAdsBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentPreviewChargingAnimationBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.ChargingAnimModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.remote.EndPointsInterface
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.FavoruiteLiveWallpaperBody
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.LiveWallpaperModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.RetrofitInstance
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.LikeLiveWallpaper
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.service.ChargingAnimationService
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.service.LiveWallpaperService
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.BlurView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.BatteryAnimationViewmodel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
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
        SDKBaseController.getInstance()
            .loadBannerAds(
                requireActivity(),
                binding.adsWidget as? ViewGroup,
                "searchscr_bottom",
                " searchscr_bottom", object : CustomSDKAdsListenerAdapter() {
                    override fun onAdsLoaded() {
                        super.onAdsLoaded()
                        Log.e("*******ADS", "onAdsLoaded: Banner loaded")
                    }

                    override fun onAdsLoadFail() {
                        super.onAdsLoadFail()
                        Log.e("*******ADS", "onAdsLoaded: Banner failed")
                    }
                }
            )
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
                val intent = Intent(requireContext(),ChargingAnimationService::class.java)
                MySharePreference.setAnimationPath(requireContext(),BlurView.filePathBattery)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    Log.e("TAG", "setEvents: service start Q", )
                    requireContext().startForegroundService(intent)

                }else{
                    Log.e("TAG", "setEvents: service start else", )
                    requireContext().startService(intent)
                }

                if (isAdded){
                    sendTracking("typewallpaper_used",Pair("typewallpaper", "Charging"))
                    Toast.makeText(requireContext(),"Charging animation Applied Successfully",Toast.LENGTH_SHORT).show()
                }
            }else{

                findNavController().navigate(R.id.chargingAnimationPermissionFragment)
            }
        }

        binding.toolbar.setOnClickListener {
            findNavController().popBackStack(R.id.homeTabsFragment, false)
        }

        backHandle()
    }

    private fun sendTracking(
        eventName: String,
        vararg param: Pair<String, String?>
    )
    {
        SDKTrackingController.trackingAllApp(requireContext(), eventName, *param)
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
            binding.liveWallpaper.setAnimationFromUrl(livewallpaper?.hd_animation)
            binding.liveWallpaper.playAnimation()
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}