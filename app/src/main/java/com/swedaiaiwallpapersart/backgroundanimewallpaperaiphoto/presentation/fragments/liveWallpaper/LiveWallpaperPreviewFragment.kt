package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.fragments.liveWallpaper

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
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
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.navigation.fragment.findNavController
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxReward
import com.applovin.mediation.MaxRewardedAdListener
import com.applovin.mediation.ads.MaxAdView
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.DialogUnlockOrWatchAdsBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentLiveWallpaperPreviewBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MaxRewardAds
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.LiveWallpaperModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.activity.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.service.LiveWallpaperService
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.BlurView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.Constants.Companion.checkAppOpen
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.Constants.Companion.checkInter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.viewmodels.DataFromRoomViewmodel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.UnknownHostException
import javax.inject.Inject

@AndroidEntryPoint
class LiveWallpaperPreviewFragment : Fragment() {

    private var _binding: FragmentLiveWallpaperPreviewBinding? = null
    private val binding get() = _binding!!

    val sharedViewModel: SharedViewModel by activityViewModels()
    private var livewallpaper: LiveWallpaperModel? = null

    private var favLivewallpaper: LiveWallpaperModel? = null
    private var adPosition = 0
    private lateinit var myActivity: MainActivity
    private var liveComingFrom: String = ""
    private lateinit var adView: MaxAdView
    private val viewmodel: DataFromRoomViewmodel by viewModels()

    @Inject
    lateinit var appDatabase: AppDatabase
    private var checkWallpaper = false
    val TAG = "LivePREVIEW"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLiveWallpaperPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun createBannerAd() {
        adView = MaxAdView(AdConfig.applovinAndroidBanner, requireContext())

        // Stretch to the width of the screen for banners to be fully functional
        val width = ViewGroup.LayoutParams.MATCH_PARENT

        // Banner height on phones and tablets is 50 and 90, respectively
        val heightPx = resources.getDimensionPixelSize(R.dimen.banner_height)

        adView.layoutParams = FrameLayout.LayoutParams(width, heightPx)

        // Set background color for banners to be fully functional
        adView.setBackgroundColor(resources.getColor(R.color.new_main_background))

        val rootView = binding.BannerLivePreview
        rootView.addView(adView)

        // Load the ad
        adView.loadAd()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myActivity = activity as MainActivity
        initObservers()
        setEvents()
        liveComingFrom = MySharePreference.getLiveFrom(requireContext())
    }

    private fun initObservers() {

        sharedViewModel.liveWallpaperResponseList.observe(viewLifecycleOwner) { wallpaper ->
            if (wallpaper.isNotEmpty()) {

                Log.e("Favourite", "initObservers: $wallpaper")

                livewallpaper = wallpaper[0]

                if (livewallpaper?.liked == true) {
                    Log.e("Favourite", "initObservers: liked" + livewallpaper?.liked)
                    binding.setLiked.setImageResource(R.drawable.button_like_selected)
                } else {
                    binding.setLiked.setImageResource(R.drawable.button_like)
                    Log.e("Favourite", "initObservers: unliked" + livewallpaper?.liked)
                }
            }
        }

        sharedViewModel.favLiveWallpaperResponseList.observe(viewLifecycleOwner) { wallpaper ->
            if (wallpaper.isNotEmpty()) {
                Log.e("Favourite", "initObservers: $wallpaper")
                favLivewallpaper = wallpaper[0]
                if (favLivewallpaper?.liked == true) {
                    Log.e("Favourite", "initObservers: liked" + favLivewallpaper?.liked)
                    binding.setLiked.setImageResource(R.drawable.button_like_selected)
                } else {
                    binding.setLiked.setImageResource(R.drawable.button_like)
                    Log.e("Favourite", "initObservers: unliked" + favLivewallpaper?.liked)
                }
            }
        }

        sharedViewModel.liveAdPosition.observe(viewLifecycleOwner) {
            adPosition = it
        }
    }

    private fun backHandle() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (liveComingFrom == "Favourite") {
                        findNavController().popBackStack(R.id.favouriteFragment, false)
                    } else {
                        findNavController().popBackStack(R.id.homeTabsFragment, false)
                    }
                }
            })
    }

    private fun setEvents() {
        backHandle()

        binding.buttonApplyWallpaper.setOnClickListener {
            Log.e("TAG", "setEvents: $livewallpaper")

            if (livewallpaper?.unlocked == false) {
                if (AdConfig.ISPAIDUSER) {
                    setWallpaper()
                } else {
                    unlockDialog()
                }
            } else {
                setWallpaper()
            }
        }
        binding.toolbar.setOnClickListener {
            checkInter = false
            checkAppOpen = false
            findNavController().popBackStack(R.id.homeTabsFragment, false)
        }
        if (livewallpaper?.liked == true) {
            binding.setLiked.setImageResource(R.drawable.button_like)
        } else {
            binding.setLiked.setImageResource(R.drawable.button_like_selected)
        }

        binding.setLiked.setOnClickListener {
            liveComingFrom = MySharePreference.getLiveFrom(requireContext())
            Log.d("Favourite", "setEvents:liveComingFrom: $liveComingFrom")
            if (liveComingFrom == "Favourite") {
                binding.setLiked.isEnabled = false
                binding.setLiked.setImageResource(R.drawable.button_like)
                Toast.makeText(requireContext(), "Removed from favorites", Toast.LENGTH_SHORT)
                    .show()
                Log.d("Favourite", "setEvents: Id ${favLivewallpaper?.id}")
                lifecycleScope.launch {
                    viewmodel.updateLiveFavourite(false, Integer.parseInt(favLivewallpaper?.id!!))
                }
                MySharePreference.setLiveComingFrom(requireContext(), "FragmentLive")
                findNavController().popBackStack(R.id.favouriteFragment, false)
            } else {
                binding.setLiked.isEnabled = false
                if (livewallpaper?.liked == true) {
                    livewallpaper?.liked = false
                    binding.setLiked.setImageResource(R.drawable.button_like)
                    Toast.makeText(requireContext(), "Removed from favorites", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    livewallpaper?.liked = true
                    binding.setLiked.setImageResource(R.drawable.button_like_selected)
                    Toast.makeText(requireContext(), "Added to favorites", Toast.LENGTH_SHORT)
                        .show()
                    Log.d("Favourite", "setEvents: Id ${livewallpaper?.id}")
                    //addFavourite(requireContext(), binding.setLiked)
                    viewmodel.updateLiveFavourite(true, Integer.parseInt(livewallpaper?.id!!))
                }
                checkInter = false
                checkAppOpen = false

            }
        }

        /*binding.setLiked.setOnClickListener {
            if (liveComingFrom == "Favourite") {
                binding.setLiked.isEnabled = false
                binding.setLiked.setImageResource(R.drawable.button_like)
                Toast.makeText(requireContext(), "Removed from favorites", Toast.LENGTH_SHORT)
                    .show()
                Log.d(
                    "Favourite",
                    "setEvents: favLivewallpaper Id ${favLivewallpaper?.id}\nLiveWallpaperId: ${livewallpaper?.id}"
                )
                lifecycleScope.launch {
                    viewmodel.updateLiveFavourite(false, Integer.parseInt(favLivewallpaper?.id!!))
                }
                MySharePreference.setLiveComingFrom(requireContext(), "FragmentLive")
            } else {
                // Ensure the wallpaper object is not null
                val wallpaper = livewallpaper
                if (wallpaper?.id.isNullOrEmpty()) return@setOnClickListener

                val wallpaperId = wallpaper!!.id.toIntOrNull() ?: return@setOnClickListener

                // Toggle liked state
                val isLiked = wallpaper.liked == true
                wallpaper.liked = !isLiked

                // Update UI immediately
                binding.setLiked.setImageResource(
                    if (!isLiked) R.drawable.button_like_selected else R.drawable.button_like
                )

                // Update DB accordingly
                lifecycleScope.launch {
                    if (!isLiked) {
                        viewmodel.updateLiveFavourite(true, wallpaperId)
                    } else {
                        viewmodel.updateLiveFavourite(false, wallpaperId)
                    }
                }

                // Prevent rapid double clicks
                binding.setLiked.isEnabled = false
                binding.setLiked.postDelayed({ binding.setLiked.isEnabled = true }, 300)
            }
        }*/
        /*binding.setLiked.setOnClickListener {
            liveComingFrom = MySharePreference.getLiveFrom(requireContext())
            Log.d("Favourite", "setEvents:liveComingFrom: $liveComingFrom")
            if (liveComingFrom == "Favourite") {
                binding.setLiked.isEnabled = false
                binding.setLiked.setImageResource(R.drawable.button_like)
                Toast.makeText(requireContext(), "Removed from favorites", Toast.LENGTH_SHORT)
                    .show()
                Log.d("Favourite", "setEvents: Id ${favLivewallpaper?.id}")
                lifecycleScope.launch {
                    viewmodel.updateLiveFavourite(false, Integer.parseInt(favLivewallpaper?.id!!))
                }
                MySharePreference.setLiveComingFrom(requireContext(), "FragmentLive")
            } else {
                binding.setLiked.isEnabled = false
                if (livewallpaper?.liked == true) {
                    livewallpaper?.liked = false
                    binding.setLiked.setImageResource(R.drawable.button_like)
                    Toast.makeText(requireContext(), "Removed from favorites", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    livewallpaper?.liked = true
                    binding.setLiked.setImageResource(R.drawable.button_like_selected)
                    Toast.makeText(requireContext(), "Added to favorites", Toast.LENGTH_SHORT)
                        .show()
                    Log.d("Favourite", "setEvents: Id ${livewallpaper?.id}")
                    lifecycleScope.launch {
                        viewmodel.updateLiveFavourite(true, Integer.parseInt(livewallpaper?.id!!))
                    }
                    //addFavourite(requireContext(), binding.setLiked)
                }
                checkInter = false
                checkAppOpen = false

            }
        }*/
        binding.downloadWallpaper.setOnClickListener {
            val source = File(BlurView.filePath)
            val file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            val destination = File(file, BlurView.fileName)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                if (ContextCompat.checkSelfPermission(
                        requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_DENIED
                ) {
                    Log.e("TAG", "functionality: inside click permission")
                    ActivityCompat.requestPermissions(
                        myActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1
                    )
                } else {
                    Log.e("TAG", "functionality: inside click dialog")
                    if (AdConfig.ISPAIDUSER) {
                        copyFiles(source, destination)
                        try {
                            lifecycleScope.launch {
                                val requestBody = mapOf("imageid" to livewallpaper?.id)

                                //webApiInterface.postDownloadedLive(requestBody)
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
                            //webApiInterface.postDownloadedLive(requestBody)
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
            checkInter = false
            checkAppOpen = false
        }
    }

    private fun unlockDialog() {
        val dialog = Dialog(requireContext())
        val bindingDialog =
            DialogUnlockOrWatchAdsBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(bindingDialog.root)
        val width = WindowManager.LayoutParams.MATCH_PARENT
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.setLayout(width, height)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        if (AdConfig.iapScreenType == 0) {
            bindingDialog.upgradeButton.visibility = View.GONE
            bindingDialog.orTxt.visibility = View.INVISIBLE
            bindingDialog.dividerEnd.visibility = View.INVISIBLE
            bindingDialog.dividerStart.visibility = View.INVISIBLE
        }

        bindingDialog.watchAds.setOnClickListener {
            MaxRewardAds.showRewardedAd(requireActivity(), object : MaxRewardedAdListener {
                override fun onAdLoaded(p0: MaxAd) {

                }

                override fun onAdDisplayed(p0: MaxAd) {

                }

                override fun onAdHidden(p0: MaxAd) {

                }

                override fun onAdClicked(p0: MaxAd) {

                }

                override fun onAdLoadFailed(p0: String, p1: MaxError) {

                }

                override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {

                }

                override fun onUserRewarded(p0: MaxAd, p1: MaxReward) {
                    setWallpaper()
                }
            })
            dialog.dismiss()
        }
        bindingDialog.upgradeButton.setOnClickListener {
            dialog.dismiss()
            findNavController().navigate(R.id.IAPFragment)
        }
        bindingDialog.cancelDialog.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun setWallpaper() {
        if (isAdded) {
            val context = requireContext()
            val file = context.filesDir
            val filepath = File(file, BlurView.fileName)
            val newFile = File(file, "video.mp4")

            val info = WallpaperManager.getInstance(context.applicationContext).wallpaperInfo

            if (info == null || info.packageName != context.packageName) {
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
        builder.setTitle(title).setMessage(message)

        /* builder.setPositiveButton(
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
         }*/

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
        context: Context?, oldFilePath: String?, newFilePath: String?
    ) {
        val oldFile = File(oldFilePath!!)
        val newFile = File(newFilePath!!)

        // Make sure both old and new files exist before proceeding
        if (oldFile.exists() && newFile.exists()) {
            // Get the MIME type of the file
            val mimeType = getMimeType(newFilePath)

            // Notify the system about the file name change
            MediaScannerConnection.scanFile(
                context,
                arrayOf(oldFile.absolutePath, newFile.absolutePath),
                arrayOf(mimeType, mimeType)
            ) { _, _ ->

                Toast.makeText(
                    context, "renaming a file may take sometime to take effect.", Toast.LENGTH_SHORT
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

        getReward.setOnClickListener {
            Log.d("LivePreview", "getUserIdDialog: Clicking")
            MaxRewardAds.showRewardedAd(requireActivity(), object : MaxRewardedAdListener {
                override fun onAdLoaded(p0: MaxAd) {

                }

                override fun onAdDisplayed(p0: MaxAd) {

                }

                override fun onAdHidden(p0: MaxAd) {

                }

                override fun onAdClicked(p0: MaxAd) {

                }

                override fun onAdLoadFailed(p0: String, p1: MaxError) {

                }

                override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {

                }

                override fun onUserRewarded(p0: MaxAd, p1: MaxReward) {
                    copyFiles(source, destination)
                }
            })
        }
        dismiss?.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun copyFiles(source: File, destination: File) {
        try {
            val inputStream = FileInputStream(source)
            val outputStream = FileOutputStream(destination)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()

            Toast.makeText(requireContext(), "Wallpaper downloaded", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(requireContext(), "Download failed", Toast.LENGTH_SHORT).show()
        }
    }

    @UnstableApi
    override fun onResume() {
        super.onResume()

        setWallpaperOnView()

        if (checkWallpaper) {
            lifecycleScope.launch {
                checkWallpaperActive()
            }
        }
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

                if (currentWallpaperComponent != null && currentWallpaperComponent == wallpaperComponent) {
                    // The live wallpaper is set successfully, perform your action here
                    checkWallpaper = false
                    Log.d("LiveWallpaper", "Live wallpaper set successfully")
                    if (isAdded) {
                        MySharePreference.firstLiveWallpaper(requireContext(), true)
                    }

                    Toast.makeText(
                        requireContext(), "Wallpaper set successfully", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // The live wallpaper is not set successfully
                    Log.e("LiveWallpaper", "Failed to set live wallpaper")
                    Toast.makeText(
                        requireContext(), "Failed to set live wallpaper", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    @UnstableApi
    private fun setWallpaperOnView() {
        if (isAdded) {
            val renderersFactory =
                DefaultRenderersFactory(requireContext()).setEnableDecoderFallback(true) // Fallback to software decoding if hardware fails

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                renderersFactory.setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF)
            } else {
                renderersFactory.setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
            }
            val loadControl = DefaultLoadControl.Builder().setBufferDurationsMs(
                DefaultLoadControl.DEFAULT_MIN_BUFFER_MS,
                DefaultLoadControl.DEFAULT_MAX_BUFFER_MS,
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
            ).setPrioritizeTimeOverSizeThresholds(true).build()

            val trackSelector = DefaultTrackSelector(requireContext())
            trackSelector.setParameters(trackSelector.buildUponParameters().setMaxVideoSizeSd())

            val exoPlayer = ExoPlayer.Builder(
                requireContext(), renderersFactory
            ).setLoadControl(loadControl).setTrackSelector(trackSelector)
                .setUseLazyPreparation(true).build()

            // Bind the player to the PlayerView (your binding.liveWallpaper)
            binding.liveWallpaper.player = exoPlayer

            // Prepare the media item
            val mediaItem = MediaItem.fromUri(BlurView.filePath)
            exoPlayer.setMediaItem(mediaItem)

            // Set event listeners for playback and error handling
            exoPlayer.addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    val errorMessage = when (error.errorCode) {
                        PlaybackException.ERROR_CODE_DECODING_FAILED -> "The video is malformed."
                        PlaybackException.ERROR_CODE_REMOTE_ERROR -> "Remote playback error."
                        else -> "An unknown error occurred. Error code: ${error.message}"
                    }
                    Log.d(TAG, "onPlayerError: $errorMessage")
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        exoPlayer.seekTo(0) // Restart the video when it ends
                        exoPlayer.playWhenReady = true
                    }
                }
            })

            // Enable looping and scale mode
            exoPlayer.repeatMode = Player.REPEAT_MODE_ALL
            exoPlayer.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                exoPlayer.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
            }
            // Prepare and start playback
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.liveWallpaper.player?.release() // Release the player
        binding.liveWallpaper.player = null
        _binding = null
    }

}