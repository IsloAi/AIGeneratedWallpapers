package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.fragmentsIG

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.ikame.android.sdk.IKSdkController

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.adaptersIG.CreationWallpaperSliderAdapter
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding
.FragmentCreationSliderViewBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.FavouriteListIGEntity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.RoomViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.ViewModelFactory
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.InterstitialAdDismiss
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyWallpaperManager
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.PostDataOnServer
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.concurrent.Executors

class CreationSliderViewFragment : Fragment() {
    private lateinit var binding: FragmentCreationSliderViewBinding
    private var myContext:Context? = null
    private var arrayList = ArrayList<String>()
    private var position :Int =0
    private var viewPager2: ViewPager2? = null
    private var bitmap: Bitmap? = null
    private val myExecutor = Executors.newSingleThreadExecutor()
    private val myHandler = Handler(Looper.getMainLooper())
    private var reviewManager: ReviewManager? = null
    private var getLargImage: String = ""
    private var state = true
    private val STORAGE_PERMISSION_CODE = 1
    private var dialog: Dialog?= null
    private lateinit var myWallpaperManager : MyWallpaperManager
    private var navController: NavController? = null
    private lateinit var myActivity : MainActivity
    private lateinit var viewModel:RoomViewModel
    private var favouriteListIGEntity : List<FavouriteListIGEntity>? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View{
       binding =  FragmentCreationSliderViewBinding.inflate(inflater,container,false)
       if(myContext != null){onCreatingViewCalling()}
        return  binding.root
    }
    private fun onCreatingViewCalling() {
        myWallpaperManager = MyWallpaperManager(requireContext(),requireActivity())
        navController = findNavController()
        reviewManager = ReviewManagerFactory.create(requireContext())

        Glide.with(requireContext())
            .asGif()
            .load(R.raw.gems_animaion)
            .into(binding.animationDdd)

        val roomDatabase = AppDatabase.getInstance(requireContext())
        viewModel = ViewModelProvider(this, ViewModelFactory(roomDatabase,0))[RoomViewModel::class.java]

        val arrayListJson = arguments?.getString("arrayListJson")
        val pos = arguments?.getInt("position")
        val id = arguments?.getInt("id")
        val prompt = arguments?.getString("prompt")
        if (arrayListJson != null && pos != null) {
            val gson = Gson()
            val arrayListType = object : TypeToken<ArrayList<String>>() {}.type
            arrayList = gson.fromJson(arrayListJson, arrayListType)
            Log.d("imageLists", "my list at slider vew $arrayList")
            position = pos
            Log.d("imageLists", "my position at slider vew $pos")
        }
        functionality(id,prompt)
    }

    private fun functionality(myId: Int?, prompt: String?) {
        myActivity = activity as MainActivity
        viewPager2 = binding.viewPager
        binding.backButton.setOnClickListener {
            // Set up the onBackPressed callback
            navController?.navigateUp()
        }
        getLargImage = arrayList[position]
        Log.d("imageLists", "image $getLargImage")
        checkAlreadyFavourite()
        binding.gemsText.text = MySharePreference.getGemsValue(requireContext()).toString()
        setViewPager()
        getBitmapFromGlide(getLargImage)
        binding.buttonApplyWallpaper.setOnClickListener {
                if(bitmap != null){
                    openPopupMenu()
                }else{
                    Toast.makeText(requireContext(), resources.getString(R.string.your_image_not_fetched_properly), Toast.LENGTH_SHORT).show()
                }
        }
        binding.favouriteButton.setOnClickListener {
                if(favouriteListIGEntity!!.isEmpty()){
                   addFavouriteList(myId!!,prompt!!)
                }else{
                    val image = favouriteListIGEntity!!.any {it.image == getLargImage}
                    if(!image){
                        addFavouriteList(myId!!,prompt!!)
                    }
                    else{
                        viewModel.deleteItem(getLargImage)
                    }
                }
        }
        binding.downloadWallpaper.setOnClickListener{
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(requireContext() as Activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
                }else{
                    watchAdToDownload()
                }
            }else{
                watchAdToDownload()
            }

        }
    }
    private fun addFavouriteList(myId:Int,prompt:String){
        val data = FavouriteListIGEntity(imageId = myId, image = getLargImage, prompt = prompt)
        viewModel.insertFavourite(data)
        binding.favouriteButton.setImageResource(R.drawable.heart_red)
    }
    private fun checkAlreadyFavourite():Boolean{
        var checkingValue = false
        viewModel.myFavouriteList.observe(viewLifecycleOwner){list->
          favouriteListIGEntity = list
            val image = list.any {it.image== getLargImage}
            checkingValue = if(image){
                binding.favouriteButton.setImageResource(R.drawable.heart_red)
                true
            }else{
                binding.favouriteButton.setImageResource(R.drawable.heart_unsel)
                false
            }
        }
        return checkingValue
    }

    private fun setViewPager() {
        val adopter = CreationWallpaperSliderAdapter(arrayList, viewPager2!!)
        viewPager2?.adapter = adopter
        viewPager2?.setCurrentItem(position, false)
        viewPager2?.clipToPadding = false
        viewPager2?.clipChildren = false
        viewPager2?.offscreenPageLimit = 3
        viewPager2?.getChildAt(0)!!.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
        transformer.addTransformer { page, position ->
            val r: Float = 1 - Math.abs(position)
            page.scaleY = 0.75f + r * 0.13f
        }
        viewPager2?.setPageTransformer(transformer)
        val viewPagerChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(positi: Int) {
                getLargImage = arrayList[positi]
                position = positi
                getBitmapFromGlide(getLargImage)
                checkAlreadyFavourite()

            }
        }
        viewPager2?.registerOnPageChangeCallback(viewPagerChangeCallback)
    }
    private fun getBitmapFromGlide(url:String){
        Glide.with(requireContext()).asBitmap().load(url)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    bitmap = resource }
                override fun onLoadCleared(placeholder: Drawable?) {
                } })
    }
    @SuppressLint("ResourceType")
    private fun openPopupMenu() {
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
//
//            IKSdkController.getInstance().showInterstitialAds(
//                requireActivity(),
//                "viewlistwallscr_setdilog_set_button",
//                "viewlistwallscr_setdilog_set_button",
//                showLoading = true,
//                adsListener = object : CommonAdsListenerAdapter() {
//                    override fun onAdsShowFail(errorCode: Int) {
//                        Log.e("********ADS", "onAdsShowFail: "+errorCode )
//                        Toast.makeText(requireContext(),"Ad not available,Please try again...",Toast.LENGTH_SHORT).show()
//                        //do something
//                    }
//
//                    override fun onAdsDismiss() {
//                        myExecutor.execute {myWallpaperManager.homeScreen(bitmap!!)}
//                        myHandler.post { if(state){
//                            interstitialAdWithToast(getString(R.string.set_successfully_on_home_screen), dialog)
//                            state = false
//                            postDelay()
//                        } }
//                        showRateApp()
//                    }
//                }
//            )
        }
        buttonLock.setOnClickListener {

//            IKSdkController.getInstance().showInterstitialAds(
//                requireActivity(),
//                "viewlistwallscr_setdilog_set_button",
//                "viewlistwallscr_setdilog_set_button",
//                showLoading = true,
//                adsListener = object : CommonAdsListenerAdapter() {
//                    override fun onAdsShowFail(errorCode: Int) {
//                        Log.e("********ADS", "onAdsShowFail: "+errorCode )
//                        Toast.makeText(requireContext(),"Ad not available,Please try again...",Toast.LENGTH_SHORT).show()
//                        //do something
//                    }
//
//                    override fun onAdsDismiss() {
//                        myExecutor.execute {
//                            myWallpaperManager.lockScreen(bitmap!!)
//                        }
//                        myHandler.post {
//                            if(state){
//                                interstitialAdWithToast(getString(R.string.set_successfully_on_lock_screen), dialog)
//                                state = false
//                                postDelay()
//                            }
//                        }
//                        showRateApp()
//                        binding.viewPager.setCurrentItem(position+1,true)
//                    }
//                }
//            )
        }
        buttonBothScreen.setOnClickListener {


//            IKSdkController.getInstance().showInterstitialAds(
//                requireActivity(),
//                "viewlistwallscr_setdilog_set_button",
//                "viewlistwallscr_setdilog_set_button",
//                showLoading = true,
//                adsListener = object : CommonAdsListenerAdapter() {
//                    override fun onAdsShowFail(errorCode: Int) {
//                        Log.e("********ADS", "onAdsShowFail: "+errorCode )
//                        Toast.makeText(requireContext(),"Ad not available,Please try again...",Toast.LENGTH_SHORT).show()
//                        //do something
//                    }
//
//                    override fun onAdsDismiss() {
//                        myExecutor.execute {
//                            myWallpaperManager.homeAndLockScreen(bitmap!!)
//                        }
//                        myHandler.post {
//                            if(state){
//                                interstitialAdWithToast(getString(R.string.set_successfully_on_both),dialog)
//                                state = false
//                                postDelay()
//                            }
//                        }
//                        showRateApp()
//                        binding.viewPager.setCurrentItem(position+1,true)
//                    }
//                }
//            )
        }
        dialog.show()
    }
    private fun postDelay(){
        Handler().postDelayed({
            state = true
        }, 5000)
    }
    private fun interstitialAdWithToast (message: String, dialog: BottomSheetDialog){
        Toast.makeText(requireContext(),message, Toast.LENGTH_SHORT).show()
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
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(requireContext() as Activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
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
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), resources.getString(R.string.permission_granted_click_again_to_save_image), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), resources.getString(R.string.storage_permission_denied), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadingPopup() {
        dialog = Dialog(requireContext())
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.loading)
        val width = WindowManager.LayoutParams.WRAP_CONTENT
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog?.window!!.setLayout(width, height)
        dialog?.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCancelable(false)
    }

    private fun watchAdToDownload() {
        dialog = Dialog(requireContext())
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
//            IKSdkController.getInstance().showRewardedAds(requireActivity(),"viewlistwallscr_download_item","viewlistwallscr_download_item",object:
//                CustomSDKRewardedAdsListener {
//                override fun onAdsDismiss() {
//                    Log.e("********ADS", "onAdsDismiss: ")
//                }
//
//                override fun onAdsRewarded() {
//                    Log.e("********ADS", "onAdsRewarded: ")
//                        mSaveMediaToStorage(bitmap)
//
//
//
//                }
//
//                override fun onAdsShowFail(errorCode: Int) {
//                    Log.e("********ADS", "onAdsShowFail: ")
//                    IKSdkController.getInstance().showInterstitialAds(
//                        requireActivity(),
//                        "viewlistwallscr_download_item_inter",
//                        "viewlistwallscr_download_item_inter",
//                        showLoading = true,
//                        adsListener = object : CommonAdsListenerAdapter() {
//                            override fun onAdsShowFail(errorCode: Int) {
//                                if (isAdded){
//                                    Toast.makeText(requireContext(),"Ad not available, Please try again later",Toast.LENGTH_SHORT).show()
//                                }
//                            }
//
//                            override fun onAdsDismiss() {
//                                mSaveMediaToStorage(bitmap)
//                            }
//                        }
//                    )
//
//                }
//
//            })
        }

        dismiss?.setOnClickListener {
            dialog?.dismiss()
        }

        dialog?.show()
    }

    private fun showRateApp() {
        if(myContext!=null){
            val request: Task<ReviewInfo> = reviewManager!!.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful()) {
                    // Getting the ReviewInfo object
                    val reviewInfo: ReviewInfo = task.getResult()
                    val flow: Task<Void> = reviewManager!!.launchReviewFlow(requireActivity(), reviewInfo)
                    flow.addOnCompleteListener { task1 -> }
                }
            }
        }
    }

}