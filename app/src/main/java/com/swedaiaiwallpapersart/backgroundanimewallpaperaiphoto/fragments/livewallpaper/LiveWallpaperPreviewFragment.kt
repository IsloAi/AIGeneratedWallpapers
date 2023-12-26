package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.livewallpaper

import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer.OnCompletionListener
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bmik.android.sdk.IkmSdkController
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentLiveWallpaperPreviewBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.service.LiveWallpaperService
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.BlurView
import java.io.File


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