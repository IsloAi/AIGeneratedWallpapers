package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT
import android.text.TextPaint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.CustomSDKAdsListenerAdapter
import com.bmik.android.sdk.listener.keep.SDKNewVersionUpdateCallback
import com.bmik.android.sdk.model.dto.UpdateAppDto
import com.bmik.android.sdk.widgets.IkmWidgetAdLayout
import com.bmik.android.sdk.widgets.IkmWidgetAdView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.common.IntentSenderForResultStarter
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.analytics.FirebaseAnalytics
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.DialogFeedbackMomentBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.DialogFeedbackQuestionBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.DialogFeedbackRateBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentHomeTabsBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.UpdateDialogBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.SaveStateViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.ViewPagerAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.SplashOnFragment.Companion.exit
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.batteryanimation.ChargingAnimationFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.livewallpaper.LiveWallpaperFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.menuFragments.CategoryFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.menuFragments.HomeFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.fragmentsIG.GenerateImageFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyDialogs
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class HomeTabsFragment : Fragment() {
    private var _binding:FragmentHomeTabsBinding ?= null
    private val binding get() = _binding!!

    private val viewModel: SaveStateViewModel by viewModels()

    private var existDialog = MyDialogs()
    private lateinit var myActivity : MainActivity

    val sharedViewModel: SharedViewModel by activityViewModels()


    @Inject
    lateinit var appDatabase: AppDatabase



    private lateinit var firebaseAnalytics: FirebaseAnalytics

    companion object{
        var navigationInProgress = false
    }


    val images = arrayOf(R.drawable.tab_icon_popular,R.drawable.tab_icon_trending,R.drawable.tab_icon_live,R.drawable.tab_icon_ai_wallpaper,R.drawable.tab_icon_categories,R.drawable.tab_icon_generate)
    private val tabIconMap = mapOf(
        "Popular" to R.drawable.tab_icon_popular,
        "Trending" to R.drawable.tab_icon_trending,
        "Live" to R.drawable.tab_icon_live,
        "Anime" to R.drawable.anime_tab,
        "Category" to R.drawable.tab_icon_categories,
        "Gen AI" to R.drawable.tab_icon_generate,
        "Charging" to R.drawable.battery_tab,
        "Car" to R.drawable.car_tab,
        "4K" to R.drawable.car_tab

    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeTabsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        SplashOnFragment.exit = false
            myActivity = activity as MainActivity

        if (AdConfig.iapScreenType == 0){
            binding.goPremium.visibility = View.GONE
        }else{
            if (AdConfig.ISPAIDUSER){
                binding.goPremium.visibility = View.GONE
            }else{
                binding.goPremium.visibility = View.VISIBLE
            }
        }
        Log.e("TAG", "onViewCreated: "+AdConfig.tabPositions.contentToString() )
        if (AdConfig.tabPositions[0].isEmpty()){
            Log.e("TAG", "onViewCreated: "+AdConfig.tabPositions )
            AdConfig.tabPositions = arrayOf("Live", "Popular", "Category", "Anime", "Car", "Charging", "Gen AI")

        }else{
//            AdConfig.tabPositions = AdConfig.tabPositions.filter { it != "Charging" }.toTypedArray()
        }

        loadbannerAd()
        setGradienttext()
        setViewPager()
        initTabs()
        setEvents()
        lifecycleScope.launch {
            SDKBaseController.getInstance().checkUpdateApp(object: SDKNewVersionUpdateCallback {
                override fun onUpdateAvailable(updateDto: UpdateAppDto?) {

                    try {
                        val pInfo: PackageInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
                        val version = pInfo.versionName
                        val versionCode = pInfo.versionCode

                        if (versionCode < updateDto?.minVersionCode!!){
                            if (updateDto.forceUpdateApp){
                                getUserIdDialog()
                            }else{
                                launchUpdateFlow()
                            }

                        }

                        Log.e("TAG", "onUpdateAvailable: "+version +versionCode )
                    } catch (e: PackageManager.NameNotFoundException) {
                        e.printStackTrace()
                    }

                }

                override fun onUpdateFail() {
                    Log.e("TAG", "onUpdateFail: " )

                }

            })

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (isAdded) {
                try {
                    if (NotificationManagerCompat.from(requireContext()).canUseFullScreenIntent()) {
                        Log.e("TAG", "onViewCreated: canUseFullScreenIntent" )

                    } else {
                        val intent = Intent(ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT)
                        intent.putExtra(Intent.EXTRA_PACKAGE_NAME, requireContext().packageName)
                        requireContext().startActivity(intent)
                        Log.e("TAG", "onViewCreated: not canUseFullScreenIntent" )
                    }
                }catch (e: ActivityNotFoundException){
                    e.printStackTrace()
                }

            }
        }

        feedback1Sheet()
    }

    fun feedback1Sheet(){
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val binding = DialogFeedbackMomentBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(binding.root)
        binding.feedbackHappy.setOnClickListener {
            feedbackRateSheet()
        }

        binding.feedbacksad.setOnClickListener {
            feedbackQuestionSheet()
        }
        bottomSheetDialog.show()
    }


    fun feedbackRateSheet(){
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val binding = DialogFeedbackRateBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(binding.root)
        binding.simpleRatingBar.setOnRatingChangeListener { ratingBar, rating, fromUser ->

        }

        binding.buttonApplyWallpaper.setOnClickListener {
            if (binding.simpleRatingBar.rating >= 4){

            }else{
                feedbackQuestionSheet()
            }
        }
        bottomSheetDialog.show()
    }

    fun feedbackQuestionSheet(){
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val binding = DialogFeedbackQuestionBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(binding.root)

        binding.probExperience.setOnClickListener {
            binding.probExperience.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.button_bg))
            binding.probCrash.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light))
            binding.probSlow.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light))
            binding.probSuggestion.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light))
            binding.probOthers.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light))

        }

        binding.probCrash.setOnClickListener {
            binding.probExperience.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light))
            binding.probCrash.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.button_bg))
            binding.probSlow.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light))
            binding.probSuggestion.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light))
            binding.probOthers.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light))
        }

        binding.probSlow.setOnClickListener {
            binding.probExperience.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light))
            binding.probCrash.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light))
            binding.probSlow.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.button_bg))
            binding.probSuggestion.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light))
            binding.probOthers.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light))
        }

        binding.probSuggestion.setOnClickListener {
            binding.probExperience.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light))
            binding.probCrash.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light))
            binding.probSlow.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light))
            binding.probSuggestion.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.button_bg))
            binding.probOthers.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light))
        }

        binding.probOthers.setOnClickListener {
            binding.probExperience.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light))
            binding.probCrash.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light))
            binding.probSlow.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light))
            binding.probSuggestion.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light))
            binding.probOthers.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.button_bg))
        }
        bottomSheetDialog.show()
    }


    private fun getUserIdDialog() {
        val dialogBinding = UpdateDialogBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext())
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(dialogBinding.root)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCancelable(false)

        dialogBinding.btnYes.setOnClickListener {
            launchUpdateFlow()
        }

        dialogBinding.btnNo.setOnClickListener {
            dialog?.dismiss()
        }

        dialog?.show()
    }


    fun launchUpdateFlow(){
        if (isAdded){
            val appUpdateManager = AppUpdateManagerFactory.create(requireContext())
            val appUpdateInfoTask = appUpdateManager.appUpdateInfo

            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE

                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                ) {

                    try {
                        appUpdateManager.startUpdateFlowForResult(
                            // Pass the intent that is returned by 'getAppUpdateInfo()'.
                            appUpdateInfo,
                            // an activity result launcher registered via registerForActivityResult
                            updateResultStarter,

                            AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build(),

                            150
                        )
                    } catch (exception: IntentSender.SendIntentException) {
                        Toast.makeText(context, "Something wrong went wrong!", Toast.LENGTH_SHORT).show()
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
        // handle callback
        if (result.data == null) return@registerForActivityResult
        if (result.resultCode == 150) {
            Toast.makeText(context, "Downloading stated", Toast.LENGTH_SHORT).show()
            if (result.resultCode != Activity.RESULT_OK) {
                Toast.makeText(context, "Downloading failed" , Toast.LENGTH_SHORT).show()
            }
        }
    }







    fun loadbannerAd(){
        binding.adsView.loadAd(requireContext(),"mainscr_bottom",
            " mainscr_bottom", object : CustomSDKAdsListenerAdapter() {
                override fun onAdsLoaded() {
                    super.onAdsLoaded()
                    Log.e("*******ADS", "onAdsLoaded: Banner loaded")
                }

                override fun onAdsLoadFail() {
                    super.onAdsLoadFail()

                    if (isAdded){
//                        binding.adsView.reCallLoadAd(this)
                    }
                    Log.e("*******ADS", "onAdsLoaded: Banner failed")
                }
            })
    }
    private fun setEvents(){
        binding.settings.setOnClickListener {
            findNavController().navigate(R.id.settingFragment)
        }


        binding.search.setOnClickListener {
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

    private fun backHandle(){
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.viewPager.currentItem != 0){
                    binding.viewPager.setCurrentItem(0)
                }else{

                    exit = true
                    existDialog.exitPopup(requireContext(),requireActivity(),myActivity)
                }

            }
        })
    }

    private fun setGradienttext(){
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

    fun initTabs(){

        val images = generateImagesArray(AdConfig.tabPositions)
        AdConfig.tabPositions = AdConfig.tabPositions.map { if (it == "4K") "Car" else it }.toTypedArray()



        val titles = arrayOf(getString(R.string.popular),getString(R.string.trending),
            getString(R.string.live), getString(R.string.ai_wallpaper),
            getString(R.string.category), getString(R.string.gen_ai))



        binding.tabLayout.setSelectedTabIndicatorHeight(0)
        val tabCount: Int = binding.tabLayout.tabCount
        for (i in 0 until tabCount) {
            val tab: TabLayout.Tab? = binding.tabLayout.getTabAt(i)
            if (tab != null) {
                tab.setCustomView(R.layout.tab_item)
                val tabCardView = tab.customView!!.findViewById<CardView>(R.id.container)
                val tabIcon = tab.customView!!.findViewById<ImageView>(R.id.icon)
                var tabtitle = tab.customView!!.findViewById<TextView>(R.id.text)

                tabIcon.setImageResource(images[i])
                tabtitle.text = AdConfig.tabPositions[i]



                if (i == 0 ){
                    tabCardView.setCardBackgroundColor(resources.getColor(R.color.button_bg))
                    tabIcon.visibility = View.VISIBLE
//                    setMarginsForTab(0,0,13)
                }


            }
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewModel.setData(true)
                updateTabAppearance(tab!!,true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                updateTabAppearance(tab!!,false)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
    }

    private fun generateImagesArray(tabNames: Array<String>): Array<Int> {
        return tabNames.map { tabIconMap[it.trim()] ?: R.drawable.tab_icon_popular }.toTypedArray()
    }

    fun setViewPager(){
        val adapter= ViewPagerAdapter(childFragmentManager)

        for (tabName in AdConfig.tabPositions) {
            val fragment = getFragmentForTab(tabName)
            adapter.addFragment(fragment, tabName)
        }
        binding.viewPager.adapter=adapter
        binding.viewPager.offscreenPageLimit = 1
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    fun getFragmentForTab(tabName: String): Fragment {
        return when (tabName.trim()) {
            "Popular" -> PopularWallpaperFragment()
            "Car" -> HomeFragment()
            "Live" -> LiveWallpaperFragment()
            "Anime" -> AnimeWallpaperFragment()
            "Category" -> CategoryFragment()
            "Gen AI" -> GenerateImageFragment()
            "Charging" -> ChargingAnimationFragment()

            else -> {HomeFragment()}
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
        sharedViewModel.selectTab.observe(viewLifecycleOwner){
            if (it !=  null && it != 0){
                navigateToTrending(it)
                sharedViewModel.selectTab(0)
            }
        }

        if (isAdded){
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Home Screen")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, javaClass.simpleName)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }
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

    fun navigateToTrending(index:Int){
        if (isAdded){
            binding.viewPager.currentItem = index
        }

    }
}