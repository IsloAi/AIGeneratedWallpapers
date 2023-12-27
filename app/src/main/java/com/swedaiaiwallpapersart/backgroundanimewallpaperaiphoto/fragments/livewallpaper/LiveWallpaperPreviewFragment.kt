package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.livewallpaper

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer.OnCompletionListener
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bmik.android.sdk.IkmSdkController
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.CommonAdsListenerAdapter
import com.bmik.android.sdk.listener.CustomSDKRewardedAdsListener
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentLiveWallpaperPreviewBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.service.LiveWallpaperService
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.BlurView
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


class LiveWallpaperPreviewFragment : Fragment() {

    private var _binding:FragmentLiveWallpaperPreviewBinding ?= null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLiveWallpaperPreviewBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setWallpaperOnView()
        setEvents()

    }

    private fun backHandle(){
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack(R.id.homeTabsFragment,false)
            }
        })
    }


    private fun setEvents(){
        binding.buttonApplyWallpaper.setOnClickListener {

            if (isLiveWallpaperSupported(requireContext())){
                IkmSdkController.setEnableShowResumeAds(false)
                LiveWallpaperService.setToWallPaper(requireContext())
            }else{
                Toast.makeText(requireContext(),"This device do not support Live Wallpapers",Toast.LENGTH_SHORT).show()
            }

        }

        binding.toolbar.setOnClickListener {
            findNavController().popBackStack(R.id.homeTabsFragment,false)
        }

        backHandle()


        binding.downloadWallpaper.setOnClickListener {

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2){
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    Log.e("TAG", "functionality: inside click permission", )
                    ActivityCompat.requestPermissions(requireContext() as Activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                }else{
                    Log.e("TAG", "functionality: inside click dialog", )
                    getUserIdDialog()
                }
            }else{
                getUserIdDialog()
            }

        }
    }

    private fun getUserIdDialog() {

        val source =  File(BlurView.filePath)
        val file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val destination =  File(file,BlurView.fileName)
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.rewarded_ad_dialog)
        val width = WindowManager.LayoutParams.MATCH_PARENT
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.setLayout(width, height)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        val getReward = dialog.findViewById<LinearLayout>(R.id.buttonGetReward)
        val dismiss = dialog.findViewById<TextView>(R.id.noThanks)

        getReward?.setOnClickListener {
            dialog.dismiss()
            SDKBaseController.getInstance().showRewardedAds(requireActivity(),"viewlistwallscr_download_item","viewlistwallscr_download_item",object:
                CustomSDKRewardedAdsListener {
                override fun onAdsDismiss() {
                    Log.e("********ADS", "onAdsDismiss: ")
                }

                override fun onAdsRewarded() {
                    Log.e("********ADS", "onAdsRewarded: ")

                    copyFiles(source,destination)

                }

                override fun onAdsShowFail(errorCode: Int) {
                    SDKBaseController.getInstance().showInterstitialAds(
                        requireActivity(),
                        "viewlistwallscr_download_item_inter",
                        "viewlistwallscr_download_item_inter",
                        showLoading = true,
                        adsListener = object : CommonAdsListenerAdapter() {
                            override fun onAdsShowFail(errorCode: Int) {
                                copyFiles(source,destination)
                            }

                            override fun onAdsDismiss() {
                                copyFiles(source,destination)
                            }
                        }
                    )
                    Log.e("********ADS", "onAdsShowFail: ")

                }

            })
        }

        dismiss?.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


        fun copyFiles(source:File,destination:File){
            try {
                val inputStream = FileInputStream(source)
                val outputStream = FileOutputStream(destination)
                inputStream.copyTo(outputStream)
                inputStream.close()
                outputStream.close()

                Toast.makeText(requireContext(),"Wallpaper downloaded",Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                Toast.makeText(requireContext(),"Download failed",Toast.LENGTH_SHORT).show()
                // Handle error
            }
        }

    fun isLiveWallpaperSupported(context: Context): Boolean {
        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
        val activities = context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return activities.isNotEmpty()
    }


    override fun onResume() {
        super.onResume()
        setWallpaperOnView()
    }


    private fun setWallpaperOnView(){

        binding.liveWallpaper.setMediaController(null)
        binding.liveWallpaper.setVideoPath(BlurView.filePath)
        binding.liveWallpaper.setOnCompletionListener(OnCompletionListener {
            binding.liveWallpaper.start()
        })

        binding.liveWallpaper.setOnPreparedListener { mediaPlayer ->
            // Adjust video looping here if needed
            mediaPlayer.isLooping = true
        }

        binding.liveWallpaper.start()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}