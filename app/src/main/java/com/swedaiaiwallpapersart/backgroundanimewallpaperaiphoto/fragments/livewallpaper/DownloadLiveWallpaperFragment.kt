package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.livewallpaper

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.DownloadListener
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentDownloadLiveWallpaperBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.AdEventListener
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MaxNativeAd
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MyApp
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.NativeAdManager
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.BlurView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants.Companion.checkAppOpen
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants.Companion.checkInter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class DownloadLiveWallpaperFragment : Fragment(), AdEventListener {

    private var _binding: FragmentDownloadLiveWallpaperBinding? = null

    private val binding get() = _binding!!

    val sharedViewModel: SharedViewModel by activityViewModels()

    private var bitmap: Bitmap? = null

    private var animationJob: Job? = null

    val TAG = "DOWNLOAD_SCREEN"

    var showAd: Boolean? = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDownloadLiveWallpaperBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showAd = arguments?.getBoolean("adShowed")

        Log.e(TAG, "onViewCreated: $showAd")

        setEvents()
        initObservers()

        /*val nativeAd = NativeAdManager(
            requireContext(),
            AdConfig.admobAndroidNative,
            R.layout.admob_native_medium
        )
        nativeAd.loadNativeAd(binding.NativeAd)*/
        MaxNativeAd.createNativeAdLoader(
            requireContext(),
            AdConfig.applovinAndroidNativeManual,
            object : MaxNativeAdListener() {
                override fun onNativeAdLoaded(adView: MaxNativeAdView?, ad: MaxAd) {
                    binding.NativeAd.removeAllViews()
                    adView?.let {
                        binding.NativeAd.addView(it)
                    }
                }

                override fun onNativeAdLoadFailed(adUnitId: String, error: MaxError) {
                    // Handle failure (optional retry logic)
                }

                override fun onNativeAdClicked(ad: MaxAd) {
                    // Handle click
                }

                override fun onNativeAdExpired(ad: MaxAd) {
                    // Ad expired - reload if needed
                }
            }
        )

        MaxNativeAd.loadNativeAd(R.layout.max_native_medium, requireContext())
    }

    override fun onStart() {
        super.onStart()
        (requireActivity().application as MyApp).registerAdEventListener(this)

    }

    fun setEvents() {
        binding.buttonApplyWallpaper.setOnClickListener {
            navigateToPreview()
        }

        binding.toolbar.setOnClickListener {
            checkInter = false
            checkAppOpen = false
            findNavController().popBackStack()
        }
    }

    private fun navigateToPreview() {
        if (isAdded) {
            findNavController().navigate(R.id.liveWallpaperPreviewFragment)

        }
    }

    private fun initObservers() {
        if (shouldObserveLiveWallpapers) {
            sharedViewModel.liveWallpaperResponseList.observe(viewLifecycleOwner) { wallpaper ->
                if (wallpaper.isNotEmpty()) {

                    Log.e("TAG", "initObservers: $wallpaper")

                    if (BlurView.filePath == "") {
                        Log.e(TAG, "initObservers123: " + wallpaper[0])
                        Log.e(TAG, "initObservers123: " + AdConfig.BASE_URL_DATA)
                        downloadVideo(
                            AdConfig.BASE_URL_DATA + "/livewallpaper/" + wallpaper[0].livewallpaper_url,
                            wallpaper[0].videoSize
                        )
                    }
                    getBitmapFromGlide(AdConfig.BASE_URL_DATA + "/livewallpaper/" + wallpaper[0].thumnail_url)
                }
            }
        }

        if (shouldObserveFavorites) {
            sharedViewModel.favLiveWallpaperResponseList.observe(viewLifecycleOwner) { wallpaper ->
                if (wallpaper.isNotEmpty()) {

                    Log.e("Favourite", "initObservers: $wallpaper")

                    if (BlurView.filePath == "") {
                        Log.e("Favourite", "initObservers123: " + wallpaper[0])
                        Log.e("Favourite", "initObservers123: " + AdConfig.BASE_URL_DATA)
                        downloadVideo(
                            AdConfig.BASE_URL_DATA + "/livewallpaper/" + wallpaper[0].livewallpaper_url,
                            wallpaper[0].videoSize
                        )
                    }
                    getBitmapFromGlide(AdConfig.BASE_URL_DATA + "/livewallpaper/" + wallpaper[0].thumnail_url)
                }
            }
        }

        /*sharedViewModel.favLiveWallpaperResponseList.observe(viewLifecycleOwner) { wallpaper ->
            if (wallpaper.isNotEmpty()) {

                Log.e("TAG", "initObservers: $wallpaper")

                if (BlurView.filePath == "") {
                    Log.e(TAG, "initObservers123: " + wallpaper[0])
                    Log.e(TAG, "initObservers123: " + AdConfig.BASE_URL_DATA)
                    downloadVideo(
                        AdConfig.BASE_URL_DATA + "/livewallpaper/" + wallpaper[0].livewallpaper_url,
                        wallpaper[0].videoSize
                    )
                }
                getBitmapFromGlide(AdConfig.BASE_URL_DATA + "/livewallpaper/" + wallpaper[0].thumnail_url)
            }
        }
        sharedViewModel.liveWallpaperResponseList.observe(viewLifecycleOwner) { wallpaper ->
            if (wallpaper.isNotEmpty()) {

                Log.e("TAG", "initObservers: $wallpaper")

                if (BlurView.filePath == "") {
                    Log.e(TAG, "initObservers123: " + wallpaper[0])
                    Log.e(TAG, "initObservers123: " + AdConfig.BASE_URL_DATA)
                    downloadVideo(
                        AdConfig.BASE_URL_DATA + "/livewallpaper/" + wallpaper[0].livewallpaper_url,
                        wallpaper[0].videoSize
                    )
                }
                getBitmapFromGlide(AdConfig.BASE_URL_DATA + "/livewallpaper/" + wallpaper[0].thumnail_url)
            }
        }*/
    }

    private fun getBitmapFromGlide(url: String) {
        Glide.with(requireContext()).asBitmap().load(url)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    bitmap = resource

                    if (isAdded) {
                        val blurImage: Bitmap = BlurView.blurImage(requireContext(), bitmap!!)!!
                        //binding.backImage.setImageBitmap(blurImage)
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }

    private fun downloadVideo(url: String, size: Float) {
        Log.e(TAG, "downloadVideo: $url")

        val file = requireContext().filesDir
        val fileName = System.currentTimeMillis().toString() + ".mp4"
        val filepath = File(file, fileName)

        BlurView.filePath = filepath.path
        BlurView.fileName = fileName
        MySharePreference.setFileName(requireContext(), fileName)
        Log.e("TAG", "downloadVideo: " + BlurView.fileName)

        val totalSize = (size * 1024).toLong()
        animateLoadingText()
        lifecycleScope.launch(Dispatchers.IO) {
            AndroidNetworking.download(url, file.path, fileName).setTag("downloadTest")
                .setPriority(Priority.HIGH).doNotCacheResponse().build()
                .setDownloadProgressListener { bytesDownloaded, totalBytes ->
                    Log.e("TAG", "downloadVideo: $bytesDownloaded")
                    Log.e("TAG", "downloadVideo total bytes: $totalBytes")
                    lifecycleScope.launch(Dispatchers.Main) {
                        val percentage = (bytesDownloaded * 100 / totalSize).toInt()
                        Log.e("TAG", "downloadVideo: $percentage")
                        if (isAdded) {
                            val currentCount = (bytesDownloaded * 100 / totalSize)
                            if (currentCount <= 100) {
                                binding.progressTxt.text = currentCount.toString() + "%"
                            } else {
                                Log.e(TAG, "downloadVideo: $currentCount")
                            }
                            binding.progress.progress = percentage
                        }
                    }
                }.startDownload(object : DownloadListener {
                    override fun onDownloadComplete() {
                        lifecycleScope.launch(Dispatchers.Main) {
                            if (isAdded) {
                                binding.progressTxt.text = "100%"
                                binding.progress.progress = 100
                                binding.buttonApplyWallpaper.visibility = View.VISIBLE
                                animationJob?.cancel()
                                binding.loadingTxt.text = "Download Successfull"
                            }
                        }
                    }

                    override fun onError(error: ANError?) {
                        if (isAdded) {
                            checkInter = true
                            checkAppOpen = true
                            Log.d(TAG, "onErrorVideoDownloadFailed $error")
                            Toast.makeText(
                                requireContext(),
                                "Server down. Please try again later!",
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().navigateUp()
                        }
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
        textView.text = "Downloading $dots"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (animationJob?.isActive == true) {
            animationJob?.cancel()
        }

        _binding = null
    }

    override fun onAdDismiss() {
        checkAppOpen = true
        Log.e(TAG, "app open dismissed: ")
    }

    override fun onAdLoading() {}

    override fun onAdsShowTimeout() {}

    override fun onShowAdComplete() {}

    override fun onShowAdFail() {}

    companion object {
        var shouldObserveFavorites = false
        var shouldObserveLiveWallpapers = true
    }
}