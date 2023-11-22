package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding
.FragmentPremiumPlanBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.PremiumAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.PremiumPlanCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.PremiumPlanModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.RetrofitInstance
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.GoogleLogin
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyDialogs
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.PostDataOnServer

class PremiumPlanFragment : Fragment() {
    private var _binding: FragmentPremiumPlanBinding? = null
    private val binding get() = _binding!!
    private var isFragmentAttached: Boolean = false
    private lateinit var recyclerView: RecyclerView
    private var  navController: NavController? = null
    private val postDataOnServer = PostDataOnServer()
    private var dialog:Dialog? = null
    private val googleLogin =GoogleLogin()
    private var isPopOpen = false
    private var adapter : PremiumAdapter? =null
    private val myDialogs = MyDialogs()
    private var  dialog2:Dialog? = null
    private var whichPlanSelected:Int?=null
    private val rewardAdWatched = 5

    private var isPurchaseInProgress = false


    var progressDialog:ProgressDialog ?= null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPremiumPlanBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreteViewCalling()
        isPurchaseInProgress = false
    }
   private fun onCreteViewCalling(){
        recyclerView = binding.premiumRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        setRecyclerView()
        navController = findNavController()
        binding.backButton.setOnClickListener {
            navController?.navigateUp()
        }

       Glide.with(requireContext())
           .asGif()
           .load(R.raw.gems_animaion)
           .into(binding.animationView)
        binding.gemsText.text = MySharePreference.getGemsValue(requireContext()).toString()
    }
    private fun setRecyclerView(){
        adapter = PremiumAdapter(planList(),object: PremiumPlanCallback {
            override fun getPlan(planId: Int) {
                setCondition(planId)
            }
        })
        recyclerView.adapter = adapter
    }
    private fun setCondition(plan: Int) {
        if (!isPurchaseInProgress) {
            isPurchaseInProgress = true
            when(plan){
                0->  loadRewardedAd()
//                1->  purchasesPlan(0)
//                2->  purchasesPlan(1)
//                3->  purchasesPlan(2)
//                4->  purchasesPlan(3)
//                5->  purchasesPlan(4)
//                6->  purchasesPlan(5)
            }
        } else {
            Toast.makeText(requireContext(),"Purchase is already in progress",Toast.LENGTH_SHORT).show()
        }

    }
    private fun planList():ArrayList<PremiumPlanModel>{
        val arrayList = ArrayList<PremiumPlanModel>()
        arrayList.add(PremiumPlanModel("Watch Ad",5,R.drawable.card_baja,0))
//        arrayList.add(PremiumPlanModel(Constants.plan1,25,R.drawable.card_simple,1))
//        arrayList.add(PremiumPlanModel(Constants.plan2,90,R.drawable.card_simple,2))
//        arrayList.add(PremiumPlanModel(Constants.plan3,250,R.drawable.card_simple,3))
//        arrayList.add(PremiumPlanModel(Constants.plan4,700,R.drawable.card_simple,4))
//        arrayList.add(PremiumPlanModel(Constants.plan5,2000,R.drawable.card_simple,5))
//        arrayList.add(PremiumPlanModel(Constants.plan6,5000,R.drawable.card_ring,6))
        return arrayList
    }
    private fun loadRewardedAd(){

        //real Rewarded: 	ca-app-pub-5887559234735462/3077252807
        progressDialog = ProgressDialog(requireContext())
        progressDialog!!.setCancelable(false)
        progressDialog?.setMessage("Loading Ad")
        progressDialog?.show()

//        val adRequest = AdRequest.Builder().build()
//        RewardedAd.load(requireContext(), "ca-app-pub-3940256099942544/5224354917", adRequest, object : RewardedAdLoadCallback() {
//            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
//                // Handle the error.
//                Log.d("loading error", loadAdError.toString())
//                progressDialog?.dismiss()
//                if (isFragmentAttached){
//                    Toast.makeText(requireContext(), "Ad failed to load. Please check your internet connection and Try again", Toast.LENGTH_SHORT).show()
//
//                }
//            }
//
//            override fun onAdLoaded(ad: RewardedAd) {
//
//                Log.d("TAG", "Ad was loaded.")
//                progressDialog?.dismiss()
//                ad.show(requireActivity()
//                ) { rewardItem -> // Handle the reward.
//                    Log.d("TAG", "The user earned the reward.")
//                    val rewardAmount = rewardItem.amount
//                    val rewardType = rewardItem.type
//                    postGems(5)
//                    if (isFragmentAttached){
//                        Toast.makeText(requireContext(),"Congratulations! You have earned 5 gems",Toast.LENGTH_SHORT).show()
//
//                    }
//                    Log.e("Reward", "onUserEarnedReward: $rewardAmount")
//                    Log.e("Reward", "onUserEarnedReward: $rewardType")
//                }
//            }
//        })
    }
    private fun favPopup(dialog: Dialog) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.loading_ad)
        val width = WindowManager.LayoutParams.WRAP_CONTENT
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.setLayout(width, height)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.show()
    }

//    private fun purchasesPlan(index:Int) {
//        whichPlanSelected = index
//        val billingClient = BillingClient.newBuilder(requireActivity())
//            .setListener(purchasesUpdatedListener)
//            .enablePendingPurchases()
//            .build()
//        billingClient.startConnection(object : BillingClientStateListener {
//            override fun onBillingSetupFinished(billingResult: BillingResult) {
//                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
//                    // The BillingClient is ready. You can query purchases here.
//                    Log.e("TAG", "ready to purchess")
//                    val skuList: MutableList<String> = ArrayList()
//                    skuList.add("plan1")
//                    skuList.add("plan2")
//                    skuList.add("plan3")
//                    skuList.add("plan4")
//                    skuList.add("plan5")
//                    skuList.add("plan6")
//                    val params = SkuDetailsParams.newBuilder()
//                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
//                    billingClient.querySkuDetailsAsync(
//                        params.build()
//                    ) { billingResult, skuDetailsList ->
//                        try {
////                            progressDialog.dismiss()
//                        } catch (c: java.lang.Exception) {
//                            c.printStackTrace()
//                        }
//                        Log.e("TAG", "sku details " + skuDetailsList!!.size)
//                        // Process the result.
//                        Log.e(
//                            "TAG",
//                            "skuDetailsList.get(0).getTitle() " + skuDetailsList[0].title
//                        )
//                        val billingFlowParams = BillingFlowParams.newBuilder()
//                            .setSkuDetails(skuDetailsList[index])
//                            .build()
//                        val responseCode = billingClient.launchBillingFlow(
//                            requireActivity(),
//                            billingFlowParams
//                        ).responseCode
//                        Log.e("TAG", "responseCode $responseCode")
//                    }
//                }
//            }
//            override fun onBillingServiceDisconnected() {
//                // Try to restart the connection on the next request to
//                // Google Play by calling the startConnection() method.
//                Log.e("TAG", "service disconnected")
//            }
//        })
//    }
//    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
//        if (isFragmentAttached) {
//            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
//                when(whichPlanSelected){
//                    0->{postGems(25)}
//                    1->{postGems(90)}
//                    2->{postGems(250)}
//                    3->{postGems(700)}
//                    4->{postGems(2000)}
//                    5->{postGems(5000)}
//                }
//            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
//                // Handle an error caused by a user cancelling the purchase flow.
//                Toast.makeText(requireContext(), "Purchases Error ", Toast.LENGTH_SHORT).show()
//            } else {
//                // Handle any other error codes.
//            }
//
//            isPurchaseInProgress = false
//        }
//        }
    private fun postGems(gems:Int){
        val totalGems = MySharePreference.getGemsValue(requireContext())!!+gems
        postDataOnServer.gemsPostData(requireContext(), MySharePreference.getDeviceID(requireContext())!!,RetrofitInstance.getInstance(),totalGems, PostDataOnServer.isPlan)
        binding.gemsText.text = gems.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        isFragmentAttached =  false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        isFragmentAttached = true
    }


}