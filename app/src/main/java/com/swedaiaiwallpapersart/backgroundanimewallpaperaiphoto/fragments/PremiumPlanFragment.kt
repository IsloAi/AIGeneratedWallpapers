package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetailsParams
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.RetrofitInstance
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.PostDataOnServer
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.PremiumAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.PremiumPlanModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentPremiumPlanBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.menuFragments.HomeFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.PremiumPlanCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.GoogleLogin
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyDialogs
class PremiumPlanFragment : Fragment() {
    private var _binding: FragmentPremiumPlanBinding? = null
    private val binding get() = _binding!!
    private var isFragmentAttached: Boolean = false
    private lateinit var recyclerView: RecyclerView
    private var  navController: NavController? = null
    private val postDataOnServer = PostDataOnServer()
    private var dialog:Dialog? = null
    private lateinit var auth: FirebaseAuth
    private val googleLogin =GoogleLogin()
    private var isPopOpen = false
    private var adapter : PremiumAdapter? =null
    private val myDialogs = MyDialogs()
    private var  dialog2:Dialog? = null
    private var whichPlanSelected:Int?=null
    private val rewardAdWatched = 5
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPremiumPlanBinding.inflate(inflater, container, false)
        onCreteViewCalling()
        return binding.root
    }
   private fun onCreteViewCalling(){
        recyclerView = binding.premiumRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        setRecyclerView()
        navController = findNavController()
        binding.backButton.setOnClickListener {
            navController?.navigateUp()
        }
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
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            when(plan){
                0-> {
                    if(MySharePreference.getCounterValue(requireContext())!! <= 5){
                        loadRewardedAd()
                    }else{
                        Toast.makeText(requireContext(), "Your daily limit is full ", Toast.LENGTH_SHORT).show() } }
                1->  purchasesPlan(0)
                2->  purchasesPlan(1)
                3->  purchasesPlan(2)
                4->  purchasesPlan(3)
                5->  purchasesPlan(4)
                6->  purchasesPlan(5)
            }
        }else{
            findNavController().navigate(R.id.action_premiumPlanFragment_to_signInFragment)
        }
    }
    private fun planList():ArrayList<PremiumPlanModel>{
        val arrayList = ArrayList<PremiumPlanModel>()
        arrayList.add(PremiumPlanModel("Watch Ad",5,R.drawable.card_baja,0))
        arrayList.add(PremiumPlanModel(Constants.plan1,25,R.drawable.card_simple,1))
        arrayList.add(PremiumPlanModel(Constants.plan2,90,R.drawable.card_simple,2))
        arrayList.add(PremiumPlanModel(Constants.plan3,250,R.drawable.card_simple,3))
        arrayList.add(PremiumPlanModel(Constants.plan4,700,R.drawable.card_simple,4))
        arrayList.add(PremiumPlanModel(Constants.plan5,2000,R.drawable.card_simple,5))
        arrayList.add(PremiumPlanModel(Constants.plan6,5000,R.drawable.card_ring,6))
        return arrayList
    }
    private fun loadRewardedAd(){
        Toast.makeText(requireContext(), "coming soon", Toast.LENGTH_SHORT).show()
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
//    private fun getUserIdDialog() {
//        dialog = Dialog(requireContext())
//        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog?.setContentView(R.layout.get_user_id)
//        val width = WindowManager.LayoutParams.MATCH_PARENT
//        val height = WindowManager.LayoutParams.WRAP_CONTENT
//        dialog?.window!!.setLayout(width, height)
//        dialog?.window!!.setBackgroundDrawableResource(android.R.color.transparent)
//        dialog?.setCancelable(false)
//        dialog?.findViewById<RelativeLayout>(R.id.closeButton)?.setOnClickListener {
//            isPopOpen = false
//            dialog?.dismiss()}
//        dialog?.findViewById<LinearLayout>(R.id.googleLogin)?.setOnClickListener {
//            isPopOpen=false
//            dialog2 = Dialog(requireContext())
//            myDialogs.waiting(dialog2!!)
//            dialog?.dismiss()
//            login()
//        }
//        dialog?.show()
//    }
//    private fun login() {
//        auth = FirebaseAuth.getInstance()
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//        val googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
//        val signInIntent = googleSignInClient.signInIntent
//        startActivityForResult(signInIntent, HomeFragment.RC_SIGN_IN)
//    }
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == HomeFragment.RC_SIGN_IN) {
//            dialog2?.dismiss()
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            try {
//                val account = task.getResult(ApiException::class.java)
//                firebaseAuthWithGoogle(account.idToken!!)
//            } catch (e: ApiException) {
//                Toast.makeText(requireContext(), "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//    private fun firebaseAuthWithGoogle(idToken: String) {
//        val credential = GoogleAuthProvider.getCredential(idToken, null)
//        auth.signInWithCredential(credential)
//            .addOnCompleteListener(requireActivity()){ task ->
//                if (task.isSuccessful) {
//                    Toast.makeText(requireContext(), "Successfully login", Toast.LENGTH_SHORT).show()
//                    val user = auth.currentUser
//                    val  name = user?.displayName
//                    val  email = user?.email
//                    val  image = user?.photoUrl
//                    if(email != null){
//                        googleLogin.fetchGems(requireContext(),email,dialog,binding.gemsText)
//                    }
//                } else {
//                    Toast.makeText(requireContext(), "Authentication failed", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }

    private fun purchasesPlan(index:Int) {
        whichPlanSelected = index
        val billingClient = BillingClient.newBuilder(requireActivity())
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    Log.e("TAG", "ready to purchess")
                    val skuList: MutableList<String> = ArrayList()
                    skuList.add("plan1")
                    skuList.add("plan2")
                    skuList.add("plan3")
                    skuList.add("plan4")
                    skuList.add("plan5")
                    skuList.add("plan6")
                    val params = SkuDetailsParams.newBuilder()
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
                    billingClient.querySkuDetailsAsync(
                        params.build()
                    ) { billingResult, skuDetailsList ->
                        try {
//                            progressDialog.dismiss()
                        } catch (c: java.lang.Exception) {
                            c.printStackTrace()
                        }
                        Log.e("TAG", "sku details " + skuDetailsList!!.size)
                        // Process the result.
                        Log.e(
                            "TAG",
                            "skuDetailsList.get(0).getTitle() " + skuDetailsList[0].title
                        )
                        val billingFlowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(skuDetailsList[index])
                            .build()
                        val responseCode = billingClient.launchBillingFlow(
                            requireActivity(),
                            billingFlowParams
                        ).responseCode
                        Log.e("TAG", "responseCode $responseCode")
                    }
                }
            }
            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.e("TAG", "service disconnected")
            }
        })
    }
    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (isFragmentAttached) {
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                when(whichPlanSelected){
                    0->{postGems(25)}
                    1->{postGems(90)}
                    2->{postGems(250)}
                    3->{postGems(700)}
                    4->{postGems(2000)}
                    5->{postGems(5000)}
                }
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
                Toast.makeText(requireContext(), "Purchases Error ", Toast.LENGTH_SHORT).show()
            } else {
                // Handle any other error codes.
            }
        }
        }
    private fun postGems(gems:Int){
        val totalGems = MySharePreference.getGemsValue(requireContext())!!+gems
        postDataOnServer.gemsPostData(requireContext(), MySharePreference.getUserID(requireContext())!!,RetrofitInstance.getInstance(),totalGems, PostDataOnServer.isPlan)
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