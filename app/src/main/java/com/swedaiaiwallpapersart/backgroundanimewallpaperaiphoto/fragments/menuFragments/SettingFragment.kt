package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.menuFragments

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.iosParameters
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentSettingBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.service.ChargingAnimationService
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.InternetState
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference

class SettingFragment : Fragment() {
   private var _binding: FragmentSettingBinding?=null
    private val binding get() = _binding!!

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingBinding.inflate(inflater
            ,container,false)
        myCreated()
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        return binding.root
    }
    @SuppressLint("SuspiciousIndentation")
    private fun myCreated() {
          binding.premiumCardButton.setOnClickListener {
              requireParentFragment().findNavController().navigate(R.id.action_mainFragment_to_premiumPlanFragment)
          }

        binding.gemsText.text = MySharePreference.getGemsValue(requireContext()).toString()

        val isServiceRunning = isServiceRunning(requireContext(), ChargingAnimationService::class.java)

        binding.mySwitch.isChecked = isServiceRunning

        Glide.with(requireContext())
            .asGif()
            .load(R.raw.gems_animaion)
            .into(binding.animation)

        Glide.with(requireContext())
            .asGif()
            .load(R.raw.gems_animaion)
            .into(binding.animationDdd)
        binding.rateUsButton.setOnClickListener {feedback()}
        binding.customerSupportButton.setOnClickListener {findNavController().navigate(R.id.feedbackFragment)}
        binding.shareAppButton.setOnClickListener {
           shareApp(requireContext())
        }

        binding.mySwitch.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                if (MySharePreference.getAnimationPath(requireContext()) != ""){
                    val intent = Intent(requireContext(),ChargingAnimationService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                        requireContext().startForegroundService(intent)
                    }else{
                        requireContext().startService(intent)
                    }
                    Toast.makeText(requireContext(),"Animation Activated",Toast.LENGTH_SHORT).show()
                }else{
                    binding.mySwitch.isChecked = false
                    Toast.makeText(requireContext(),"Please Set Animation first",Toast.LENGTH_SHORT).show()
                }
            }else{
                if (isServiceRunning(requireContext(), ChargingAnimationService::class.java)){
                    val intent = Intent(requireContext(),ChargingAnimationService::class.java)
                    requireContext().stopService(intent)
                    Toast.makeText(requireContext(),"Animation Deactivated Successfully",Toast.LENGTH_SHORT).show()
                }
            }


        }

        binding.privacyPolicyButton.setOnClickListener { openLink("https://bluell.net/privacy/") }
        binding.moreAppButton.setOnClickListener {
            if (isAdded){
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "More apps click")
                bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "More apps click")
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
            }

            openLink("https://play.google.com/store/apps/dev?id=6602716762126600526")
        }
        binding.logOutButton.setOnClickListener {
            findNavController().navigate(R.id.localizationFragment)
            }

        binding.toolbar.setOnClickListener {

            findNavController().popBackStack()

        }

        binding.favorites.setOnClickListener {
            findNavController().navigate(R.id.favouriteFragment)
        }
        }


    fun isServiceRunning(context: Context, serviceClass: Class<ChargingAnimationService>): Boolean {
        val serviceManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningServices = serviceManager.getRunningServices(Integer.MAX_VALUE)

        for (serviceInfo in runningServices) {
            if (serviceInfo.service.className == serviceClass.name) {
                return true
            }
        }
        return false
    }

    fun openDeveloperPage(context: Context, developerId: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://developer?id=$developerId"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // If the Play Store app is not available, open the Play Store website
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=$developerId"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
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


    fun shareApp(context: Context) {
        val appPackageName = context.packageName
        val appName = context.getString(R.string.app_name)

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Check out $appName! Get it from the Play Store:\nhttps://play.google.com/store/apps/details?id=$appPackageName"
        )

        val chooser = Intent.createChooser(shareIntent, "Share $appName via")
        chooser.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(chooser)
    }
    private fun openLink(url:String) {
        try {
            if (InternetState.checkForInternet(requireContext())) {
                val myWebLink = Intent(Intent.ACTION_VIEW)
                myWebLink.data = Uri.parse(url)
                startActivity(myWebLink)
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.no_internet), Toast.LENGTH_SHORT
                ).show()
            }

        }catch (e: ActivityNotFoundException) {
            val developerId = "6602716762126600526"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=$developerId"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
    private fun generateSharingLink(
        deepLink: Uri,
        previewImageLink: Uri,
        getShareableLink: (String) -> Unit = {},
    ) {
        FirebaseDynamicLinks.getInstance().createDynamicLink().run {
            link = deepLink
            domainUriPrefix = Constants.PREFIX

            setSocialMetaTagParameters(
                DynamicLink.SocialMetaTagParameters.Builder().setImageUrl(previewImageLink).build())
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

    override fun onResume() {
        super.onResume()

        if (isAdded){
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Settings Screen")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, javaClass.simpleName)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }
    }

}