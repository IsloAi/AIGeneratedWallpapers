package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.launcherHomeFragment

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.WallpaperManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.DialogAppsDrawerBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentLauncherHomeBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.HomeTabsFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.appsDrawerFragment.AppAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.appsDrawerFragment.AppInfo
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.launcherHomeFragment.homeScreen.HomeScreen
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class LauncherHomeFragment : Fragment() {

    lateinit var binding: FragmentLauncherHomeBinding
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var adapter: AppAdapter
    private lateinit var appList: List<AppInfo>
    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
    private lateinit var gestureDetector: GestureDetectorCompat

    @Inject
    lateinit var appDatabase: AppDatabase

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

        appList = ArrayList()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            adapter = AppAdapter(requireContext(), appList)
            binding.menuIcon.setOnClickListener {
                val bottomSheetDialog =
                    BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialog)
                val dialogBinding = DialogAppsDrawerBinding.inflate(layoutInflater)
                bottomSheetDialog.setContentView(dialogBinding.root)
                val recyclerview = dialogBinding.Apps
                if (appList.isEmpty()) {
                    dialogBinding.loading.visibility = View.VISIBLE
                    dialogBinding.Apps.visibility = View.VISIBLE
                } else {
                    dialogBinding.loading.visibility = View.GONE
                    dialogBinding.Apps.visibility = View.VISIBLE
                }
                recyclerview.adapter = adapter
                recyclerview.layoutManager = GridLayoutManager(requireContext(), 4)
                lifecycleScope.launch(Dispatchers.IO) {

                    appList = appDatabase.AppsDAO().getAllApps()
                    withContext(Dispatchers.Main) {
                        adapter.updateList(appList)
                        dialogBinding.loading.visibility = View.GONE
                    }
                }
                dialogBinding.cancel.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
            }
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
                if (position == 1) {
                    binding.menuIcon.visibility = View.VISIBLE
                    binding.main.background = null
                    setWallpaper()
                }
                if (position == 0){
                    binding.menuIcon.visibility = View.GONE
                    binding.main.setBackgroundColor(resources.getColor(R.color.new_main_background))
                }
            }
        })

        checkAndRequestPermission()

        sharedViewModel.currentPage.observe(viewLifecycleOwner) { page ->
            binding.homePager.setCurrentItem(page, true)
            binding.menuIcon.visibility = View.GONE
        }
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
                checkNotificationPermission()

            }
        } else {
            // For Android versions below R, just run setWallpaper()
            setWallpaper()
            checkNotificationPermission()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onResume() {
        super.onResume()
        // Check permission again when the app resumes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()) {
            setWallpaper()
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.POST_NOTIFICATIONS
                )
                != PackageManager.PERMISSION_GRANTED
            ) {

                // Request permission
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            } else {
                // Permission already granted, you can proceed with showing notifications
                checkAlramPermission()
            }
        } else {
            // Below Android 13, no permission is required
            checkAlramPermission()
        }
    }

    private fun checkAlramPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
            val alarmManager = requireContext().getSystemService(AlarmManager::class.java)

            if (!alarmManager.canScheduleExactAlarms()) {
                // Open settings to allow exact alarms
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:${requireContext().packageName}")
                }
                startActivity(intent)
            } else {
                // Permission already granted, proceed with scheduling alarms
            }
        } else {
            // Below Android 12, no permission needed
        }
    }

    private fun setWallpaper() {
        val wallpaperManager = WallpaperManager.getInstance(requireContext())
        val wallpaperDrawable = wallpaperManager.drawable
        // Set the wallpaper as the background for the root layout of the fragment
        binding.main.background = wallpaperDrawable
    }

}