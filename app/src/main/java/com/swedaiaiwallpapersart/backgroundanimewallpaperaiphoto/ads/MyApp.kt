package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads

import android.content.Context
import com.bmik.android.sdk.SDKBaseApplication
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.keep.SDKIAPProductIDProvider
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference

class MyApp : SDKBaseApplication() {

//    init {
//        enableReward = true
//    }


    companion object{
        var path = ""

        fun getCurrentPath(context: Context):String{
            return MySharePreference.getFileName(context.applicationContext)!!
        }
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
        addActivityEnableShowResumeAd(MainActivity::class.java)
        setEnableShowResumeAds(true)
        SDKBaseController.getInstance().setAutoReloadRewarded(true)
        setEnableShowLoadingResumeAds(true)
    }
}

