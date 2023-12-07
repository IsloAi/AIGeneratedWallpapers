package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.CommonAdsListenerAdapter
import com.bumptech.glide.Glide
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentSplashOnBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.NativeAdsPreLoading
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

    private lateinit var myActivity : MainActivity

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

        myActivity = activity as MainActivity



        Glide.with(this)
            .asGif()
            .load(R.drawable.splash_anim)
            .into(binding.splashAnimBottom)


        val lan = MySharePreference.getLanguage(requireContext())


        if (lan?.isEmpty() == true){
            SDKBaseController.getInstance().preloadNativeAd(requireActivity(),"languagescr_bottom","languagescr_bottom")

        }


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

            delay(5000)
            SDKBaseController.getInstance().showFirstOpenAppAds(myActivity,12000,object:CommonAdsListenerAdapter(){
                override fun onAdReady(priority: Int) {
//                progress.dismiss()
                }

                override fun onAdsDismiss() {
                    if (lan?.isEmpty() == true && isAdded){
                        findNavController().navigate(R.id.localizationFragment)
                    }else{
                        if (isAdded){
                                findNavController().navigate(R.id.action_splashFragment_to_mainFragment)


                        }

                    }

                }

                override fun onAdsShowFail(errorCode: Int) {
                    Log.e("TAG", "onAdsShowFail: $errorCode")
                    if (lan?.isEmpty() == true && isAdded){
                        findNavController().navigate(R.id.localizationFragment)
                    }else{
                        if (isAdded) {
                            findNavController().navigate(R.id.action_splashFragment_to_mainFragment)
                        }
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