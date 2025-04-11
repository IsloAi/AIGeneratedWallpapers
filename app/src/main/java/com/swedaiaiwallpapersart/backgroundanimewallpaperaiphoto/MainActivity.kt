package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Window
import android.view.WindowInsets
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.example.ohmywall.presentation.activities.billing.ProductModel
import com.example.ohmywall.presentation.activities.billing.SkuDetailsStorage
import com.example.ohmywall.presentation.activities.billing.Store
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
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.ActivityMainBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MyApp
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.billing.BillingConstant.InAppProducts
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.billing.BillingConstant.billingClient
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.billing.BillingConstant.purchasedProducts
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.ListResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.SingleDatabaseResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.batteryanimation.ChargingAnimationViewmodel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.ConnectivityListener
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.LiveImagesResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.BillingStore
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
        setUpBilling()
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

    private fun setUpBilling() {
        billingClient =
            BillingClient.newBuilder(this@MainActivity).setListener { billingResult, purchases ->
                // Handle purchase updates
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    for (purchase in purchases) {
                        handlePurchase(purchase)
                    }
                }
            }.enablePendingPurchases() // Required for acknowledging purchases
                .build()

        billingClient.startConnection(object : BillingClientStateListener {
            @RequiresApi(Build.VERSION_CODES.S)
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // Billing client is ready
                    queryProducts()
                    queryPurchases()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Retry connection
                setUpBilling()
            }
        })
    }

    private fun queryPurchases() {
        // Query INAPP purchases (one-time purchases)
        billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                processExistingPurchases(purchases)
            }
        }

        // Query SUBS purchases (subscriptions)
        billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                processExistingPurchases(purchases)
            }
        }
    }

    private fun processExistingPurchases(purchases: List<Purchase>) {
        for (purchase in purchases) {
            Log.d("Billing", "Existing purchase found: ${purchase.products}")

            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                if (!purchase.isAcknowledged) {
                    acknowledgePurchase(purchase) // Ensure it's acknowledged
                }
                // ✅ Save the purchase state in SharedPreferences or a database
                for (productId in purchase.products) {
                    Log.d("Billing", "processExistingPurchases: productId: $productId")
                    purchasedProducts.add(productId)
                }
                if (purchasedProducts.isNotEmpty()) {
                    AdConfig.ISPAIDUSER = true
                    AdConfig.inAppConfig = false
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun queryProducts() {
        val productList = BillingStore.SUBSCRIPTION_IDS + BillingStore.PRODUCT_IDS
        //subscriptions
        val subscriptionParams = SkuDetailsParams.newBuilder().setSkusList(productList)
            .setType(BillingClient.SkuType.SUBS).build()

        billingClient.querySkuDetailsAsync(subscriptionParams) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                for (skuDetails in skuDetailsList) {
                    InAppProducts.add(
                        ProductModel(
                            skuDetails.sku,
                            skuDetails.title,
                            skuDetails.price,
                            "SUBS",
                            getDuration(skuDetails)
                        )
                    )
                    //binding.priceWeekly.text = skuDetails.price
                    SkuDetailsStorage.skuDetailsMap[skuDetails.sku] = skuDetails // Save SKU Details
                    Log.d(
                        "Billing", "Subs List: $skuDetails"
                    )
                }
            }
        }
        //product ids
        val productParams = SkuDetailsParams.newBuilder().setSkusList(productList)
            .setType(BillingClient.SkuType.INAPP).build()

        billingClient.querySkuDetailsAsync(productParams) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                for (skuDetails in skuDetailsList) {
                    InAppProducts.add(
                        ProductModel(
                            skuDetails.sku,
                            skuDetails.title,
                            skuDetails.price,
                            "INAPP",
                            getDuration(skuDetails)
                        )
                    )
                    //binding.pricelifeTime.text = skuDetails.price
                    SkuDetailsStorage.skuDetailsMap[skuDetails.sku] = skuDetails // Save SKU Details

                    // Store skuDetails for later use
                    Log.d(
                        "Billing", "InApp List: $skuDetails"
                    )
                }
            }
        }

    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            // Unlock content or features
            if (!purchase.isAcknowledged) {
                acknowledgePurchase(purchase)
            } else {
                Log.d("Billing", "handlePurchase: Purchase already acknowledged: $purchase.")
            }
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        val purchasedProduct = purchase.products.firstOrNull()
        Log.d("Billing", "acknowledgePurchase: ProductPurchased: $purchasedProduct")
        val params =
            AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()

        billingClient.acknowledgePurchase(params) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                Log.d("Billing", "acknowledgePurchase: Purchase acknowledged: ${purchase.products}")
                //purchasedProducts.add(purchase.products.toString())
                //billingViewModel.purchaseAcknowledged.postValue(true)
                /*lifecycleScope.launch {
                    mainViewModel.postPurchase(
                        purchase.products.toString(),
                        purchase.purchaseToken,
                        getDeviceId(this@MainActivity)
                    )
                }*/
                //getResponse()
                Log.d(
                    "APIResponse",
                    "acknowledgePurchase: Purchase acknowledged\ntoken:${purchase.purchaseToken}\nid:${purchase.products}"
                )

            } else {
                Log.e("Billing", "Error acknowledging purchase: ${billingResult.debugMessage}")
            }
        }
    }

    fun getDuration(iapItem: SkuDetails): String {
        val isRenewable = Store.SUBSCRIPTION_IDS.any { it == iapItem.sku }

        return if (isRenewable) {
            if (iapItem.sku.contains("weekly", ignoreCase = true)) {
                "Weekly"
            } else {
                "Yearly"
            }
        } else {
            "Lifetime"
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
                navController.popBackStack()

            }
        })
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

                        val positionTabs = remoteConfig["tablist_156"].asString()
                        val languagesOrder = remoteConfig["languages"].asString()
                        val languagesOrderArray =
                            languagesOrder.split(",").map { it.trim().removeSurrounding("\"") }
                        AdConfig.languagesOrder = languagesOrderArray

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
                val positionTabs = remoteConfig["tablist_156"].asString()
                val languagesOrder = remoteConfig["languages"].asString()
                val baseUrls = remoteConfig["dataUrl"].asString()
                val appFree = remoteConfig["wholeAppFree"].asBoolean()
                AdConfig.BASE_URL_DATA = baseUrls
                AdConfig.inAppConfig = appFree
                //AdConfig.Reward_Screen = appFree

                try {
                    val languagesOrderArray =
                        languagesOrder.split(",").map { it.trim().removeSurrounding("\"") }

                    AdConfig.languagesOrder = languagesOrderArray
                } catch (e: StringIndexOutOfBoundsException) {
                    e.printStackTrace()
                }

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
            }
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