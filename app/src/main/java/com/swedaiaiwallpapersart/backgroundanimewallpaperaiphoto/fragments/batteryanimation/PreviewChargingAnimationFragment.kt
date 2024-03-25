package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.batteryanimation

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.app.WallpaperManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bmik.android.sdk.IkmSdkController
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.CommonAdsListenerAdapter
import com.bmik.android.sdk.listener.CustomSDKAdsListenerAdapter
import com.bmik.android.sdk.listener.CustomSDKRewardedAdsListener
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.DialogUnlockOrWatchAdsBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentPreviewChargingAnimationBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.ChargingAnimModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.remote.EndPointsInterface
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.FavoruiteLiveWallpaperBody
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.LiveWallpaperModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.RetrofitInstance
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.LikeLiveWallpaper
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.service.LiveWallpaperService
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.BlurView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class PreviewChargingAnimationFragment : Fragment() {
    private var _binding:FragmentPreviewChargingAnimationBinding ?= null
    private val binding get() = _binding!!

    val sharedViewModel: SharedViewModel by activityViewModels()

    private var livewallpaper: ChargingAnimModel? = null
    var adPosition = 0

    private lateinit var myActivity : MainActivity

    @Inject
    lateinit var webApiInterface: EndPointsInterface

    @Inject
    lateinit var appDatabase: AppDatabase



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPreviewChargingAnimationBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        myActivity = activity as MainActivity
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

        sharedViewModel.chargingAnimationResponseList.observe(viewLifecycleOwner) { wallpaper ->
            if (wallpaper.isNotEmpty()) {

                Log.e("TAG", "initObservers: $wallpaper")

                livewallpaper = wallpaper[0]
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
            if (isDrawOverlaysPermissionGranted(requireContext())){

            }else{

                findNavController().navigate(R.id.chargingAnimationPermissionFragment)
            }
        }

        binding.toolbar.setOnClickListener {
            findNavController().popBackStack(R.id.homeTabsFragment, false)
        }

        backHandle()
    }

    fun isDrawOverlaysPermissionGranted(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

//    fun setWallpaper(){
//
//        if (isAdded){
//            val file = requireContext().filesDir
//            val filepath = File(file, BlurView.fileName)
//            val newFile = File(file, "video.mp4")
//
//            val info = WallpaperManager.getInstance(requireContext().applicationContext).wallpaperInfo
//            if (info == null || info.packageName != requireContext().packageName) {
//                IkmSdkController.setEnableShowResumeAds(true)
//                filepath.renameTo(newFile)
//                BlurView.filePath = newFile.path
//                LiveWallpaperService.setToWallPaper(requireContext())
//
//                try {
//                    lifecycleScope.launch {
//                        val requestBody = mapOf("imageid" to livewallpaper?.id)
//
//                        webApiInterface.postDownloadedLive(requestBody)
//                    }
//                }catch (e:Exception){
//                    e.printStackTrace()
//                }
//
//
//            } else {
//                showSimpleDialog(
//                    requireContext(),
//                    "Do you want to change the live wallpaper? The applied wallpaper will be removed",
//                    ""
//                )
//
//
//            }
//
//        }
//    }


//    fun showSimpleDialog(context: Context, title: String, message: String) {
//        val builder = AlertDialog.Builder(context)
//        builder.setTitle(title)
//            .setMessage(message)
//
//        builder.setPositiveButton(
//            "Yes"
//        ) { p0, p1 ->
//            val file = requireContext().filesDir
//            val filepath = File(file, BlurView.fileName)
//            val newFile = File(file, "video.mp4")
//
//            if (newFile.exists()) {
//                if (newFile.delete()) {
//                    Log.e("TAG", "showSimpleDialog:fileDelete ")
//                }
//            }
//            BlurView.filePath = newFile.path
//            if (filepath.renameTo(newFile)) {
//                BlurView.filePath = newFile.path
//
//                notifyFileNameChanged(requireContext(), filepath.path, newFile.path)
//                Log.e("TAG", "showSimpleDialog: renamed")
//                IkmSdkController.setEnableShowResumeAds(true)
//                LiveWallpaperService.setToWallPaper(requireContext())
//                try {
//                    lifecycleScope.launch {
//                        val requestBody = mapOf("imageid" to livewallpaper?.id)
//
//                        webApiInterface.postDownloadedLive(requestBody)
//                    }
//                }catch (e:Exception){
//                    e.printStackTrace()
//                }
//
//            } else {
//                Log.e("TAG", "showSimpleDialog: failed")
//            }
//
//
//
//            p0.dismiss()
//        }
//
//        builder.setNegativeButton(
//            "No"
//        ) { p0, p1 ->
//
//            p0.dismiss()
//
//        }
//
//        // Create and show the dialog
//        val dialog = builder.create()
//        dialog.show()
//    }


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
                        if (isAdded){
                            SDKBaseController.getInstance().showInterstitialAds(
                                requireActivity(),
                                "viewlistwallscr_download_item_inter",
                                "viewlistwallscr_download_item_inter",
                                showLoading = true,
                                adsListener = object : CommonAdsListenerAdapter() {
                                    override fun onAdsShowFail(errorCode: Int) {
                                        if (isAdded){
                                            Toast.makeText(requireContext(),"Ad not available, Please try again later",
                                                Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    override fun onAdsDismiss() {
                                        copyFiles(source, destination)

//                                        try {
//                                            lifecycleScope.launch {
//                                                val requestBody = mapOf("imageid" to livewallpaper?.id)
//
//                                                webApiInterface.postDownloadedLive(requestBody)
//                                            }
//                                        }catch (e:Exception){
//                                            e.printStackTrace()
//                                        }

                                    }
                                }
                            )
                        }

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


    override fun onResume() {
        super.onResume()
        setWallpaperOnView()
    }


    private fun setWallpaperOnView() {
        if (isAdded){
            binding.liveWallpaper.setMediaController(null)
            binding.liveWallpaper.setVideoPath(BlurView.filePath)
            binding.liveWallpaper.setOnCompletionListener(MediaPlayer.OnCompletionListener {
                binding.liveWallpaper.start()
            })

            binding.liveWallpaper.setOnPreparedListener { mediaPlayer ->
                // Adjust video looping here if needed
                mediaPlayer.isLooping = true
            }

            binding.liveWallpaper.start()
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}