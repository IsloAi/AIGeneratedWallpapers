package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads

import com.bmik.android.sdk.SDKBaseApplication
import com.bmik.android.sdk.listener.keep.SDKIAPProductIDProvider
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity

class MyApp : SDKBaseApplication() {
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
        addActivityEnableShowResumeAd(MainActivity::class.java)
        setEnableShowResumeAds(true)
        setEnableShowLoadingResumeAds(true)
    }
}

