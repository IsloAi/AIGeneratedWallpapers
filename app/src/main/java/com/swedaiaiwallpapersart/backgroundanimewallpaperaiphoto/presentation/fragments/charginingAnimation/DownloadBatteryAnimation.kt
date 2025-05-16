package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.fragments.charginingAnimation

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.DownloadListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentDownloadBatteryAnimationBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.BlurView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.Constants
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.Constants.Companion.checkAppOpen
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.MySharePreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class DownloadBatteryAnimation : Fragment() {

    private var _binding: FragmentDownloadBatteryAnimationBinding? = null
    private val binding get() = _binding!!

    //val sharedViewModel: BatteryAnimationViewmodel by activityViewModels()
    private var bitmap: Bitmap? = null
    private var animationJob: Job? = null
    val TAG = "DOWNLOAD_SCREEN"
    var adShowed: Boolean? = false
    private val totalTimeInMillis: Long = 15000 // 15 seconds in milliseconds
    private val intervalInMillis: Long = 100 // Update interval in milliseconds
    private var job: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDownloadBatteryAnimationBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adShowed = arguments?.getBoolean("adShowed")
        loadAd()
        AndroidNetworking.initialize(requireContext())
        if (AdConfig.ISPAIDUSER) {

        } else {
            //Max Medium Native Ad
            if (AdConfig.globalTemplateNativeAdView != null) {
                // Detach globalNativeAdView from its previous parent if it has one
                AdConfig.globalTemplateNativeAdView?.parent?.let { parent ->
                    (parent as ViewGroup).removeView(AdConfig.globalTemplateNativeAdView)
                }
                binding.NativeAD.removeAllViews()
                binding.NativeAD.addView(AdConfig.globalTemplateNativeAdView)
            } else {
                // maybe show a placeholder or hide the view
                binding.NativeAD.visibility = View.GONE
            }
        }
        setEvents()
        initObservers()
    }

    fun loadAd() {

    }

    fun setEvents() {
        binding.buttonApplyWallpaper.setOnClickListener {
            navigateToNext()
        }
        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
            Constants.checkInter = false
            checkAppOpen = false
        }
    }

    private fun navigateToNext() {
        if (isAdded) {
            findNavController().navigate(R.id.previewChargingAnimationFragment)

        }
    }

    private fun initObservers() {

        val file = requireContext().filesDir
        val video = File(file, "video.mp4")
        /*sharedViewModel.chargingAnimationResponseList.observe(viewLifecycleOwner) { wallpaper ->
            if (wallpaper.isNotEmpty()) {

                startProgressCoroutine()
                Log.e("TAG", "initObservers: $wallpaper")

                if (BlurView.filePathBattery == "") {
                    downloadVideo(
                        AdConfig.BASE_URL_DATA + "/animation/" + wallpaper[0].hd_animation,
                        video
                    )

                }
                getBitmapFromGlide(AdConfig.BASE_URL_DATA + "/animation/" + wallpaper[0].thumnail)


            }
        }*/
    }

    private fun startProgressCoroutine() {
        job = lifecycleScope.launch(Dispatchers.Main) {
            var progress = 0
            repeat((totalTimeInMillis / intervalInMillis).toInt()) {
                progress = ((it * intervalInMillis * 100) / totalTimeInMillis).toInt()
                if (isAdded) {
                    binding.progress.progress = progress
                    binding.progressTxt.text =
                        progress.toString() + "%"
                }
                delay(intervalInMillis)
            }

            if (isAdded) {
                binding.progress.progress = 100 // Ensure progress reaches 100% even if not exact
                binding.progressTxt.text = "100%"
                onProgressComplete(100)
            }


        }
    }

    private fun onProgressComplete(progress: Int) {
        if (progress >= 100) {
            if (isAdded) {
                binding.buttonApplyWallpaper.visibility = View.VISIBLE

                animationJob?.cancel()
                binding.loadingTxt.text = "Download Successfull"
                job?.cancel()
            }

        }
    }

    private fun getBitmapFromGlide(url: String) {
        Glide.with(requireContext()).asBitmap().load(url)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    bitmap = resource

                    if (isAdded) {
                        val blurImage: Bitmap = BlurView.blurImage(requireContext(), bitmap!!)!!
                        binding.backImage.setImageBitmap(blurImage)
                    }


                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }

    private fun downloadVideo(url: String, destinationFile: File) {
//        val file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val file = requireContext().filesDir
        val fileName = System.currentTimeMillis().toString() + ".json"

        val filepath = File(file, fileName)

        BlurView.filePathBattery = filepath.path
        BlurView.fileNameBattery = fileName
        MySharePreference.setFileName(requireContext(), fileName)
        Log.e("TAG", "downloadVideo: " + BlurView.fileName)

        animateLoadingText()
        lifecycleScope.launch(Dispatchers.IO) {
            AndroidNetworking.download(url, file.path, fileName)
                .setTag("downloadTest")
                .setPriority(Priority.HIGH)
                .doNotCacheResponse()
                .build()
                .setDownloadProgressListener { bytesDownloaded, totalBytes ->
                    Log.e("TAG", "downloadVideo: $bytesDownloaded")
                    Log.e("TAG", "downloadVideo total bytes: $totalBytes")
                }
                .startDownload(object : DownloadListener {
                    override fun onDownloadComplete() {
                    }

                    override fun onError(error: ANError?) {
                        Log.e(TAG, "onError: ")
                        // handle error
                    }
                })
        }
    }

    private fun animateLoadingText() {
        animationJob = viewLifecycleOwner.lifecycleScope.launch {
            while (isActive) {
                delay(500) // Adjust the delay for animation speed
                withContext(Dispatchers.Main) {
                    animateDots(binding.loadingTxt)
                }
            }
        }
    }

    private var dotCount = 0

    private fun animateDots(textView: TextView) {
        dotCount = (dotCount + 1) % 4
        val dots = ".".repeat(dotCount)
        textView.text = "Downloading$dots"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (animationJob?.isActive == true) {
            animationJob?.cancel()
        }

        _binding = null
    }

}