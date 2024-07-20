package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.livewallpaper

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
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.DownloadListener
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.CommonAdsListenerAdapter
import com.bmik.android.sdk.listener.CustomSDKAdsListenerAdapter
import com.bmik.android.sdk.tracking.SDKTrackingController
import com.bmik.android.sdk.widgets.IkmWidgetAdLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentDownloadLiveWallpaperBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.BlurView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


class DownloadLiveWallpaperFragment : Fragment() {

    private var _binding: FragmentDownloadLiveWallpaperBinding?= null

    private val binding get() = _binding!!

    val sharedViewModel: SharedViewModel by activityViewModels()

    private var bitmap: Bitmap? = null

    private var animationJob: Job? = null

    val TAG = "DOWNLOAD_SCREEN"

    var showAd :Boolean? = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDownloadLiveWallpaperBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showAd = arguments?.getBoolean("adShowed")

        Log.e(TAG, "onViewCreated: $showAd")

        if (AdConfig.ISPAIDUSER){
            binding.adsView.visibility = View.GONE
        }else{
            loadAd()
        }
        AndroidNetworking.initialize(requireContext())

        setEvents()
        initObservers()


        if (isAdded){
            sendTracking("screen_active",Pair("action_type", "screen"), Pair("action_name", "Downloadscr_View"))
        }
    }

    private fun sendTracking(
        eventName: String,
        vararg param: Pair<String, String?>
    )
    {
        SDKTrackingController.trackingAllApp(requireContext(), eventName, *param)
    }

    fun loadAd(){
        val adLayout = LayoutInflater.from(activity).inflate(
            R.layout.large_native_layout,
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

        binding.adsView.loadAd(requireActivity(),"downloadscr_native_bottom","downloadscr_native_bottom",
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
//                        binding.adsView.reCallLoadAd(this)
                        binding.adsView.visibility = View.VISIBLE
                    }

                    Log.e("TAG", "onAdsLoaded: native loaded" )
                }
            }
        )
    }


    fun setEvents(){
        binding.buttonApplyWallpaper.setOnClickListener {
            if (isAdded){
                sendTracking("click_button",Pair("action_type", "button"), Pair("action_name", "Downloadscr_Previewbt_click"))
            }

            if (AdConfig.ISPAIDUSER){
                navigateToPreview()
            }else{
                if (showAd == true){
                    navigateToPreview()
                }else{
                    SDKBaseController.getInstance().showInterstitialAds(
                        requireActivity(),
                        "downloadscr_set_click",
                        "downloadscr_set_click",
                        showLoading = true,
                        adsListener = object : CommonAdsListenerAdapter() {
                            override fun onAdsShowFail(errorCode: Int) {
                                Log.e("********ADS", "onAdsShowFail: "+errorCode )
                                navigateToPreview()
                                //do something
                            }

                            override fun onAdsDismiss() {
                                Log.e(TAG, "onAdsDismiss: ", )
                                navigateToPreview()
                            }
                        }
                    )
                }
            }
        }

        binding.toolbar.setOnClickListener {

            if (isAdded){
                sendTracking("click_button",Pair("action_type", "button"), Pair("action_name", "Downloadscr_Backbutton_click"))
            }
            findNavController().popBackStack()
        }
    }

    private fun navigateToPreview() {
        if (isAdded) {
            findNavController().navigate(R.id.liveWallpaperPreviewFragment)

        }
    }

    private fun initObservers(){

//        val file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)

        val file = requireContext().filesDir
        val video = File(file,"video.mp4")
        sharedViewModel.liveWallpaperResponseList.observe(viewLifecycleOwner){wallpaper ->
            if (wallpaper.isNotEmpty()){

                Log.e("TAG", "initObservers: $wallpaper")

                if (BlurView.filePath == ""){
                    Log.e(TAG, "initObservers: "+wallpaper[0] )
                    downloadVideo(AdConfig.BASE_URL_DATA + "/livewallpaper/"+wallpaper[0].livewallpaper_url,video,wallpaper[0].videoSize)

                }
                getBitmapFromGlide(AdConfig.BASE_URL_DATA + "/livewallpaper/"+wallpaper[0].thumnail_url)


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



    private fun downloadVideo(url: String, destinationFile: File,size:Float) {
        Log.e(TAG, "downloadVideo: "+url )

//        val file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val file = requireContext().filesDir
        val fileName = System.currentTimeMillis().toString() + ".mp4"

        val filepath = File(file,fileName)

        BlurView.filePath = filepath.path
        BlurView.fileName = fileName
        MySharePreference.setFileName(requireContext(),fileName)
        Log.e("TAG", "downloadVideo: "+BlurView.fileName )

        val totalSize = (size * 1024).toLong()
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
                    lifecycleScope.launch(Dispatchers.Main) {

                        val percentage = (bytesDownloaded * 100 / totalSize).toInt()
                        Log.e("TAG", "downloadVideo: $percentage")

                        if (isAdded){
                            val currentCount = (bytesDownloaded * 100 / totalSize)
                            if (currentCount <= 100){
                                binding.progressTxt.text =
                                    currentCount.toString() + "%"
                            }else{
                                Log.e(TAG, "downloadVideo: $currentCount", )
                            }
                            binding.progress.progress =percentage
                        }


                    }
                }
                .startDownload(object : DownloadListener {
                    override fun onDownloadComplete() {
                        lifecycleScope.launch(Dispatchers.Main) {
                            if (isAdded){
                                binding.progressTxt.text = "100%"
                                binding.progress.progress = 100
                                binding.buttonApplyWallpaper.visibility = View.VISIBLE

                                animationJob?.cancel()

                                binding.loadingTxt.text = "Download Successfull"


                                if (isAdded){
                                    sendTracking("click_button",Pair("action_type", "button"), Pair("action_name", "Downloadscr_Successful"))
                                }
                            }




                        }
                    }

                    override fun onError(error: ANError?) {
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



    // Function for saving video file directly to the destination file
    private fun saveVideoToFile(inputStream: InputStream, destinationFile: File) {
        val outputStream = FileOutputStream(destinationFile)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (animationJob?.isActive == true){
            animationJob?.cancel()
        }

        _binding =  null
    }
}