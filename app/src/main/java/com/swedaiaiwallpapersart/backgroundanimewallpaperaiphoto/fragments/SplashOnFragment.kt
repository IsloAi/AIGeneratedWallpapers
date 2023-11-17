package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments

import android.animation.ValueAnimator
import android.app.AlertDialog
import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.CommonAdsListener
import com.bmik.android.sdk.listener.CommonAdsListenerAdapter
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.debug.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.debug.databinding
.FragmentSplashOnBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.debug.databinding
.ImageGenerationDialogBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashOnFragment : Fragment() {

    companion object{
        var exit = true
    }

    private var _binding: FragmentSplashOnBinding?= null
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

//        val progress = ProgressDialog(requireContext())
//        progress.setMessage("Loading Ads")
//        progress.setCancelable(false)
//        progress.show()


        val lan = MySharePreference.getLanguage(requireContext())
//        val videoPath = "android.resource://" + requireContext().packageName + "/" + R.raw.splash_animation
//        binding.splashAnim.setMediaController(null)
//
//
//
//        // Set the video URI and start playing
//        binding.splashAnim.setVideoURI(Uri.parse(videoPath))
//        binding.splashAnim.start()
//
//        binding.splashAnim.setOnPreparedListener { mp ->
//            isVideoPrepared = true
//            mp.isLooping = true
//            mp.seekTo(currentPosition)
//            mp.start()
//        }

//        lifecycleScope.launch {
//            delay(6000)
//
//
//        }

        lifecycleScope.launch(Dispatchers.Main) {

            delay(3000)
            SDKBaseController.getInstance().showFirstOpenAppAds(requireActivity(),12000,object:CommonAdsListenerAdapter(){
                override fun onAdReady(priority: Int) {
//                progress.dismiss()
                }

                override fun onAdsDismiss() {
                    if (lan?.isEmpty() == true){
                        findNavController().navigate(R.id.localizationFragment)
                    }else{
                        findNavController().navigate(R.id.mainFragment)
                    }

                }

                override fun onAdsShowFail(errorCode: Int) {
                    Log.e("TAG", "onAdsShowFail: $errorCode")
                    if (lan?.isEmpty() == true){
                        findNavController().navigate(R.id.localizationFragment)
                    }else{
                        findNavController().navigate(R.id.mainFragment)
                    }
//                progress.dismiss()
                }

                override fun onAdsShowed(priority: Int) {
//                progress.dismiss()

                }

            })
        }




        binding.getStarted.setOnClickListener {
            findNavController().navigate(R.id.onBoardingFragment)
        }
    }

    override fun onPause() {
        super.onPause()
//        if (isVideoPrepared) {
//            // Save the current video position
//            currentPosition = binding.splashAnim.currentPosition
//
//            // Pause or stop video playback
//            binding.splashAnim.pause()
//        }
    }

    override fun onResume() {
        super.onResume()
//        if (isVideoPrepared) {
//            // Resume video playback
//            binding.splashAnim.seekTo(currentPosition)
//            binding.splashAnim.start()
//        }
    }





    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}