package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
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
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.ActivityMainBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.AdClickCounter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MyApp
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.billing.BillingConstant.InAppProducts
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.billing.BillingConstant.billingClient
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.billing.BillingConstant.purchasedProducts
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.ConnectivityListener
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.BillingStore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.LocaleManager
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.AllWallpapersViewmodel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import kotlin.collections.set

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ConnectivityListener {

    lateinit var binding: ActivityMainBinding
    private lateinit var job: Job
    val TAG = "MainActivity"
    private var selectedPrompt: String? = null
    private var alertDialog: AlertDialog? = null

    //VIEW MODELS VARIABLES
    private val allWallpapersViewmodel: AllWallpapersViewmodel by viewModels()

    /*private val liveViewModel: LiveWallpaperViewModel by viewModels()
    private val myCatNameViewModel: MyCatNameViewModel by viewModels()
    private val mainActivityViewModel: MainActivityViewModel by viewModels()
    private val myViewModel: MyHomeViewModel by viewModels()
    private val chargingAnimationViewmodel: ChargingAnimationViewmodel by viewModels()
    private val doubleWallpaperVideModel: DoubeWallpaperViewModel by viewModels()*/

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
        fetchDatafromAPI()

        /*handleBackPress()
        fetchData(deviceID.toString())
        observefetechedData()
        if (!isNetworkAvailable()) {
            showNoInternetDialog()
        }
        readJsonAndSaveDataToDb()
        getSetTotallikes()*/

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
                    //disableEdgeToEdge(window)
                    val windowInsetsController =
                        WindowCompat.getInsetsController(window, window.decorView)
                    windowInsetsController.systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
                }
            }
        }

        AdClickCounter.reset()
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
                    AdConfig.iapScreenType = 0
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
                purchasedProducts.add(purchase.products.toString())
                AdConfig.ISPAIDUSER = true
                AdConfig.inAppConfig = false
                AdConfig.iapScreenType = 0
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

    override fun onNetworkAvailable() {

    }

    override fun onNetworkLost() {
        showNoInternetDialog()
    }

    private fun showNoInternetDialog() {
        lifecycleScope.launch(Dispatchers.Main) {
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle("No Internet Connection")
            builder.setMessage("Please check your internet connection")
            builder.setCancelable(false)
            alertDialog = builder.create()
            alertDialog?.show()
        }
    }

    private fun fetchDatafromAPI() {
        //FETCHING ALL WALLPAPERS
        allWallpapersViewmodel.fetchWallpapers(this@MainActivity)
    }

}