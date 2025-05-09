package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.livewallpaper

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bmik.android.sdk.IkmSdkController
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.CommonAdsListenerAdapter
import com.bmik.android.sdk.listener.CustomSDKAdsListenerAdapter
import com.bmik.android.sdk.listener.CustomSDKRewardedAdsListener
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentLiveWallpaperPreviewBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.FavoruiteLiveWallpaperBody
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.LiveWallpaperModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.RetrofitInstance
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.LikeLiveWallpaper
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.service.LiveWallpaperService
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.BlurView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


class LiveWallpaperPreviewFragment : Fragment() {

    private var _binding: FragmentLiveWallpaperPreviewBinding? = null
    private val binding get() = _binding!!

    val sharedViewModel: SharedViewModel by activityViewModels()

    private var livewallpaper: LiveWallpaperModel? = null
    var adPosition = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLiveWallpaperPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        SDKBaseController.getInstance()
            .loadBannerAds(
                requireActivity(),
                binding.adsWidget as? ViewGroup,
                "searchscr_bottom",
                " searchscr_bottom", object : CustomSDKAdsListenerAdapter() {
                    override fun onAdsLoaded() {
                        super.onAdsLoaded()
                        Log.e("*******ADS", "onAdsLoaded: Banner loaded")
                    }

                    override fun onAdsLoadFail() {
                        super.onAdsLoadFail()
                        Log.e("*******ADS", "onAdsLoaded: Banner failed")
                    }
                }
            )
        initObservers()
        setWallpaperOnView()

        setEvents()

    }

    private fun initObservers() {

        sharedViewModel.liveWallpaperResponseList.observe(viewLifecycleOwner) { wallpaper ->
            if (wallpaper.isNotEmpty()) {

                Log.e("TAG", "initObservers: $wallpaper")

                livewallpaper = wallpaper[0]

                if (livewallpaper?.liked == true) {
                    binding.setLiked.setImageResource(R.drawable.button_like_selected)
                } else {
                    binding.setLiked.setImageResource(R.drawable.button_like)
                }
            }
        }


        sharedViewModel.liveAdPosition.observe(viewLifecycleOwner) {
            adPosition = it
        }
    }

    private fun backHandle() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack(R.id.homeTabsFragment, false)
                }
            })
    }


    private fun setEvents() {
        binding.buttonApplyWallpaper.setOnClickListener {

            if (adPosition % 2 != 0){
                setWallpaper()
            }else{
                SDKBaseController.getInstance().showInterstitialAds(
                    requireActivity(),
                    "mainscr_live_tab_click_item",
                    "mainscr_live_tab_click_item",
                    showLoading = true,
                    adsListener = object : CommonAdsListenerAdapter() {
                        override fun onAdsShowFail(errorCode: Int) {
                            setWallpaper()

                            //do something
                        }

                        override fun onAdsDismiss() {
                            setWallpaper()

                        }

                        override fun onAdsShowTimeout() {
                            setWallpaper()
                        }
                    }
                )
            }





        }

        binding.toolbar.setOnClickListener {
            findNavController().popBackStack(R.id.homeTabsFragment, false)
        }

        binding.setLiked.setOnClickListener {
            binding.setLiked.isEnabled = false
            if (livewallpaper?.liked == true) {
                livewallpaper?.liked = false
                binding.setLiked.setImageResource(R.drawable.button_like)
            } else {
                livewallpaper?.liked = true
                binding.setLiked.setImageResource(R.drawable.button_like_selected)
            }
            addFavourite(requireContext(), binding.setLiked)
        }

        backHandle()


        binding.downloadWallpaper.setOnClickListener {

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_DENIED
                ) {
                    Log.e("TAG", "functionality: inside click permission")
                    ActivityCompat.requestPermissions(
                        requireContext() as Activity,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        1
                    )
                } else {
                    Log.e("TAG", "functionality: inside click dialog")
                    getUserIdDialog()
                }
            } else {
                getUserIdDialog()
            }

        }
    }


    fun setWallpaper(){
        val file = requireContext().filesDir
        val filepath = File(file, BlurView.fileName)
        val newFile = File(file, "video.mp4")

        val info = WallpaperManager.getInstance(requireContext().applicationContext).wallpaperInfo
        if (info == null || info.packageName != requireContext().packageName) {
            IkmSdkController.setEnableShowResumeAds(false)
            filepath.renameTo(newFile)
            BlurView.filePath = newFile.path
            LiveWallpaperService.setToWallPaper(requireContext())
        } else {
            showSimpleDialog(
                requireContext(),
                "Do you want to change the live wallpaper? The applied wallpaper will be removed",
                ""
            )


        }
    }


    fun showSimpleDialog(context: Context, title: kotlin.String, message: kotlin.String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
            .setMessage(message)

        builder.setPositiveButton(
            "Yes"
        ) { p0, p1 ->
            val file = requireContext().filesDir
            val filepath = File(file, BlurView.fileName)
            val newFile = File(file, "video.mp4")

            if (newFile.exists()) {
                if (newFile.delete()) {
                    Log.e("TAG", "showSimpleDialog:fileDelete ")
                }
            }
            BlurView.filePath = newFile.path
            if (filepath.renameTo(newFile)) {
                BlurView.filePath = newFile.path

                notifyFileNameChanged(requireContext(), filepath.path, newFile.path)
                Log.e("TAG", "showSimpleDialog: renamed")
                IkmSdkController.setEnableShowResumeAds(false)
                LiveWallpaperService.setToWallPaper(requireContext())


            } else {
                Log.e("TAG", "showSimpleDialog: failed")
            }



            p0.dismiss()
        }

        builder.setNegativeButton(
            "No"
        ) { p0, p1 ->

            p0.dismiss()

        }

        // Create and show the dialog
        val dialog = builder.create()
        dialog.show()
    }


    fun notifyFileNameChanged(context: Context?, oldFilePath: String?, newFilePath: String?) {
        val oldFile = File(oldFilePath)
        val newFile = File(newFilePath)

        // Make sure both old and new files exist before proceeding
        if (oldFile.exists() && newFile.exists()) {
            // Get the MIME type of the file
            val mimeType = getMimeType(newFilePath!!)

            // Notify the system about the file name change
            MediaScannerConnection.scanFile(
                context,
                arrayOf(oldFile.absolutePath, newFile.absolutePath),
                arrayOf(mimeType, mimeType)
            ) { path, uri ->
                // File scan completed

                Toast.makeText(
                    context,
                    "renaming a file may take sometime to take effect.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getMimeType(filePath: String): String? {
        val extension = MimeTypeMap.getFileExtensionFromUrl(filePath)
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }

    private fun getUserIdDialog() {

        val source = File(BlurView.filePath)
        val file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val destination = File(file, BlurView.fileName)
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.rewarded_ad_dialog)
        val width = WindowManager.LayoutParams.MATCH_PARENT
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.setLayout(width, height)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        val getReward = dialog.findViewById<LinearLayout>(R.id.buttonGetReward)
        val dismiss = dialog.findViewById<TextView>(R.id.noThanks)

        getReward?.setOnClickListener {
            dialog.dismiss()
            SDKBaseController.getInstance().showRewardedAds(
                requireActivity(),
                "viewlistwallscr_download_item",
                "viewlistwallscr_download_item",
                object :
                    CustomSDKRewardedAdsListener {
                    override fun onAdsDismiss() {
                        Log.e("********ADS", "onAdsDismiss: ")
                    }

                    override fun onAdsRewarded() {
                        Log.e("********ADS", "onAdsRewarded: ")

                        copyFiles(source, destination)

                    }

                    override fun onAdsShowFail(errorCode: Int) {
                        SDKBaseController.getInstance().showInterstitialAds(
                            requireActivity(),
                            "viewlistwallscr_download_item_inter",
                            "viewlistwallscr_download_item_inter",
                            showLoading = true,
                            adsListener = object : CommonAdsListenerAdapter() {
                                override fun onAdsShowFail(errorCode: Int) {
                                    if (isAdded){
                                        Toast.makeText(requireContext(),"Ad not available, Please try again later",Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onAdsDismiss() {
                                    copyFiles(source, destination)
                                }
                            }
                        )
                        Log.e("********ADS", "onAdsShowFail: ")

                    }

                })
        }

        dismiss?.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    fun copyFiles(source: File, destination: File) {
        try {
            val inputStream = FileInputStream(source)
            val outputStream = FileOutputStream(destination)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()

            Toast.makeText(requireContext(), "Wallpaper downloaded", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(requireContext(), "Download failed", Toast.LENGTH_SHORT).show()
            // Handle error
        }
    }

    fun isLiveWallpaperSupported(context: Context): Boolean {
        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
        val activities =
            context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return activities.isNotEmpty()
    }

    private fun addFavourite(
        context: Context,
        favouriteButton: ImageView
    ) {
        val retrofit = RetrofitInstance.getInstance()
        val apiService = retrofit.create(LikeLiveWallpaper::class.java)
        val postData = FavoruiteLiveWallpaperBody(
            MySharePreference.getDeviceID(context)!!,
            livewallpaper?.id.toString()
        )
        val call = apiService.postLike(postData)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val message = response.body()?.string()
                    if (message == "Liked") {
//                        livewallpaper.id = true
                        favouriteButton.setImageResource(R.drawable.button_like_selected)
                    } else {
                        favouriteButton.setImageResource(R.drawable.button_like)
//                        arrayList[position]?.liked = false
                    }
                    favouriteButton.isEnabled = true
                } else {
                    favouriteButton.isEnabled = true
                    Toast.makeText(context, "onResponse error", Toast.LENGTH_SHORT).show()
                    favouriteButton.setImageResource(R.drawable.button_like)
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "onFailure error", Toast.LENGTH_SHORT).show()
                favouriteButton.isEnabled = true
            }
        })
    }


    override fun onResume() {
        super.onResume()
        setWallpaperOnView()
    }


    private fun setWallpaperOnView() {

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


    fun isLiveWallpaperServiceRunning(context: Context): Boolean {
        val wallpaperManager = WallpaperManager.getInstance(context)
        val wallpaperInfo = wallpaperManager.wallpaperInfo
        return wallpaperInfo != null && wallpaperInfo.serviceName == LiveWallpaperService::class.java.name
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}