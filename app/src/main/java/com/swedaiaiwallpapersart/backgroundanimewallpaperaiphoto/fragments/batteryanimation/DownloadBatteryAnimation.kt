package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.batteryanimation

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentDownloadBatteryAnimationBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.AdEventListener
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MaxNativeAd
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MyApp
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.NativeAdManager
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.BlurView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants.Companion.checkAppOpen
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.BatteryAnimationViewmodel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class DownloadBatteryAnimation : Fragment(), AdEventListener {
    private var _binding: FragmentDownloadBatteryAnimationBinding? = null
    private val binding get() = _binding!!

    val sharedViewModel: BatteryAnimationViewmodel by activityViewModels()

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
    ): View {
        _binding = FragmentDownloadBatteryAnimationBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adShowed = arguments?.getBoolean("adShowed")

        /*val native = NativeAdManager(
            requireContext(),
            AdConfig.admobAndroidNative,
            R.layout.admob_native_medium
        )
        native.loadNativeAd(binding.NativeAD)*/
        MaxNativeAd.createNativeAdLoader(
            requireContext(),
            AdConfig.applovinAndroidNativeManual,
            object : MaxNativeAdListener() {
                override fun onNativeAdLoaded(adView: MaxNativeAdView?, ad: MaxAd) {
                    binding.NativeAD.removeAllViews()
                    adView?.let {
                        binding.NativeAD.addView(it)
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
        setEvents()
        initObservers()
    }

    override fun onStart() {
        super.onStart()
        (requireActivity().application as MyApp).registerAdEventListener(this)

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
        sharedViewModel.chargingAnimationResponseList.observe(viewLifecycleOwner) { wallpaper ->
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
        }
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
        lifecycleScope.launch(Dispatchers.IO) {}
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

    override fun onAdDismiss() {
        checkAppOpen = true
        Log.e(TAG, "app open dismissed: ")
    }

    override fun onAdLoading() {

    }

    override fun onAdsShowTimeout() {

    }

    override fun onShowAdComplete() {

    }

    override fun onShowAdFail() {

    }
}