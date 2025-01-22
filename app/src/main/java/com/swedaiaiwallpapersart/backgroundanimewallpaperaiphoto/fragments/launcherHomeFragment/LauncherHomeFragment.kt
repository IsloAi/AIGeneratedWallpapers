package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.launcherHomeFragment

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentLauncherHomeBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.HomeTabsFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.launcherHomeFragment.homeScreen.HomeScreen

class LauncherHomeFragment : Fragment() {

    lateinit var binding: FragmentLauncherHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLauncherHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.menuIcon.setOnClickListener {
            findNavController().navigate(R.id.appsDrawerFragment)
        }
        val fragments = listOf(
            HomeTabsFragment(),
            HomeScreen(),

            )
        binding.homePager.adapter = HomePagerAdapter(requireActivity(), fragments)
        binding.homePager.setCurrentItem(1, false)
        binding.homePager.offscreenPageLimit = 1
        binding.homePager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            @SuppressLint("SetTextI18n")
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 0) {

                }
            }
        })

        checkAndRequestPermission()

    }

    private fun checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                // Permission is not granted; request it
                try {
                    val intent =
                        Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                            data = Uri.parse("package:${requireContext().packageName}")
                        }
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    // Fallback if the intent is not supported
                    e.printStackTrace()
                    Toast.makeText(context, "Permission settings not available", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                // Permission is already granted; run setWallpaper()
                setWallpaper()
            }
        } else {
            // For Android versions below R, just run setWallpaper()
            setWallpaper()
        }
    }

    private fun setWallpaper() {
        val wallpaperManager = WallpaperManager.getInstance(requireContext())
        val wallpaperDrawable = wallpaperManager.drawable
        // Set the wallpaper as the background for the root layout of the fragment
        binding.main.background = wallpaperDrawable
    }
}