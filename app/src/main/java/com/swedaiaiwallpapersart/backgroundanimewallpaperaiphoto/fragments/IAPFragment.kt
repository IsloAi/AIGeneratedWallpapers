package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.ikame.android.sdk.billing.IKBillingController
import com.ikame.android.sdk.data.dto.pub.IKBillingError
import com.ikame.android.sdk.listener.pub.IKBillingPurchaseListener
import com.ikame.android.sdk.listener.pub.IKBillingValueListener
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentIAPBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IAPFragment : Fragment() {

    private var _binding:FragmentIAPBinding ?= null
    private val binding get() = _binding!!

    var priceWeekly:String = ""
    var priceMonthly = ""
    var priceYearly = ""
    var priceLife = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentIAPBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isavailable = IKBillingController.isIabServiceAvailable(requireContext())
        Log.e("TAG", "onViewCreated: "+isavailable )

        IKBillingController.initBilling(requireContext())

        binding.close.setOnClickListener {
            findNavController().popBackStack()
        }

        Glide.with(requireContext()).load(R.drawable.art).into(binding.mainImage)


        lifecycleScope.launch {
            delay(1000)
            IKBillingController.getPriceSubscribe(
                "unlock_all_premium_wallpaper_weekly_1",
                object: IKBillingValueListener {
                    override fun onResult(price: String, salePrice: String) {
                        lifecycleScope.launch(Dispatchers.Main){
                            price.let {

                                if (isAdded){

                                    binding.priceWeekly.text = it
                                }

                                priceWeekly = it

                            }
                        }
                        Log.e("TAG", "onResult: $price$salePrice" )
                    }

                }
            )

            IKBillingController.getPriceSubscribe(
                "unlock_all_premium_wallpaper_monthly_1",
                object: IKBillingValueListener{
                    override fun onResult(price: String, salePrice: String) {
                        lifecycleScope.launch(Dispatchers.Main){
                            price.let {
                                priceMonthly = it
                            }
                        }
                        Log.e("TAG", "onResult: $price$salePrice" )
                    }

                }
            )

            IKBillingController.getPriceSubscribe(
                "unlock_all_premium_wallpaper_yearly_2",
                object: IKBillingValueListener{
                    override fun onResult(price: String, salePrice: String) {
                        lifecycleScope.launch(Dispatchers.Main){
                            price.let {
                                if (isAdded){
                                    binding.priceYearly.text = it
                                }

                                priceYearly = it
                            }
                        }
                        Log.e("TAG", "onResult: $price$salePrice" )
                    }

                }
            )

            IKBillingController.getPricePurchase(
                "unlock_all_premium_lifetime",
                object: IKBillingValueListener{
                    override fun onResult(price: String, salePrice: String) {
                        lifecycleScope.launch(Dispatchers.Main){
                            price.let {
                                if (isAdded){
                                    binding.pricelifeTime.text = it
                                }

                                priceLife = it
                            }
                        }
                        Log.e("TAG", "onResult: $price$salePrice" )
                    }

                }
            )


            delay(2000)

            if (isAdded){
                binding.terms.text = "- Subscribed users have unlimited use and access to all of its Premium features, without any ads.\n" +
                        "- Non-subscribed users can continuously use the app with advertisements, and have a limited for use of Premium features.\n" +
                        "- Users can subscribe with different plans: Weekly($priceWeekly), Monthly ($priceMonthly), Yearly ($priceYearly) auto-renewing subscriptions.\n" +
                        "- Alternatively, users can purchase the full app (LIFETIME) for a one-time payment of ($priceLife). All updates and new features are received.\n" +
                        "- Payment will be charged to your Google Account at confirmation of purchase.\n" +
                        "- Subscriptions automatically renew unless auto-renew is disabled at least 24 hours before the end of the current period.\n" +
                        "- Account will be charged for renewal within 24-hour prior to the end of the current period, and identify the cost of renewal.\n" +
                        "- Any unused portion of a free trial period, if offered, will be forfeited when the user purchases a subscription to that publication, where applicable.\n" +
                        "- Subscriptions may be managed by the user and auto-renewal may be turned off by going to the user's Account Settings after purchase. Note that uninstalling the app will not cancel your subscription.\n" +
                        "1. On your Android phone or tablet, let's open the Google Play Store.\n" +
                        "2. Check if you're signed in to the correct Google Account.\n" +
                        "3. Tap Menu Subscriptions and Select the subscription you want to cancel.\n" +
                        "4. Tap Cancel subscription."
            }



        }


        binding.iapYearlyCard.setOnClickListener {
            val billingHelper = IKBillingController
            startPay(billingHelper,"unlock_all_premium_wallpaper_yearly_2","sub")
        }

        binding.iapLifeCard.setOnClickListener {
            val billingHelper = IKBillingController
            startPay(billingHelper,"unlock_all_premium_lifetime","pur")
        }

        binding.upgradeButton.setOnClickListener {
            val billingHelper = IKBillingController
            startPay(billingHelper,"unlock_all_premium_wallpaper_weekly_1","sub")
        }
    }

    private fun startPay(billingHelper: IKBillingController,id:String,type:String) {

        if (type == "sub"){
            billingHelper.subscribe(requireActivity(),id, object :
                IKBillingPurchaseListener {

                override fun onBillingFail(productId: String, error: IKBillingError) {
                    Log.e("TAG", "onBillingFail: $productId")
                    if (isAdded){
                        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onBillingSuccess(productId: String) {
                    Log.e("TAG", "onBillingSuccess: $productId")
                    AdConfig.ISPAIDUSER = true
                    if (isAdded){
                        findNavController().popBackStack()
                    }
                }

                override fun onProductAlreadyPurchased(productId: String) {
                    Log.e("TAG", "onProductAlreadyPurchased: ", )
                    if (isAdded){
                        Toast.makeText(requireContext(), "Already Purchased", Toast.LENGTH_SHORT).show()
                    }
                }

            })

        }else{
            billingHelper.purchase(requireActivity(),id, object :
                IKBillingPurchaseListener {
                override fun onBillingFail(productId: String, error: IKBillingError) {
                    Log.e("TAG", "onBillingFail: ", )
                    if (isAdded){
                        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onBillingSuccess(productId: String) {
                    AdConfig.ISPAIDUSER = true
                    if (isAdded){
                        findNavController().popBackStack()
                    }
                    Log.e("TAG", "onBillingSuccess: $productId" )
                }

                override fun onProductAlreadyPurchased(productId: String) {
                    Log.e("TAG", "onProductAlreadyPurchased: ", )
                    if (isAdded){
                        Toast.makeText(requireContext(), "Already Purchased", Toast.LENGTH_SHORT).show()
                    }
                }

            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}