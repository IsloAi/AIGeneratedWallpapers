package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.CommonAdsListenerAdapter
import com.bmik.android.sdk.listener.CustomSDKAdsListenerAdapter
import com.bmik.android.sdk.listener.CustomSDKRewardedAdsListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentWallpaperViewBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.WallpaperApiSliderAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.FullViewImage
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.ViewPagerImageClick
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.PostData
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.RetrofitInstance
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.ApiService
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.BlurView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.FullViewImagePopup
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.GoogleLogin
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyDialogs
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyWallpaperManager
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.PostDataOnServer
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.Executors


class WallpaperViewFragment : Fragment() {
    private var _binding: FragmentWallpaperViewBinding? = null
    private val binding get() = _binding!!
    private var arrayList = ArrayList<CatResponse?>()
    private var isFragmentAttached: Boolean = false
    private var position :Int =0
    private var viewPager2: ViewPager2? = null
    private var bitmap: Bitmap? = null
    private val myExecutor = Executors.newSingleThreadExecutor()
    private val myHandler = Handler(Looper.getMainLooper())
    private var reviewManager: ReviewManager? = null
    private var getLargImage: String = ""
    private var getSmallImage: String = ""
    private var state = true
    private val STORAGE_PERMISSION_CODE = 1
    private var mImage: Bitmap? = null
    private var dialog: Dialog?= null
    private lateinit var myWallpaperManager : MyWallpaperManager
    private var navController: NavController? = null
    val myDialogs = MyDialogs()
    var adcount = 0
    var totalADs = 0
    private val googleLogin = GoogleLogin()
    private lateinit var myActivity : MainActivity
    private var from = ""

    var showInter = true
    val sharedViewModel: SharedViewModel by activityViewModels()



    var adapter: WallpaperApiSliderAdapter?= null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View{
        _binding = FragmentWallpaperViewBinding.inflate(inflater,container,false)
        myWallpaperManager = MyWallpaperManager(requireContext(),requireActivity())
        navController = findNavController()

        reviewManager = ReviewManagerFactory.create(requireContext())

        var arrayListJson:ArrayList<CatResponse> = ArrayList()

        sharedViewModel.catResponseList.observe(viewLifecycleOwner) { catResponses ->
            if (catResponses.isNotEmpty()){
                arrayListJson = catResponses as ArrayList<CatResponse>
                val pos = arguments?.getInt("position")
                from = arguments?.getString("from")!!
                adcount = pos!!
                if (arrayListJson != null && pos != null) {
                    val gson = Gson()
//            val arrayListType = object : TypeToken<ArrayList<CatResponse>>() {}.type
                    val arrayListOfImages = arrayListJson
                    arrayListOfImages.filterNotNull()

                    Log.e("******NULL", "onCreate: "+arrayListOfImages.size )
                    var adjustedPos = pos

                    arrayList = addNullValueInsideArray(arrayListOfImages)

                    val firstAdLineThreshold = if (AdConfig.firstAdLineTrending != 0) AdConfig.firstAdLineTrending else 4

                    // Calculate the adjusted position by considering the null ads in the array
                    position = if (pos == firstAdLineThreshold){
                        pos +totalADs
                    }else if (pos < firstAdLineThreshold){
                        pos
                    }else{
                        pos +totalADs
                    }




                    Log.d("gsonParsingData", "onCreate:  $arrayListOfImages"  )
                }




                functionality()
            }
        }



        Glide.with(requireContext())
            .asGif()
            .load(R.raw.gems_animaion)
            .into(binding.animationDdd)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SDKBaseController.getInstance()
            .loadBannerAds(
                requireActivity(),
                binding.adsWidget as? ViewGroup,
                "viewlistwallscr_bottom",
                " viewlistwallscr_bottom", object : CustomSDKAdsListenerAdapter() {
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
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    private fun addNullValueInsideArray(data: List<CatResponse?>): ArrayList<CatResponse?>{

         val firstAdLineThreshold = if (AdConfig.firstAdLineTrending != 0) AdConfig.firstAdLineTrending else 4

         val lineCount = if (AdConfig.lineCountTrending != 0) AdConfig.lineCountTrending else 5
        val newData = arrayListOf<CatResponse?>()

        if (from == "trending"){
            for (i in data.indices){
                if (i > firstAdLineThreshold && (i - firstAdLineThreshold) % (lineCount -1)  == 0) {
                    newData.add(null)
                    if (i <= adcount){
                        totalADs++
                        Log.e("******NULL", "addNullValueInsideArray adcount: "+adcount )
                        Log.e("******NULL", "addNullValueInsideArray adcount: "+totalADs )
                    }
                    Log.e("******NULL", "addNullValueInsideArray: null "+i )

                }else if (i == firstAdLineThreshold){
                    newData.add(null)
                    totalADs++
                    Log.e("******NULL", "addNullValueInsideArray adcount: "+adcount )
                    Log.e("******NULL", "addNullValueInsideArray adcount: "+totalADs )

                    Log.e("******NULL", "addNullValueInsideArray: null first "+i )
                }
                Log.e("******NULL", "addNullValueInsideArray: not null "+i )
                newData.add(data[i])

            }
            Log.e("******NULL", "addNullValueInsideArray:size "+newData.size )
        }else{
            for (i in data.indices){
                if (i > firstAdLineThreshold && (i - firstAdLineThreshold) % (lineCount -1)  == 0) {
                    newData.add(null)
                    if (i <= adcount){
                        totalADs++
                        Log.e("******NULL", "addNullValueInsideArray adcount: "+adcount )
                        Log.e("******NULL", "addNullValueInsideArray adcount: "+totalADs )
                    }
                    Log.e("******NULL", "addNullValueInsideArray: null "+i )

                }else if (i == firstAdLineThreshold){
                    newData.add(null)
                    totalADs++
                    Log.e("******NULL", "addNullValueInsideArray adcount: "+adcount )
                    Log.e("******NULL", "addNullValueInsideArray adcount: "+totalADs )

                    Log.e("******NULL", "addNullValueInsideArray: null first "+i )
                }
                Log.e("******NULL", "addNullValueInsideArray: not null "+i )
                newData.add(data[i])

            }
            Log.e("******NULL", "addNullValueInsideArray:size "+newData.size )
        }



        return newData
    }


    private fun functionality(){
       myActivity = activity as MainActivity
       viewPager2 = binding.viewPager
       binding.toolbar.setOnClickListener {
           // Set up the onBackPressed callback
           navController?.navigateUp()
       }

       binding.gemsText.text = MySharePreference.getGemsValue(requireContext()).toString()
        setViewPager()
        checkRedHeart(position)
        if (arrayList[position] != null){
            getLargImage = arrayList[position]?.hd_image_url!!
            getSmallImage = arrayList[position]?.compressed_image_url!!
            getBitmapFromGlide(getLargImage)


        }


        binding.buttonApplyWallpaper.setOnClickListener {
//           if(arrayList[position]?.gems==0 || arrayList[position]?.unlockimges==true){
               if(bitmap != null){
                   openPopupMenu()
               }else{
                   Toast.makeText(requireContext(),
                       getString(R.string.your_image_not_fetched_properly), Toast.LENGTH_SHORT).show()
               }
//           }else{
//               Toast.makeText(requireContext(), "Please first buy your wallpaper", Toast.LENGTH_SHORT).show()
//           }

       }
       binding.favouriteButton.setOnClickListener {
               binding.favouriteButton.isEnabled = false
               if(arrayList[position]?.liked==true){
                   arrayList[position]?.liked = false
                   binding.favouriteButton.setImageResource(R.drawable.heart_unsel)
               }else{
                   arrayList[position]?.liked = true
                   binding.favouriteButton.setImageResource(R.drawable.heart_red)
               }
               addFavourite(requireContext(),position,binding.favouriteButton)


       }


       binding.unlockWallpaper.setOnClickListener {

           if(bitmap != null){
               SDKBaseController.getInstance().showRewardedAds(requireActivity(),"viewlistwallscr_item_vip_reward","viewlistwallscr_item_vip_reward",object:
                   CustomSDKRewardedAdsListener {
                   override fun onAdsDismiss() {
                       Log.e("********ADS", "onAdsDismiss: ")

                   }

                   override fun onAdsRewarded() {
                       Log.e("********ADS", "onAdsRewarded: ")
                       val postData = PostDataOnServer()
                       val model = arrayList[position]
                       arrayList[position]?.unlockimges = true
                       arrayList[position]?.gems = 0

                       val model1 = arrayList[position]
                       postData.unLocking(MySharePreference.getDeviceID(requireContext())!!,
                           model1!!,requireContext(),0)
                       showInter = false
                       openPopupMenu()
                       binding.buttonApplyWallpaper.visibility = View.VISIBLE
                       binding.unlockWallpaper.visibility = View.GONE
                       adapter?.notifyItemChanged(position)
                       viewPager2?.invalidate()
                       binding.viewPager.setCurrentItem(position,true)
                   }

                   override fun onAdsShowFail(errorCode: Int) {
                       Log.e("********ADS", "onAdsShowFail: ")

                               SDKBaseController.getInstance().showInterstitialAds(
                                   requireActivity(),
                                   "viewlistwallscr_item_vip_inter",
                                   "viewlistwallscr_item_vip_inter",
                                   showLoading = true,
                                   adsListener = object : CommonAdsListenerAdapter() {
                                       override fun onAdsShowFail(errorCode: Int) {
                                           Toast.makeText(requireContext(),"Ad Load Failed. Please try again",Toast.LENGTH_SHORT).show()
                                       }

                                       override fun onAdsDismiss() {
                                           Log.e("********ADS", "onAdsRewarded: ")
                                           val postData = PostDataOnServer()
                                           val model = arrayList[position]
                                           arrayList[position]?.unlockimges = true
                                           arrayList[position]?.gems = 0

                                           val model1 = arrayList[position]
                                           postData.unLocking(MySharePreference.getDeviceID(requireContext())!!,
                                               model1!!,requireContext(),0)
                                           showInter = false
                                           openPopupMenu()
                                           binding.buttonApplyWallpaper.visibility = View.VISIBLE
                                           binding.unlockWallpaper.visibility = View.GONE
                                           adapter?.notifyItemChanged(position)
                                           viewPager2?.invalidate()
                                           binding.viewPager.setCurrentItem(position,true)
                                       }
                                   }
                               )


                   }

               })
           }else{
               Toast.makeText(requireContext(),
                   getString(R.string.your_image_not_fetched_properly), Toast.LENGTH_SHORT).show()
           }




       }
       binding.downloadWallpaper.setOnClickListener{

           if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
               ActivityCompat.requestPermissions(requireContext() as Activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
           }else{
               getUserIdDialog()
           }

//           if(arrayList[position]?.gems==0 || arrayList[position]?.unlockimges==true){
//
//           }else{
//               Toast.makeText(requireContext(), "First Unlock the wallpaper to download", Toast.LENGTH_SHORT).show()
//
//           }

       }
   }

    private val fragmentScope: CoroutineScope by lazy { MainScope() }
    private fun setViewPager() {
        adapter = WallpaperApiSliderAdapter(arrayList, viewPager2!!,from,object :
            ViewPagerImageClick {
            @SuppressLint("SuspiciousIndentation")
            override fun getImagePosition(pos: Int, layout: ConstraintLayout) {
                    val model = arrayList[pos]
//                    myDialogs.getWallpaperPopup(
//                        context!!,
//                        model,
//                        navController!!, R.id.action_wallpaperViewFragment_to_premiumPlanFragment,
//                        RetrofitInstance.getInstance(), binding.gemsText, layout,requireActivity())

            }
        },object:FullViewImage{
            override fun getFullImageUrl(image:String) {
               FullViewImagePopup.openFullViewWallpaper(myContext(),image)
            }
        },myActivity)
        adapter!!.setCoroutineScope(fragmentScope)
        viewPager2?.adapter = adapter
        viewPager2?.setCurrentItem(position, false)


        if(arrayList[position]?.gems==0 || arrayList[position]?.unlockimges==true){
            binding.unlockWallpaper.visibility = View.GONE
            binding.buttonApplyWallpaper.visibility = View.VISIBLE
        }else{
            binding.unlockWallpaper.visibility = View.GONE
            binding.buttonApplyWallpaper.visibility = View.VISIBLE
        }

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
                if (positi >= 0 && positi < arrayList.size) {
                if (arrayList[positi]?.hd_image_url != null){
                    getLargImage = arrayList[positi]?.hd_image_url!!
                    getSmallImage = arrayList[positi]?.compressed_image_url!!

                    position = positi


                    if(arrayList[position]?.gems==0 || arrayList[position]?.unlockimges==true){

                        binding.unlockWallpaper.visibility = View.GONE
                        binding.buttonApplyWallpaper.visibility = View.VISIBLE
                    }else{
                        binding.unlockWallpaper.visibility = View.GONE
                        binding.buttonApplyWallpaper.visibility = View.VISIBLE
                    }
                }else{
                    position = positi
                }




                if (arrayList[positi]?.hd_image_url ==  null){
                    binding.unlockWallpaper.visibility = View.GONE
                    binding.buttonApplyWallpaper.visibility = View.GONE
                    binding.bottomMenu.visibility = View.GONE

                    binding.adsWidget.visibility = View.GONE
                }else{
                    binding.bottomMenu.visibility = View.VISIBLE
                    binding.adsWidget.visibility = View.VISIBLE

                    if(arrayList[position]?.gems==0 || arrayList[position]?.unlockimges==true){

                        binding.unlockWallpaper.visibility = View.GONE
                        binding.buttonApplyWallpaper.visibility = View.VISIBLE
                    }else{
                        binding.unlockWallpaper.visibility = View.GONE
                        binding.buttonApplyWallpaper.visibility = View.VISIBLE
                    }

                }

                checkRedHeart(positi)
                getBitmapFromGlide(getLargImage)
                }
            }
        }
        viewPager2?.registerOnPageChangeCallback(viewPagerChangeCallback)
    }


    private fun getUserIdDialog() {
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
//                                if(arrayList[position]?.gems==0 || arrayList[position]?.unlockimges==true){
                                    mSaveMediaToStorage(bitmap)
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

                    if (isAdded){
                        val blurImage: Bitmap = BlurView.blurImage(requireContext(), bitmap!!)!!
                        binding.backImage.setImageBitmap(blurImage)
                    }



                }
                override fun onLoadCleared(placeholder: Drawable?) {
                } })
    }
    private fun checkRedHeart(position: Int) {
        if (isAdded) {
        if (arrayList[position]?.liked == true) {
            binding.favouriteButton.setImageResource(R.drawable.heart_red) }
        else
        {
            binding.favouriteButton.setImageResource(R.drawable.heart_unsel)
        }
    }
    }
    private fun addFavourite(
        context: Context,
        position: Int,
        favouriteButton: ImageView
    ){
        val retrofit = RetrofitInstance.getInstance()
        val apiService = retrofit.create(ApiService::class.java)
        val postData = PostData(MySharePreference.getDeviceID(context)!!, arrayList[position]?.id.toString())
        val call = apiService.postData(postData)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val message = response.body()?.string()
                    if(message=="Liked"){
                        arrayList[position]?.liked = true
                        favouriteButton.setImageResource(R.drawable.heart_red)
                    }
                    else
                    {
                        favouriteButton.setImageResource(R.drawable.heart_unsel)
                        arrayList[position]?.liked = false
                    }
                    favouriteButton.isEnabled = true
                }
                else
                {
                    favouriteButton.isEnabled = true
                    Toast.makeText(context, "onResponse error", Toast.LENGTH_SHORT).show()
                    favouriteButton.setImageResource(R.drawable.heart_unsel)
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "onFailure error", Toast.LENGTH_SHORT).show()
                favouriteButton.isEnabled = true
            }
        })
    }
    private suspend fun loadBitmapFromUrlAsync(urlString: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.connect()
            val inputStream: InputStream = BufferedInputStream(connection.inputStream)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            connection.disconnect()
            bitmap
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun mLoad(string: String): Bitmap? {
        val url: URL = mStringToURL(string)!!
        val connection: HttpURLConnection?
        try {
            connection = url.openConnection() as HttpURLConnection
            connection.connect()
            val inputStream: InputStream = connection.inputStream
            val bufferedInputStream = BufferedInputStream(inputStream)
            return BitmapFactory.decodeStream(bufferedInputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
    private fun mStringToURL(string: String): URL? {
        try {
            return URL(string)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return null
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
            if (showInter){
                SDKBaseController.getInstance().showInterstitialAds(
                    requireActivity(),
                    "viewlistwallscr_setdilog_set_button",
                    "viewlistwallscr_setdilog_set_button",
                    showLoading = true,
                    adsListener = object : CommonAdsListenerAdapter() {
                        override fun onAdsShowFail(errorCode: Int) {
                            myExecutor.execute {myWallpaperManager.homeScreen(bitmap!!)}
                            myHandler.post { if(state){
                                if (isAdded) {
                                    interstitialAdWithToast(
                                        getString(R.string.set_successfully_on_home_screen),
                                        dialog
                                    )
                                }
                                state = false
                                postDelay()
                            } }
                            showRateApp()
                            binding.viewPager.setCurrentItem(position+1,true)
                            Log.e("********ADS", "onAdsShowFail: "+errorCode )
                            //do something
                        }

                        override fun onAdsDismiss() {
                            lifecycleScope.launch(Dispatchers.IO) {
                                try {
                                    myWallpaperManager.homeScreen(bitmap!!)
                                    withContext(Dispatchers.Main) {
                                        if (state) {
                                            if (isAdded) {
                                                interstitialAdWithToast(
                                                    getString(R.string.set_successfully_on_home_screen),
                                                    dialog
                                                )
                                            }
                                            state = false
                                            postDelay()
                                        }
                                    }
                                }catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
//                            myExecutor.execute {myWallpaperManager.homeScreen(bitmap!!)}
//                            myHandler.post { if(state){
//                                interstitialAdWithToast(getString(R.string.set_successfully_on_home_screen), dialog)
//                                state = false
//                                postDelay()
//                            } }
                            showRateApp()
                            binding.viewPager.setCurrentItem(position+1,true)
                        }
                    }
                )
            }else{
                myExecutor.execute {myWallpaperManager.homeScreen(bitmap!!)}
                myHandler.post { if(state){
                    if (isAdded) {
                        interstitialAdWithToast(
                            getString(R.string.set_successfully_on_home_screen),
                            dialog
                        )
                    }
                    state = false
                    postDelay()
                } }
                showRateApp()
                binding.viewPager.setCurrentItem(position+1,true)
                showInter = true
            }


        }
        buttonLock.setOnClickListener {

            if (showInter){
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
                            myHandler.post {
                                if(state){
                                    if (isAdded) {
                                        interstitialAdWithToast(
                                            getString(R.string.set_successfully_on_lock_screen),
                                            dialog
                                        )
                                    }
                                    state = false
                                    postDelay()
                                }
                            }
                            showRateApp()
                            binding.viewPager.setCurrentItem(position+1,true)
                            //do something
                        }

                        override fun onAdsDismiss() {
                            myExecutor.execute {
                                myWallpaperManager.lockScreen(bitmap!!)
                            }
                            myHandler.post {
                                if(state){
                                    if (isAdded) {
                                        interstitialAdWithToast(
                                            getString(R.string.set_successfully_on_lock_screen),
                                            dialog
                                        )
                                    }
                                    state = false
                                    postDelay()
                                }
                            }
                            showRateApp()
                            binding.viewPager.setCurrentItem(position+1,true)
                        }
                    }
                )
            }else{
                myExecutor.execute {
                    myWallpaperManager.lockScreen(bitmap!!)
                }
                myHandler.post {
                    if(state){
                        interstitialAdWithToast(getString(R.string.set_successfully_on_lock_screen), dialog)
                        state = false
                        postDelay()
                    }
                }
                showRateApp()
                binding.viewPager.setCurrentItem(position+1,true)
                showInter = true
            }





        }
        buttonBothScreen.setOnClickListener {
            if (showInter){
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
                                if(state){
                                    if (isAdded){
                                        interstitialAdWithToast(getString(R.string.set_successfully_on_both),dialog)

                                    }
                                    state = false
                                    postDelay()
                                }
                            }
                            showRateApp()
                            binding.viewPager.setCurrentItem(position+1,true)
                            //do something
                        }

                        override fun onAdsDismiss() {
                            myExecutor.execute {
                                myWallpaperManager.homeAndLockScreen(bitmap!!)
                            }
                            myHandler.post {
                                if(state){
                                    if (isAdded){
                                    interstitialAdWithToast(getString(R.string.set_successfully_on_both),dialog)
                                        }
                                    state = false
                                    postDelay()
                                }
                            }
                            showRateApp()
                            binding.viewPager.setCurrentItem(position+1,true)
                        }
                    }
                )
            }else{
                myExecutor.execute {
                    myWallpaperManager.homeAndLockScreen(bitmap!!)
                }
                myHandler.post {
                    if(state){
                        interstitialAdWithToast(getString(R.string.set_successfully_on_both),dialog)
                        state = false
                        postDelay()
                    }
                }
                showRateApp()
                binding.viewPager.setCurrentItem(position+1,true)
                showInter = true
            }


        }
        dialog.show()
    }

    fun resizeBitmap(originalBitmap: Bitmap): Bitmap {
        val width = originalBitmap.width / 2
        val height = originalBitmap.height / 2
        return Bitmap.createScaledBitmap(originalBitmap, width, height, true)
    }
    private fun postDelay(){
        Handler().postDelayed({
            state = true
        }, 5000)
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

    fun notifyFileAdded(context: Context?, filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            val mimeType = getMimeType(filePath)
            MediaScannerConnection.scanFile(
                context,
                arrayOf(file.absolutePath),
                arrayOf(mimeType),
                null
            )
        }
    }

    private fun getMimeType(filePath: String): String? {
        val extension = MimeTypeMap.getFileExtensionFromUrl(filePath)
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(),
                    getString(R.string.permission_granted_click_again_to_save_image), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(),
                    getString(R.string.storage_permission_denied), Toast.LENGTH_SHORT).show()
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

    fun myContext(): Context {
        return this.context
            ?: throw IllegalStateException("Fragment $this not attached to a context.")
    }

   private fun showRateApp() {
       if(isFragmentAttached && isAdded){
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        isFragmentAttached = false
    }

    override fun onDetach() {
        super.onDetach()
        isFragmentAttached = false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        isFragmentAttached = true
    }

}