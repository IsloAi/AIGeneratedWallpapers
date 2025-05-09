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
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.CommonAdsListenerAdapter
import com.bmik.android.sdk.listener.CustomSDKRewardedAdsListener
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
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentFullScreenImageViewBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.PostData
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.RetrofitInstance
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.ApiService
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.SetMostDownloaded
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyWallpaperManager
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.concurrent.Executors

class FullScreenImageViewFragment : DialogFragment() {

    private var _binding:FragmentFullScreenImageViewBinding ?= null
    private val binding get() = _binding!!
    var responseData:CatResponse ?= null

    private var bitmap: Bitmap? = null
    private val myExecutor = Executors.newSingleThreadExecutor()
    private val myHandler = Handler(Looper.getMainLooper())
    val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var myWallpaperManager : MyWallpaperManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFullScreenImageViewBinding.inflate(inflater,container,false)

        return binding.root
    }


    private fun initDataObservers(){
        sharedViewModel.selectedCat.observe(viewLifecycleOwner){
            if (it != null){
                responseData = it
                setImageToView()

                getBitmapFromGlide(responseData!!.hd_image_url!!)
            }

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fullViewImage.isEnabled = false
        initDataObservers()
        myWallpaperManager = MyWallpaperManager(requireContext(),requireActivity())

        binding.closeButton.setOnClickListener {
            findNavController().popBackStack()
        }

        setEvents()



    }


    fun setEvents(){
        binding.favouriteButton.setOnClickListener {
            binding.favouriteButton.isEnabled = false
            if(responseData?.liked==true){
                responseData?.liked = false
                binding.favouriteButton.setImageResource(R.drawable.button_like)
            }else{
                responseData?.liked = true
                binding.favouriteButton.setImageResource(R.drawable.button_like_selected)
            }
            addFavourite(requireContext(),binding.favouriteButton)


        }

        binding.downloadWallpaper.setOnClickListener{
            Log.e("TAG", "functionality: inside click", )
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2){
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    Log.e("TAG", "functionality: inside click permission", )
                    ActivityCompat.requestPermissions(requireContext() as Activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                }else{
                    Log.e("TAG", "functionality: inside click dialog", )
                    getUserIdDialog()
                }
            }else{
                getUserIdDialog()
            }

        }

        binding.buttonApplyWallpaper.setOnClickListener {
//           if(arrayList[position]?.gems==0 || arrayList[position]?.unlockimges==true){
            if(bitmap != null){

                openPopupMenu(responseData!!)
            }else{
                Toast.makeText(requireContext(),
                    getString(R.string.your_image_not_fetched_properly), Toast.LENGTH_SHORT).show()
            }
//           }else{
//               Toast.makeText(requireContext(), "Please first buy your wallpaper", Toast.LENGTH_SHORT).show()
//           }

        }
    }


    private fun getUserIdDialog() {
        val dialog = Dialog(requireContext())
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.rewarded_ad_dialog)
        val width = WindowManager.LayoutParams.MATCH_PARENT
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog?.window!!.setLayout(width, height)
        dialog?.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCancelable(false)
        var getReward = dialog?.findViewById<LinearLayout>(R.id.buttonGetReward)
        var dismiss = dialog?.findViewById<TextView>(R.id.noThanks)

        getReward?.setOnClickListener {
            dialog?.dismiss()
            SDKBaseController.getInstance().showRewardedAds(requireActivity(),"viewlistwallscr_download_item","viewlistwallscr_download_item",object:
                CustomSDKRewardedAdsListener {
                override fun onAdsDismiss() {
                    Log.e("********ADS", "onAdsDismiss: ")
                }

                override fun onAdsRewarded() {
                    Log.e("********ADS", "onAdsRewarded: ")
//                    if(arrayList[position]?.gems==0 || arrayList[position]?.unlockimges==true){
                    mSaveMediaToStorage(bitmap)
//                    }else{
//                        Toast.makeText(requireContext(), "Please first buy your wallpaper", Toast.LENGTH_SHORT).show()
//
//                    }


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
//                                if(arrayList[position]?.gems==0 || arrayList[position]?.unlockimges==true){
//                                mSaveMediaToStorage(bitmap)
//                                }else{
//                                    Toast.makeText(requireContext(), "Please first buy your wallpaper", Toast.LENGTH_SHORT).show()
//
//                                }
                                //do something
                            }

                            override fun onAdsDismiss() {
//                                if(arrayList[position]?.gems==0 || arrayList[position]?.unlockimges==true){
                                mSaveMediaToStorage(bitmap)
//                                }else{
//                                    Toast.makeText(requireContext(), "Please first buy your wallpaper", Toast.LENGTH_SHORT).show()
//
//                                }
                            }
                        }
                    )
                    Log.e("********ADS", "onAdsShowFail: ")

                }

            })
        }

        dismiss?.setOnClickListener {
            dialog?.dismiss()
        }

        dialog?.show()
    }
    private fun getBitmapFromGlide(url:String){
        Glide.with(requireContext()).asBitmap().load(url)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    bitmap = resource

                }
                override fun onLoadCleared(placeholder: Drawable?) {
                } })
    }


    private fun setImageToView(){
        Glide.with(requireContext())
            .load(responseData!!.hd_image_url)
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
                    if (isFragmentVisibleAndBindingAvailable()){
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
        val params = (view.getParent() as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        (view.getParent() as View).setBackgroundColor(Color.TRANSPARENT)
        dialog.setCancelable(false)
        val buttonHome = view.findViewById<Button>(R.id.buttonHome)
        val buttonLock = view.findViewById<Button>(R.id.buttonLock)
        val buttonBothScreen = view.findViewById<Button>(R.id.buttonBothScreen)
        val closeButton = view.findViewById<RelativeLayout>(R.id.closeButton)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }
        buttonHome.setOnClickListener {
                SDKBaseController.getInstance().showInterstitialAds(
                    requireActivity(),
                    "viewlistwallscr_setdilog_set_button",
                    "viewlistwallscr_setdilog_set_button",
                    showLoading = true,
                    adsListener = object : CommonAdsListenerAdapter() {
                        override fun onAdsShowFail(errorCode: Int) {
                            myExecutor.execute {myWallpaperManager.homeScreen(bitmap!!)}
                            interstitialAdWithToast(
                                        getString(R.string.set_successfully_on_home_screen),
                                        dialog
                                    )
                            setDownloaded(model)
                            Log.e("********ADS", "onAdsShowFail: "+errorCode )
                            //do something
                        }

                        override fun onAdsDismiss() {
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


                                }catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                )



        }
        buttonLock.setOnClickListener {


                SDKBaseController.getInstance().showInterstitialAds(
                    requireActivity(),
                    "viewlistwallscr_setdilog_set_button",
                    "viewlistwallscr_setdilog_set_button",
                    showLoading = true,
                    adsListener = object : CommonAdsListenerAdapter() {
                        override fun onAdsShowFail(errorCode: Int) {
                            Log.e("********ADS", "onAdsShowFail: "+errorCode )
                            myExecutor.execute {
                                myWallpaperManager.lockScreen(bitmap!!)
                            }
                                    if (isAdded) {
                                        interstitialAdWithToast(
                                            getString(R.string.set_successfully_on_lock_screen),
                                            dialog
                                        )
                                    }




                            setDownloaded(model)
                            //do something
                        }

                        override fun onAdsDismiss() {
                            myExecutor.execute {
                                myWallpaperManager.lockScreen(bitmap!!)
                            }

                                    if (isAdded) {
                                        interstitialAdWithToast(
                                            getString(R.string.set_successfully_on_lock_screen),
                                            dialog
                                        )
                                    }
                            setDownloaded(model)

                        }
                    }
                )






        }
        buttonBothScreen.setOnClickListener {
                SDKBaseController.getInstance().showInterstitialAds(
                    requireActivity(),
                    "viewlistwallscr_setdilog_set_button",
                    "viewlistwallscr_setdilog_set_button",
                    showLoading = true,
                    adsListener = object : CommonAdsListenerAdapter() {
                        override fun onAdsShowFail(errorCode: Int) {
                            Log.e("********ADS", "onAdsShowFail: "+errorCode )
                            myExecutor.execute {
                                myWallpaperManager.homeAndLockScreen(bitmap!!)
                            }
                            myHandler.post {
                                if (isAdded){
                                    interstitialAdWithToast(getString(R.string.set_successfully_on_both),dialog)

                                }
                            }
                            setDownloaded(model)
                            //do something
                        }

                        override fun onAdsDismiss() {
                            myExecutor.execute {
                                myWallpaperManager.homeAndLockScreen(bitmap!!)
                            }
                            myHandler.post {

                                    if (isAdded){
                                        interstitialAdWithToast(getString(R.string.set_successfully_on_both),dialog)
                                    }

                            }
                            setDownloaded(model)
                        }
                    }
                )



        }
        dialog.show()
    }

    private fun interstitialAdWithToast (message: String, dialog: BottomSheetDialog){
        Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show()
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
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "Wallpapers")
                }
                val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                MediaScannerConnection.scanFile(
                    requireContext(),
                    arrayOf(imageUri?.path),
                    arrayOf("image/jpeg"), // Adjust the MIME type as per your image type
                    null
                )
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(requireContext() as Activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            } else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+ File.separator + "Wallpapers")
                if(!imagesDir.exists()){
                    imagesDir.mkdir()
                }
                val image = File(imagesDir, filename)
                fos = FileOutputStream(image)
            }
        }
        fos?.use {
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(requireContext() , "Saved to Gallery" , Toast.LENGTH_SHORT).show()
        }
    }


    private fun setDownloaded( model:CatResponse){

        lifecycleScope.launch(Dispatchers.IO) {
            val retrofit = RetrofitInstance.getInstance()
            val apiService = retrofit.create(SetMostDownloaded::class.java)
            val call = apiService.setDownloaded(model.id.toString())
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {


                        Log.e("TAG", "onResponse: success"+response.body().toString() )
                    }
                    else
                    {
                        Log.e("TAG", "onResponse: not success" )
//                        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("TAG", "onResponse: failed" )
//                    Toast.makeText(requireContext(), "onFailure error", Toast.LENGTH_SHORT).show()
                }
            })
        }


    }


    private fun addFavourite(
        context: Context,
        favouriteButton: ImageView
    ){
        val retrofit = RetrofitInstance.getInstance()
        val apiService = retrofit.create(ApiService::class.java)
        val postData = PostData(MySharePreference.getDeviceID(context)!!, responseData?.id.toString())
        val call = apiService.postData(postData)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val message = response.body()?.string()
                    if(message=="Liked"){
                        responseData?.liked = true
                        favouriteButton.setImageResource(R.drawable.button_like_selected)
                    }
                    else
                    {
                        favouriteButton.setImageResource(R.drawable.button_like)
                        responseData?.liked = false
                    }
                    favouriteButton.isEnabled = true
                }
                else
                {
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}