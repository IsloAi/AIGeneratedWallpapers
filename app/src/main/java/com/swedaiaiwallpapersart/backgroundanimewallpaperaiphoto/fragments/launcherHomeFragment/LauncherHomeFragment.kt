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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.DialogAppsDrawerBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentLauncherHomeBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.HomeTabsFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.appsDrawerFragment.AppAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.appsDrawerFragment.AppInfo
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.launcherHomeFragment.homeScreen.HomeScreen
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class LauncherHomeFragment : Fragment() {

    lateinit var binding: FragmentLauncherHomeBinding
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var adapter: AppAdapter
    private lateinit var appList: List<AppInfo>

    @Inject
    private lateinit var appDatabase: AppDatabase

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
            val bottomSheetDialog = BottomSheetDialog(requireContext())
            val dialogBinding = DialogAppsDrawerBinding.inflate(layoutInflater)
            bottomSheetDialog.setContentView(dialogBinding.root)
            val recyclerview = dialogBinding.Apps
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                adapter = AppAdapter(requireContext(), appList)
                recyclerview.adapter = adapter
                recyclerview.layoutManager = GridLayoutManager(requireContext(), 4)
                if (appList.isNotEmpty()) {
                    adapter.updateList(appList)
                }
            }

            dialogBinding.cancel.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.show()
            //findNavController().navigate(R.id.appsDrawerFragment)
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

        // Observe the currentPage LiveData and update the ViewPager2
        sharedViewModel.currentPage.observe(viewLifecycleOwner) { page ->
            binding.homePager.setCurrentItem(page, true)
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private suspend fun getAllApps(): List<AppInfo> {
        val pManager = context?.packageManager
        appList = ArrayList()

        val i = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val allApps = pManager?.queryIntentActivities(i, 0)
        if (allApps != null) {
            for (ri in allApps) {
                val app = AppInfo(
                    ri.loadLabel(pManager).toString(),
                    ri.activityInfo.packageName
                )
                Log.i("Log", app.packageName)
                (appList as ArrayList<AppInfo>).add(app)
                appDatabase.AppsDAO().insertApp(app)
            }
        }
        return appList
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            getAllApps()
        }
        // Check permission again when the app resumes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()) {
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