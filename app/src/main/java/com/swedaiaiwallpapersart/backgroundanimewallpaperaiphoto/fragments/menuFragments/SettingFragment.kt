package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.menuFragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetailsParams
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.iosParameters
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.InternetState
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentSettingBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants

class SettingFragment : Fragment() {
   private var _binding: FragmentSettingBinding?=null
    private val binding get() = _binding!!
    private lateinit var referrerClient: InstallReferrerClient
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingBinding.inflate(inflater
            ,container,false)
        myCreated()
        return binding.root
    }
    @SuppressLint("SuspiciousIndentation")
    private fun myCreated() {
          binding.premiumCardButton.setOnClickListener {
              requireParentFragment().findNavController().navigate(R.id.action_mainFragment_to_premiumPlanFragment)
          }
        binding.rateUsButton.setOnClickListener {feedback()}
        binding.customerSupportButton.setOnClickListener {requireParentFragment().findNavController().navigate(R.id.action_mainFragment_to_feedbackFragment)}
        binding.shareAppButton.setOnClickListener {
            generateSharingLink(
                deepLink = "${Constants.PREFIX}/post/installapp".toUri(),
                previewImageLink = "https://edecator.com/wallpaperApp/share.jpeg".toUri()
            ) { generatedLink ->
                shareDeepLink(generatedLink)
            }
        }
        binding.privacyPolicyButton.setOnClickListener { openLink("https://swedebras.blogspot.com/2023/09/privacy-policy.html") }
        binding.moreAppButton.setOnClickListener { openLink("https://play.google.com/store/apps/developer?id=Swed+AI")  }
        binding.logOutButton.setOnClickListener {
            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser
            if (currentUser != null) {
                auth.signOut()
                Toast.makeText(requireContext(), "Successfully logout", Toast.LENGTH_SHORT).show()

                }
            }
        }

    private fun inviteFriend(){
        referrerClient = InstallReferrerClient.newBuilder(requireContext()).build()
        referrerClient.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                when (responseCode) {
                    InstallReferrerClient.InstallReferrerResponse.OK -> {
                        val response: ReferrerDetails = referrerClient.installReferrer
                        val referrerUrl: String = response.installReferrer
                        val referrerClickTime: Long = response.referrerClickTimestampSeconds
                        val appInstallTime: Long = response.installBeginTimestampSeconds
                        val instantExperienceLaunched: Boolean = response.googlePlayInstantParam
                        Toast.makeText(requireContext(), "installed ", Toast.LENGTH_SHORT).show()
                        Log.d("onInstallReferrer", "SetupFinished: referrerUrl $referrerUrl ,  referrerClickTime $referrerClickTime  , appInstallTime  $appInstallTime" +
                                "  instantExperienceLaunched   $instantExperienceLaunched")
                    }
                    InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                        // API not available on the current Play Store app.
                    }
                    InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                        // Connection couldn't be established.
                    }
                }
            }
            override fun onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }
    private fun feedback() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${requireActivity().packageName}"))
            startActivity(intent)
        } catch (e: Exception) {
            val i = Intent(Intent.ACTION_VIEW)
            i.data =
                Uri.parse("https://play.google.com/store/apps/details?id=" + requireActivity().packageName)
            startActivity(i)
        }
    }
    private fun openLink(url:String) {
        if (InternetState.checkForInternet(requireContext())) {
            val myWebLink = Intent(Intent.ACTION_VIEW)
            myWebLink.data = Uri.parse(url)
            startActivity(myWebLink)
        } else { Toast.makeText(requireContext(), "No Internet", Toast.LENGTH_SHORT).show() }
    }
    private fun generateSharingLink(
        deepLink: Uri,
        previewImageLink: Uri,
        getShareableLink: (String) -> Unit = {},
    ) {
        FirebaseDynamicLinks.getInstance().createDynamicLink().run {
            // What is this link parameter? You will get to know when we will actually use this function.
            link = deepLink

            // [domainUriPrefix] will be the domain name you added when setting up Dynamic Links at Firebase Console.
            // You can find it in the Dynamic Links dashboard.
            domainUriPrefix = Constants.PREFIX

            // Pass your preview Image Link here;
            setSocialMetaTagParameters(
                DynamicLink.SocialMetaTagParameters.Builder().setImageUrl(previewImageLink).build())
            // Required
            androidParameters {
                build()
            }

            // Finally
            buildShortDynamicLink()
        }.also {
            it.addOnSuccessListener { dynamicLink ->
                // This lambda will be triggered when short link generation is successful
                // Retrieve the newly created dynamic link so that we can use it further for sharing via Intent.
                getShareableLink.invoke(dynamicLink.shortLink.toString())
            }
            it.addOnFailureListener {
                // This lambda will be triggered when short link generation failed due to an exception
                Toast.makeText(requireContext(), "on failure", Toast.LENGTH_SHORT).show()
                // Handle
            }
        }
    }

    private fun Fragment.shareDeepLink(deepLink: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, "You have been shared an amazing meme, check it out ->")
        intent.putExtra(Intent.EXTRA_TEXT, deepLink)
         requireContext().startActivity(intent)
    }

    fun createDynamicLink(){
        val dynamicLink = Firebase.dynamicLinks.dynamicLink {
            link = Uri.parse("http://example.com/")
            domainUriPrefix = "https://swedai.page.link/installapp"
            // Open links with this app on Android
            androidParameters { }
            // Open links with com.example.ios on iOS
            iosParameters("com.example.ios") { }
        }

        val dynamicLinkUri = dynamicLink.uri
        Log.d("createDynamicLink", ": ${dynamicLinkUri.toString()}")
        //https://example.page.link?apn=app.example&ibi=com.example.ios&link=http%3A%2F%2Fexample.com%2F%2F
//        Utils.showToast(this, dynamicLinkUri.toString(), AppConstant.SUCCESS)

        val shortLinkTask = Firebase.dynamicLinks.shortLinkAsync {
            longLink = dynamicLinkUri
        }.addOnSuccessListener { shortLinkResult ->
            // Short link created
            val shortLink = shortLinkResult.shortLink
            val flowChartLink = shortLinkResult.previewLink

            Log.d("createDynamicLink", "createDynamicLink: shortLink-$shortLink --- flowChartLink-$flowChartLink")

            val welcomeMessage = "Welcome: $shortLink. Hi, this is a good app.\n"
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, welcomeMessage)
            startActivity(intent)
        }.addOnFailureListener { exception ->
            // Error
            Log.d("Error", exception.message.toString())
            // Handle the error appropriately
        }


//        val shortLinkTask = Firebase.dynamicLinks.shortLinkAsync {
//            longLink = dynamicLinkUri
//        }.addOnSuccessListener { (shortLink, flowChartLink) ->
//            // You'll need to import com.google.firebase.dynamiclinks.ktx.component1 and
//            // com.google.firebase.dynamiclinks.ktx.component2
//
//            // Short link created
//            Log.d("createDynamicLink", "createDynamicLink: shortLInk-${shortLink} ---  flowChartLink-${flowChartLink}")
////            processShortLink(shortLink, flowChartLink)
//           val welcomeMessage = "welcome  ${shortLink}. hi this a a good app\n"
//            val intent = Intent(Intent.ACTION_SEND)
//            intent.type = "text/plain"
//            intent.putExtra(Intent.EXTRA_TEXT, welcomeMessage)
//            startActivity(intent)
//        }.addOnFailureListener {
//            // Error
//            Log.d("Error", it.message.toString())
//            // ...
//        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}