package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.doublewallpaper

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
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentDoubleWallpaperDownloadBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.AdEventListener
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MaxNativeAd
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MyApp
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.DoubleWallModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.BlurView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants.Companion.checkAppOpen
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.DoubeWallpaperViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.DoubleSharedViewmodel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class DoubleWallpaperDownloadFragment : Fragment(), AdEventListener {

    private var _binding: FragmentDoubleWallpaperDownloadBinding? = null
    private val binding get() = _binding!!

    val sharedViewModel: DoubleSharedViewmodel by activityViewModels()

    val doubleWallpaperViewmodel: DoubeWallpaperViewModel by activityViewModels()

    var wallModel: DoubleWallModel? = null
    private var bitmap: Bitmap? = null

    private var animationJob: Job? = null

    val TAG = "DOWNLOAD_SCREEN_DOUBLE"

    private val totalTimeInMillis: Long = 15000 // 15 seconds in milliseconds
    private val intervalInMillis: Long = 100 // Update interval in milliseconds
    private var job: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDoubleWallpaperDownloadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Max Medium Native Ad
        MaxNativeAd.createTemplateNativeAdLoader(
            requireContext(),
            AdConfig.applovinAndroidNativeMedium,
            object : MaxNativeAdListener() {
                override fun onNativeAdLoaded(p0: MaxNativeAdView?, p1: MaxAd) {
                    super.onNativeAdLoaded(p0, p1)
                    binding.NativeAdDouble.removeAllViews()
                    p0?.let {
                        binding.NativeAdDouble.addView(it)
                        binding.NativeAdDouble.visibility = View.VISIBLE
                    }
                }

                override fun onNativeAdLoadFailed(p0: String, p1: MaxError) {
                    super.onNativeAdLoadFailed(p0, p1)
                }

                override fun onNativeAdClicked(p0: MaxAd) {
                    super.onNativeAdClicked(p0)
                }

                override fun onNativeAdExpired(p0: MaxAd) {
                    super.onNativeAdExpired(p0)
                }
            })
        MaxNativeAd.loadTemplateNativeAd(MaxNativeAdView.MEDIUM_TEMPLATE_1, requireContext())

        setEvents()
        initObservers()
    }

    override fun onStart() {
        super.onStart()
        (requireActivity().application as MyApp).registerAdEventListener(this)

    }

    fun setEvents() {
        binding.buttonApplyWallpaper.setOnClickListener {
            wallModel?.downloaded = true
            setDownloadedAndPopBack()
        }

        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
            Constants.checkInter = false
            Constants.checkAppOpen = false
        }
    }

    private fun setDownloadedAndPopBack() {
        if (isAdded) {
            wallModel?.let { it1 ->
                sharedViewModel.updateDoubleWallById(
                    wallModel?.id!!,
                    it1
                )

                doubleWallpaperViewmodel.updateValueById(
                    wallModel?.id!!,
                    it1
                )
            }
            findNavController().popBackStack()

        }
    }

    private fun initObservers() {

        val file = requireContext().filesDir
        val video = File(file, "video.mp4")
        sharedViewModel.chargingAnimationResponseList.observe(viewLifecycleOwner) { wallpaper ->
            if (wallpaper.isNotEmpty()) {

                wallModel = wallpaper[0]

                startProgressCoroutine()
                Log.e("TAG", "initObservers: $wallpaper")

                if (BlurView.filePathBattery == "") {
                    downloadVideo(
                        AdConfig.BASE_URL_DATA + "/doublewallpaper/" + wallpaper[0]?.hd_url1,
                        video
                    )

                }
                getBitmapFromGlide(AdConfig.BASE_URL_DATA + "/doublewallpaper/" + wallpaper[0]?.hd_url2)


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