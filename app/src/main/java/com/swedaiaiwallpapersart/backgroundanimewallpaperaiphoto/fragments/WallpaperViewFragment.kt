package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
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
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.BuildCompat
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
import com.bmik.android.sdk.listener.keep.IKLoadNativeAdListener
import com.bmik.android.sdk.tracking.SDKTrackingController
import com.bmik.android.sdk.widgets.IkmNativeAdView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.BottomSheetInfoBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.DialogCongratulationsBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.DialogFeedbackMomentBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.DialogFeedbackQuestionBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.DialogFeedbackRateBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.DialogUnlockOrWatchAdsBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentWallpaperViewBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.WallpaperApiSliderAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.remote.EndPointsInterface
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.AnimeWallpaperFragment.Companion.hasToNavigateAnime
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.AnimeWallpaperFragment.Companion.wallFromAnime
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.HomeTabsFragment.Companion.navigationInProgress
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.ListViewFragment.Companion.hasToNavigateList
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.ListViewFragment.Companion.wallFromList
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.PopularWallpaperFragment.Companion.hasToNavigate
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.PopularWallpaperFragment.Companion.wallFromPopular
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.menuFragments.HomeFragment.Companion.hasToNavigateHome
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.menuFragments.HomeFragment.Companion.wallFromHome
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.FullViewImage
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.FeedbackModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.PostData
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.RetrofitInstance
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.ApiService
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.SetMostDownloaded
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.BlurView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.GoogleLogin
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyDialogs
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyWallpaperManager
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.PostDataOnServer
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
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
import javax.inject.Inject

@AndroidEntryPoint
class WallpaperViewFragment : Fragment() {
    private var _binding: FragmentWallpaperViewBinding? = null
    private val binding get() = _binding!!
    private var arrayList = ArrayList<CatResponse?>()
    private var isFragmentAttached: Boolean = false
    private var position: Int = 0
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
    private var dialog: Dialog? = null
    private lateinit var myWallpaperManager: MyWallpaperManager
    private var navController: NavController? = null
    val myDialogs = MyDialogs()
    var adcount = 0
    var totalADs = 0
    private val googleLogin = GoogleLogin()
    private lateinit var myActivity: MainActivity
    private var from = ""

    private var wall = ""

    var showInter = true
    val sharedViewModel: SharedViewModel by activityViewModels()

    @Inject
    lateinit var endPointsInterface: EndPointsInterface

    var firstTime = true
    var oldPosition = 0

    val TAG = "SLIDERFRAGMENT"

    @Inject
    lateinit var appDatabase: AppDatabase


    companion object {
        var isNavigated = false
    }


    var adapter: WallpaperApiSliderAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWallpaperViewBinding.inflate(inflater, container, false)
        myWallpaperManager = MyWallpaperManager(requireContext(), requireActivity())

        navigationInProgress = false

        navController = findNavController()

        reviewManager = ReviewManagerFactory.create(requireContext())

        var arrayListJson: ArrayList<CatResponse> = ArrayList()
        sharedViewModel.catResponseList.observe(viewLifecycleOwner) { catResponses ->
            if (catResponses.isNotEmpty()) {
                arrayListJson.clear()

                arrayListJson = catResponses as ArrayList<CatResponse>
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

                    arrayList = addNullValueInsideArray(arrayListOfImages)

                    val firstAdLineThreshold =
                        if (AdConfig.firstAdLineTrending != 0) AdConfig.firstAdLineTrending else 4


                    // Calculate the adjusted position by considering the null ads in the array
                    if (firstTime) {
                        position = 0
                        position = if (pos == firstAdLineThreshold) {
                            pos + totalADs
                        } else if (pos < firstAdLineThreshold) {
                            pos
                        } else {
                            pos + totalADs
                        }

                        firstTime = false
                    }

                    isNavigated = true


                    Log.e(TAG, "new position: $position")




                    Log.d(TAG, "onCreate:  $arrayListOfImages")
                }




                functionality()
            }
        }




        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
//        outState.putInt("currentItem", position)
    }

    // Restore the state
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
//        val currentItem = savedInstanceState?.getInt("currentItem") ?: 0
//        position = currentItem
//        binding.viewPager.setCurrentItem(currentItem, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.adsView.loadAd(requireContext(), "viewlistwallscr_bottom",
            " viewlistwallscr_bottom", object : CustomSDKAdsListenerAdapter() {
                override fun onAdsLoaded() {
                    super.onAdsLoaded()
                    Log.e("*******ADS", "onAdsLoaded: Banner loaded")
                }

                override fun onAdsLoadFail() {
                    super.onAdsLoadFail()

                    if (isAdded) {
//                        binding.adsView.reCallLoadAd(this)
                    }
                    Log.e("*******ADS", "onAdsLoaded: Banner failed")
                }
            })

        if (isAdded) {
            sendTracking(
                "screen_active",
                Pair("action_type", "screen"),
                Pair("action_name", "ViewListWallScr_View")
            )
        }

        backHandle()
    }

    private fun backHandle() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (isAdded) {
                        sendTracking(
                            "click_button",
                            Pair("action_type", "button"),
                            Pair("action_name", "Sytem_BackButton_Click")
                        )
                    }
                    firstTime = true
                    isNavigated = false
                    hasToNavigate = false
                    hasToNavigateHome = false
                    hasToNavigateAnime = false
                    hasToNavigateList = false
                    navController?.popBackStack()
                }
            })

        if (BuildCompat.isAtLeastT()) {
            requireActivity().onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                if (isAdded) {
                    sendTracking(
                        "click_button",
                        Pair("action_type", "button"),
                        Pair("action_name", "Sytem_BackButton_Click")
                    )
                }
                firstTime = true
                isNavigated = false
                hasToNavigate = false
                hasToNavigateList = false
                hasToNavigateAnime = false
                hasToNavigateHome = false
                navController?.popBackStack()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    private fun addNullValueInsideArray(data: List<CatResponse?>): ArrayList<CatResponse?> {

        totalADs = 0
        val firstAdLineThreshold =
            if (AdConfig.firstAdLineTrending != 0) AdConfig.firstAdLineTrending else 4

        val lineCount = if (AdConfig.lineCountTrending != 0) AdConfig.lineCountTrending else 5
        val newData = arrayListOf<CatResponse?>()

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
            isNavigated = false
            hasToNavigate = false
            hasToNavigateHome = false
            hasToNavigateAnime = false
            hasToNavigateList = false
            navController?.popBackStack()
        }
        setViewPager()
        checkRedHeart(position)
        if (arrayList[position] != null) {
            getLargImage = AdConfig.BASE_URL_DATA + "/images/" +arrayList[position]?.hd_image_url!!
            getSmallImage = AdConfig.BASE_URL_DATA + "/compress/" +arrayList[position]?.compressed_image_url!!
            getBitmapFromGlide(getLargImage)


        }


        binding.buttonApplyWallpaper.setOnClickListener {
//           if(arrayList[position]?.gems==0 || arrayList[position]?.unlockimges==true){
            if (bitmap != null) {
                if (arrayList[position]?.unlockimges == true) {
                    val model = arrayList[position]
                    openPopupMenu(model!!)
                } else {

                    if (AdConfig.ISPAIDUSER) {
                        val model = arrayList[position]
                        openPopupMenu(model!!)
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
//           }else{
//               Toast.makeText(requireContext(), "Please first buy your wallpaper", Toast.LENGTH_SHORT).show()
//           }

        }
        binding.favouriteButton.setOnClickListener {

            if (isAdded) {
                sendTracking(
                    "click_button",
                    Pair("action_type", "button"),
                    Pair("action_name", "ViewListWallScr_FavoriteBT_Click")
                )
            }
            binding.favouriteButton.isEnabled = false
            if (arrayList[position]?.liked == true) {
                arrayList[position]?.liked = false
                arrayList[position]?.id?.let { it1 ->
                    appDatabase.wallpapersDao().updateLiked(
                        false,
                        it1
                    )
                }
                binding.favouriteButton.setImageResource(R.drawable.button_like)
            } else {
                arrayList[position]?.liked = true
                arrayList[position]?.id?.let { it1 ->
                    appDatabase.wallpapersDao().updateLiked(
                        true,
                        it1
                    )
                }

                binding.favouriteButton.setImageResource(R.drawable.button_like_selected)
            }
            adapter?.notifyItemChanged(position)
            viewPager2?.invalidate()
            binding.viewPager.setCurrentItem(position, true)
            try {
                val countOfNulls = arrayList.subList(0, position).count { it == null }
                arrayList[position]?.let { it1 -> sharedViewModel.updateCatResponseAtIndex(it1,countOfNulls) }
            }catch (e:IndexOutOfBoundsException){
                e.printStackTrace()
            }catch (e:Exception){
                e.printStackTrace()
            }
            addFavourite(requireContext(), position, binding.favouriteButton)


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

        }


        binding.unlockWallpaper.setOnClickListener {

            if (bitmap != null) {
                SDKBaseController.getInstance().showRewardedAds(
                    requireActivity(),
                    "viewlistwallscr_item_vip_reward",
                    "viewlistwallscr_item_vip_reward",
                    object :
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
                            postData.unLocking(
                                MySharePreference.getDeviceID(requireContext())!!,
                                model1!!, requireContext(), 0
                            )
                            showInter = false
                            binding.buttonApplyWallpaper.visibility = View.VISIBLE
                            binding.unlockWallpaper.visibility = View.GONE
                            adapter?.notifyItemChanged(position)
                            viewPager2?.invalidate()
                            binding.viewPager.setCurrentItem(position, true)
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
                                        if (isAdded) {
                                            Toast.makeText(
                                                requireContext(),
                                                "Ad not available, Please try again later",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }

                                    override fun onAdsDismiss() {
                                        Log.e("********ADS", "onAdsRewarded: ")
                                        val postData = PostDataOnServer()
                                        val model = arrayList[position]
                                        arrayList[position]?.unlockimges = true
                                        arrayList[position]?.gems = 0

                                        val model1 = arrayList[position]
                                        postData.unLocking(
                                            MySharePreference.getDeviceID(requireContext())!!,
                                            model1!!, requireContext(), 0
                                        )
                                        showInter = false
                                        binding.buttonApplyWallpaper.visibility = View.VISIBLE
                                        binding.unlockWallpaper.visibility = View.GONE
                                        adapter?.notifyItemChanged(position)
                                        viewPager2?.invalidate()
                                        binding.viewPager.setCurrentItem(position, true)
                                    }
                                }
                            )


                        }

                    })
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.your_image_not_fetched_properly), Toast.LENGTH_SHORT
                ).show()
            }


        }
        binding.downloadWallpaper.setOnClickListener {
            Log.e("TAG", "functionality: inside click")


            if (isAdded) {
                sendTracking(
                    "click_button",
                    Pair("action_type", "button"),
                    Pair("action_name", "ViewListWallScr_SaveBT_Click")
                )
            }
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
                        STORAGE_PERMISSION_CODE
                    )
                } else {
                    Log.e("TAG", "functionality: inside click dialog")
                    if (AdConfig.ISPAIDUSER) {
                        mSaveMediaToStorage(bitmap)
                        val model = arrayList[position]
                        model?.let { it1 -> setDownloaded(it1) }
                    } else {

                        getUserIdDialog()
                    }
                }
            } else {
                if (AdConfig.ISPAIDUSER) {
                    mSaveMediaToStorage(bitmap)
                    val model = arrayList[position]
                    model?.let { it1 -> setDownloaded(it1) }
                } else {

                    getUserIdDialog()
                }
            }

        }
    }

    private val fragmentScope: CoroutineScope by lazy { MainScope() }
    private fun setViewPager() {
        adapter = WallpaperApiSliderAdapter(arrayList, object : FullViewImage {
            override fun getFullImageUrl(image: CatResponse) {
                if (isAdded) {
                    sendTracking(
                        "click_button",
                        Pair("action_type", "ITEM"),
                        Pair("action_name", "ViewListWallScr_Item_Click")
                    )
                }

                sharedViewModel.selectCat(image)

                sharedViewModel.setPosition(position)

                navController?.navigate(R.id.fullScreenImageViewFragment)
            }
        }, myActivity)
        adapter!!.setCoroutineScope(fragmentScope)

        SDKBaseController.getInstance().loadIkmNativeAdView(
            requireContext(),
            "viewlistwallscr_scrollview",
            "viewlistwallscr_scrollview",
            object :
                IKLoadNativeAdListener {
                override fun onAdFailedToLoad(errorCode: Int) {
                    Log.e(TAG, "onAdFailedToLoad: " + errorCode)

                }

                override fun onAdLoaded(adsResult: IkmNativeAdView?) {
                    if (isAdded && view != null) {
                        adapter?.nativeAdView = adsResult
                        viewPager2?.adapter = adapter
                        viewPager2?.setCurrentItem(position, false)

                    }
                }

            })
        viewPager2?.adapter = adapter
        Log.e(TAG, "setViewPager: " + position)
        viewPager2?.setCurrentItem(position, false)


        if (arrayList[position]?.unlockimges == true) {
            binding.unlockWallpaper.visibility = View.GONE
            binding.buttonApplyWallpaper.visibility = View.VISIBLE
        } else {
            binding.unlockWallpaper.visibility = View.GONE
            binding.buttonApplyWallpaper.visibility = View.VISIBLE
        }

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
                    if (arrayList[positi]?.hd_image_url != null) {
                        getLargImage = AdConfig.BASE_URL_DATA + "/images/" +arrayList[positi]?.hd_image_url!!
                        getSmallImage = AdConfig.BASE_URL_DATA + "/compress/" +arrayList[positi]?.compressed_image_url!!

                        position = positi


                        if (arrayList[position]?.gems == 0 || arrayList[position]?.unlockimges == true) {

                            binding.unlockWallpaper.visibility = View.GONE
                            binding.buttonApplyWallpaper.visibility = View.VISIBLE
                        } else {
                            binding.unlockWallpaper.visibility = View.GONE
                            binding.buttonApplyWallpaper.visibility = View.VISIBLE
                        }
                    } else {
                        position = positi
                    }




                    if (arrayList[positi]?.hd_image_url == null) {
                        binding.unlockWallpaper.visibility = View.GONE
                        binding.buttonApplyWallpaper.visibility = View.GONE
                        binding.bottomMenu.visibility = View.GONE

                        binding.adsView.visibility = View.GONE
                    } else {
                        binding.bottomMenu.visibility = View.VISIBLE
                        binding.adsView.visibility = View.VISIBLE

                        if (arrayList[position]?.gems == 0 || arrayList[position]?.unlockimges == true) {

                            binding.unlockWallpaper.visibility = View.GONE
                            binding.buttonApplyWallpaper.visibility = View.VISIBLE
                        } else {
                            binding.unlockWallpaper.visibility = View.GONE
                            binding.buttonApplyWallpaper.visibility = View.VISIBLE
                        }

                    }
                    Log.e(TAG, "onPageSelected: "+position )

                    checkRedHeart(position)
                    getBitmapFromGlide(getLargImage)
                }
            }
        }
        viewPager2?.registerOnPageChangeCallback(viewPagerChangeCallback)
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
            if (bitmap != null) {
                SDKBaseController.getInstance().showRewardedAds(
                    requireActivity(),
                    "viewlistwallscr_item_vip_reward",
                    "viewlistwallscr_item_vip_reward",
                    object :
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

                            model?.id?.let { it1 ->
                                appDatabase.wallpapersDao().updateLocked(
                                    true,
                                    it1
                                )
                            }
                            adapter?.notifyItemChanged(position)
                            viewPager2?.invalidate()
                            binding.viewPager.setCurrentItem(position, true)
                            openPopupMenu(model!!)
                        }

                        override fun onAdsShowFail(errorCode: Int) {
                            Log.e("********ADS", "onAdsShowFail: ")
                            if (isAdded) {
                                Toast.makeText(
                                    requireContext(),
                                    "Ad not available, Try again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    })
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.your_image_not_fetched_properly), Toast.LENGTH_SHORT
                ).show()
            }
        }

        bindingDialog.upgradeButton?.setOnClickListener {
            dialog?.dismiss()
            findNavController().navigate(R.id.IAPFragment)
        }
        bindingDialog.cancelDialog?.setOnClickListener {
            dialog?.dismiss()
        }

        dialog?.show()
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
//                    if(arrayList[position]?.gems==0 || arrayList[position]?.unlockimges==true){
                        mSaveMediaToStorage(bitmap)
                        val model = arrayList[position]

                        model?.let { it1 -> setDownloaded(it1) }


//                    openPopupMenu(model!!)
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
                                    if (isAdded) {
                                        Toast.makeText(
                                            requireContext(),
                                            "Ad not available, Please try again later",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
//                                }else{
//                                    Toast.makeText(requireContext(), "Please first buy your wallpaper", Toast.LENGTH_SHORT).show()
//
//                                }
                                    //do something
                                }

                                override fun onAdsDismiss() {
//                                if(arrayList[position]?.gems==0 || arrayList[position]?.unlockimges==true){
                                    mSaveMediaToStorage(bitmap)
                                    val model = arrayList[position]
                                    model?.let { it1 -> setDownloaded(it1) }
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

    private fun checkRedHeart(position: Int) {
        if (isAdded) {
            Log.e(TAG, "checkRedHeart: "+arrayList[position]?.liked )
            if (arrayList[position]?.liked == true) {
                Log.e(TAG, "checkRedHeart: liked" )
                binding.favouriteButton.setImageResource(R.drawable.button_like_selected)
            } else {
                Log.e(TAG, "checkRedHeart: like" )
                binding.favouriteButton.setImageResource(R.drawable.button_like)
            }
        }
    }

    private fun addFavourite(
        context: Context,
        position: Int,
        favouriteButton: ImageView
    ) {

        val id = arrayList[position]?.id.toString()


        val retrofit = RetrofitInstance.getInstance()
        val apiService = retrofit.create(ApiService::class.java)
        val postData =
            PostData(MySharePreference.getDeviceID(context)!!, id)
        val call = apiService.postData(postData)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val message = response.body()?.string()
//                    if (message == "Liked") {
//                        arrayList[position]?.liked = true
//                    } else {
//                        arrayList[position]?.liked = false
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

    private suspend fun loadBitmapFromUrlAsync(urlString: String): Bitmap? =
        withContext(Dispatchers.IO) {
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
                    if (response.isSuccessful) {


                        Log.e("TAG", "onResponse: success" + response.body().toString())
                    } else {
                        Log.e("TAG", "onResponse: not success")
//                        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("TAG", "onResponse: failed")
//                    Toast.makeText(requireContext(), "onFailure error", Toast.LENGTH_SHORT).show()
                }
            })
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

    //    private fun congratulationsDialog() {
//        val dialog = Dialog(requireContext())
//        val bindingDialog = DialogCongratulationsBinding.inflate(LayoutInflater.from(requireContext()))
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setContentView(bindingDialog.root)
//        val width = WindowManager.LayoutParams.MATCH_PARENT
//        val height = WindowManager.LayoutParams.WRAP_CONTENT
//        dialog.window!!.setLayout(width, height)
//        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
//        dialog.setCancelable(false)
////        var getReward = dialog?.findViewById<LinearLayout>(R.id.buttonGetReward)
//
//
//        bindingDialog.continueBtn.setOnClickListener {
//            dialog.dismiss()
//            if (AdConfig.regularWallpaperFlow == 1){
//                if (wall == "home"){
//                    wallFromHome = true
//                }else if (wall == "popular"){
//                    wallFromPopular = true
//                }else if (wall == "anime"){
//                    wallFromAnime = true
//                }else{
//                   wallFromList = true
//                }
//                if (from == "trending"){
//                    findNavController().popBackStack(R.id.homeTabsFragment, false)
//                }else{
//                    findNavController().popBackStack(R.id.listViewFragment,false)
//                }
//            }else if (AdConfig.regularWallpaperFlow == 2){
//                //do nothing
//            }else{
//                //doniothing
//            }
//
//        }
//
//        dialog.show()
//    }
    @SuppressLint("ResourceType")
    private fun openPopupMenu(model: CatResponse) {
        if (isAdded) {
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
                if (isAdded) {
                    sendTracking(
                        "click_button",
                        Pair("action_type", "button"),
                        Pair("action_name", "SetWallpaperDlg_Xbt_Click")
                    )


                    dialog.dismiss()
                }
            }




            if (isAdded) {
                sendTracking(
                    "screen_active",
                    Pair("action_type", "layout"),
                    Pair("action_name", "SetWallpaperDlg_View")
                )
            }
            buttonHome.setOnClickListener {
                if (isAdded) {
                    sendTracking(
                        "click_button",
                        Pair("action_type", "button"),
                        Pair("action_name", "SetWallpaperDlg_Homebt_Click")
                    )

                    settrackingWallpaperType(model)

                }
                if (isAdded) {
                    if (showInter) {
                        SDKBaseController.getInstance().showInterstitialAds(
                            requireActivity(),
                            "viewlistwallscr_setdilog_set_button",
                            "viewlistwallscr_setdilog_set_button",
                            showLoading = true,
                            adsListener = object : CommonAdsListenerAdapter() {
                                override fun onAdsShowFail(errorCode: Int) {
                                    myExecutor.execute { myWallpaperManager.homeScreen(bitmap!!) }
                                    myHandler.post {
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

                                    setDownloaded(model)
                                    showRateApp()
                                    Log.e("********ADS", "onAdsShowFail: " + errorCode)
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

                                            setDownloaded(model)


                                        } catch (e: Exception) {
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
                                }
                            }
                        )
                    } else {
                        myExecutor.execute { myWallpaperManager.homeScreen(bitmap!!) }
                        myHandler.post {
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
                        showRateApp()
                        showInter = true
                    }
                } else {
                    dialog.dismiss()
                }


            }
            buttonLock.setOnClickListener {
                if (isAdded) {
                    sendTracking(
                        "click_button",
                        Pair("action_type", "button"),
                        Pair("action_name", "SetWallpaperDlg_Lockbt_Click")
                    )
                    settrackingWallpaperType(model)

                }
                if (isAdded) {
                    if (showInter) {
                        SDKBaseController.getInstance().showInterstitialAds(
                            requireActivity(),
                            "viewlistwallscr_setdilog_set_button",
                            "viewlistwallscr_setdilog_set_button",
                            showLoading = true,
                            adsListener = object : CommonAdsListenerAdapter() {
                                override fun onAdsShowFail(errorCode: Int) {
                                    Log.e("********ADS", "onAdsShowFail: " + errorCode)
                                    myExecutor.execute {
                                        bitmap?.let { it1 -> myWallpaperManager.lockScreen(it1) }
                                    }
                                    myHandler.post {
                                        if (state) {
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

                                    setDownloaded(model)
                                    showRateApp()
                                    //do something
                                }

                                override fun onAdsDismiss() {
                                    myExecutor.execute {
                                        myWallpaperManager.lockScreen(bitmap!!)
                                    }
                                    myHandler.post {
                                        if (state) {
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

                                    setDownloaded(model)
                                    showRateApp()

                                }
                            }
                        )
                    } else {
                        myExecutor.execute {
                            myWallpaperManager.lockScreen(bitmap!!)
                        }
                        myHandler.post {
                            if (state) {
                                interstitialAdWithToast(
                                    getString(R.string.set_successfully_on_lock_screen),
                                    dialog
                                )
                                state = false
                                postDelay()
                            }
                        }
                        showRateApp()
                        showInter = true
                    }
                } else {
                    dialog.dismiss()
                }


            }
            buttonBothScreen.setOnClickListener {
                if (isAdded) {
                    sendTracking(
                        "click_button",
                        Pair("action_type", "button"),
                        Pair("action_name", "SetWallpaperDlg_Bothbt_Click")
                    )
                    settrackingWallpaperType(model)
                }

                if (isAdded) {
                    if (showInter) {
                        SDKBaseController.getInstance().showInterstitialAds(
                            requireActivity(),
                            "viewlistwallscr_setdilog_set_button",
                            "viewlistwallscr_setdilog_set_button",
                            showLoading = true,
                            adsListener = object : CommonAdsListenerAdapter() {
                                override fun onAdsShowFail(errorCode: Int) {
                                    Log.e("********ADS", "onAdsShowFail: " + errorCode)
                                    myExecutor.execute {
                                        myWallpaperManager.homeAndLockScreen(bitmap!!)
                                    }
                                    myHandler.post {
                                        if (state) {
                                            if (isAdded) {
                                                interstitialAdWithToast(
                                                    getString(R.string.set_successfully_on_both),
                                                    dialog
                                                )

                                            }
                                            state = false
                                            postDelay()
                                        }
                                    }
                                    setDownloaded(model)
                                    showRateApp()
                                    //do something
                                }

                                override fun onAdsDismiss() {
                                    myExecutor.execute {
                                        myWallpaperManager.homeAndLockScreen(bitmap!!)
                                    }
                                    myHandler.post {
                                        if (state) {
                                            if (isAdded) {
                                                interstitialAdWithToast(
                                                    getString(R.string.set_successfully_on_both),
                                                    dialog
                                                )
                                            }
                                            state = false
                                            postDelay()
                                        }
                                    }
                                    setDownloaded(model)
                                    showRateApp()
                                }
                            }
                        )
                    } else {
                        myExecutor.execute {
                            myWallpaperManager.homeAndLockScreen(bitmap!!)
                        }
                        myHandler.post {
                            if (state) {
                                interstitialAdWithToast(
                                    getString(R.string.set_successfully_on_both),
                                    dialog
                                )
                                state = false
                                postDelay()
                            }
                        }
                        showRateApp()
                        showInter = true
                    }
                } else {
                    dialog.dismiss()
                }


            }
            dialog.show()

            if (!isAdded) {
                dialog.dismiss()
            }
        }

    }

    fun settrackingWallpaperType(model: CatResponse) {
        sendTracking("typewallpaper_used",Pair("typewallpaper", "regular"))
        sendTracking("category_used",Pair("category", model.cat_name))
    }

    fun resizeBitmap(originalBitmap: Bitmap): Bitmap {
        val width = originalBitmap.width / 2
        val height = originalBitmap.height / 2
        return Bitmap.createScaledBitmap(originalBitmap, width, height, true)
    }

    private fun postDelay() {
        Handler().postDelayed({
            state = true
        }, 5000)
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
                    STORAGE_PERMISSION_CODE
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.permission_granted_click_again_to_save_image),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.storage_permission_denied), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun sendTracking(
        eventName: String,
        vararg param: Pair<String, String?>
    ) {
        SDKTrackingController.trackingAllApp(requireContext(), eventName, *param)
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
        try {
            viewLifecycleOwner.lifecycleScope.launch {
                if (isAdded && isResumed) {
                    MySharePreference.firstWallpaperSet(requireContext(), true)
//                   val request: Task<ReviewInfo> = reviewManager!!.requestReviewFlow()
//                   request.addOnCompleteListener { task ->
                    if (isAdded && isResumed) {
//                           if (task.isSuccessful) {
//                               val reviewInfo: ReviewInfo = task.result
//                               val flow: Task<Void> = reviewManager!!.launchReviewFlow(myActivity, reviewInfo)
//                               flow.addOnCompleteListener { task1 ->
//                                   Log.e(TAG, "showRateApp:  addOnCompleteListener"+task1.exception +task1.result )
                        Log.e(TAG, "showRateApp: " + AdConfig.regularWallpaperFlow)
                        if (AdConfig.regularWallpaperFlow == 1) {
                            if (wall == "home") {
                                wallFromHome = true
                            } else if (wall == "popular") {
                                wallFromPopular = true
                            } else if (wall == "anime") {
                                wallFromAnime = true
                            } else {
                                wallFromList = true
                            }
                            firstTime = true
                            isNavigated = false
                            hasToNavigate = false
                            hasToNavigateHome = false
                            hasToNavigateAnime = false
                            hasToNavigateList = false
                            if (from == "trending") {
                                if (isAdded) {

                                    findNavController().popBackStack(R.id.homeTabsFragment, false)
                                }

                            } else {
                                if (isAdded) {
                                    findNavController().popBackStack(R.id.listViewFragment, false)
                                }
                            }
                        } else if (AdConfig.regularWallpaperFlow == 2) {
                            if (isAdded) {
                                congratulationsDialog()

                            }
                            //do nothing
                        } else {
                            //doniothing
                            if (MySharePreference.getartGeneratedFirst(requireContext()) || MySharePreference.getfirstWallpaperSet(requireContext()) || MySharePreference.getfirstLiveWallpaper(requireContext())){
                                Log.e("TAG", "onResume: getartGeneratedFirst || getfirstWallpaperSet  ||getfirstLiveWallpaper", )
                                if (!MySharePreference.getReviewedSuccess(requireContext()) && !MySharePreference.getFeedbackSession1Completed(requireContext())){
                                    if (isAdded){
                                        Log.e("TAG", "onResume: getReviewedSuccess && getfirstWallpaperSet  ||getfirstLiveWallpaper", )
                                        feedback1Sheet()
                                    }
                                }

                            }

                            if (!MySharePreference.getReviewedSuccess(requireContext()) && MySharePreference.getFeedbackSession1Completed(requireContext()) && !MySharePreference.getFeedbackSession2Completed(requireContext())){
                                if (isAdded){
                                    feedback1Sheet()
                                }
                            }
                        }
//                               }
//                           }else{
//                               @ReviewErrorCode val reviewErrorCode = (task.getException() as ReviewException).errorCode
//                               Log.e(TAG, "showRateApp: "+task.exception?.localizedMessage )
//
//                           }
                    }
//                   }
//                       .addOnFailureListener {it ->
//                           it.printStackTrace()
//                       }
                }
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    private fun congratulationsDialog() {
        val dialog = Dialog(requireContext())
        val bindingDialog =
            DialogCongratulationsBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(bindingDialog.root)
        val width = WindowManager.LayoutParams.MATCH_PARENT
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.setLayout(width, height)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
//        var getReward = dialog?.findViewById<LinearLayout>(R.id.buttonGetReward)


        bindingDialog.continueBtn.setOnClickListener {
            if (MySharePreference.getartGeneratedFirst(requireContext()) || MySharePreference.getfirstWallpaperSet(requireContext()) || MySharePreference.getfirstLiveWallpaper(requireContext())){
                Log.e("TAG", "onResume: getartGeneratedFirst || getfirstWallpaperSet  ||getfirstLiveWallpaper", )
                if (!MySharePreference.getReviewedSuccess(requireContext()) && !MySharePreference.getFeedbackSession1Completed(requireContext())){
                    if (isAdded){
                        Log.e("TAG", "onResume: getReviewedSuccess && getfirstWallpaperSet  ||getfirstLiveWallpaper", )
                        feedback1Sheet()
                    }
                }

            }

            if (!MySharePreference.getReviewedSuccess(requireContext()) && MySharePreference.getFeedbackSession1Completed(requireContext()) && !MySharePreference.getFeedbackSession2Completed(requireContext())){
                if (isAdded){
                    feedback1Sheet()
                }
            }
            dialog.dismiss()
        }

        dialog.show()
    }


    fun feedback1Sheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val binding = DialogFeedbackMomentBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(binding.root)
        binding.feedbackHappy.setOnClickListener {
            MySharePreference.setFeedbackSession1Completed(requireContext(),true)
            bottomSheetDialog.dismiss()
            feedbackRateSheet()

        }

        binding.feedbacksad.setOnClickListener {
            MySharePreference.setFeedbackSession1Completed(requireContext(),true)
            bottomSheetDialog.dismiss()
            feedbackQuestionSheet()
        }

        if (isAdded){
            if (MySharePreference.getFeedbackSession1Completed(requireContext())){
                MySharePreference.setFeedbackSession2Completed(requireContext(),true)
            }
        }


        binding.cancel.setOnClickListener {
            if (isAdded){
                MySharePreference.setUserCancelledprocess(requireContext(),true)
            }
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    fun googleInAppRate() {
        try {
            viewLifecycleOwner.lifecycleScope.launch {
                if (isAdded && isResumed) {
                    val request: Task<ReviewInfo> = reviewManager!!.requestReviewFlow()
                    request.addOnCompleteListener { task ->
                        if (isAdded && isResumed) {
                            if (task.isSuccessful) {
                                val reviewInfo: ReviewInfo = task.result
                                val flow: Task<Void> =
                                    reviewManager!!.launchReviewFlow(myActivity!!, reviewInfo)
                                flow.addOnCompleteListener { task1 ->

                                }
                            }
                        }
                    }.addOnFailureListener { it ->
                        it.printStackTrace()
                    }

                }
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    fun feedbackRateSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val binding = DialogFeedbackRateBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(binding.root)
        binding.simpleRatingBar.setOnRatingChangeListener { ratingBar, rating, fromUser ->

        }

        binding.buttonApplyWallpaper.setOnClickListener {
            MySharePreference.setReviewedSuccess(requireContext(),true)
            bottomSheetDialog.dismiss()
            if (binding.simpleRatingBar.rating >= 4) {
                googleInAppRate()
            } else {
                feedbackQuestionSheet()
            }
        }

        binding.cancel.setOnClickListener {
            MySharePreference.setUserCancelledprocess(requireContext(),true)
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    fun feedbackQuestionSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val binding = DialogFeedbackQuestionBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(binding.root)

        var subject = ""

        binding.exitBtn.setOnClickListener {
            MySharePreference.setUserCancelledprocess(requireContext(),true)
            bottomSheetDialog.dismiss()
        }

        binding.probExperience.setOnClickListener {
            subject = "Experience"
            binding.probExperience.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.button_bg))
            binding.probCrash.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.light))
            binding.probSlow.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.light))
            binding.probSuggestion.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.light))
            binding.probOthers.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.light))

        }

        binding.probCrash.setOnClickListener {
            subject = "Crash & Bugs"
            binding.probExperience.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.light))
            binding.probCrash.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.button_bg))
            binding.probSlow.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.light))
            binding.probSuggestion.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.light))
            binding.probOthers.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.light))
        }

        binding.probSlow.setOnClickListener {
            subject = "Slow Performance"
            binding.probExperience.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.light))
            binding.probCrash.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.light))
            binding.probSlow.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.button_bg))
            binding.probSuggestion.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.light))
            binding.probOthers.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.light))
        }

        binding.probSuggestion.setOnClickListener {
            subject = "Suggestion"
            binding.probExperience.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.light))
            binding.probCrash.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.light))
            binding.probSlow.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.light))
            binding.probSuggestion.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.button_bg))
            binding.probOthers.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.light))
        }

        binding.probOthers.setOnClickListener {
            subject = "Others"
            binding.probExperience.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.light))
            binding.probCrash.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.light))
            binding.probSlow.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.light))
            binding.probSuggestion.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.light))
            binding.probOthers.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.button_bg))
        }


        binding.buttonApplyWallpaper.setOnClickListener {
            if (binding.feedbackEdt.text.isNotEmpty()){
                lifecycleScope.launch(Dispatchers.IO) {
                    MySharePreference.setReviewedSuccess(requireContext(),true)
                    endPointsInterface.postData(
                        FeedbackModel("From Review","In app review",subject,binding.feedbackEdt.text.toString(),
                            MySharePreference.getDeviceID(requireContext())!!
                        )
                    )

                     withContext(Dispatchers.Main){
                        Toast.makeText(requireContext(),"Thank you!",Toast.LENGTH_SHORT).show()
                         bottomSheetDialog.dismiss()
                    }
                }
            }
        }
        bottomSheetDialog.show()
    }


    fun imageDetailsSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val binding = BottomSheetInfoBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(binding.root)

        binding.btnYes.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        if (arrayList[position] != null) {
            binding.imageName.text = arrayList[position]?.image_name
            binding.imageSize.text = arrayList[position]?.img_size.toString() + " Kb"
            binding.imageCapacity.text = arrayList[position]?.capacity
            binding.imageTags.text = arrayList[position]?.Tags
        } else {
            Toast.makeText(
                requireContext(),
                "This is Ad position,No info Available",
                Toast.LENGTH_SHORT
            ).show()
        }



        bottomSheetDialog.show()
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