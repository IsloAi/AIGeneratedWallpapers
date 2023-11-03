package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments

import android.animation.ValueAnimator
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentSplashOnBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.ImageGenerationDialogBinding

class SplashOnFragment : Fragment() {

    private var _binding:FragmentSplashOnBinding ?= null
    private val binding get() = _binding!!

    private var currentPosition = 0
    private var isVideoPrepared = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSplashOnBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val videoPath = "android.resource://" + requireContext().packageName + "/" + R.raw.splash_animation
        binding.splashAnim.setMediaController(null)

        // Set the video URI and start playing
        binding.splashAnim.setVideoURI(Uri.parse(videoPath))
        binding.splashAnim.start()

        binding.splashAnim.setOnPreparedListener { mp ->
            isVideoPrepared = true
            mp.isLooping = true
            mp.seekTo(currentPosition)
            mp.start()
        }

        binding.getStarted.setOnClickListener {
            findNavController().navigate(R.id.action_splashOnFragment_to_splashFragment)
        }
    }

    override fun onPause() {
        super.onPause()
        if (isVideoPrepared) {
            // Save the current video position
            currentPosition = binding.splashAnim.currentPosition

            // Pause or stop video playback
            binding.splashAnim.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isVideoPrepared) {
            // Resume video playback
            binding.splashAnim.seekTo(currentPosition)
            binding.splashAnim.start()
        }
    }





    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}