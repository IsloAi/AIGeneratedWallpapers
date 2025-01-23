package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.getStartedFragment

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentGetStartedBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.InternetState
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference


class GetStartedFragment : Fragment() {

    lateinit var binding: FragmentGetStartedBinding
    var firsTime: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGetStartedBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firsTime = MySharePreference.getFirstTime(requireContext())

        binding.viewPager2.adapter = pagerAdapter(requireContext())
        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            @SuppressLint("SetTextI18n")
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 0) {
                    setCurrentIndicator(0)
                    binding.topText.setImageResource(R.drawable.txt1)
                } else {
                    setCurrentIndicator(1)
                    binding.topText.setImageResource(R.drawable.txt2)
                }
            }
        })
        binding.privacyBTN.setOnClickListener {
            openLink("https://isloai.com/privacy/")
        }
        binding.termsOfServiceBTN.setOnClickListener {
            openLink("https://isloai.com/terms/")
        }
        binding.BtnGetStarted.setOnClickListener {
            openSetDefaultHomeScreen()
        }

        setIndicator()
        setCurrentIndicator(0)
    }

    override fun onResume() {
        super.onResume()
        firsTime = MySharePreference.getFirstTime(requireContext())

        if (!checkAppDefaultHome()) { // App is not set as default home
            if (firsTime) {
                Log.d("Launcher", "onResume: Navigating to DefaultSetterFragment (First Time)")
                findNavController().navigate(R.id.defaultSetterFragment)
            } else {
                Log.d("Launcher", "onResume: Staying on GetStartedFragment")
                // Stay on GetStartedFragment
            }
        } else {
            // Handle the case where the app is already the default home
            Log.d("Launcher", "onResume: App is Default Home, navigating if required")
        }
    }

    private fun checkAppDefaultHome(): Boolean {
        val packageManager = requireContext().packageManager
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
        }
        val resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return resolveInfo?.activityInfo?.packageName == requireContext().packageName
    }

    private fun setCurrentIndicator(index: Int) {

        val childCount = binding.layoutGetStartedIndicators.childCount
        val isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                Configuration.UI_MODE_NIGHT_YES

        for (i in 0 until childCount) {
            val imageView = binding.layoutGetStartedIndicators.getChildAt(i) as ImageView

            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.onboarding_inactive
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.onboarding_indicator
                    )
                )
            }

        }
    }

    private fun setIndicator() {

        val welcomeIndicators = arrayOfNulls<ImageView>(2)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in welcomeIndicators.indices) {
            welcomeIndicators[i] = ImageView(requireContext())
            welcomeIndicators[i]!!.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.onboarding_indicator
                )
            )
            welcomeIndicators[i]!!.layoutParams = layoutParams
            binding.layoutGetStartedIndicators.addView(welcomeIndicators[i])
        }
    }

    private fun openLink(url: String) {
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

        } catch (e: ActivityNotFoundException) {
            val developerId = "6602716762126600526"
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/developer?id=$developerId")
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun openSetDefaultHomeScreen() {
        try {
            val intent = Intent(Settings.ACTION_HOME_SETTINGS)
            startActivity(intent)
            // Set 'firstTime' to true when the user interacts with this screen
            MySharePreference.setFirstTime(requireContext(), true)
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback: Open general settings if the specific screen can't be opened
            val fallbackIntent = Intent(Settings.ACTION_SETTINGS)
            startActivity(fallbackIntent)
        }
    }
}