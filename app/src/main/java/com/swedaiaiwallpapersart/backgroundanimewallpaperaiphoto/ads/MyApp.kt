package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.bmik.android.sdk.SDKBaseApplication
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.keep.SDKIAPProductIDProvider
import com.google.firebase.FirebaseApp
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
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
                get() = false

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

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(applicationContext)
        addActivityEnableShowResumeAd(MainActivity::class.java)
        setEnableShowResumeAds(true)
        SDKBaseController.getInstance().setAutoReloadRewarded(true)
        setEnableShowLoadingResumeAds(true)
    }

    override val workManagerConfiguration: Configuration
        get() =  Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

}

