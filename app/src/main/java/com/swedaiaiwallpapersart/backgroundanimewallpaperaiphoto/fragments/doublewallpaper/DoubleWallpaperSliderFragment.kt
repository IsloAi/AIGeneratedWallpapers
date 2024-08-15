package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.doublewallpaper

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ikame.android.sdk.IKSdkController
import com.ikame.android.sdk.data.dto.pub.IKAdError
import com.ikame.android.sdk.format.intertial.IKInterstitialAd
import com.ikame.android.sdk.format.rewarded.IKRewardAd
import com.ikame.android.sdk.listener.pub.IKLoadAdListener
import com.ikame.android.sdk.listener.pub.IKLoadDisplayAdViewListener
import com.ikame.android.sdk.listener.pub.IKShowAdListener
import com.ikame.android.sdk.listener.pub.IKShowWidgetAdListener
import com.ikame.android.sdk.tracking.IKTrackingHelper
import com.ikame.android.sdk.widgets.IkmDisplayWidgetAdView
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.BottomSheetInfoBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentDoubleWallpaperSliderBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.DoubleWallpaperSliderAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.DoubleWallModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.remote.EndPointsInterface
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.WallpaperViewFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.BlurView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyDialogs
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyWallpaperManager
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.DoubleSharedViewmodel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import java.util.concurrent.Executors
import javax.inject.Inject

@AndroidEntryPoint
class DoubleWallpaperSliderFragment : Fragment() {

    private var _binding:FragmentDoubleWallpaperSliderBinding ?= null
    private val binding get() = _binding!!

    private var isFragmentAttached: Boolean = false
    private var position: Int = 0
    private var viewPager2: ViewPager2? = null
    private var bitmap: Bitmap? = null
    private val myExecutor = Executors.newSingleThreadExecutor()
    private val myHandler = Handler(Looper.getMainLooper())
    private var getLargImage: String = ""
    private var getSmallImage: String = ""
    private var state = true
    private val STORAGE_PERMISSION_CODE = 1
    private var mImage: Bitmap? = null
    private var dialog: Dialog? = null
    private lateinit var myWallpaperManager: MyWallpaperManager
    private var navController: NavController? = null
    val myDialogs = MyDialogs()
    var adcount = 0
    var totalADs = 0
    private lateinit var myActivity: MainActivity
    private var from = ""
    private var wall = ""

    val sharedViewModel: DoubleSharedViewmodel by activityViewModels()

    @Inject
    lateinit var endPointsInterface: EndPointsInterface

    private var arrayList = ArrayList<DoubleWallModel?>()

    var adapter: DoubleWallpaperSliderAdapter? = null


    var firstTime = true
    var oldPosition = 0

    val TAG = "SLIDERFRAGMENT"

    var homeScreenBitmap:Bitmap ?= null
    var lockScreenBitmap:Bitmap ?= null

    val interAd = IKInterstitialAd()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDoubleWallpaperSliderBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myWallpaperManager = MyWallpaperManager(requireContext(), requireActivity())

        navController = findNavController()

        if(AdConfig.ISPAIDUSER){
            binding.adsView.visibility = View.GONE
        }else{

            interAd.attachLifecycle(this.lifecycle)
// Load ad with a specific screen ID, considered as a unitId
            interAd.loadAd("downloadscr_set_click", object : IKLoadAdListener {
                override fun onAdLoaded() {
                    // Ad loaded successfully
                }
                override fun onAdLoadFail(error: IKAdError) {
                    // Handle ad load failure
                }
            })

            binding.adsView.attachLifecycle(lifecycle)
            binding.adsView.loadAd("viewlistdoublewallscr_bottom", object : IKShowWidgetAdListener {
                override fun onAdShowed() {}
                override fun onAdShowFail(error: IKAdError) {
//                    binding.adsView?.visibility = View.GONE
                }

            })
        }


        getWallpapers()
    }

    fun getWallpapers(){
        var arrayListJson: ArrayList<DoubleWallModel> = ArrayList()
        sharedViewModel.doubleWallResponseList.observe(viewLifecycleOwner) { catResponses ->
            if (catResponses.isNotEmpty()) {
                arrayListJson.clear()

                Log.e(TAG, "getWallpapers: "+catResponses )
                arrayListJson = catResponses as ArrayList<DoubleWallModel>
                val pos = arguments?.getInt("position")
                from = arguments?.getString("from")!!
                wall = arguments?.getString("wall")!!

                Log.e(TAG, "recieved position: $pos")
                Log.e(TAG, "recieved from: $from")

                adcount = pos!!
                if (arrayListJson != null && pos != null) {
                    val arrayListOfImages = arrayListJson
                    arrayListOfImages.filterNotNull()

                    Log.e(TAG, "onCreate: " + arrayListOfImages.size)
                    Log.e(TAG, "onCreate: " + arrayListOfImages)

                    arrayList = if (AdConfig.ISPAIDUSER){
                        arrayListOfImages as ArrayList<DoubleWallModel?>
                    }else{
                        addNullValueInsideArray(arrayListOfImages)
                    }

                    val firstAdLineThreshold =
                        if (AdConfig.firstAdLineTrending != 0) AdConfig.firstAdLineTrending else 4


                    // Calculate the adjusted position by considering the null ads in the array
                    if (firstTime) {
                        position = 0
                        position = if (AdConfig.ISPAIDUSER){
                            pos
                        }else{
                             if (pos == firstAdLineThreshold) {
                                pos + totalADs
                            } else if (pos < firstAdLineThreshold) {
                                pos
                            } else {
                                pos + totalADs
                            }
                        }


                        firstTime = false
                    }

                    WallpaperViewFragment.isNavigated = true


                    Log.e(TAG, "new position: $position")




                    Log.d(TAG, "onCreate:  $arrayListOfImages")
                }




                functionality()
            }
        }
    }

    private fun getBitmapFromGlide(url: String) {
        Glide.with(requireContext()).asBitmap().load(url)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    bitmap = resource

                    if (isAdded) {
                        val blurImage: Bitmap = BlurView.blurImage(requireContext(), bitmap!!)!!
                        binding.backImage.setImageBitmap(blurImage)
                    }


                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }
    private fun functionality() {
        myActivity = activity as MainActivity
        viewPager2 = binding.viewPager
        binding.toolbar.setOnClickListener {
            // Set up the onBackPressed callback

            if (isAdded) {
                sendTracking(
                    "click_button",
                    Pair("action_type", "button"),
                    Pair("action_name", "ViewListWallScr_BackBT_Click")
                )
            }
            firstTime = true
            navController?.popBackStack()
            Constants.checkInter = false
            Constants.checkAppOpen = false
        }
        setViewPager()
        if (arrayList[position] != null) {
            getLargImage = AdConfig.BASE_URL_DATA + "/doublewallpaper/" +arrayList[position]?.hd_url1!!
            getSmallImage = AdConfig.BASE_URL_DATA + "/doublewallpaper/" +arrayList[position]?.compress_url1!!
            getBitmapFromGlide(getLargImage)
            arrayList[position]?.let { loadBitmapsAndPerformAction(it) }
        }

        if (arrayList[position]?.downloaded == true){
            binding.buttonApplyWallpaper.text = "Set Wallpaper"
        }else{
            binding.buttonApplyWallpaper.text = "Download"
        }


        binding.buttonApplyWallpaper.setOnClickListener {
//           if(arrayList[position]?.gems==0 || arrayList[position]?.unlockimges==true){
            if (bitmap != null) {

                if (binding.buttonApplyWallpaper.text == "Download"){
                    sharedViewModel.clearChargeAnimation()
                    sharedViewModel.setchargingAnimation(listOf(arrayList[position]))
                    findNavController().navigate(R.id.doubleWallpaperDownloadFragment)
                }else{
                    if (homeScreenBitmap != null && lockScreenBitmap != null){
                        if (AdConfig.ISPAIDUSER){
                            if (isAdded){
                                myExecutor.execute { myWallpaperManager.doubleWallpaper(lockScreenBitmap!!,
                                    homeScreenBitmap!!
                                ) }

                                myHandler.post {
                                    if (isAdded) {
                                        interstitialAdWithToast(
                                            "Double Wallpaper applied successfully"
                                        )
                                    }
                                }
                            }
                        }else{

                            interAd.showAd(
                                requireActivity(),
                                "downloadscr_set_click",
                                adListener = object : IKShowAdListener {
                                    override fun onAdsShowFail(error: IKAdError) {
                                        if (isAdded){
                                            myExecutor.execute { myWallpaperManager.doubleWallpaper(lockScreenBitmap!!,
                                                homeScreenBitmap!!
                                            ) }
                                            myHandler.post {
                                                if (isAdded) {
                                                    interstitialAdWithToast(
                                                        "Double Wallpaper applied successfully"
                                                    )
                                                }
                                            }

                                        }
                                    }
                                    override fun onAdsDismiss() {
                                        if (isAdded){
                                            myExecutor.execute { myWallpaperManager.doubleWallpaper(lockScreenBitmap!!,
                                                homeScreenBitmap!!
                                            ) }

                                            myHandler.post {
                                                if (isAdded) {
                                                    interstitialAdWithToast(
                                                        "Double Wallpaper applied successfully"
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            )
                        }


                    }
                }


            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.your_image_not_fetched_properly), Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.shareAPp.setOnClickListener {
            if (isAdded) {
                sendTracking(
                    "click_button",
                    Pair("action_type", "button"),
                    Pair("action_name", "ViewListWallScr_ShareBT_Click")
                )
            }
            val appPackageName = requireContext().packageName
            val appName = requireContext().getString(R.string.app_name)

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                "Check out $appName! Get it from the Play Store:\nhttps://play.google.com/store/apps/details?id=$appPackageName"
            )

            val chooser = Intent.createChooser(shareIntent, "Share $appName via")
            chooser.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            requireContext().startActivity(chooser)
            Constants.checkInter = false
            Constants.checkAppOpen = false
        }


        binding.wallpaperInfo.setOnClickListener {
            if (isAdded) {
                sendTracking(
                    "click_button",
                    Pair("action_type", "button"),
                    Pair("action_name", "ViewListWallScr_InfoBT_Click")
                )
            }
            if (arrayList[position]?.id != null) {
                imageDetailsSheet()
            } else {
                Toast.makeText(
                    requireContext(),
                    "This is Ad position,No info Available",
                    Toast.LENGTH_SHORT
                ).show()
            }
            Constants.checkInter = false
            Constants.checkAppOpen = false

        }

    }

    fun imageDetailsSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val binding = BottomSheetInfoBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(binding.root)

        binding.btnYes.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        if (arrayList[position] != null) {
            binding.imageName.text = "Double Wallpaper"
            binding.imageSize.text = (arrayList[position]?.size1!! + arrayList[position]?.size2!!).toString() + " Kb"
            binding.imageCapacity.text = "832 X 1456"
            binding.imageTags.text = ""
        } else {
            Toast.makeText(
                requireContext(),
                "This is Ad position,No info Available",
                Toast.LENGTH_SHORT
            ).show()
        }



        bottomSheetDialog.show()
    }
    private fun interstitialAdWithToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    private val fragmentScope: CoroutineScope by lazy { MainScope() }
    private fun setViewPager() {
        adapter = DoubleWallpaperSliderAdapter(arrayList, myActivity)
        adapter!!.setCoroutineScope(fragmentScope)

        IKSdkController.loadNativeDisplayAd("viewlistdoublewallscr_scroll", object :
            IKLoadDisplayAdViewListener {
            override fun onAdLoaded(adObject: IkmDisplayWidgetAdView?) {
                if (isAdded && view!= null){
                    adapter?.nativeAdView = adObject
                    viewPager2?.adapter = adapter
                    viewPager2?.setCurrentItem(position, false)
                }
            }

            override fun onAdLoadFail(error: IKAdError) {
                // Handle ad load failure with view object
            }
        })
        viewPager2?.adapter = adapter
        Log.e(TAG, "setViewPager: " + position)
        viewPager2?.setCurrentItem(position, false)

        viewPager2?.clipToPadding = false
        viewPager2?.clipChildren = false
        viewPager2?.offscreenPageLimit = 3



        viewPager2?.getChildAt(0)!!.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(50))
        transformer.addTransformer { page, position ->
            val r: Float = 1 - Math.abs(position)
            page.scaleY = 0.82f + r * 0.16f
        }
        viewPager2?.setPageTransformer(transformer)
        val viewPagerChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(positi: Int) {
                if (positi >= 0 && positi < arrayList.size) {
                    if (arrayList[positi]?.hd_url1 != null) {
                        getLargImage = AdConfig.BASE_URL_DATA + "/doublewallpaper/" +arrayList[positi]?.hd_url1!!
                        getSmallImage = AdConfig.BASE_URL_DATA + "/doublewallpaper/" +arrayList[positi]?.compress_url1!!

                        position = positi
                    } else {
                        position = positi
                    }




                    if (arrayList[positi]?.hd_url1 == null) {
                        binding.unlockWallpaper.visibility = View.GONE
                        binding.buttonApplyWallpaper.visibility = View.GONE
                        binding.bottomMenu.visibility = View.GONE

                        binding.adsView.visibility = View.GONE
                    } else {
                        binding.bottomMenu.visibility = View.VISIBLE
                        if (AdConfig.ISPAIDUSER){

                            binding.adsView.visibility = View.GONE
                        }else{
                            binding.adsView.visibility = View.VISIBLE

                        }
                        binding.buttonApplyWallpaper.visibility = View.VISIBLE

                    }

                    if (arrayList[position]?.downloaded == true){
                        binding.buttonApplyWallpaper.text = "Set Wallpaper"
                    }else{
                        binding.buttonApplyWallpaper.text = "Download"
                    }
                    Log.e(TAG, "onPageSelected: "+position )
                    getBitmapFromGlide(getLargImage)
                    arrayList[positi]?.let { loadBitmapsAndPerformAction(it) }
                }
            }
        }
        viewPager2?.registerOnPageChangeCallback(viewPagerChangeCallback)
    }

    fun loadBitmapsAndPerformAction(doubleWallModelList: DoubleWallModel) {
        if (isAdded){
            Glide.with(requireContext())
                .asBitmap()
                .load(AdConfig.BASE_URL_DATA + "/doublewallpaper/" +doubleWallModelList.hd_url1)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        // Store the first bitmap and increment the counter
                        lockScreenBitmap = resource
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // Not used
                    }
                })

            Glide.with(requireContext())
                .asBitmap()
                .load(AdConfig.BASE_URL_DATA + "/doublewallpaper/" +doubleWallModelList.hd_url2)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        // Store the second bitmap and increment the counter
                        homeScreenBitmap = resource

                        // Check if both bitmaps are loaded

                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // Not used
                    }
                })
        }


    }


    private fun addNullValueInsideArray(data: List<DoubleWallModel?>): ArrayList<DoubleWallModel?> {

        totalADs = 0
        val firstAdLineThreshold =
            if (AdConfig.firstAdLineTrending != 0) AdConfig.firstAdLineTrending else 4

        val lineCount = if (AdConfig.lineCountTrending != 0) AdConfig.lineCountTrending else 5
        val newData = arrayListOf<DoubleWallModel?>()

        if (from == "trending") {
            for (i in data.indices) {
                if (i > firstAdLineThreshold && (i - firstAdLineThreshold) % (lineCount - 1) == 0) {
                    newData.add(null)
                    if (i <= adcount) {
                        totalADs++
                        Log.e("******NULL", "addNullValueInsideArray adcount: " + adcount)
                        Log.e("******NULL", "addNullValueInsideArray adcount: " + totalADs)
                    }
                    Log.e("******NULL", "addNullValueInsideArray: null " + i)

                } else if (i == firstAdLineThreshold) {
                    newData.add(null)
                    totalADs++
                    Log.e("******NULL", "addNullValueInsideArray adcount: " + adcount)
                    Log.e("******NULL", "addNullValueInsideArray adcount: " + totalADs)

                    Log.e("******NULL", "addNullValueInsideArray: null first " + i)
                }
                Log.e("******NULL", "addNullValueInsideArray: not null " + i)
                newData.add(data[i])

            }
            Log.e(TAG, "addNullValueInsideArray:size " + newData.size)
        } else {
            for (i in data.indices) {
                if (i > firstAdLineThreshold && (i - firstAdLineThreshold) % (lineCount - 1) == 0) {
                    newData.add(null)
                    if (i <= adcount) {
                        totalADs++
                        Log.e("******NULL", "addNullValueInsideArray adcount: " + adcount)
                        Log.e("******NULL", "addNullValueInsideArray adcount: " + totalADs)
                    }
                    Log.e("******NULL", "addNullValueInsideArray: null " + i)

                } else if (i == firstAdLineThreshold) {
                    newData.add(null)
                    totalADs++
                    Log.e("******NULL", "addNullValueInsideArray adcount: " + adcount)
                    Log.e("******NULL", "addNullValueInsideArray adcount: " + totalADs)

                    Log.e("******NULL", "addNullValueInsideArray: null first " + i)
                }
                Log.e("******NULL", "addNullValueInsideArray: not null " + i)
                newData.add(data[i])

            }
            Log.e("******NULL", "addNullValueInsideArray:size " + newData.size)
        }



        return newData
    }


    private fun sendTracking(
        eventName: String,
        vararg param: Pair<String, String?>
    ) {
        IKTrackingHelper.sendTracking( eventName, *param)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}