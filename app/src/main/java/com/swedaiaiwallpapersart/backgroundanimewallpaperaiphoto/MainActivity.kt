package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
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
import com.ikame.android.sdk.tracking.IKTrackingHelper
import com.ikame.android.sdk.utils.IKUtils
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.ActivityMainBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MyApp
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.ListResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.SingleDatabaseResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.batteryanimation.ChargingAnimationViewmodel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.adaptersIG.PromptListAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.interfaces.GetPromptDetails
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.models.Prompts
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
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
    private var selectedPrompt: String? = null

    private var alertDialog: AlertDialog? = null

    private val liveViewModel: LiveWallpaperViewModel by viewModels()
    val TAG = "ANRSPY"

    val myCatNameViewModel: MyCatNameViewModel by viewModels()

    val mainActivityViewModel: MainActivityViewModel by viewModels()

    @Inject
    lateinit var appDatabase: AppDatabase

    private val myViewModel: MyHomeViewModel by viewModels()

    private val chargingAnimationViewmodel: ChargingAnimationViewmodel by viewModels()
    private val doubleWallpaperVideModel: DoubeWallpaperViewModel by viewModels()

    private var _navController: NavController? = null

    private val navController get() = _navController!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val deviceID = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        val lan = MySharePreference.getLanguage(this)
        (application as? MyApp)?.setConnectivityListener(this)

        val context = LocaleManager.setLocale(this, lan!!)
        val resources = context.resources
        val newLocale = Locale(lan!!)
        val resources1 = getResources()
        val configuration = resources1.configuration
        configuration.setLocale(newLocale)
        configuration.setLayoutDirection(Locale(lan!!));
        resources1.updateConfiguration(configuration, resources.displayMetrics)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        initFirebaseRemoteConfig()

        handleBackPress()
        fetchData(deviceID)

        observefetechedData()

        if (!isNetworkAvailable()) {
            showNoInternetDialog()
        }
        readjsonAndSaveDataToDb()
        if (deviceID != null) {
            MySharePreference.setDeviceID(this, deviceID)
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

// Hide the system bars.
                    windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
                }
                else -> {
                    disableEdgeToEdge(window)
                    val windowInsetsController =
                        WindowCompat.getInsetsController(window, window.decorView)
                    windowInsetsController.show(WindowInsetsCompat.Type.navigationBars())
                }
            }

        }


    }

    fun disableEdgeToEdge(window: Window) {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.apply {
                show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            }
        }
    }

    private fun readjsonAndSaveDataToDb() {
        GlobalScope.launch {
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
                        mainActivityViewModel.updates.observe(this@MainActivity){result ->
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
                            }
                        }

                        mainActivityViewModel.deletedIds.observe(this@MainActivity){result->
                            when(result){
                                is Response.Success -> {
                                    Log.e(TAG, "updatedWalls: " + result.data)

                                    result.data?.forEach { item ->

                                        Log.e(TAG, "readjsonAndSaveDataToDb: "+item )


                                        CoroutineScope(Dispatchers.IO).async {
                                            appDatabase.wallpapersDao().deleteById(item.imgid.toInt())
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
                        val topDownloadedWallpapers = appDatabase.liveWallpaperDao().getTopDownloadedWallpapers(percent)

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
        eventName: String,
        vararg param: Pair<String, String?>
    ) {
        IKTrackingHelper.sendTracking( eventName, *param)
    }


    private fun saveLiveWallpapersInDB() {
        liveViewModel.liveWallpapers.observe(this) { result ->
            when (result) {
                is Response.Success -> {
                    lifecycleScope.launch(Dispatchers.IO) {
                        result.data?.forEach { wallpaper ->

                            Log.e(TAG, "saveLiveWallpapersInDB: $wallpaper")
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
                            getSetTotallikes()
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

    fun getSetTotallikes() {
        myViewModel.getAllLikes()

        MySharePreference.getDeviceID(this@MainActivity)?.let { myViewModel.getAllLiked(it) }

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

        myViewModel.allLiked.observe(this@MainActivity) { result ->
            when (result) {
                is Response.Success -> {

                    lifecycleScope.launch(Dispatchers.IO) {
                        result.data?.forEach { item ->

                            Log.e("TAG", "getSetTotallikes: " + item)
                            appDatabase.wallpapersDao().updateLiked(true, item.imageid.toInt())
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
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
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

    suspend fun parseJsonLive(jsonString: String): LiveImagesResponse? {
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


    suspend fun readJsonFile(context: Context, fileName: String): String {
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

        var remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        remoteConfig.setDefaultsAsync(R.xml.remote_config)


        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                Log.d("TAG", "Updated keys: " + configUpdate.updatedKeys)

                if (configUpdate.updatedKeys.contains(first)) {
                    remoteConfig.activate().addOnCompleteListener {

                        Log.e("TAG", "onUpdate: " + configUpdate.updatedKeys)

                        val welcomeMessage = remoteConfig[first].asString()

                        val onboarding = remoteConfig["onboarding_screen"].asBoolean()
                        val positionTabs = remoteConfig["tablist_156"].asString()
                        val iap = remoteConfig["iap_config"].asString()

                        val categoryOrder = remoteConfig["category_order"].asString()
                        Log.e(TAG, "onUpdate: $categoryOrder")
                        Log.e(TAG, "onUpdate: $iap")

                        val inAppConfig = remoteConfig["in_app_config"].asString()

                        Log.e(TAG, "onUpdate: $inAppConfig")

                        val categoryOrderArray =
                            categoryOrder.substring(1, categoryOrder.length - 1).split(", ")
                                .toList()

                        AdConfig.categoryOrder = categoryOrderArray

                        val fullOnboardingAutoNext = remoteConfig["fullonboarding_auto_next"].asString()
                        Log.e("RemoteConfig123", fullOnboardingAutoNext)
                        if (fullOnboardingAutoNext.isEmpty()) {
                            Log.e("RemoteConfig123", "Remote Config value for fullonboarding_auto_next is null or empty")
                        } else {

                            val jsonArray = JSONArray(fullOnboardingAutoNext)
                            val jsonObject = jsonArray.getJSONObject(0)
                            autoNext = jsonObject.getBoolean("auto_next")
                            timeNext = jsonObject.getLong("time_next")
                        }

                        val languagesOrder = remoteConfig["languages"].asString()
                        val languagesOrderArray = languagesOrder.split(",").map { it.trim().removeSurrounding("\"") }

                        Log.e(TAG, "initFirebaseRemoteConfig: "+languagesOrderArray )
                        AdConfig.languagesOrder = languagesOrderArray
                        val languageShowNative = remoteConfig["Language_logic_show_native"].asLong()
                        AdConfig.languageLogicShowNative = languageShowNative.toInt()
                        val onboardingFullNative = remoteConfig["Onboarding_Full_Native"].asLong()
                        AdConfig.onboarding_Full_Native = onboardingFullNative.toInt()

                        val policyOpenAd = remoteConfig["avoid_policy_openad_inter"].asLong()
                        AdConfig.avoidPolicyOpenAdInter = policyOpenAd.toInt()

                        val policyRepInter = remoteConfig["avoid_policy_repeating_inter"].asLong()
                        AdConfig.avoidPolicyRepeatingInter = policyRepInter.toInt()


                        Log.e(
                            TAG,
                            "initFirebaseRemoteConfig: " + languageShowNative + "full native$onboardingFullNative"
                        )





                        Log.e(TAG, "onUpdate: $categoryOrderArray")

                        val baseUrls = remoteConfig["dataUrl"].asString()

                        AdConfig.BASE_URL_DATA = baseUrls

                        Log.e(TAG, "initFirebaseRemoteConfig: " + baseUrls)



                        try {

                            val jsonObject = JSONObject(inAppConfig)

                            // Retrieve the boolean value associated with the key "languagescralwayshow"
                            val languagescralwayshow =
                                jsonObject.getBoolean("language_scr_alway_show")

                            val regularWallpaperFlow = jsonObject.getInt("regular_wallpaper_flow")

                            Log.e(TAG, "onUpdate: " + languagescralwayshow)
                            Log.e(TAG, "onUpdate: regular wallpaper " + regularWallpaperFlow)
                            AdConfig.regularWallpaperFlow = regularWallpaperFlow
                            AdConfig.inAppConfig = languagescralwayshow

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }



                        try {


                            val jsonObject = JSONObject(iap)

                            // Get the value associated with the key "IAPScreentype"
                            val iapScreenType = jsonObject.optInt("IAPScreentype")
                            Log.e(TAG, "onUpdate: $iapScreenType")

                            AdConfig.iapScreenType = iapScreenType
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }


                        Log.e(TAG, "onUpdate: $positionTabs")

                        var tabNamesArray: Array<String> = positionTabs
                            .replace("{", "")   // Remove the opening curly brace
                            .replace("}", "")   // Remove the closing curly brace
                            .replace("\"", "")
                            .split(", ")        // Split the string into an array using ", " as the delimiter
                            .toTypedArray()

                        for (i in 0 until tabNamesArray.size) {
                            Log.e(TAG, "onUpdate: " + tabNamesArray[i])
                        }
                        //in next update
//                        tabNamesArray += "Trending"

                        AdConfig.tabPositions = tabNamesArray

                        Log.e(TAG, "onUpdate: " + positionTabs)

                        AdConfig.showOnboarding = onboarding
                        Log.e(TAG, "onUpdate: " + onboarding)
                        Log.e("TAG update", "initFirebaseRemoteConfig: $welcomeMessage")

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
                                println("Status: $status, Threshold: $threshold, Line Count: $lineCount, Design Type: $designType")
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
                                println("Status: $status, Threshold: $threshold, Line Count: $lineCount, Design Type: $designType")
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
                                println("Status: $status, Threshold: $threshold, Line Count: $lineCount, Design Type: $designType")
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


        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val updated = task.result

                    Log.e("TAG", "Config params updated: $updated")

                }
            }

        val welcomeMessage = remoteConfig[first].asString()
        val onboarding = remoteConfig["onboarding_screen"].asBoolean()
        val positionTabs = remoteConfig["tablist_156"].asString()
        val categoryOrder = remoteConfig["category_order"].asString()
        Log.e(TAG, "onUpdate: $categoryOrder")
        val languagesOrder = remoteConfig["languages"].asString()
        val inAppConfig = remoteConfig["in_app_config"].asString()

        val fullOnboardingAutoNext = remoteConfig["fullonboarding_auto_next"].asString()
        Log.e("RemoteConfig123", fullOnboardingAutoNext)
        if (fullOnboardingAutoNext.isEmpty()) {
            Log.e("RemoteConfig123", "Remote Config value for fullonboarding_auto_next is null or empty")
        } else {

            val jsonArray = JSONArray(fullOnboardingAutoNext)
            val jsonObject = jsonArray.getJSONObject(0)
            autoNext = jsonObject.getBoolean("auto_next")
            timeNext = jsonObject.getLong("time_next")
        }
        val baseUrls = remoteConfig["dataUrl"].asString()

        AdConfig.BASE_URL_DATA = baseUrls
//        AdConfig.BASE_URL_DATA = "https://4kwallpaper-zone.b-cdn.net"


        Log.e(TAG, "initFirebaseRemoteConfig: " + baseUrls)

        Log.e(TAG, "onUpdate: " + inAppConfig)

        try {
            val categoryOrderArray =
                categoryOrder.substring(1, categoryOrder.length - 1).replace("\"", "").split(", ")
                    .toList()

            AdConfig.categoryOrder = categoryOrderArray

            Log.e(TAG, "onUpdate: $categoryOrderArray")
        } catch (e: StringIndexOutOfBoundsException) {
            e.printStackTrace()
        }

        try {

            val languagesOrderArray = languagesOrder.split(",").map { it.trim().removeSurrounding("\"") }

            Log.e(TAG, "initFirebaseRemoteConfig: "+languagesOrderArray )

            AdConfig.languagesOrder = languagesOrderArray



        } catch (e: StringIndexOutOfBoundsException) {
            e.printStackTrace()
        }


        val iap = remoteConfig["iap_config"].asString()
        Log.e(TAG, "onUpdate: $iap")


        try {

            val jsonObject = JSONObject(inAppConfig)

            // Retrieve the boolean value associated with the key "languagescralwayshow"
            val languagescralwayshow = jsonObject.getBoolean("language_scr_alway_show")
            val regularWallpaperFlow = jsonObject.getInt("regular_wallpaper_flow")

            Log.e(TAG, "onUpdate: " + languagescralwayshow)
            Log.e(TAG, "onUpdate: regular wallpaper " + regularWallpaperFlow)
            AdConfig.regularWallpaperFlow = regularWallpaperFlow
            AdConfig.inAppConfig = languagescralwayshow

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        try {
            val jsonObject = JSONObject(iap)
            val iapScreenType = jsonObject.optInt("IAPScreentype")
            Log.e(TAG, "onUpdate: $iapScreenType")

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

        Log.e(
            TAG,
            "initFirebaseRemoteConfig: " + languageShowNative + "full native$onboardingFullNative"
        )


        // Get the value associated with the key "IAPScreentype"


        Log.e(TAG, "onUpdate: $positionTabs")
        val tabNamesArray: Array<String> = positionTabs
            .replace("{", "")   // Remove the opening curly brace
            .replace("}", "")   // Remove the closing curly brace
            .replace("\"", "")
            .split(", ")        // Split the string into an array using ", " as the delimiter
            .toTypedArray()

        for (element in tabNamesArray) {
            Log.e(TAG, "onUpdate: " + element)
        }

        //in next update
//        tabNamesArray += "Charging Battery"

//        tabNamesArray += "Trending"


        AdConfig.tabPositions = tabNamesArray
        AdConfig.showOnboarding = onboarding
        Log.e(TAG, "onUpdate: $onboarding")
        Log.e("TAG new", "initFirebaseRemoteConfig: $welcomeMessage")

        Log.e(TAG, "initFirebaseRemoteConfig: $remoteConfig")

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
                println("Status: $status, Threshold: $threshold, Line Count: $lineCount, Design Type: $designType")
            }

            val cateScrollViewArray = jsonObject.getJSONArray("mainscr_cate_tab_scroll_view")
            for (i in 0 until cateScrollViewArray.length()) {
                val obj = cateScrollViewArray.getJSONObject(i)
                val status = obj.getString("Status")
                val threshold = obj.getString("fisrt_ad_line_threshold")
                val lineCount = obj.getString("line_count")
                val designType = obj.getString("native_design_type")

                AdConfig.adStatusCategoryArt = status.toInt()
                AdConfig.firstAdLineCategoryArt = threshold.toInt()
                AdConfig.lineCountCategoryArt = lineCount.toInt()
                println("Status: $status, Threshold: $threshold, Line Count: $lineCount, Design Type: $designType")
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
                println("MostUsed: Status: $status, Threshold: $threshold, Line Count: $lineCount, Design Type: $designType")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }


    }

    fun openPopupMenu(name: String, editPrompt: EditText) {
        val dialog = BottomSheetDialog(this@MainActivity)
        val view = layoutInflater.inflate(R.layout.prompt_bulider, null)
        dialog.setContentView(view)
        val width = WindowManager.LayoutParams.MATCH_PARENT
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.setLayout(width, height)
        val params = (view.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        (view.parent as View).setBackgroundColor(Color.TRANSPARENT)
        dialog.setCancelable(false)
        dialog.findViewById<RelativeLayout>(R.id.closeButton)
            ?.setOnClickListener { dialog.dismiss() }
        dialog.findViewById<TextView>(R.id.titleOfPromptsCat)?.text = "$name prompts"
        val promptRecyclerView = dialog.findViewById<RecyclerView>(R.id.promptsRecyclerView)
        val applyButton: Button = dialog.findViewById(R.id.applButton)!!
        //styleRecyclerView(styleRecyclerView)
        promptRecyclerView(promptRecyclerView, name, applyButton, editPrompt, dialog)
        dialog.show()

    }


    private fun promptRecyclerView(
        promptRecyclerView: RecyclerView?,
        name: String,
        applyButton: Button,
        editPrompt: EditText,
        dialog: BottomSheetDialog
    ) {
        promptRecyclerView?.layoutManager = LinearLayoutManager(this@MainActivity)
        val adapter = PromptListAdapter(prompts(name, false), object : GetPromptDetails {
            override fun getPrompt(prompt: String) {
                selectedPrompt = ""
                selectedPrompt = prompt

            }
        })
        promptRecyclerView?.adapter = adapter

        applyButton.setOnClickListener {
            val getPromptText = editPrompt.text.toString()
            if (getPromptText.isNotEmpty()) {
                val extendPrompt = "$selectedPrompt"
                editPrompt.setText(extendPrompt)
            } else {
                editPrompt.setText(selectedPrompt)
            }

            dialog.dismiss()


        }
    }

    private fun prompts(name: String, notSelected: Boolean): ArrayList<Prompts> {
        val list = ArrayList<Prompts>()
        when (name) {
            "Nature" -> {
                list.add(Prompts("Create a serene nature wallpaper featuring a peaceful lakeside scene"))
                list.add(Prompts("Design a nature wallpaper with a lush, green forest and a hidden waterfall"))
                list.add(Prompts("Capture the beauty of a vibrant sunrise over a calm ocean for your nature wallpaper"))
                list.add(Prompts("Illustrate a starry night in the mountains as a stunning nature background"))
                list.add(Prompts("Craft a nature wallpaper showcasing a field of colorful wildflowers"))
                list.add(Prompts("Paint a tropical paradise with palm trees and a pristine beach for your wallpaper"))
                list.add(Prompts("Compose a nature background with a snow-covered landscape and a cozy cabin"))
                list.add(Prompts("Design a peaceful nature wallpaper with a tranquil meadow and grazing deer"))
                list.add(Prompts("Create a mesmerizing wallpaper of the Northern Lights dancing in the Arctic sky"))
                list.add(Prompts("Illustrate the grandeur of a canyon with layers of red rocks as your nature background"))
                list.add(Prompts("Craft a serene lake surrounded by autumn trees for a calming wallpaper"))
                list.add(Prompts("Design a mystical forest with fireflies illuminating the night for your wallpaper."))
                list.add(Prompts("Capture the elegance of a lone, majestic eagle soaring in the sky for your nature wallpaper"))
                list.add(Prompts("Paint a vibrant coral reef with tropical fish for an underwater nature background"))
                list.add(Prompts("Create a nature wallpaper featuring a field of lavender in full bloom"))
                list.add(Prompts("Illustrate a towering waterfall crashing into a crystal-clear pool in the woods"))
                list.add(Prompts("Craft a wallpaper with a close-up of dewdrops on a spiderweb"))
                list.add(Prompts("Design a nature background with a serene garden full of blooming roses"))
                list.add(Prompts("Capture a breathtaking sunset over a serene mountain lake for your wallpaper"))
                list.add(Prompts("Compose a wallpaper featuring a rainbow stretching across a lush valley"))
            }

            "Anime" -> {
                list.add(Prompts("Design a striking anime wallpaper featuring your favorite anime character"))
                list.add(Prompts("Illustrate a serene and dreamy anime landscape with cherry blossoms"))
                list.add(Prompts("Create a dynamic action scene with two anime characters clashing in battle"))
                list.add(Prompts("Craft a minimalist anime wallpaper with a single character and a meaningful quote"))
                list.add(Prompts("Compose an anime wallpaper with a futuristic cityscape and flying cars"))
                list.add(Prompts("Design a magical anime background with a spellbinding castle in the clouds"))
                list.add(Prompts("Illustrate a heartwarming moment between anime characters under a starry sky"))
                list.add(Prompts("Capture the essence of a cyberpunk world in your anime wallpaper"))
                list.add(Prompts("Craft a wallpaper inspired by a popular anime series with a unique twist"))
                list.add(Prompts("Create a playful chibi-style wallpaper of your favorite character"))
                list.add(Prompts("Design an anime wallpaper set in a post-apocalyptic world with ruins and nature"))
                list.add(Prompts("Illustrate an underwater world with anime mermaids and colorful fish"))
                list.add(Prompts("Compose a battle-themed wallpaper with mecha anime characters and robots"))
                list.add(Prompts("Craft a dark and mysterious anime wallpaper with shadowy figures"))
                list.add(Prompts("Create a sports-themed wallpaper with energetic anime characters playing a game."))
                list.add(Prompts("Design an anime wallpaper that showcases a character's special powers or abilities."))
                list.add(Prompts("Illustrate a peaceful and cozy anime cafe scene with cute maids or butlers"))
                list.add(Prompts("Craft a romantic anime wallpaper with characters sharing a tender moment"))
                list.add(Prompts("Create a steampunk-inspired anime background with intricate machinery"))
                list.add(Prompts("Compose a surreal and abstract anime wallpaper with vibrant colors"))
            }

            "Fantasy" -> {
                list.add(Prompts("Design a whimsical fantasy wallpaper with magical creatures in an enchanted forest."))
                list.add(Prompts("Illustrate a breathtaking dragon soaring over a mystical landscape."))
                list.add(Prompts("Create a wallpaper featuring a hidden, ancient city in a fantasy realm."))
                list.add(Prompts("Craft a fantasy world under the sea, filled with mermaids, seahorses, and underwater castles."))
                list.add(Prompts("Compose an otherworldly space fantasy wallpaper with planets, stars, and celestial beings."))
                list.add(Prompts("Design a wallpaper inspired by a classic fantasy novel or fairy tale."))
                list.add(Prompts("Illustrate a fantasy battle scene with knights, wizards, and mythical beasts."))
                list.add(Prompts("Create a magical fantasy kingdom with towering castles and floating islands."))
                list.add(Prompts("Craft a wallpaper that captures the essence of a dreamy and surreal fantasy landscape."))
                list.add(Prompts("Compose a dark fantasy scene featuring a mysterious and cloaked figure."))
                list.add(Prompts("Design a fantasy wallpaper based on ancient mythology or legends."))
                list.add(Prompts("Illustrate a magical forest with talking animals and glowing fireflies."))
                list.add(Prompts("Create an underwater fantasy world with vibrant coral reefs and exotic sea creatures."))
                list.add(Prompts("Craft a steampunk-fantasy hybrid wallpaper with airships and mechanical wonders."))
                list.add(Prompts("Compose a whimsical fairy garden scene with tiny fairies and toadstool houses."))
                list.add(Prompts("Design a fantasy wallpaper featuring a journey to the center of the Earth."))
                list.add(Prompts("Illustrate a fantasy realm where time stands still, with floating clocks and surreal landscapes."))
                list.add(Prompts("Create a wallpaper that portrays the meeting of two different fantasy worlds."))
                list.add(Prompts("Craft a fantasy marketplace bustling with strange and magical items."))
                list.add(Prompts("Compose a wallpaper inspired by a unique fantasy concept or your own imagination."))
            }

            "Pattern" -> {
                list.add(Prompts("Design an intricate floral pattern wallpaper with vibrant, blooming flowers."))
                list.add(Prompts("Create a geometric pattern wallpaper featuring mesmerizing shapes and symmetry."))
                list.add(Prompts("Illustrate a playful polka dot pattern wallpaper in lively, contrasting colors."))
                list.add(Prompts("Craft an elegant damask pattern wallpaper with a touch of vintage charm."))
                list.add(Prompts("Compose a tropical leaf pattern wallpaper inspired by lush jungle foliage."))
                list.add(Prompts("Design a minimalist, Scandinavian-inspired pattern with soothing, neutral tones."))
                list.add(Prompts("Create a chevron pattern wallpaper with bold, zigzag stripes in contemporary colors."))
                list.add(Prompts("Craft a herringbone pattern wallpaper for a classic and timeless look."))
                list.add(Prompts("Illustrate a nautical-themed pattern with anchors, sailboats, and ocean waves."))
                list.add(Prompts("Design an animal print pattern wallpaper, such as zebra stripes or leopard spots."))
                list.add(Prompts("Create a mesmerizing mandala pattern with intricate details and vibrant hues."))
                list.add(Prompts("Compose a celestial pattern wallpaper with stars, moons, and galaxies."))
                list.add(Prompts("Craft a retro-inspired, 1970s-style pattern with groovy shapes and colors."))
                list.add(Prompts("Design a digital-inspired pixel art pattern for a modern and pixelated look."))
                list.add(Prompts("Illustrate a hodgepodge pattern with a mix of eclectic elements and motifs."))
                list.add(Prompts("Create a mosaic pattern wallpaper with tessellating tiles in various shades."))
                list.add(Prompts("Craft an optical illusion pattern that tricks the eye with depth and movement."))
                list.add(Prompts("Compose a watercolor-inspired pattern with soft, flowing brushstrokes."))
                list.add(Prompts("Design a tribal pattern wallpaper influenced by indigenous art and symbols."))
                list.add(Prompts("Illustrate an abstract pattern with bold and freeform shapes."))
            }

            "Space" -> {
                list.add(Prompts("Illustrate a breathtaking cosmic scene with galaxies, nebulae, and distant stars."))
                list.add(Prompts("Design a futuristic space station wallpaper with sleek, metallic elements."))
                list.add(Prompts("Create a planetary system wallpaper featuring various colorful planets and their moons."))
                list.add(Prompts("Compose an astronaut's view of Earth from space, showing our beautiful planet."))
                list.add(Prompts("Craft a space exploration-themed wallpaper with rockets and spacecraft."))
                list.add(Prompts("Design a celestial zodiac wallpaper featuring all 12 zodiac signs and constellations."))
                list.add(Prompts("Illustrate an interstellar journey with a spaceship soaring through the cosmos."))
                list.add(Prompts("Create a retro-inspired sci-fi wallpaper with a vintage space-age aesthetic."))
                list.add(Prompts("Craft an alien world landscape wallpaper with exotic plants and creatures."))
                list.add(Prompts("Design a celestial map wallpaper, highlighting famous constellations and stars."))
                list.add(Prompts("Compose a cosmic collision wallpaper with meteors and celestial fireworks."))
                list.add(Prompts("Craft a space fantasy scene featuring mystical planets and otherworldly beings."))
                list.add(Prompts("Illustrate a black hole wallpaper with its gravitational pull and radiant accretion disk."))
                list.add(Prompts("Create a space-time continuum wallpaper, inspired by the theory of relativity."))
                list.add(Prompts("Design a futuristic city on a distant planet wallpaper, featuring advanced technology."))
                list.add(Prompts("Compose a surreal space dream with floating islands and surreal landscapes."))
                list.add(Prompts("Craft a retro-futuristic space race wallpaper with classic sci-fi imagery."))
                list.add(Prompts("Illustrate an astronaut's spacewalk wallpaper with Earth as a backdrop."))
                list.add(Prompts("Create a starship bridge wallpaper with advanced controls and navigation systems."))
                list.add(Prompts("Design an alien encounter wallpaper with friendly extraterrestrial beings."))
            }

            "Super Heroes" -> {
                list.add(Prompts("Design an epic battle scene between superheroes and supervillains."))
                list.add(Prompts("Create a wallpaper featuring your favorite comic book superhero in action."))
                list.add(Prompts("Illustrate a superhero team-up wallpaper with multiple heroes joining forces."))
                list.add(Prompts("Craft a dramatic silhouette wallpaper of a superhero against a cityscape."))
                list.add(Prompts("Design a dynamic comic book cover-inspired wallpaper featuring a superhero in the spotlight."))
                list.add(Prompts("Compose a retro-style superhero wallpaper reminiscent of the golden age of comics."))
                list.add(Prompts("Create an origin story wallpaper that shows a superhero's transformation."))
                list.add(Prompts("Design a minimalist superhero emblem wallpaper with iconic symbols."))
                list.add(Prompts("Illustrate a crossover wallpaper with heroes from different comic universes."))
                list.add(Prompts("Craft a humorous wallpaper that explores the everyday life of a superhero."))
                list.add(Prompts("Create a dark and moody antihero wallpaper featuring an edgy protagonist."))
                list.add(Prompts("Design a superheroine wallpaper that celebrates powerful female heroes."))
                list.add(Prompts("Compose a superhero in training wallpaper with a young hero learning the ropes."))
                list.add(Prompts("Craft a vintage comic panel wallpaper with classic superhero dialogue."))
                list.add(Prompts("Illustrate a sidekick wallpaper featuring the unsung heroes of the superhero world."))
                list.add(Prompts("Design a city in distress wallpaper with a superhero coming to the rescue."))
                list.add(Prompts("Create a space-faring superhero wallpaper set in the cosmos."))
                list.add(Prompts("Craft a technology-themed wallpaper with a hero in a high-tech suit."))
                list.add(Prompts("Illustrate a patriotic hero wallpaper with a flag-waving, justice-seeking character."))
                list.add(Prompts("Design a retro-futuristic superhero wallpaper inspired by science fiction."))
            }

            "Art" -> {
                list.add(Prompts("Design an abstract art wallpaper using vibrant colors and shapes."))
                list.add(Prompts("Create a surreal art wallpaper with dreamlike and imaginative elements."))
                list.add(Prompts("Illustrate a nature-inspired art wallpaper featuring landscapes or wildlife."))
                list.add(Prompts("Craft a minimalist art wallpaper with a focus on simplicity and elegance."))
                list.add(Prompts("Compose a pop art wallpaper inspired by the works of famous pop artists."))
                list.add(Prompts("Design a digital art wallpaper showcasing futuristic and technological themes."))
                list.add(Prompts("Create a vintage art wallpaper that emulates the styles of different time periods."))
                list.add(Prompts("Craft a mosaic art wallpaper using small pieces to form a larger image."))
                list.add(Prompts("Illustrate a watercolor art wallpaper with soft and flowing colors."))
                list.add(Prompts("Design a graffiti art wallpaper that captures the urban and street art vibe."))
                list.add(Prompts("Compose a collage art wallpaper with a mix of images and textures."))
                list.add(Prompts("Create an impressionist art wallpaper inspired by famous painters like Monet."))
                list.add(Prompts("Craft a surrealism art wallpaper that blurs the lines between reality and dreams."))
                list.add(Prompts("Illustrate a cubist art wallpaper with geometric shapes and abstraction."))
                list.add(Prompts("Design a pointillism art wallpaper using tiny dots to create the image."))
                list.add(Prompts("Craft a calligraphy art wallpaper with beautiful handwritten text or quotes."))
                list.add(Prompts("Create a stained glass art wallpaper with colorful and intricate patterns."))
                list.add(Prompts("Design a paper-cut art wallpaper that mimics the art of paper cutting."))
                list.add(Prompts("Compose a 3D art wallpaper with depth and realism."))
                list.add(Prompts("Illustrate a still-life art wallpaper featuring everyday objects as the subject."))
            }

            "City & Building" -> {
                list.add(Prompts("Design a cityscape wallpaper with a futuristic, sci-fi twist."))
                list.add(Prompts("Capture the essence of a bustling urban city in your wallpaper."))
                list.add(Prompts("Create a night skyline wallpaper with the city's lights shining brightly."))
                list.add(Prompts("Illustrate an architectural wallpaper highlighting iconic city buildings."))
                list.add(Prompts("Design a vintage city wallpaper featuring historic landmarks."))
                list.add(Prompts("Craft a minimalistic city wallpaper with clean lines and simple shapes."))
                list.add(Prompts("Capture the charm of a small town in your wallpaper design."))
                list.add(Prompts("Compose a waterfront city wallpaper with views of the sea or river."))
                list.add(Prompts("Create a fantasy city wallpaper with imaginative and surreal elements."))
                list.add(Prompts("Design a skyline silhouette wallpaper with a modern aesthetic."))
                list.add(Prompts("Illustrate a city in different seasons, from winter to summer."))
                list.add(Prompts("Craft a black and white city wallpaper with a classic look."))
                list.add(Prompts("Capture the city's energy and movement in your wallpaper."))
                list.add(Prompts("Design a wallpaper featuring famous skyscrapers and landmarks."))
                list.add(Prompts("Create a panoramic city view wallpaper that spans multiple screens."))
                list.add(Prompts("Craft a vintage travel poster-inspired city wallpaper."))
                list.add(Prompts("Illustrate a cozy city street scene with cafes and shops."))
                list.add(Prompts("Design a street art-themed city wallpaper with vibrant colors."))
                list.add(Prompts("Capture the beauty of city parks and green spaces in your wallpaper."))
                list.add(Prompts("Create a city at sunset or sunrise with warm, golden hues."))
            }

            "Ocean" -> {
                list.add(Prompts("Design a serene ocean sunset wallpaper with calming colors."))
                list.add(Prompts("Capture the power of crashing waves in a coastal wallpaper."))
                list.add(Prompts("Illustrate a tropical paradise wallpaper with palm trees and turquoise waters."))
                list.add(Prompts("Create a deep-sea wallpaper featuring marine life and corals."))
                list.add(Prompts("Craft a nautical-themed wallpaper with sailboats and lighthouses."))
                list.add(Prompts("Design an underwater world wallpaper with exotic fish and seascapes."))
                list.add(Prompts("Capture the vastness of the open ocean in your wallpaper."))
                list.add(Prompts("Illustrate a beachside scene with surfers and beachgoers."))
                list.add(Prompts("Create a seashell and sand dollar-themed wallpaper."))
                list.add(Prompts("Craft a tranquil island wallpaper with hammocks and crystal-clear waters."))
                list.add(Prompts("Design a coastal village wallpaper with fishing boats and colorful houses."))
                list.add(Prompts("Illustrate a stormy sea wallpaper with dramatic waves and clouds."))
                list.add(Prompts("Capture the beauty of ocean sunrises in your wallpaper."))
                list.add(Prompts("Create a seagull and pelican wallpaper with a coastal vibe."))
                list.add(Prompts("Craft a vintage ocean travel poster-inspired wallpaper."))
                list.add(Prompts("Design a fantasy underwater kingdom wallpaper with mermaids and sea creatures."))
                list.add(Prompts("Illustrate a lighthouse wallpaper with its beam shining over the water."))
                list.add(Prompts("Create an abstract ocean-themed wallpaper with waves and patterns."))
                list.add(Prompts("Craft a night sky over the ocean wallpaper with stars and moonlight."))
                list.add(Prompts("Design an ocean conservancy wallpaper to raise awareness of marine life."))
            }

            "Travel" -> {
                list.add(Prompts("Design a wanderlust-inspired travel wallpaper featuring iconic landmarks."))
                list.add(Prompts("Capture the beauty of a mountainous landscape in your wallpaper."))
                list.add(Prompts("Illustrate a tropical paradise wallpaper with palm trees and crystal-clear waters."))
                list.add(Prompts("Create a vintage travel poster-style wallpaper for a favorite destination."))
                list.add(Prompts("Craft a road trip-inspired wallpaper with scenic highways and adventure vibes."))
                list.add(Prompts("Design a world map-themed wallpaper with markers for dream destinations."))
                list.add(Prompts("Illustrate a backpacker's adventure wallpaper with a sense of exploration."))
                list.add(Prompts("Capture the charm of a European cityscape in your wallpaper."))
                list.add(Prompts("Create a desert oasis-themed wallpaper with sand dunes and camels."))
                list.add(Prompts("Craft a travel essentials wallpaper with suitcases, passports, and cameras."))
                list.add(Prompts("Design an airport terminal wallpaper with bustling travelers and planes."))
                list.add(Prompts("Illustrate a cruise ship wallpaper with a view of the open sea."))
                list.add(Prompts("Create a train journey-themed wallpaper with scenic railways."))
                list.add(Prompts("Craft a wildlife safari wallpaper featuring exotic animals in their habitat."))
                list.add(Prompts("Design a space travel-themed wallpaper with rockets and distant planets."))
                list.add(Prompts("Capture the spirit of a cultural festival in your wallpaper."))
                list.add(Prompts("Illustrate a beach vacation wallpaper with beach chairs and parasols."))
                list.add(Prompts("Create a winter wonderland travel wallpaper with snow-covered landscapes."))
                list.add(Prompts("Craft an adventure sports-themed wallpaper with surfing and mountain climbing."))
                list.add(Prompts("Design a travel journal-inspired wallpaper with sketches and notes."))
            }

            "Love" -> {
                list.add(Prompts("Design a romantic wallpaper with a couple sharing a kiss at sunset."))
                list.add(Prompts("Create a heartwarming wallpaper featuring hand-drawn love quotes."))
                list.add(Prompts("Illustrate a cozy fireplace scene with two mugs of hot cocoa for your wallpaper."))
                list.add(Prompts("Craft a love letter-themed wallpaper with handwritten notes and roses."))
                list.add(Prompts("Design a Valentine's Day wallpaper with hearts, chocolates, and cupid."))
                list.add(Prompts("Create a vintage love story wallpaper with a timeless romantic feel."))
                list.add(Prompts("Illustrate a beachside proposal wallpaper with a ring and 'Will You Marry Me?' written in the sand."))
                list.add(Prompts("Craft a celestial love-themed wallpaper with stars, the moon, and constellations."))
                list.add(Prompts("Design an elegant wedding-themed wallpaper with rings and floral arrangements."))
                list.add(Prompts("Capture the essence of a long-distance relationship in your wallpaper."))
                list.add(Prompts("Create a heart-shaped puzzle wallpaper with pieces coming together."))
                list.add(Prompts("Illustrate a couple's silhouette dancing in the rain for your wallpaper."))
                list.add(Prompts("Craft a playful love-themed wallpaper with cute animal characters."))
                list.add(Prompts("Design a minimalistic wallpaper with a single red rose on a white background."))
                list.add(Prompts("Celebrate LGBTQ+ love with a pride-themed wallpaper."))
                list.add(Prompts("Illustrate a cozy book nook wallpaper with love stories on the shelves."))
                list.add(Prompts("Craft a travel-themed love wallpaper with passport stamps and suitcases."))
                list.add(Prompts("Design a pixel art-style heart wallpaper with a retro vibe."))
                list.add(Prompts("Create a garden of love-themed wallpaper with blooming flowers."))
                list.add(Prompts("Illustrate a futuristic love story wallpaper with sci-fi elements."))
            }

            "Sadness" -> {
                list.add(Prompts("Design a somber wallpaper featuring a lone figure walking in the rain."))
                list.add(Prompts("Illustrate a tearful eye with expressive emotions for your wallpaper."))
                list.add(Prompts("Create a melancholic landscape wallpaper with fading colors and dark clouds."))
                list.add(Prompts("Craft a broken heart-themed wallpaper with shattered glass or cracks."))
                list.add(Prompts("Design a desolate urban scene wallpaper with abandoned buildings."))
                list.add(Prompts("Capture the feeling of loss in a minimalist wallpaper with a fading silhouette."))
                list.add(Prompts("Illustrate a wilted flower wallpaper to symbolize fading hope."))
                list.add(Prompts("Craft a rainy day wallpaper with raindrops on a window pane."))
                list.add(Prompts("Create a grayscale wallpaper with a person looking out into the distance."))
                list.add(Prompts("Design a surreal dream-like wallpaper with a sense of isolation."))
                list.add(Prompts("Illustrate a sinking ship wallpaper as a metaphor for despair."))
                list.add(Prompts("Craft a dark forest wallpaper with a hidden path leading to nowhere."))
                list.add(Prompts("Create a wallpaper with a broken mirror reflecting fragmented emotions."))
                list.add(Prompts("Design a deserted beach scene wallpaper with footprints leading away."))
                list.add(Prompts("Capture the essence of grief with a simple candlelit vigil wallpaper."))
                list.add(Prompts("Illustrate a surreal underwater world wallpaper with a sense of drowning."))
                list.add(Prompts("Craft a moonlit night wallpaper with a lonely figure gazing at the stars."))
                list.add(Prompts("Create a lost-in-space wallpaper with an astronaut drifting in the void."))
                list.add(Prompts("Design a rainy city street wallpaper with reflections on wet pavement."))
                list.add(Prompts("Illustrate a fallen angel-themed wallpaper with a sense of loss."))
            }

            "Mountain" -> {
                list.add(Prompts("Design a majestic mountain range wallpaper at sunrise or sunset."))
                list.add(Prompts("Illustrate a serene lake nestled amidst towering mountains for your wallpaper."))
                list.add(Prompts("Create an adventurous hiking trail wallpaper with breathtaking mountain views."))
                list.add(Prompts("Craft a minimalist mountain silhouette wallpaper in calming pastel colors."))
                list.add(Prompts("Capture the grandeur of a snow-capped mountain peak wallpaper."))
                list.add(Prompts("Design a tranquil forest nestled in the foothills of the mountains."))
                list.add(Prompts("Illustrate a mountain village wallpaper with cozy cabins and rolling hills."))
                list.add(Prompts("Craft a starry night mountain wallpaper with the Milky Way above."))
                list.add(Prompts("Create an autumn landscape wallpaper with colorful trees against a mountain backdrop."))
                list.add(Prompts("Design a mountain reflection wallpaper in the calm waters of a lake."))
                list.add(Prompts("Illustrate a mountain adventure wallpaper with climbers on a challenging ascent."))
                list.add(Prompts("Craft a rugged canyon wallpaper with steep cliffs and winding rivers."))
                list.add(Prompts("Capture the mystery of a misty mountain wallpaper."))
                list.add(Prompts("Create a mountain wildlife wallpaper with native animals in their habitat."))
                list.add(Prompts("Design a rocky terrain wallpaper with layers of geological formations."))
                list.add(Prompts("Illustrate a mountain zen wallpaper with meditation and tranquility."))
                list.add(Prompts("Craft a fantasy world wallpaper where mountains touch the sky."))
                list.add(Prompts("Capture the beauty of the Himalayas in a wallpaper with prayer flags."))
                list.add(Prompts("Create a mountain sunrise wallpaper with the first light touching the peaks."))
                list.add(Prompts("Design a mountain seasons wallpaper showcasing the four seasons' beauty."))
            }

            "Music" -> {
                list.add(Prompts("Design a musical notes wallpaper with vibrant colors and a dynamic composition."))
                list.add(Prompts("Illustrate a vintage vinyl record wallpaper with a touch of nostalgia."))
                list.add(Prompts("Create a rock and roll wallpaper featuring iconic electric guitars."))
                list.add(Prompts("Craft a jazz-inspired wallpaper with saxophones, trumpets, and a smoky ambiance."))
                list.add(Prompts("Capture the magic of a live concert in a crowd-swaying music festival wallpaper."))
                list.add(Prompts("Design a classical music wallpaper with a grand piano and orchestral instruments."))
                list.add(Prompts("Illustrate a retro cassette tape wallpaper with a mixtape of your favorite songs."))
                list.add(Prompts("Craft a reggae-themed wallpaper with vibrant Rastafarian colors."))
                list.add(Prompts("Create a psychedelic music wallpaper with trippy visuals and swirling patterns."))
                list.add(Prompts("Craft a modern headphones wallpaper in minimalist black and white."))
                list.add(Prompts("Design a punk rock-inspired wallpaper featuring anarchy symbols and punk bands."))
                list.add(Prompts("Illustrate a music studio wallpaper with soundboards and recording equipment."))
                list.add(Prompts("Capture the spirit of hip-hop in a graffiti-style wallpaper with urban elements."))
                list.add(Prompts("Create a disco dance floor wallpaper with colorful lights and a funky vibe."))
                list.add(Prompts("Design a country music wallpaper featuring acoustic guitars and cowboy hats."))
                list.add(Prompts("Illustrate a marching band wallpaper with brass and percussion instruments."))
                list.add(Prompts("Craft a soothing acoustic guitar wallpaper with a campfire under the stars."))
                list.add(Prompts("Create a music notes wallpaper that spells out a meaningful song lyric."))
                list.add(Prompts("Design a world music-themed wallpaper with instruments from around the globe."))
                list.add(Prompts("Illustrate a futuristic music wallpaper with a sci-fi twist and electronic vibes."))
            }
        }
        return list
    }


    override fun onResume() {
        super.onResume()
//        lifecycleScope.launch {
//            val premium = IKUtils.isUserIAPAvailableAsync()
//            AdConfig.ISPAIDUSER = premium
//            Log.e(TAG, "InAppPurchase12: $premium")
//        }
        IKSdkController.setEnableShowResumeAds(true)
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

            // Check if the activity is still running before showing the dialog
            alertDialog?.show()
        }


    }

    private fun dismissNoInternetDialog() {
        lifecycleScope.launch(Dispatchers.Main) {
            alertDialog?.dismiss()

        }
    }
}
























