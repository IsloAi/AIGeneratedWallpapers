package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.doublewallpaper

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.DownloadListener
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.CommonAdsListenerAdapter
import com.bmik.android.sdk.listener.CustomSDKAdsListenerAdapter
import com.bmik.android.sdk.widgets.IkmWidgetAdLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentDoubleWallpaperDownloadBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.DoubleWallModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.BlurView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.BatteryAnimationViewmodel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.DoubeWallpaperViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.DoubleSharedViewmodel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class DoubleWallpaperDownloadFragment : Fragment() {

    private var _binding:FragmentDoubleWallpaperDownloadBinding ?= null
    private val binding get() = _binding!!

    val sharedViewModel: DoubleSharedViewmodel by activityViewModels()

    val doubleWallpaperViewmodel: DoubeWallpaperViewModel by activityViewModels()

    var wallModel:DoubleWallModel ?= null
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
        _binding = FragmentDoubleWallpaperDownloadBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadAd()
        AndroidNetworking.initialize(requireContext())

        setEvents()
        initObservers()
    }

    fun loadAd(){
        val adLayout = LayoutInflater.from(activity).inflate(
            R.layout.new_native_language,
            null, false
        ) as? IkmWidgetAdLayout
        adLayout?.titleView = adLayout?.findViewById(R.id.custom_headline)
        adLayout?.bodyView = adLayout?.findViewById(R.id.custom_body)
        adLayout?.callToActionView = adLayout?.findViewById(R.id.custom_call_to_action)
        adLayout?.iconView = adLayout?.findViewById(R.id.custom_app_icon)
        adLayout?.mediaView = adLayout?.findViewById(R.id.custom_media)

        binding.adsView.setCustomNativeAdLayout(
            R.layout.shimmer_loading_native,
            adLayout!!
        )

        binding.adsView.loadAd(requireActivity(),"doublewalldownloadscr_bottom","doublewalldownloadscr_bottom",
            object : CustomSDKAdsListenerAdapter() {
                override fun onAdsLoadFail() {
                    super.onAdsLoadFail()
                    Log.e("TAG", "onAdsLoadFail: native failded " )
//                                    binding.adsView.visibility = View.GONE
                }

                override fun onAdsLoaded() {
                    super.onAdsLoaded()
                    if (isAdded && view != null) {
                        // Modify view visibility here
                        binding.adsView.visibility = View.VISIBLE
                    }

                    Log.e("TAG", "onAdsLoaded: native loaded" )
                }
            }
        )
    }


    fun setEvents(){
        binding.buttonApplyWallpaper.setOnClickListener {
            wallModel?.downloaded = true
            SDKBaseController.getInstance().showInterstitialAds(
                requireActivity(),
                "downloadscr_set_click",
                "downloadscr_set_click",
                showLoading = true,
                adsListener = object : CommonAdsListenerAdapter() {
                    override fun onAdsShowFail(errorCode: Int) {
                        Log.e("********ADS", "onAdsShowFail: "+errorCode )
                        if (isAdded){
                            wallModel?.let { it1 ->
                                sharedViewModel.updateDoubleWallById(
                                    wallModel?.id!!,
                                    it1
                                )

                                doubleWallpaperViewmodel.updateValueById(  wallModel?.id!!,
                                    it1)
                            }
                            findNavController().popBackStack()

                        }
                        //do something
                    }

                    override fun onAdsDismiss() {
                        Log.e(TAG, "onAdsDismiss: ", )
                        if (isAdded){



                            wallModel?.let { it1 ->
                                sharedViewModel.updateDoubleWallById(
                                    wallModel?.id!!,
                                    it1
                                )

                                doubleWallpaperViewmodel.updateValueById(  wallModel?.id!!,
                                    it1)
                            }
                            findNavController().popBackStack()

                        }
                    }
                }
            )

        }

        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initObservers(){

        val file = requireContext().filesDir
        val video = File(file,"video.mp4")
        sharedViewModel.chargingAnimationResponseList.observe(viewLifecycleOwner){wallpaper ->
            if (wallpaper.isNotEmpty()){

                wallModel = wallpaper[0]

                startProgressCoroutine()
                Log.e("TAG", "initObservers: $wallpaper")

                if (BlurView.filePathBattery == ""){
                    downloadVideo(AdConfig.BASE_URL_DATA + "/doublewallpaper/"+wallpaper[0]?.hd_url1,video)

                }
                getBitmapFromGlide(AdConfig.BASE_URL_DATA + "/doublewallpaper/" +wallpaper[0]?.hd_url2)


            }
        }
    }

    private fun startProgressCoroutine() {
        job = lifecycleScope.launch(Dispatchers.Main) {
            var progress = 0
            repeat((totalTimeInMillis / intervalInMillis).toInt()) {
                progress = ((it * intervalInMillis * 100) / totalTimeInMillis).toInt()
                if (isAdded){
                    binding.progress.progress = progress
                    binding.progressTxt.text =
                        progress.toString() + "%"
                }
                delay(intervalInMillis)
            }

            if (isAdded){
                binding.progress.progress = 100 // Ensure progress reaches 100% even if not exact
                binding.progressTxt.text = "100%"
                onProgressComplete(100)
            }


        }
    }

    private fun onProgressComplete(progress: Int) {
        if (progress >= 100) {
            if (isAdded){
                binding.buttonApplyWallpaper.visibility =  View.VISIBLE

                animationJob?.cancel()
                binding.loadingTxt.text = "Download Successfull"
                job?.cancel()
            }

        }
    }





    private fun getBitmapFromGlide(url:String){
        Glide.with(requireContext()).asBitmap().load(url)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    bitmap = resource

                    if (isAdded){
                        val blurImage: Bitmap = BlurView.blurImage(requireContext(), bitmap!!)!!
                        binding.backImage.setImageBitmap(blurImage)
                    }



                }
                override fun onLoadCleared(placeholder: Drawable?) {
                } })
    }



    private fun downloadVideo(url: String, destinationFile: File) {

//        val file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val file = requireContext().filesDir
        val fileName = System.currentTimeMillis().toString() + ".json"

        val filepath = File(file,fileName)

        BlurView.filePathBattery = filepath.path
        BlurView.fileNameBattery = fileName
        MySharePreference.setFileName(requireContext(),fileName)
        Log.e("TAG", "downloadVideo: "+ BlurView.fileName )

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
                        Log.e(TAG, "onError: ", )
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
        if (animationJob?.isActive == true){
            animationJob?.cancel()
        }

        _binding = null
    }
}