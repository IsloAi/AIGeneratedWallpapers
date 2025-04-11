package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.billing

import com.android.billingclient.api.BillingClient
import com.example.ohmywall.presentation.activities.billing.ProductModel

object BillingConstant {
    var InAppProducts: ArrayList<ProductModel> = ArrayList()
    var purchasedProducts: ArrayList<String> = ArrayList()
    lateinit var billingClient: BillingClient
}