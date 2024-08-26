package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.livewallpaper

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.service.wallpaper.WallpaperService
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ikame.android.sdk.IKSdkController
import com.ikame.android.sdk.data.dto.pub.IKAdError
import com.ikame.android.sdk.format.intertial.IKInterstitialAd
import com.ikame.android.sdk.format.rewarded.IKRewardAd
import com.ikame.android.sdk.listener.pub.IKLoadAdListener
import com.ikame.android.sdk.listener.pub.IKShowAdListener
import com.ikame.android.sdk.listener.pub.IKShowRewardAdListener
import com.ikame.android.sdk.listener.pub.IKShowWidgetAdListener
import com.ikame.android.sdk.tracking.IKTrackingHelper
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.DialogUnlockOrWatchAdsBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentLiveWallpaperPreviewBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.AdEventListener
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MyApp
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.remote.EndPointsInterface
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.FavoruiteLiveWallpaperBody
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.LiveWallpaperModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.RetrofitInstance
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.LikeLiveWallpaper
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.service.BroadcastReceiver
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.service.LiveWallpaperService
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.BlurView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants.Companion.checkAppOpen
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.ForegroundWorker.Companion.TAG
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.UnknownHostException
import javax.inject.Inject

@AndroidEntryPoint
class LiveWallpaperPreviewFragment : Fragment(), AdEventListener {

//    private val wallpaperSetReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent) {
//            Log.d("WallpaperSetReceiver", "Broadcast received: ${intent.action}")
//            when (intent.action) {
//                LiveWallpaperService.ACTION_WALLPAPER_SET_SUCCESS -> {
//                    lifecycleScope.launch {
//                        checkWallpaperActive()
//                    }
//                }
//
//                LiveWallpaperService.ACTION_WALLPAPER_SET_FAILURE -> {
//                    Toast.makeText(
//                        requireContext(),
//                        "Failed to set live wallpaper",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//    }

    private var _binding: FragmentLiveWallpaperPreviewBinding? = null
    private val binding get() = _binding!!

    val sharedViewModel: SharedViewModel by activityViewModels()

    private var livewallpaper: LiveWallpaperModel? = null
    private var adPosition = 0

    private lateinit var myActivity: MainActivity

    @Inject
    lateinit var webApiInterface: EndPointsInterface

    @Inject
    lateinit var appDatabase: AppDatabase

    private var checkWallpaper = false

    private val rewardAd = IKRewardAd()
    val interAd = IKInterstitialAd()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLiveWallpaperPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        (myActivity.application as MyApp).registerAdEventListener(this)

//        val intentFilter = IntentFilter().apply {
//            addAction(LiveWallpaperService.ACTION_WALLPAPER_SET_SUCCESS)
//            addAction(LiveWallpaperService.ACTION_WALLPAPER_SET_FAILURE)
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            requireContext().registerReceiver(wallpaperSetReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)
//        } else {
//            requireContext().registerReceiver(wallpaperSetReceiver, intentFilter)
//        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myActivity = activity as MainActivity

        if (!AdConfig.ISPAIDUSER) {

            loadRewardAd()

            interAd.attachLifecycle(this.lifecycle)
// Load ad with a specific screen ID, considered as a unitId
            interAd.loadAd("viewlivewallscr_click_set", object : IKLoadAdListener {
                override fun onAdLoaded() {
                    // Ad loaded successfully
                }

                override fun onAdLoadFail(error: IKAdError) {
                    // Handle ad load failure
                }
            })

            binding.adsView.attachLifecycle(lifecycle)
            binding.adsView.loadAd("viewlivewallscr_bottom", object : IKShowWidgetAdListener {
                override fun onAdShowed() {}
                override fun onAdShowFail(error: IKAdError) {

                    Log.d(TAG, "onAdsShowFailLivePreviewBanner: $error")
//                    binding.adsView?.visibility = View.GONE
                }

            })
        } else {
            binding.adsView.visibility = View.GONE
        }

        initObservers()
        setWallpaperOnView()

        setEvents()


        if (isAdded) {
            sendTracking(
                "screen_active",
                Pair("action_type", "screen"),
                Pair("action_name", "SetLiveWallScr_View")
            )
        }

    }

    private fun loadRewardAd() {
        rewardAd.attachLifecycle(this.lifecycle)
        // Load ad with a specific screen ID, considered as a unitId
        rewardAd.loadAd("viewlistwallscr_item_vip_reward", object : IKLoadAdListener {
            override fun onAdLoaded() {
                // Ad loaded successfully
            }

            override fun onAdLoadFail(error: IKAdError) {
                // Handle ad load failure
            }
        })
    }

    private fun sendTracking(
        eventName: String,
        vararg param: Pair<String, String?>
    ) {
        IKTrackingHelper.sendTracking(eventName, *param)
    }

    private fun initObservers() {

        sharedViewModel.liveWallpaperResponseList.observe(viewLifecycleOwner) { wallpaper ->
            if (wallpaper.isNotEmpty()) {

                Log.e("TAG", "initObservers: $wallpaper")

                livewallpaper = wallpaper[0]

                if (livewallpaper?.liked == true) {
                    Log.e("TAG", "initObservers: liked" + livewallpaper?.liked)
                    binding.setLiked.setImageResource(R.drawable.button_like_selected)
                } else {
                    binding.setLiked.setImageResource(R.drawable.button_like)
                    Log.e("TAG", "initObservers: unliked" + livewallpaper?.liked)
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
            if (isAdded) {
                sendTracking(
                    "click_button",
                    Pair("action_type", "button"),
                    Pair("action_name", "SetLiveWallScr_SetBt_Click")
                )
                sendTracking("typewallpaper_used", Pair("typewallpaper", "Live"))
                sendTracking("category_used", Pair("category", "Live ${livewallpaper?.catname}"))
            }
            Log.e("TAG", "setEvents: $livewallpaper")

            if (livewallpaper?.unlocked == false) {
                if (AdConfig.ISPAIDUSER) {
                    setWallpaper()
                } else {
                    unlockDialog()
                }
            } else {
                if (adPosition % 2 != 0) {
                    setWallpaper()
                } else {
                    var shouldShowInterAd = true

                    if (AdConfig.avoidPolicyRepeatingInter == 1 && Constants.checkInter) {
                        if (isAdded) {
                            Constants.checkInter = false
                            setWallpaper()
                            shouldShowInterAd = false // Skip showing the ad for this action
                        }
                    }

                    if (AdConfig.avoidPolicyOpenAdInter == 1 && checkAppOpen) {
                        if (isAdded) {
                            checkAppOpen = false
                            setWallpaper()
                            Log.e(TAG, "app open showed")
                            shouldShowInterAd = false // Skip showing the ad for this action
                        }
                    }

                    if (shouldShowInterAd) {
                        showInterAd() // Show the interstitial ad if no conditions were met
                    }
//                    if (AdConfig.avoidPolicyOpenAdInter == 1 && checkAppOpen){
//                        if (isAdded){
//                            checkAppOpen = false
//                            setWallpaper()
//                            Log.e("TAG", "app open showed: ", )
//                        }
//                    }else{
//                        showInterAd()
//                    }


                }
            }


        }

        binding.toolbar.setOnClickListener {
            if (isAdded) {
                sendTracking(
                    "click_button",
                    Pair("action_type", "button"),
                    Pair("action_name", "SetLiveWallScr_BackBt_Click")
                )
            }
            Constants.checkInter = false
            checkAppOpen = false
            findNavController().popBackStack(R.id.homeTabsFragment, false)
        }

        if (livewallpaper?.liked == true) {
            binding.setLiked.setImageResource(R.drawable.button_like)
        } else {
            binding.setLiked.setImageResource(R.drawable.button_like_selected)
        }

        binding.setLiked.setOnClickListener {
            if (isAdded) {
                sendTracking(
                    "click_button",
                    Pair("action_type", "button"),
                    Pair("action_name", "SetLiveWallScr_FavoriteBt_Click")
                )
            }
            binding.setLiked.isEnabled = false
            if (livewallpaper?.liked == true) {
                livewallpaper?.liked = false
                binding.setLiked.setImageResource(R.drawable.button_like)
                Toast.makeText(requireContext(), "Removed from favorites", Toast.LENGTH_SHORT)
                    .show()
            } else {
                livewallpaper?.liked = true
                binding.setLiked.setImageResource(R.drawable.button_like_selected)
                Toast.makeText(requireContext(), "Added to favorites", Toast.LENGTH_SHORT).show()
            }
            Constants.checkInter = false
            Constants.checkAppOpen = false
            addFavourite(requireContext(), binding.setLiked)
        }

        backHandle()


        binding.downloadWallpaper.setOnClickListener {

            if (isAdded) {
                sendTracking(
                    "click_button",
                    Pair("action_type", "button"),
                    Pair("action_name", "SetLiveWallScr_SaveBt_Click")
                )
            }
            val source = File(BlurView.filePath)
            val file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            val destination = File(file, BlurView.fileName)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_DENIED
                ) {
                    Log.e("TAG", "functionality: inside click permission")
                    ActivityCompat.requestPermissions(
                        myActivity,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        1
                    )
                } else {
                    Log.e("TAG", "functionality: inside click dialog")
                    if (AdConfig.ISPAIDUSER) {
                        copyFiles(source, destination)

                        try {
                            lifecycleScope.launch {
                                val requestBody = mapOf("imageid" to livewallpaper?.id)

                                webApiInterface.postDownloadedLive(requestBody)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } catch (e: UnknownHostException) {
                            e.printStackTrace()
                        }
                    } else {

                        getUserIdDialog()
                    }
                }
            } else {
                if (AdConfig.ISPAIDUSER) {
                    copyFiles(source, destination)

                    try {
                        lifecycleScope.launch {
                            val requestBody = mapOf("imageid" to livewallpaper?.id)

                            webApiInterface.postDownloadedLive(requestBody)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } catch (e: UnknownHostException) {
                        e.printStackTrace()
                    }
                } else {

                    getUserIdDialog()
                }
            }
            Constants.checkInter = false
            Constants.checkAppOpen = false
        }
    }

    private fun showInterAd() {
        interAd.showAd(
            requireActivity(),
            "viewlivewallscr_click_set",
            adListener = object : IKShowAdListener {
                override fun onAdsShowFail(error: IKAdError) {
                    if (isAdded) {
                        Log.d(TAG, "onAdsShowFailLivePreview: $error")
                        setWallpaper()
                    }
                }

                override fun onAdsDismiss() {
                    Constants.checkInter = true
                    setWallpaper()
                }
            }
        )
    }

    private fun unlockDialog() {
        val dialog = Dialog(requireContext())
        val bindingDialog =
            DialogUnlockOrWatchAdsBinding.inflate(LayoutInflater.from(requireContext()))
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(bindingDialog.root)
        val width = WindowManager.LayoutParams.MATCH_PARENT
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog?.window!!.setLayout(width, height)
        dialog?.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCancelable(false)

        if (AdConfig.iapScreenType == 0) {
            bindingDialog.upgradeButton.visibility = View.GONE
            bindingDialog.orTxt.visibility = View.INVISIBLE
            bindingDialog.dividerEnd.visibility = View.INVISIBLE
            bindingDialog.dividerStart.visibility = View.INVISIBLE
        }
//        var getReward = dialog?.findViewById<LinearLayout>(R.id.buttonGetReward)


        bindingDialog.watchAds?.setOnClickListener {
            dialog.dismiss()

            rewardAd.showAd(
                requireActivity(),
                "viewlistwallscr_item_vip_reward",
                adListener = object : IKShowRewardAdListener {
                    override fun onAdsRewarded() {
                        livewallpaper?.unlocked = true
                        livewallpaper?.id?.let { it1 ->
                            appDatabase.liveWallpaperDao().updateLocked(
                                true,
                                it1.toInt()
                            )
                        }
                    }

                    override fun onAdsShowFail(error: IKAdError) {
                        if (isAdded) {
                            Toast.makeText(
                                requireContext(),
                                "Ad not available, Try again",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onAdsDismiss() {
                        loadRewardAd()
                    }
                }
            )

        }

        bindingDialog.upgradeButton?.setOnClickListener {
            dialog.dismiss()
            findNavController().navigate(R.id.IAPFragment)
        }
        bindingDialog.cancelDialog?.setOnClickListener {
            dialog?.dismiss()
        }

        dialog?.show()
    }

    fun setWallpaper() {
        if (isAdded) {
            val context = requireContext()
            val file = context.filesDir
            val filepath = File(file, BlurView.fileName)
            val newFile = File(file, "video.mp4")

            val info = WallpaperManager.getInstance(context.applicationContext).wallpaperInfo

            if (info == null || info.packageName != context.packageName) {
                // Enabling ads
                IKSdkController.setEnableShowResumeAds(true)

                // Show ProgressBar
                binding.progressBar.visibility = View.VISIBLE

                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    // Rename the file in the background
                    filepath.renameTo(newFile)
                    BlurView.filePath = newFile.path
                    Log.d(TAG, "setWallpaper: ${newFile.path}")
                    // Set wallpaper in background
                    LiveWallpaperService.setToWallPaper(context, true)
                    checkWallpaper = true

                    // Post download info
                    try {
                        val requestBody = mapOf("imageid" to livewallpaper?.id)
                        webApiInterface.postDownloadedLive(requestBody)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } catch (e: UnknownHostException) {
                        e.printStackTrace()
                    }

                    // Hide ProgressBar on the main thread
                    withContext(Dispatchers.Main) {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            } else {
                showSimpleDialog(
                    context,
                    "Do you want to change the live wallpaper? The applied wallpaper will be removed",
                    ""
                )
            }
        }
    }


    private fun showSimpleDialog(context: Context, title: String, message: String) {
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
            Log.d(TAG, "showSimpleDialog123: ${newFile.path}")
            BlurView.filePath = newFile.path

            Log.d(TAG, "showSimpleDialog: ${newFile.exists()}")
            if (filepath.renameTo(newFile)) {
                BlurView.filePath = newFile.path

                notifyFileNameChanged(requireContext(), filepath.path, newFile.path)
                Log.e("TAG", "showSimpleDialog: renamed")
                IKSdkController.setEnableShowResumeAds(true)
                LiveWallpaperService.setToWallPaper(requireContext(), false)
                checkWallpaper = true
                lifecycleScope.launch {
                    checkWallpaperActive()
                }
                try {
                    lifecycleScope.launch {
                        val requestBody = mapOf("imageid" to livewallpaper?.id)

                        webApiInterface.postDownloadedLive(requestBody)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } catch (e: UnknownHostException) {
                    e.printStackTrace()
                }

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

    private fun notifyFileNameChanged(
        context: Context?,
        oldFilePath: String?,
        newFilePath: String?
    ) {
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


        rewardAd.showAd(
            requireActivity(),
            "viewlistwallscr_item_vip_reward",
            adListener = object : IKShowRewardAdListener {
                override fun onAdsRewarded() {
                    copyFiles(source, destination)
                }

                override fun onAdsShowFail(error: IKAdError) {
                    if (isAdded) {

                        interAd.showAd(
                            requireActivity(),
                            "mainscr_live_tab_click_item",
                            adListener = object : IKShowAdListener {
                                override fun onAdsShowFail(error: IKAdError) {
                                    if (isAdded) {
                                        Toast.makeText(
                                            requireContext(),
                                            "Ad not available, Please try again later",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                                override fun onAdsDismiss() {
                                    copyFiles(source, destination)

                                    try {
                                        lifecycleScope.launch {
                                            val requestBody = mapOf("imageid" to livewallpaper?.id)

                                            webApiInterface.postDownloadedLive(requestBody)
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    } catch (e: UnknownHostException) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                        )

                    }
                }

                override fun onAdsDismiss() {
                    loadRewardAd()
                }
            }
        )

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
                    Log.e("TAG", "onResponse: $message")
//                    if (message == "Liked") {
////                        livewallpaper.id = true
//                        favouriteButton.setImageResource(R.drawable.button_like_selected)
//                    } else {
//                        favouriteButton.setImageResource(R.drawable.button_like)
////                        arrayList[position]?.liked = false
//                    }
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

        if (checkWallpaper){
            lifecycleScope.launch {
                checkWallpaperActive()
            }
        }
        setWallpaperOnView()
    }

    private fun checkWallpaperActive() {
        if (isAdded) {
            val wallpaperComponent =
                ComponentName(requireContext(), LiveWallpaperService::class.java)
            val wallpaperManager = WallpaperManager.getInstance(requireContext())

            lifecycleScope.launch {
                val currentWallpaperComponent = wallpaperManager.wallpaperInfo?.component

                Log.d("LiveWallpaper", "Current wallpaper component: $currentWallpaperComponent")
                Log.d("LiveWallpaper", "Expected wallpaper component: $wallpaperComponent")

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // Implement the if statement directly without checking the component
                    checkWallpaper = false
                    Log.d("LiveWallpaper", "Live wallpaper set successfully")
                    if (isAdded) {
                        MySharePreference.firstLiveWallpaper(requireContext(), true)
                    }
                    findNavController().popBackStack(R.id.homeTabsFragment, false)

                    if (isAdded) {
                        sendTracking(
                            "screen_active",
                            Pair("action_type", "Toast"),
                            Pair("action_name", "SetLiveWallScr_SuccessToast_Click")
                        )
                    }

                    Toast.makeText(
                        requireContext(),
                        "Wallpaper set successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // For other Android versions, check the component as before
                    if (currentWallpaperComponent != null && currentWallpaperComponent == wallpaperComponent) {
                        checkWallpaper = false
                        Log.d("LiveWallpaper", "Live wallpaper set successfully")
                        if (isAdded) {
                            MySharePreference.firstLiveWallpaper(requireContext(), true)
                        }
                        findNavController().popBackStack(R.id.homeTabsFragment, false)

                        if (isAdded) {
                            sendTracking(
                                "screen_active",
                                Pair("action_type", "Toast"),
                                Pair("action_name", "SetLiveWallScr_SuccessToast_Click")
                            )
                        }

                        Toast.makeText(
                            requireContext(),
                            "Wallpaper set successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Log.e("LiveWallpaper", "Failed to set live wallpaper")
                        Toast.makeText(
                            requireContext(),
                            "Failed to set live wallpaper",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun setWallpaperOnView() {
        if (isAdded) {
            binding.liveWallpaper.setMediaController(null)
            binding.liveWallpaper.setVideoPath(BlurView.filePath)

            binding.liveWallpaper.setOnCompletionListener(OnCompletionListener {
                if (view != null && isAdded) {
                    binding.liveWallpaper.start()
                }
            })

            binding.liveWallpaper.setOnPreparedListener { mediaPlayer ->
                mediaPlayer.isLooping = true
                mediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
            }

            binding.liveWallpaper.start()
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
//        requireContext().unregisterReceiver(wallpaperSetReceiver)
    }

    override fun onAdDismiss() {
        checkAppOpen = true
        Log.e("TAG", "app open dismissed: ")
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