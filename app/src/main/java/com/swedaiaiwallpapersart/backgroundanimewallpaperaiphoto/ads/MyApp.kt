package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkRequest
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.bmik.android.sdk.SDKBaseApplication
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.billing.BillingHelper
import com.bmik.android.sdk.billing.SDKBillingHandler
import com.bmik.android.sdk.billing.dto.PurchaseInfo
import com.bmik.android.sdk.listener.keep.SDKIAPProductIDProvider
import com.google.firebase.FirebaseApp
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.ConnectivityListener
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.ConnectivityCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApp : SDKBaseApplication(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory


    override fun enableRewardAd(): Boolean {
        return true
    }





    override fun configIAPData(): SDKIAPProductIDProvider {
        return object : SDKIAPProductIDProvider {
            override val enableIAPFunction: Boolean
                get() = true

            override fun listProductIDsSubscription(): ArrayList<String> {
                return arrayListOf()
            }

            override fun listProductIDsPurchase(): ArrayList<String> {
                return arrayListOf()
            }

            override fun listProductIDsRemoveAd(): ArrayList<String> {
                return arrayListOf()
            }

            override fun listProductIDsCanPurchaseMultiTime(): ArrayList<String> {
                return arrayListOf()
            }

        }
    }

    private var connectivityListener: ConnectivityListener? = null

    // Method to set the listener
    fun setConnectivityListener(listener: ConnectivityListener) {
        connectivityListener = listener
    }
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(applicationContext)
        addActivityEnableShowResumeAd(MainActivity::class.java)
        setEnableShowResumeAds(true)
        SDKBaseController.getInstance().setAutoReloadRewarded(true)
        setEnableShowLoadingResumeAds(true)

        BillingHelper.getInstance().setBillingListener(object : SDKBillingHandler {
            override fun onProductPurchased(productId: String, details: PurchaseInfo?) {
                //do some thing
            }

            override fun onPurchaseHistoryRestored() {
                //do some thing
            }

            override fun onBillingError(errorCode: Int, error: Throwable?) {
                Log.e("TAG", "onBillingError: $errorCode$error" )
            }

            override fun onBillingInitialized() {
                Log.e("TAG", "onBillingInitialized: " )
            }
        })
    }

    override val workManagerConfiguration: Configuration
        get() =  Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

}

