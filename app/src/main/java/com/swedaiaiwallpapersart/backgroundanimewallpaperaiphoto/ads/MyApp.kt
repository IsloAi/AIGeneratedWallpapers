package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads

import android.util.Log
import com.google.firebase.FirebaseApp
import com.ikame.android.sdk.IKBaseApplication
import com.ikame.android.sdk.IKSdkController
import com.ikame.android.sdk.billing.IKBillingController
import com.ikame.android.sdk.data.dto.pub.IKBillingError
import com.ikame.android.sdk.listener.keep.SDKIAPProductIDProvider
import com.ikame.android.sdk.listener.pub.IKBillingHandlerListener
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.ConnectivityListener
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApp : IKBaseApplication() {


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
        IKSdkController.addActivityEnableShowResumeAd(MainActivity::class.java)
        IKSdkController.setEnableShowResumeAds(true)
//        IKSdkController.setAutoReloadRewarded(true)
        IKSdkController.setEnableShowLoadingResumeAds(true)

        IKBillingController.setBillingListener(object : IKBillingHandlerListener {
            override fun onProductPurchased(productId: String,  orderId: String?) {
                //do some thing
            }

            override fun onPurchaseHistoryRestored() {
                //do some thing
            }

            override fun onBillingError(error: IKBillingError) {
                Log.e("TAG", "onBillingError: $error" )
            }

            override fun onBillingInitialized() {
                Log.e("TAG", "onBillingInitialized: " )
            }
        })
    }

}

