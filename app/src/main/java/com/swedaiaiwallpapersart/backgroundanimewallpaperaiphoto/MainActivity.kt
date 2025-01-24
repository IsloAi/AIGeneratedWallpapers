package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.Window
import android.view.WindowInsets
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.get
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.ikame.android.sdk.IKSdkController
import com.ikame.android.sdk.billing.IKBillingController
import com.ikame.android.sdk.listener.pub.IKBillingListener
import com.ikame.android.sdk.tracking.IKTrackingHelper
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.ActivityMainBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MyApp
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.ListResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.SingleDatabaseResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.batteryanimation.ChargingAnimationViewmodel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.ConnectivityListener
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.LiveImagesResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig.autoNext
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig.timeNext
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.LocaleManager
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyCatNameViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyHomeViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.DoubeWallpaperViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.LiveWallpaperViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ConnectivityListener {

    lateinit var binding: ActivityMainBinding
    private lateinit var job: Job
    val TAG = "MainActivity"
    private var selectedPrompt: String? = null
    private var alertDialog: AlertDialog? = null
    private val liveViewModel: LiveWallpaperViewModel by viewModels()
    val myCatNameViewModel: MyCatNameViewModel by viewModels()
    private val mainActivityViewModel: MainActivityViewModel by viewModels()
    private val myViewModel: MyHomeViewModel by viewModels()
    private val chargingAnimationViewmodel: ChargingAnimationViewmodel by viewModels()
    private val doubleWallpaperVideModel: DoubeWallpaperViewModel by viewModels()
    @Inject
    lateinit var appDatabase: AppDatabase
    private var _navController: NavController? = null
    private val navController get() = _navController!!
    private var deviceID: String? = null

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        deviceID = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        Log.d(TAG, "onCreate:deviceID - $deviceID")
        val lan = MySharePreference.getLanguage(this)
        (application as? MyApp)?.setConnectivityListener(this)
        job = Job()
        val context = LocaleManager.setLocale(this, lan!!)
        val resources = context.resources
        val newLocale = Locale(lan)
        val resources1 = getResources()
        val configuration = resources1.configuration
        configuration.setLocale(newLocale)
        configuration.setLayoutDirection(Locale(lan))
        resources1.updateConfiguration(configuration, resources.displayMetrics)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initFirebaseRemoteConfig()
        handleBackPress()
        fetchData(deviceID.toString())
        observefetechedData()
        if (!isNetworkAvailable()) {
            showNoInternetDialog()
        }
        readJsonAndSaveDataToDb()
        getSetTotallikes()
        if (deviceID != null) {
            MySharePreference.setDeviceID(this, deviceID.toString())
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        _navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.liveWallpaperPreviewFragment -> {
                    enableEdgeToEdge()
                    val windowInsetsController =
                        WindowCompat.getInsetsController(window, window.decorView)
                    windowInsetsController.systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

                    windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
                }

                else -> {
                    disableEdgeToEdge(window)
                    val windowInsetsController =
                        WindowCompat.getInsetsController(window, window.decorView)
                    windowInsetsController.systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (::job.isInitialized) {
            job.cancel()
        }
    }

    private fun disableEdgeToEdge(window: Window) {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.apply {
                show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun readJsonAndSaveDataToDb() {
        GlobalScope.launch(Dispatchers.IO) {
            // Step 1: Read and insert wallpapers.json
            val jsonFileName1 = "wallpapers.json"
            val jsonString1 = readJsonFile(this@MainActivity, jsonFileName1)

            if (jsonString1.isNotEmpty()) {
                val imageList1 = parseJson(jsonString1)
                if (imageList1 != null) {
                    val images1 = imageList1.images
                    val deferreds1 = images1.map { item ->
                        val model = SingleDatabaseResponse(
                            item.id,
                            item.cat_name,
                            item.image_name,
                            item.url,
                            item.url,
                            item.likes,
                            item.liked,
                            item.size,
                            item.Tags,
                            item.capacity,
                            true
                        )
                        CoroutineScope(Dispatchers.IO).async {
                            appDatabase.wallpapersDao().insert(model)
                        }
                    }

                    // Wait for all insertions of wallpapers.json to complete
                    deferreds1.awaitAll()

                    // Start the API task after the first JSON insertion completes
                    withContext(Dispatchers.Main) {
                        mainActivityViewModel.updates.observe(this@MainActivity) { result ->
                            when (result) {
                                is Response.Success -> {
                                    //Log.d(TAG, "updatedWalls: " + result.data)

                                    result.data?.forEach { item ->
                                        val model = SingleDatabaseResponse(
                                            item.id,
                                            item.cat_name,
                                            item.image_name,
                                            item.url,
                                            item.url,
                                            item.likes,
                                            item.liked,
                                            item.size,
                                            item.Tags,
                                            item.capacity,
                                            true
                                        )

                                        CoroutineScope(Dispatchers.IO).async {
                                            appDatabase.wallpapersDao().update(model)
                                        }
                                    }


                                }

                                is Response.Error -> {
                                    Log.e(TAG, "observefetechedData: error")
                                }

                                is Response.Processing -> {
                                    Log.e(TAG, "observefetechedData: Processing")
                                }

                                Response.Loading -> {
                                    Log.e(TAG, "observefetechedData: loading")
                                }

                                is Response.Error -> TODO()
                                Response.Loading -> TODO()
                                is Response.Processing -> TODO()
                                is Response.Success -> TODO()
                            }
                        }

                        mainActivityViewModel.deletedIds.observe(this@MainActivity) { result ->
                            when (result) {
                                is Response.Success -> {
                                    //Log.d(TAG, "updatedWalls: " + result.data)

                                    result.data?.forEach { item ->

                                        //Log.d(TAG, "readJsonAndSaveDataToDb: $item")

                                        CoroutineScope(Dispatchers.IO).async {
                                            appDatabase.wallpapersDao()
                                                .deleteById(item.imgid.toInt())
                                        }
                                    }


                                }

                                is Response.Error -> {
                                    Log.e(TAG, "observefetechedData: error")
                                }

                                is Response.Processing -> {
                                    Log.e(TAG, "observefetechedData: Processing")
                                }

                                Response.Loading -> {
                                    Log.e(TAG, "observefetechedData: loading")
                                }
                            }
                        }
                    }

                } else {
                    Log.e(TAG, "readJsonAndSaveDataToDb: IMAGELIST NULL")
                    return@launch
                }
            } else {
                Log.e(TAG, "readJsonAndSaveDataToDb: string null")
                return@launch
            }

            // Step 2: Read and insert livewallpapers.json (concurrently)
            val jsonFileName2 = "livewallpapers.json"
            val jsonString2 = readJsonFile(this@MainActivity, jsonFileName2)

            if (jsonString2.isNotEmpty()) {
                val imageList2 = parseJsonLive(jsonString2)
                if (imageList2 != null) {
                    val images2 = imageList2.images
                    CoroutineScope(Dispatchers.IO).launch {
                        val deferreds2 = images2.map { item ->
                            val model = item.copy(unlocked = true)
                            CoroutineScope(Dispatchers.IO).async {
                                appDatabase.liveWallpaperDao().insert(model)
                            }
                        }

                        val percent = (images2.size * 0.3).toInt()
                        val topDownloadedWallpapers =
                            appDatabase.liveWallpaperDao().getTopDownloadedWallpapers(percent)

                        topDownloadedWallpapers.forEach { it.unlocked = false }
                        appDatabase.liveWallpaperDao().updateWallpapers(topDownloadedWallpapers)

                        deferreds2.awaitAll()
                    }
                } else {
                    Log.e(TAG, "readJsonAndSaveDataToDb: IMAGELIST NULL")
                    return@launch
                }
            } else {
                Log.e(TAG, "readJsonAndSaveDataToDb: string null")
                return@launch
            }
        }
    }

    private fun observefetechedData() {
        mainActivityViewModel.allModels.observe(this) { result ->
            when (result) {
                is Response.Success -> {
                    Log.e(TAG, "updatedWalls: " + result.data)

                    result.data?.forEach { item ->
                        val model = SingleDatabaseResponse(
                            item.id,
                            item.cat_name,
                            item.image_name,
                            item.url,
                            item.url,
                            item.likes,
                            item.liked,
                            item.size,
                            item.Tags,
                            item.capacity,
                            true
                        )

                        CoroutineScope(Dispatchers.IO).async {
                            appDatabase.wallpapersDao().insert(model)
                        }
                    }
                }

                is Response.Error -> {
                    Log.e(TAG, "observefetechedData: error")
                }

                is Response.Processing -> {
                    Log.e(TAG, "observefetechedData: Processing")
                }

                Response.Loading -> {
                    Log.e(TAG, "observefetechedData: loading")
                }
            }

        }
    }

    private fun fetchData(deviceID: String) {
        mainActivityViewModel.getDeletedImagesID()
        mainActivityViewModel.getStaticWallpaperUpdates()
        myCatNameViewModel.fetchWallpapers()
        saveLiveWallpapersInDB()
        lifecycleScope.launch {
            liveViewModel.getMostUsed("1", "500", deviceID)
        }

        chargingAnimationViewmodel.getChargingAnimations()

        doubleWallpaperVideModel.getDoubleWallpapers()

        mainActivityViewModel.getAllModels("1", "4000", "5332")

    }

    private fun handleBackPress() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.e(TAG, "handleOnBackPressed: ")
                sendTracking(
                    "click_button",
                    Pair("action_type", "button"),
                    Pair("action_name", "Sytem_BackButton_Click")
                )
                navController.popBackStack()
            }
        })
    }

    private fun sendTracking(
        eventName: String, vararg param: Pair<String, String?>
    ) {
        IKTrackingHelper.sendTracking(eventName, *param)
    }

    private fun saveLiveWallpapersInDB() {
        liveViewModel.liveWallpapers.observe(this) { result ->
            when (result) {
                is Response.Success -> {
                    lifecycleScope.launch(Dispatchers.IO) {
                        result.data?.forEach { wallpaper ->
                            //Log.d(TAG, "saveLiveWallpapersInDB: $wallpaper")
                            val model = wallpaper.copy(unlocked = true)
                            appDatabase.liveWallpaperDao().insert(model)

                        }

                        val percent = (result.data?.size?.times(0.3))?.toInt()
                        val topDownloadedWallpapers = percent?.let {
                            appDatabase.liveWallpaperDao().getTopDownloadedWallpapers(
                                it
                            )
                        }

                        topDownloadedWallpapers?.forEach { it.unlocked = false }

                        // Update the wallpapers in the database
                        topDownloadedWallpapers?.let {
                            appDatabase.liveWallpaperDao().updateWallpapers(
                                it
                            )
                        }
                    }
                }

                is Response.Loading -> {
                    Log.e(TAG, "loadData: Loading")
                }

                is Response.Error -> {
                    Log.e(TAG, "loadData: response error")
                    MySharePreference.getDeviceID(this)
                        ?.let { liveViewModel.getMostUsed("1", "500", it) }
                }

                is Response.Processing -> {
                    Log.e(TAG, "loadData: processing")
                }
            }
        }
    }

    fun initObservers() {
        mainActivityViewModel.mostUsed.observe(this) { result ->
            when (result) {
                is Response.Success -> {
                    result.data?.forEach { item ->
                        Log.e(TAG, "initObservers: $item")

                        lifecycleScope.launch(Dispatchers.IO) {
                            appDatabase.wallpapersDao().updateLocked(false, item.image_id.toInt())
                        }
                        if (item == result.data.last()) {

                        }
                    }
                }

                is Response.Processing -> {

                    Log.e(TAG, "initObservers: processing")

                }

                is Response.Error -> {
                    Log.e(TAG, "initObservers: error")
                }

                else -> {}
            }
        }
    }

    private fun getSetTotallikes() {

        myViewModel.getAllLikes()
        myViewModel.getAllLiked(deviceID.toString())

        /*MySharePreference.getDeviceID(this@MainActivity)?.let {
            myViewModel.getAllLiked(it) }*/

        myViewModel.allLikes.observe(this@MainActivity) { result ->
            when (result) {
                is Response.Success -> {

                    lifecycleScope.launch(Dispatchers.IO) {
                        result.data?.forEach { item ->
                            appDatabase.wallpapersDao().updateLikes(item.likes, item.id.toInt())
                        }
                    }
                }

                is Response.Loading -> {

                }

                is Response.Error -> {

                }

                is Response.Processing -> {

                }

            }
        }

        /*myViewModel.allLiked.observe(this@MainActivity) { result ->
            when (result) {
                is Response.Success -> {
                    result.data?.forEach { item ->
                        Log.w(TAG, "getSetTotalLikes: ${item.imageid}")
                        lifecycleScope.launch(Dispatchers.IO) {
                            appDatabase.wallpapersDao().updateLiked(true, item.imageid.toInt())
                        }
                    }
                }

                is Response.Loading -> {

                }

                is Response.Error -> {
                    Log.e(TAG, "TLikesError : ${result.message} ")
                }

                is Response.Processing -> {

                }

            }

        }*/
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(
                NetworkCapabilities.TRANSPORT_CELLULAR
            ))
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo != null && networkInfo.isConnected
        }
    }

    suspend fun parseJson(jsonString: String): ListResponse? {
        return withContext(Dispatchers.IO) {
            try {
                val gson = Gson()
                gson.fromJson(jsonString, ListResponse::class.java)
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
                null
            }
        }

    }

    private suspend fun parseJsonLive(jsonString: String): LiveImagesResponse? {
        return withContext(Dispatchers.IO) {
            try {
                val gson = Gson()
                gson.fromJson(jsonString, LiveImagesResponse::class.java)
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
                null
            }
        }
    }

    private suspend fun readJsonFile(context: Context, fileName: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream: InputStream = context.assets.open(fileName)
                val size: Int = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()
                String(buffer, Charsets.UTF_8)
            } catch (e: IOException) {
                e.printStackTrace()
                ""
            }
        }
    }

    private fun initFirebaseRemoteConfig() {
        val first = "position_ads"
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }

        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config)

        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {

                if (configUpdate.updatedKeys.contains(first)) {
                    remoteConfig.activate().addOnCompleteListener {
                        val welcomeMessage = remoteConfig[first].asString()

                        val onboarding = remoteConfig["onboarding_screen"].asBoolean()
                        val positionTabs = remoteConfig["tablist_156"].asString()
                        val iap = remoteConfig["iap_config"].asString()

                        val categoryOrder = remoteConfig["category_order"].asString()

                        val inAppConfig = remoteConfig["in_app_config"].asString()

                        val categoryOrderArray =
                            categoryOrder.substring(1, categoryOrder.length - 1).split(", ")
                                .toList()

                        AdConfig.categoryOrder = categoryOrderArray
                        AdConfig.Noti_Widget = remoteConfig["Noti_Widget"].asString()
                        AdConfig.Reward_Screen = remoteConfig.getBoolean("Reward_Screen")

                        val fullOnboardingAutoNext =
                            remoteConfig["fullonboarding_auto_next"].asString()
                        if (fullOnboardingAutoNext.isNotEmpty()) {
                            val jsonArray = JSONArray(fullOnboardingAutoNext)
                            val jsonObject = jsonArray.getJSONObject(0)
                            autoNext = jsonObject.getBoolean("auto_next")
                            timeNext = jsonObject.getLong("time_next")
                        }

                        val languagesOrder = remoteConfig["languages"].asString()
                        val languagesOrderArray =
                            languagesOrder.split(",").map { it.trim().removeSurrounding("\"") }

                        AdConfig.languagesOrder = languagesOrderArray
                        val languageShowNative = remoteConfig["Language_logic_show_native"].asLong()
                        AdConfig.languageLogicShowNative = languageShowNative.toInt()
                        val onboardingFullNative = remoteConfig["Onboarding_Full_Native"].asLong()
                        AdConfig.onboarding_Full_Native = onboardingFullNative.toInt()

                        val policyOpenAd = remoteConfig["avoid_policy_openad_inter"].asLong()
                        AdConfig.avoidPolicyOpenAdInter = policyOpenAd.toInt()

                        val policyRepInter = remoteConfig["avoid_policy_repeating_inter"].asLong()
                        AdConfig.avoidPolicyRepeatingInter = policyRepInter.toInt()

                        val baseUrls = remoteConfig["dataUrl"].asString()

                        /*here we are getting the baseUrls to show wallpaper
                        For live to change we need to get the
                         different variable from the remote config and pass that
                         value for the Live wallpaper fragment
                        Log.d("usmanTAG", "onUpdate: baseURls: $baseUrls")
                        the current url for showing wallpaper is
                         https://4kwallpaper-zone.b-cdn.net/livewallpaper/ */
                        Log.d("usmanTAG", "onUpdate: baseURls: $baseUrls")
                        AdConfig.BASE_URL_DATA = baseUrls

                        try {
                            val jsonObject = JSONObject(inAppConfig)
                            val languagescralwayshow =
                                jsonObject.getBoolean("language_scr_alway_show")
                            val regularWallpaperFlow = jsonObject.getInt("regular_wallpaper_flow")
                            AdConfig.regularWallpaperFlow = regularWallpaperFlow
                            AdConfig.inAppConfig = languagescralwayshow
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                        try {
                            val jsonObject = JSONObject(iap)
                            val iapScreenType = jsonObject.optInt("IAPScreentype")
                            AdConfig.iapScreenType = iapScreenType
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                        val tabNamesArray: Array<String> =
                            positionTabs.replace("{", "")   // Remove the opening curly brace
                                .replace("}", "")   // Remove the closing curly brace
                                .replace("\"", "")
                                .split(", ")        // Split the string into an array using ", " as the delimiter
                                .toTypedArray()

                        for (element in tabNamesArray) {
                            Log.e(TAG, "onUpdate: $element")
                        }
                        AdConfig.tabPositions = tabNamesArray
                        AdConfig.showOnboarding = onboarding

                        try {
                            val jsonObject = JSONObject(welcomeMessage)
                            val trendingScrollViewArray =
                                jsonObject.getJSONArray("mainscr_trending_tab_scroll_view")
                            for (i in 0 until trendingScrollViewArray.length()) {
                                val obj = trendingScrollViewArray.getJSONObject(i)
                                val status = obj.getString("Status")
                                val threshold = obj.getString("fisrt_ad_line_threshold")
                                val lineCount = obj.getString("line_count")
                                val designType = obj.getString("native_design_type")

                                AdConfig.adStatusViewListWallSRC = status.toInt()
                                AdConfig.firstAdLineViewListWallSRC = threshold.toInt()
                                AdConfig.lineCountViewListWallSRC = lineCount.toInt() + 1

                                AdConfig.adStatusTrending = status.toInt()
                                AdConfig.firstAdLineTrending = threshold.toInt()
                                AdConfig.lineCountTrending = lineCount.toInt() + 1
                            }

                            val cateScrollViewArray =
                                jsonObject.getJSONArray("mainscr_cate_tab_scroll_view")
                            for (i in 0 until cateScrollViewArray.length()) {
                                val obj = cateScrollViewArray.getJSONObject(i)
                                val status = obj.getString("Status")
                                val threshold = obj.getString("fisrt_ad_line_threshold")
                                val lineCount = obj.getString("line_count")
                                val designType = obj.getString("native_design_type")

                                AdConfig.adStatusCategoryArt = status.toInt()
                                AdConfig.firstAdLineCategoryArt = threshold.toInt()
                                AdConfig.lineCountCategoryArt = lineCount.toInt()
                            }

                            val mainScreenScroll =
                                jsonObject.getJSONArray("viewlistwallscr_scrollview")
                            for (i in 0 until mainScreenScroll.length()) {
                                val obj = mainScreenScroll.getJSONObject(i)
                                val status = obj.getString("Status")
                                val threshold = obj.getString("fisrt_ad_line_threshold")
                                val lineCount = obj.getString("line_count")
                                val designType = obj.getString("native_design_type")

                                AdConfig.adStatusTrending = status.toInt()
                                AdConfig.firstAdLineTrending = threshold.toInt()
                                AdConfig.lineCountTrending = lineCount.toInt() + 1
                                println("Status: $status, Threshold: $threshold, Line Count: $lineCount, Design Type: $designType")
                            }

                            val mostUsedScreen = jsonObject.getJSONArray("mainscr_all_tab_scroll")
                            for (i in 0 until mostUsedScreen.length()) {
                                val obj = mostUsedScreen.getJSONObject(i)
                                val status = obj.getString("Status")
                                val threshold = obj.getString("fisrt_ad_line_threshold")
                                val lineCount = obj.getString("line_count")
                                val designType = obj.getString("native_design_type")

                                AdConfig.adStatusMostUsed = status.toInt()
                                AdConfig.firstAdLineMostUsed = threshold.toInt()
                                AdConfig.lineCountMostUsed = lineCount.toInt() + 1
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Log.e("TAG", "Config update error with code: " + error.code, error)
            }
        })
        remoteConfig.fetchAndActivate().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val updated = task.result
                val welcomeMessage = remoteConfig[first].asString()
                val onboarding = remoteConfig["onboarding_screen"].asBoolean()
                val positionTabs = remoteConfig["tablist_156"].asString()
                val categoryOrder = remoteConfig["category_order"].asString()
                val languagesOrder = remoteConfig["languages"].asString()
                val inAppConfig = remoteConfig["in_app_config"].asString()

                val fullOnboardingAutoNext = remoteConfig["fullonboarding_auto_next"].asString()
                if (fullOnboardingAutoNext.isNotEmpty()) {
                    val jsonArray = JSONArray(fullOnboardingAutoNext)
                    val jsonObject = jsonArray.getJSONObject(0)
                    autoNext = jsonObject.getBoolean("auto_next")
                    timeNext = jsonObject.getLong("time_next")
                }
                val baseUrls = remoteConfig["dataUrl"].asString()
                AdConfig.BASE_URL_DATA = baseUrls

                try {
                    val categoryOrderArray =
                        categoryOrder.substring(1, categoryOrder.length - 1).replace("\"", "")
                            .split(", ").toList()
                    AdConfig.categoryOrder = categoryOrderArray
                } catch (e: StringIndexOutOfBoundsException) {
                    e.printStackTrace()
                }
                AdConfig.Noti_Widget = remoteConfig["Noti_Widget"].asString()
                AdConfig.Reward_Screen = remoteConfig.getBoolean("Reward_Screen")
                try {
                    val languagesOrderArray =
                        languagesOrder.split(",").map { it.trim().removeSurrounding("\"") }

                    AdConfig.languagesOrder = languagesOrderArray
                } catch (e: StringIndexOutOfBoundsException) {
                    e.printStackTrace()
                }

                val iap = remoteConfig["iap_config"].asString()
                try {
                    val jsonObject = JSONObject(inAppConfig)
                    val languagescralwayshow = jsonObject.getBoolean("language_scr_alway_show")
                    val regularWallpaperFlow = jsonObject.getInt("regular_wallpaper_flow")

                    AdConfig.regularWallpaperFlow = regularWallpaperFlow
                    AdConfig.inAppConfig = languagescralwayshow
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                try {
                    val jsonObject = JSONObject(iap)
                    val iapScreenType = jsonObject.optInt("IAPScreentype")

                    AdConfig.iapScreenType = iapScreenType
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                val languageShowNative = remoteConfig["Language_logic_show_native"].asLong()
                AdConfig.languageLogicShowNative = languageShowNative.toInt()
                val onboardingFullNative = remoteConfig["Onboarding_Full_Native"].asLong()
                AdConfig.onboarding_Full_Native = onboardingFullNative.toInt()

                val policyOpenAd = remoteConfig["avoid_policy_openad_inter"].asLong()
                AdConfig.avoidPolicyOpenAdInter = policyOpenAd.toInt()

                val policyRepInter = remoteConfig["avoid_policy_repeating_inter"].asLong()
                AdConfig.avoidPolicyRepeatingInter = policyRepInter.toInt()

                val tabNamesArray: Array<String> =
                    positionTabs.replace("{", "")
                        .replace("}", "")
                        .replace("\"", "")
                        .split(", ")
                        .toTypedArray()

                for (element in tabNamesArray) {
                    Log.e(TAG, "onUpdate: $element")
                }

                AdConfig.tabPositions = tabNamesArray
                AdConfig.showOnboarding = onboarding

                val liveScrollType = remoteConfig["Live_tab_scroll_type"].asLong()
                AdConfig.liveTabScrollType = liveScrollType.toInt()
                Log.d(TAG, "initFirebaseRemoteConfig: LiveScrollType: $liveScrollType")
                //AdConfig.liveTabScrollType = 3

                val restoreCache = remoteConfig["RestoreCache"].asBoolean()
                AdConfig.shouldRestoreCache = restoreCache
                Log.d(TAG, "initFirebaseRemoteConfig: restoreCache: $restoreCache")

                try {
                    val jsonObject = JSONObject(welcomeMessage)
                    val trendingScrollViewArray =
                        jsonObject.getJSONArray("mainscr_trending_tab_scroll_view")
                    for (i in 0 until trendingScrollViewArray.length()) {
                        val obj = trendingScrollViewArray.getJSONObject(i)
                        val status = obj.getString("Status")
                        val threshold = obj.getString("fisrt_ad_line_threshold")
                        val lineCount = obj.getString("line_count")
                        val designType = obj.getString("native_design_type")

                        AdConfig.adStatusViewListWallSRC = status.toInt()
                        AdConfig.firstAdLineViewListWallSRC = threshold.toInt()
                        AdConfig.lineCountViewListWallSRC = lineCount.toInt() + 1
                    }

                    val cateScrollViewArray =
                        jsonObject.getJSONArray("mainscr_cate_tab_scroll_view")
                    for (i in 0 until cateScrollViewArray.length()) {
                        val obj = cateScrollViewArray.getJSONObject(i)
                        val status = obj.getString("Status")
                        val threshold = obj.getString("fisrt_ad_line_threshold")
                        val lineCount = obj.getString("line_count")
                        val designType = obj.getString("native_design_type")

                        AdConfig.adStatusCategoryArt = status.toInt()
                        AdConfig.firstAdLineCategoryArt = threshold.toInt()
                        AdConfig.lineCountCategoryArt = lineCount.toInt()
                    }

                    val mainScreenScroll = jsonObject.getJSONArray("viewlistwallscr_scrollview")
                    for (i in 0 until mainScreenScroll.length()) {
                        val obj = mainScreenScroll.getJSONObject(i)
                        val status = obj.getString("Status")
                        val threshold = obj.getString("fisrt_ad_line_threshold")
                        val lineCount = obj.getString("line_count")
                        val designType = obj.getString("native_design_type")

                        AdConfig.adStatusTrending = status.toInt()
                        AdConfig.firstAdLineTrending = threshold.toInt()
                        AdConfig.lineCountTrending = lineCount.toInt() + 1
                    }

                    val mostUsedScreen = jsonObject.getJSONArray("mainscr_all_tab_scroll")
                    for (i in 0 until mostUsedScreen.length()) {
                        val obj = mostUsedScreen.getJSONObject(i)
                        val status = obj.getString("Status")
                        val threshold = obj.getString("fisrt_ad_line_threshold")
                        val lineCount = obj.getString("line_count")
                        val designType = obj.getString("native_design_type")

                        AdConfig.adStatusMostUsed = status.toInt()
                        AdConfig.firstAdLineMostUsed = threshold.toInt()
                        AdConfig.lineCountMostUsed = lineCount.toInt() + 1
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                /*if (restoreCache) {
                    val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    val isDataCleared = prefs.getBoolean("is_data_cleared", false)
                    if (!isDataCleared) {
                        clearAppData(this@MainActivity)
                        // Mark data as cleared
                        prefs.edit().putBoolean("is_data_cleared", true).apply()
                    } else {
                        Log.d(TAG, "App data already cleared, skipping...")
                    }
                    if (!isCacheCleared) {
                        clearAppData(this@MainActivity)
                    }
                }*/
            }
        }
    }

    private fun clearAppData(context: Context) {
        /*try {
            // Clear cache
            val cacheDir = context.cacheDir
            cacheDir?.deleteRecursively()

            // Clear app data (shared preferences, databases)
            val appDir = context.filesDir.parentFile
            appDir?.listFiles()?.forEach { dir ->
                if (dir != null && dir.name != "lib") {
                    dir.deleteRecursively()
                }
            }
            Log.d(TAG, "App data and cache cleared successfully")

            fetchData(deviceID.toString())

            observefetechedData()

            readJsonAndSaveDataToDb()

            if (!isNetworkAvailable()) {
                showNoInternetDialog()
            }

            getSetTotallikes()

            if (deviceID != null) {
                MySharePreference.setDeviceID(this, deviceID.toString())
            }
            //restartApp()
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear app data", e)
        }*/
    }

    override fun onResume() {
        super.onResume()
        /*startTime = System.currentTimeMillis()
        Log.d("ColdStartTime", "App start time: $startTime")*/

        lifecycleScope.launch {
            IKBillingController.reCheckIAP(object : IKBillingListener {
                override fun onBillingFail() {
                    AdConfig.ISPAIDUSER = false
                    IKSdkController.setEnableShowResumeAds(false)
                    Log.e(TAG, "InAppPurchase15: false")
                }

                override fun onBillingSuccess() {
                    AdConfig.ISPAIDUSER = true
                    IKSdkController.setEnableShowResumeAds(true)
                    Log.e(TAG, "InAppPurchase15: true")
                }
            }, false)
        }

    }

    override fun onNetworkAvailable() {
        dismissNoInternetDialog()
    }

    override fun onNetworkLost() {
        showNoInternetDialog()
    }

    private fun showNoInternetDialog() {
        lifecycleScope.launch(Dispatchers.Main) {
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle("No Internet Connection")
            builder.setMessage("Please connect to the internet and try again.")
            builder.setCancelable(false)
            alertDialog = builder.create()
            alertDialog?.show()
        }
    }

    private fun dismissNoInternetDialog() {
        lifecycleScope.launch(Dispatchers.Main) {
            alertDialog?.dismiss()
        }
    }

}