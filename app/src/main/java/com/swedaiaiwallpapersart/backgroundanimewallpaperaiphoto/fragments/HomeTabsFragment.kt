package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments

import android.app.Activity
import android.app.Dialog
import android.content.IntentSender
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
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
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.CustomSDKAdsListenerAdapter
import com.bmik.android.sdk.listener.keep.SDKNewVersionUpdateCallback
import com.bmik.android.sdk.model.dto.UpdateAppDto
import com.google.android.material.tabs.TabLayout
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.common.IntentSenderForResultStarter
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.analytics.FirebaseAnalytics
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentHomeTabsBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.UpdateDialogBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.SaveStateViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.ViewPagerAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.livewallpaper.LiveWallpaperFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.menuFragments.CategoryFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.menuFragments.HomeFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.fragmentsIG.GenerateImageFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyDialogs
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyHomeViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
        "AI Wallpaper" to R.drawable.tab_icon_ai_wallpaper,
        "Category" to R.drawable.tab_icon_categories,
        "Gen AI" to R.drawable.tab_icon_generate
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
            loadbannerAd()
            setGradienttext()
            setViewPager()
            initTabs()
            setEvents()
        lifecycleScope.launch {
            SDKBaseController.getInstance().checkUpdateApp(object:SDKNewVersionUpdateCallback{
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
        SDKBaseController.getInstance()
            .loadBannerAds(
                requireActivity(),
                binding.adsWidget as? ViewGroup,
                "mainscr_bottom",
                " mainscr_bottom", object : CustomSDKAdsListenerAdapter() {
                    override fun onAdsLoaded() {
                        super.onAdsLoaded()
                        Log.e("*******ADS", "onAdsLoaded: Banner loaded", )
                    }

                    override fun onAdsLoadFail() {
                        super.onAdsLoadFail()
                        Log.e("*******ADS", "onAdsLoaded: Banner failed", )
                    }
                }
            )
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
            "Trending" -> HomeFragment()
            "Live" -> LiveWallpaperFragment()
            "AI Wallpaper" -> HomeFragment()
            "Category" -> CategoryFragment()
            "Gen AI" -> GenerateImageFragment()

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