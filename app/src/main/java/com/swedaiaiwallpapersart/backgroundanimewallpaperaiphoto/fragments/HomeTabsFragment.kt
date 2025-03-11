package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextPaint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.common.IntentSenderForResultStarter
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.analytics.FirebaseAnalytics
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.DialogFeedbackMomentBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.DialogFeedbackQuestionBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.DialogFeedbackRateBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.DialogThankYouBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentHomeTabsBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.UpdateDialogBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.SaveStateViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.ViewPagerAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.remote.EndPointsInterface
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.SplashOnFragment.Companion.exit
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.batteryanimation.ChargingAnimationFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.doublewallpaper.DoubleWallpaperFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.livewallpaper.LiveWallpaperFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.menuFragments.CategoryFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.menuFragments.HomeFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.FeedbackModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyDialogs
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.RewardedViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import javax.inject.Inject

@AndroidEntryPoint
class HomeTabsFragment : Fragment() {
    private var _binding: FragmentHomeTabsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SaveStateViewModel by viewModels()
    private var existDialog = MyDialogs()
    private lateinit var myActivity: MainActivity
    val sharedViewModel: SharedViewModel by activityViewModels()
    private var isBottomSheetVisible = false

    @Inject
    lateinit var appDatabase: AppDatabase

    @Inject
    lateinit var endPointsInterface: EndPointsInterface
    private var reviewManager: ReviewManager? = null
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val rewardedViewModel: RewardedViewModel by activityViewModels()

    companion object {
        var navigationInProgress = false
    }

    val images = arrayOf(
        R.drawable.tab_icon_popular,
        R.drawable.tab_icon_trending,
        R.drawable.tab_icon_live,
        R.drawable.tab_icon_ai_wallpaper,
        R.drawable.tab_icon_categories,
        R.drawable.tab_icon_generate
    )
    private val tabIconMap = mapOf(
        "Popular" to R.drawable.tab_icon_popular,
        "Trending" to R.drawable.tab_icon_trending,
        "Live" to R.drawable.tab_icon_live,
        "Anime" to R.drawable.anime_tab,
        "Category" to R.drawable.tab_icon_categories,
        "Gen AI" to R.drawable.tab_icon_generate,
        "Charging" to R.drawable.battery_tab,
        "Car" to R.drawable.car_tab,
        "4K" to R.drawable.car_tab,
        "Double" to R.drawable.tab_double_icon
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeTabsBinding.inflate(inflater, container, false)

        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        reviewManager = ReviewManagerFactory.create(requireContext())
        SplashOnFragment.exit = false
        myActivity = activity as MainActivity

        if (AdConfig.iapScreenType == 0) {
            binding.goPremium.visibility = View.GONE
        } else {
            binding.goPremium.visibility = View.GONE
        }

        if (AdConfig.tabPositions[0].isEmpty()) {
            Log.e("TAG", "onViewCreated: " + AdConfig.tabPositions)
            AdConfig.tabPositions =
                arrayOf("Live", "Popular", "Double", "Category", "Anime", "Car", "Charging")
        }

        if (AdConfig.BASE_URL_DATA == "") {
            AdConfig.BASE_URL_DATA = "https://4k-pullzone.b-cdn.net"
        }

        setGradienttext()
        setViewPager()
        handleIntentNotification()
        setEvents()

    }

    private fun handleIntentNotification() {
        val feature = activity?.intent?.getStringExtra("ik_notify_feature")

        Toast.makeText(requireContext(), "Intent feature: $feature", Toast.LENGTH_SHORT).show()
        when (feature) {
            "live_wallpaper_tab" -> openLiveWallpaperTab()
            "tab_popular" -> openPopularScreen()
            "tab_double" -> openDoubleScreen()
            "tab_car" -> openCarScreen()
            "tab_charging" -> openChargingScreen()
            else -> {
                showRewardWallpaperScreen()
            }
        }

    }

    private fun openLiveWallpaperTab() {
        Log.d("FCM", "openLiveWallpaperTab: will open live tab")
        Toast.makeText(requireContext(), "going to live tab", Toast.LENGTH_SHORT).show()
        navigateTOTabs("Live")
    }

    private fun openPopularScreen() {
        Toast.makeText(requireContext(), "going to Popular tab", Toast.LENGTH_SHORT).show()
        Log.d("FCM", "openPopularWallpaperTab: will open popular tab")
        navigateTOTabs("Popular")
    }

    private fun openDoubleScreen() {
        Toast.makeText(requireContext(), "going to Double tab", Toast.LENGTH_SHORT).show()
        Log.d("FCM", "openDoubleWallpaperTab: will open Double tab")
        navigateTOTabs("Double")
    }

    private fun openCarScreen() {
        Log.d("FCM", "openCarWallpaperTab: will open Car tab")
        Toast.makeText(requireContext(), "going to Car tab", Toast.LENGTH_SHORT).show()
        navigateTOTabs("Car")
    }

    private fun openChargingScreen() {
        Toast.makeText(requireContext(), "going to Charging tab", Toast.LENGTH_SHORT).show()
        Log.d("FCM", "openChargingWallpaperTab: will open Charging tab")
        navigateTOTabs("Charging")
    }

    private fun feedback1Sheet() {
        if (isBottomSheetVisible) {
            return
        }
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val binding = DialogFeedbackMomentBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(binding.root)
        binding.feedbackHappy.setOnClickListener {
            MySharePreference.setFeedbackSession1Completed(requireContext(), true)
            MySharePreference.setLastDismissedTime(requireContext(), System.currentTimeMillis())
            bottomSheetDialog.dismiss()
            feedbackRateSheet()
        }

        binding.feedbacksad.setOnClickListener {
            MySharePreference.setFeedbackSession1Completed(requireContext(), true)
            MySharePreference.setLastDismissedTime(requireContext(), System.currentTimeMillis())
            bottomSheetDialog.dismiss()
            feedbackQuestionSheet()
        }

        bottomSheetDialog.setOnDismissListener {
            isBottomSheetVisible = false
            MySharePreference.setLastDismissedTime(requireContext(), System.currentTimeMillis())

        }

        if (isAdded) {
            if (MySharePreference.getFeedbackSession1Completed(requireContext())) {
                MySharePreference.setFeedbackSession2Completed(requireContext(), true)
            }
        }

        binding.cancel.setOnClickListener {
            if (isAdded) {
                MySharePreference.setUserCancelledprocess(requireContext(), true)
                MySharePreference.setLastDismissedTime(requireContext(), System.currentTimeMillis())
            }
            isBottomSheetVisible = false
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
        isBottomSheetVisible = true
    }

    private fun googleInAppRate() {
        try {
            viewLifecycleOwner.lifecycleScope.launch {
                if (isAdded && isResumed) {
                    val request: Task<ReviewInfo> = reviewManager!!.requestReviewFlow()
                    request.addOnCompleteListener { task ->
                        if (isAdded && isResumed) {
                            if (task.isSuccessful) {
                                val reviewInfo: ReviewInfo = task.result
                                val flow: Task<Void> =
                                    reviewManager!!.launchReviewFlow(myActivity, reviewInfo)
                                flow.addOnCompleteListener { _ ->
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

    private fun feedbackRateSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val binding = DialogFeedbackRateBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(binding.root)
        binding.simpleRatingBar.setOnRatingChangeListener { _, _, _ -> }

        binding.buttonApplyWallpaper.setOnClickListener {
            MySharePreference.setReviewedSuccess(requireContext(), true)
            bottomSheetDialog.dismiss()
            if (binding.simpleRatingBar.rating >= 4) {
                googleInAppRate()
            } else {
                feedbackQuestionSheet()
            }
        }

        binding.cancel.setOnClickListener {
            MySharePreference.setUserCancelledprocess(requireContext(), true)
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    private fun feedbackQuestionSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val binding = DialogFeedbackQuestionBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(binding.root)

        var subject = ""

        binding.exitBtn.setOnClickListener {
            MySharePreference.setUserCancelledprocess(requireContext(), true)
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
            if (binding.feedbackEdt.text.isNotEmpty()) {
                try {
                    lifecycleScope.launch(Dispatchers.IO) {
                        MySharePreference.setReviewedSuccess(requireContext(), true)
                        endPointsInterface.postData(
                            FeedbackModel(
                                "From Review",
                                "In app review",
                                subject,
                                binding.feedbackEdt.text.toString(),
                                MySharePreference.getDeviceID(requireContext())!!
                            )
                        )
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Thank you!", Toast.LENGTH_SHORT)
                                .show()
                            bottomSheetDialog.dismiss()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } catch (e: UnknownHostException) {
                    e.printStackTrace()
                }
            }
        }
        bottomSheetDialog.show()
    }

    private fun getUserIdDialog() {
        val dialogBinding = UpdateDialogBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        dialogBinding.btnYes.setOnClickListener {
            launchUpdateFlow()
        }

        dialogBinding.btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun launchUpdateFlow() {
        if (isAdded) {
            val appUpdateManager = AppUpdateManagerFactory.create(requireContext())
            val appUpdateInfoTask = appUpdateManager.appUpdateInfo

            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                ) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            updateResultStarter,
                            AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build(), 150
                        )
                    } catch (exception: IntentSender.SendIntentException) {
                        Toast.makeText(context, "Something wrong went wrong!", Toast.LENGTH_SHORT)
                            .show()
                    }

                }
            }
        }
    }

    private val updateResultStarter =
        IntentSenderForResultStarter { intent, _, fillInIntent, flagsMask, flagsValues, _, _ ->
            val request = IntentSenderRequest.Builder(intent)
                .setFillInIntent(fillInIntent)
                .setFlags(flagsValues, flagsMask)
                .build()
            updateLauncher.launch(request)
        }

    private val updateLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.data == null) return@registerForActivityResult
        if (result.resultCode == 150) {
            Toast.makeText(context, "Downloading stated", Toast.LENGTH_SHORT).show()
            if (result.resultCode != Activity.RESULT_OK) {
                Toast.makeText(context, "Downloading failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setEvents() {

        if (AdConfig.Reward_Screen && MySharePreference.getVIPGiftBool(requireActivity()) && !MySharePreference.isVIPGiftExpired(
                requireActivity()
            )
        ) {
            binding.animation.visibility = View.VISIBLE
        }

        binding.animation.setOnClickListener {
            rewardedViewModel.getAllWallpapers()
            val bundle = Bundle().apply {
                putString("name", "Vip")
                putString("from", "Vip")
            }
            findNavController().navigate(R.id.listViewFragment, bundle)
        }

        binding.settings.setOnClickListener {
            Constants.checkInter = false
            Constants.checkAppOpen = false
            findNavController().navigate(R.id.settingFragment)
        }

        binding.search.setOnClickListener {
            Constants.checkInter = false
            Constants.checkAppOpen = false
            findNavController().navigate(R.id.searchWallpapersFragment)
        }

        binding.goPremium.setOnClickListener {
            findNavController().navigate(R.id.IAPFragment)
        }
        backHandle()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun backHandle() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.viewPager.currentItem != 0) {
                        binding.viewPager.setCurrentItem(0)
                    } else {
                        if (isAdded) {
                            exit = true
                            existDialog.exitPopup(
                                requireContext(),
                                requireActivity(),
                                myActivity
                            )
                        }
                    }
                }
            })
    }

    private fun thankyouDialog() {
        val dialog = Dialog(requireContext())
        val bindingDialog = DialogThankYouBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(bindingDialog.root)
        val width = WindowManager.LayoutParams.MATCH_PARENT
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.setLayout(width, height)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        lifecycleScope.launch {
            delay(3000)
            dialog.dismiss()
            myActivity.finishAffinity()
        }
        dialog.show()
    }

    private fun setGradienttext() {
        val customColors = intArrayOf(
            Color.parseColor("#FC9502"),
            Color.parseColor("#FF6726")
        )
        val paint: TextPaint = binding.toolTxt.paint
        val width: Float = paint.measureText("4K, Wallpaper")

        val shader = LinearGradient(
            0f, 0f, width, binding.toolTxt.textSize,
            customColors, null, Shader.TileMode.CLAMP
        )
        binding.toolTxt.paint.shader = shader
    }

    private fun initTabs() {
        val images = generateImagesArray(AdConfig.tabPositions)
        AdConfig.tabPositions =
            AdConfig.tabPositions.map { if (it == "4K") "Car" else it }.toTypedArray()

        binding.tabLayout.setSelectedTabIndicatorHeight(0)
        val tabCount: Int = binding.tabLayout.tabCount
        for (i in 0 until tabCount) {
            val tab: TabLayout.Tab? = binding.tabLayout.getTabAt(i)
            if (tab != null) {
                tab.setCustomView(R.layout.tab_item)
                val tabCardView = tab.customView!!.findViewById<CardView>(R.id.container)
                val tabIcon = tab.customView!!.findViewById<ImageView>(R.id.icon)
                val tabtitle = tab.customView!!.findViewById<TextView>(R.id.text)

                tabIcon.setImageResource(images[i])
                tabtitle.text = AdConfig.tabPositions[i]

                if (i == 0) {
                    tabCardView.setCardBackgroundColor(resources.getColor(R.color.button_bg))
                    tabIcon.visibility = View.VISIBLE
                }
            }
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.text) {
                    "Live" -> {
                        Constants.checkInter = false
                        Constants.checkAppOpen = false
                        Log.e("TABS", "onTabSelected: " + tab.text)
                    }

                    "Popular" -> {
                        Constants.checkInter = false
                        Constants.checkAppOpen = false
                        Log.e("TABS", "onTabSelected: " + tab.text)
                    }

                    "Category" -> {
                        Constants.checkInter = false
                        Constants.checkAppOpen = false
                        Log.e("TABS", "onTabSelected: " + tab.text)
                    }

                    "Anime" -> {
                        Constants.checkInter = false
                        Constants.checkAppOpen = false
                        Log.e("TABS", "onTabSelected: " + tab.text)
                    }

                    "Car" -> {
                        Constants.checkInter = false
                        Constants.checkAppOpen = false
                    }

                    "Charging" -> {
                        Constants.checkInter = false
                        Constants.checkAppOpen = false
                    }

                    "Gen AI" -> {
                        Constants.checkInter = false
                        Constants.checkAppOpen = false
                    }

                    "Double" -> {
                        Constants.checkInter = false
                        Constants.checkAppOpen = false
                    }
                }
                viewModel.setData(true)
                updateTabAppearance(tab!!, true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                updateTabAppearance(tab!!, false)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun generateImagesArray(tabNames: Array<String>): Array<Int> {
        return tabNames.map { tabIconMap[it.trim()] ?: R.drawable.tab_icon_popular }.toTypedArray()
    }

    private fun setViewPager() {
        val adapter = ViewPagerAdapter(childFragmentManager)

        for (tabName in AdConfig.tabPositions) {
            val fragment = getFragmentForTab(tabName)
            adapter.addFragment(fragment, tabName)
        }

        binding.viewPager.adapter = adapter
        binding.viewPager.offscreenPageLimit = 8
        binding.viewPager.isSaveEnabled = false
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        initTabs()
        binding.viewPager.setCurrentItem(viewModel.getTab(), false)
        updateTabAppearance(binding.tabLayout.getTabAt(viewModel.getTab())!!, true)

        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (isAdded) {
                    viewModel.setTab(position)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun getFragmentForTab(tabName: String): Fragment {
        return when (tabName.trim()) {
            "Popular" -> PopularWallpaperFragment()
            "Car" -> HomeFragment()
            "Live" -> LiveWallpaperFragment()
            "Anime" -> AnimeWallpaperFragment()
            "Category" -> CategoryFragment()
            "Charging" -> ChargingAnimationFragment()
            "Double" -> DoubleWallpaperFragment()

            else -> {
                HomeFragment()
            }
        }
    }

    private fun updateTabAppearance(tab: TabLayout.Tab, isSelected: Boolean) {
        val tabCardView = tab.customView!!.findViewById<CardView>(R.id.container)
        val tabIcon = tab.customView!!.findViewById<ImageView>(R.id.icon)
        val tabtitle = tab.customView!!.findViewById<TextView>(R.id.text)
        if (tabCardView != null) {
            if (isSelected) {
                // Set the stroke color for selected tab
                tabCardView.setCardBackgroundColor(resources.getColor(R.color.button_bg))
            } else {
                // Set the stroke color for unselected tab
                tabCardView.setCardBackgroundColor(resources.getColor(R.color.tabs_bg))
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationManager = NotificationManagerCompat.from(requireContext())
            if (!notificationManager.canUseFullScreenIntent()) {
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT).apply {
                        putExtra(Intent.EXTRA_PACKAGE_NAME, requireContext().packageName)
                    }
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Log.e("usmanTAG", "Activity not found: ${e.localizedMessage}")
                } catch (e: Exception) {
                    Log.e("usmanTAG", "Error launching intent: ${e.localizedMessage}")
                }
            } else {
                Log.d("usmanTAG", "Full-screen intent permission already granted.")
            }
        }
        sharedViewModel.selectTab.observe(viewLifecycleOwner) {
            if (it != null && it != 0) {
                navigateToTrending(it)
                sharedViewModel.selectTab(0)
            }
        }

        if (shouldShowReviewDialog(requireContext())) {
            if (MySharePreference.getartGeneratedFirst(requireContext()) || MySharePreference.getfirstWallpaperSet(
                    requireContext()
                ) || MySharePreference.getfirstLiveWallpaper(requireContext())
            ) {
                Log.e(
                    "TAG",
                    "onResume: getartGeneratedFirst || getfirstWallpaperSet  ||getfirstLiveWallpaper",
                )
                if (!MySharePreference.getReviewedSuccess(requireContext()) && !MySharePreference.getFeedbackSession1Completed(
                        requireContext()
                    )
                ) {
                    if (isAdded) {
                        Log.e(
                            "TAG",
                            "onResume: getReviewedSuccess && getfirstWallpaperSet  ||getfirstLiveWallpaper",
                        )
                        feedback1Sheet()
                    }
                }

            }

            if (!MySharePreference.getReviewedSuccess(requireContext()) && MySharePreference.getFeedbackSession1Completed(
                    requireContext()
                ) && !MySharePreference.getFeedbackSession2Completed(requireContext())
            ) {
                if (isAdded) {
                    feedback1Sheet()
                }
            }
        }

        if (isAdded) {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Home Screen")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, javaClass.simpleName)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }
    }

    private fun shouldShowReviewDialog(context: Context): Boolean {
        val lastDismissedTime = MySharePreference.getLastDismissedTime(context)
        val currentTime = System.currentTimeMillis()
        val twoMinutesInMillis = 4 * 60 * 1000

        return (currentTime - lastDismissedTime) > twoMinutesInMillis
    }

    fun getHomeFragmentIndex(): Int {
        val tabLayout = binding.tabLayout
        val tabCount = tabLayout.tabCount
        for (i in 0 until tabCount) {
            val tab = tabLayout.getTabAt(i)
            val tabTitle = tab?.text?.toString()
            if (tabTitle == getString(R.string.trending)) {
                return i
            }
        }
        return -1 // If HomeFragment is not found
    }

    private fun getTabPositionByName(tabName: String): Int {
        for (i in 0 until binding.tabLayout.tabCount) {
            val tab = binding.tabLayout.getTabAt(i)
            if (tab?.text == tabName) {
                return i
            }
        }
        return -1 // Return -1 if no tab with the specified name is found
    }

    private fun navigateToTrending(index: Int) {
        if (isAdded) {
            binding.viewPager.currentItem = index
        }

    }

    fun navigateTOTabs(tabName: String) {
        val tabPos = getTabPositionByName(tabName)
        Toast.makeText(requireContext(), "Received tab:$tabName", Toast.LENGTH_SHORT).show()
        if (isAdded) {
            navigateToTrending(tabPos)
        }
    }

    private fun isDrawOverlaysPermissionGranted(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

    private fun showRewardWallpaperScreen() {
        // Use a flag to avoid multiple calls if needed
        if (!Constants.hasShownRewardScreen && !AdConfig.ISPAIDUSER) {
            lifecycleScope.launch {
                delay(200)
                if (AdConfig.Reward_Screen) {
                    if (!MySharePreference.getVIPGiftBool(requireActivity())) {
                        if (findNavController().currentDestination?.id == R.id.homeTabsFragment) {
                            findNavController().navigate(R.id.rewardDetailsFragment)
                        }
                    }
                }
            }
        }
    }

}