package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxReward
import com.applovin.mediation.MaxRewardedAdListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.DialogUnlockOrWatchAdsBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentFullScreenImageViewBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MaxRewardAds
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.PostData
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.RetrofitInstance
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.ApiService
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.SetMostDownloaded
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyWallpaperManager
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.concurrent.Executors
import javax.inject.Inject

@AndroidEntryPoint
class FullScreenImageViewFragment : DialogFragment() {

    private var _binding: FragmentFullScreenImageViewBinding? = null
    private val binding get() = _binding!!
    var responseData: CatResponse? = null

    private var bitmap: Bitmap? = null
    private val myExecutor = Executors.newSingleThreadExecutor()
    private val myHandler = Handler(Looper.getMainLooper())
    val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var myActivity: MainActivity
    private var fromStr: String = ""

    @Inject
    lateinit var appDatabase: AppDatabase

    private lateinit var myWallpaperManager: MyWallpaperManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFullScreenImageViewBinding.inflate(inflater, container, false)

        return binding.root
    }


    private fun initDataObservers() {
        sharedViewModel.selectedCat.observe(viewLifecycleOwner) { catResponse ->
            catResponse?.let { response ->
                responseData = response
                sharedViewModel.wallpaperFromType.observe(viewLifecycleOwner) { from ->
                    fromStr = from
                    val url = if (from == "Vip") {
                        AdConfig.BASE_URL_DATA + "/rewardwallpaper/hd/" + responseData?.hd_image_url
                    } else {
                        AdConfig.BASE_URL_DATA + "/staticwallpaper/hd/" + responseData?.hd_image_url
                    }
                    if (isAdded) {
                        getBitmapFromGlide(url)
                    }
                    if (isAdded) {
                        setImageToView()
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //binding.fullViewImage.isEnabled = false
        myActivity = activity as MainActivity
        initDataObservers()
        myWallpaperManager = MyWallpaperManager(requireContext(), requireActivity())

        binding.closeButton.setOnClickListener {
            findNavController().popBackStack()
        }
        setEvents()
    }

    fun setEvents() {
        binding.favouriteButton.setOnClickListener {
            binding.favouriteButton.isEnabled = false
            // Send request to the server without changing UI immediately
            addFavourite(requireContext(), binding.favouriteButton)
        }

        binding.downloadWallpaper.setOnClickListener {
            Log.e("TAG", "functionality: inside click")
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
                        mSaveMediaToStorage(bitmap)
                    } else {
                        getUserIdDialog()
                    }
                }
            } else {
                if (AdConfig.ISPAIDUSER) {
                    mSaveMediaToStorage(bitmap)
                } else {
                    getUserIdDialog()
                }
            }
        }

        binding.buttonApplyWallpaper.setOnClickListener {
            if (bitmap != null) {
                if (responseData?.unlockimges == true) {
                    openPopupMenu(responseData!!)
                } else {
                    if (AdConfig.ISPAIDUSER) {
                        openPopupMenu(responseData!!)
                    } else {
                        unlockDialog()
                    }
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.your_image_not_fetched_properly), Toast.LENGTH_SHORT
                ).show()
            }
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
            dialog.dismiss()
            MaxRewardAds.showRewardedAd(requireActivity(), object : MaxRewardedAdListener {
                override fun onAdLoaded(p0: MaxAd) {}

                override fun onAdDisplayed(p0: MaxAd) {}

                override fun onAdHidden(p0: MaxAd) {}

                override fun onAdClicked(p0: MaxAd) {}

                override fun onAdLoadFailed(p0: String, p1: MaxError) {}

                override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {}

                override fun onUserRewarded(p0: MaxAd, p1: MaxReward) {
                    if (bitmap != null) {
                        responseData?.unlockimges = true

                        responseData?.id?.let { it1 ->
                            appDatabase.wallpapersDao().updateLocked(
                                true,
                                it1
                            )
                        }
                        openPopupMenu(responseData!!)

                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.your_image_not_fetched_properly), Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        }
        bindingDialog.upgradeButton.setOnClickListener {
            findNavController().navigate(R.id.IAPFragment)
        }
        bindingDialog.cancelDialog.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun getUserIdDialog() {
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
            MaxRewardAds.showRewardedAd(requireActivity(), object : MaxRewardedAdListener {
                override fun onAdLoaded(p0: MaxAd) {}

                override fun onAdDisplayed(p0: MaxAd) {}

                override fun onAdHidden(p0: MaxAd) {}

                override fun onAdClicked(p0: MaxAd) {}

                override fun onAdLoadFailed(p0: String, p1: MaxError) {}

                override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {}

                override fun onUserRewarded(p0: MaxAd, p1: MaxReward) {
                    mSaveMediaToStorage(bitmap)
                }
            })
        }

        dismiss?.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun getBitmapFromGlide(url: String) {
        Glide.with(requireContext()).asBitmap().load(url)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    bitmap = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }

    private fun setImageToView() {
        val url = if (fromStr == "Vip") {
            AdConfig.BASE_URL_DATA + "/rewardwallpaper/hd/" + responseData!!.hd_image_url
        } else {
            AdConfig.BASE_URL_DATA + "/staticwallpaper/hd/" + responseData!!.hd_image_url
        }
        Glide.with(requireContext())
            .load(url)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    if (isFragmentVisibleAndBindingAvailable()) {
                        binding.fullViewImage.isEnabled = true
                        binding.bottomMenu.visibility = View.VISIBLE
                    }
                    return false
                }
            })
            .into(binding.fullViewImage)
    }

    private fun isFragmentVisibleAndBindingAvailable(): Boolean {
        return isResumed && view != null && _binding != null
    }

    private fun openPopupMenu(model: CatResponse) {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.set_wallpaper_menu, null)
        dialog.setContentView(view)
        val width = WindowManager.LayoutParams.MATCH_PARENT
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.setLayout(width, height)
        val params = (view.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        (view.parent as View).setBackgroundColor(Color.TRANSPARENT)
        dialog.setCancelable(false)
        val buttonHome = view.findViewById<Button>(R.id.buttonHome)
        val buttonLock = view.findViewById<Button>(R.id.buttonLock)
        val buttonBothScreen = view.findViewById<Button>(R.id.buttonBothScreen)
        val closeButton = view.findViewById<RelativeLayout>(R.id.closeButton)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }
        buttonHome.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    myWallpaperManager.homeScreen(bitmap!!)
                    withContext(Dispatchers.Main) {
                        if (isAdded) {
                            interstitialAdWithToast(
                                getString(R.string.set_successfully_on_home_screen),
                                dialog
                            )
                        }
                    }
                    setDownloaded(model)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        buttonLock.setOnClickListener {
            if (isAdded) {
                interstitialAdWithToast(
                    getString(R.string.set_successfully_on_lock_screen),
                    dialog
                )
            }
            setDownloaded(model)
        }
        buttonBothScreen.setOnClickListener {
            myExecutor.execute {
                myWallpaperManager.homeAndLockScreen(bitmap!!)
            }
            myHandler.post {

                if (isAdded) {
                    interstitialAdWithToast(
                        getString(R.string.set_successfully_on_both),
                        dialog
                    )
                }

            }
            setDownloaded(model)
        }
        dialog.show()
    }

    private fun interstitialAdWithToast(message: String, dialog: BottomSheetDialog) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        dialog.dismiss()
    }

    private fun mSaveMediaToStorage(bitmap: Bitmap?) {
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requireContext().contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES + File.separator + "Wallpapers"
                    )
                }
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                MediaScannerConnection.scanFile(
                    requireContext(),
                    arrayOf(imageUri?.path),
                    arrayOf("image/jpeg"), // Adjust the MIME type as per your image type
                    null
                )
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_DENIED
            ) {
                ActivityCompat.requestPermissions(
                    requireContext() as Activity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
            } else {
                val imagesDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + File.separator + "Wallpapers")
                if (!imagesDir.exists()) {
                    imagesDir.mkdir()
                }
                val image = File(imagesDir, filename)
                fos = FileOutputStream(image)
            }
        }
        fos?.use {
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(requireContext(), "Saved to Gallery", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setDownloaded(model: CatResponse) {
        lifecycleScope.launch(Dispatchers.IO) {
            val retrofit = RetrofitInstance.getInstance()
            val apiService = retrofit.create(SetMostDownloaded::class.java)
            val call = apiService.setDownloaded(model.id.toString())
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}
            })
        }
    }

    private fun addFavourite(context: Context, favouriteButton: ImageView) {
        val retrofit = RetrofitInstance.getInstance()
        val apiService = retrofit.create(ApiService::class.java)
        val postData =
            PostData(MySharePreference.getDeviceID(context)!!, responseData?.id.toString())

        val call = apiService.postData(postData)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val message = response.body()?.string()
                    Log.d("Favourite", "onResponse:getLikedResponse: $message")

                    try {
                        val jsonObject = JSONObject(message) // Parse JSON
                        val message = jsonObject.getString("message") // Extract "message" field

                        if (message == "Liked") {
                            responseData?.liked = true
                            favouriteButton.setImageResource(R.drawable.button_like_selected)
                        } else {
                            responseData?.liked = false
                            favouriteButton.setImageResource(R.drawable.button_like)
                        }
                    } catch (e: JSONException) {
                        Log.e("Favourite", "JSON Parsing Error: ${e.message}")
                    }
                } else {
                    Toast.makeText(context, "Error updating favorite", Toast.LENGTH_SHORT).show()
                }
                favouriteButton.isEnabled = true
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
                favouriteButton.isEnabled = true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}