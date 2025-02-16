package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.TextPaint
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentPermissionBinding

class PermissionFragment : Fragment() {

    private lateinit var binding: FragmentPermissionBinding
    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
    private val REQUEST_CODE_OVERLAY_PERMISSION = 1001
    private var isNotificationEnabled = false
    private var isOverlayEnabled = false
    private var isAllFileEnabled = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPermissionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setGradientText()

        setEvents()
        updateGoButtonState()
    }

    private fun setEvents() {
        binding.enableNotification.setOnClickListener {
            checkNotificationPermission()
        }
        binding.enableOverlay.setOnClickListener {
            checkOverlayPermission()
        }
        binding.enableAllFile.setOnClickListener {
            checkALLFilePermission()
        }
    }

    private fun updateGoButtonState() {
        val allPermissionsGranted = isNotificationEnabled && isOverlayEnabled && isAllFileEnabled
        binding.BtnGo.alpha = if (allPermissionsGranted) 1f else 0.5f
        binding.BtnGo.setOnClickListener {
            if (allPermissionsGranted) {
                findNavController().navigate(R.id.launcherHomeFragment)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please allow all permissions access",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Checking overlay permission
    private fun checkOverlayPermission() {
        if (!Settings.canDrawOverlays(requireContext())) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${requireContext().packageName}")
            )
            startActivityForResult(intent, REQUEST_CODE_OVERLAY_PERMISSION)
        } else {
            Toast.makeText(requireContext(), "Overlay Permission Granted", Toast.LENGTH_SHORT)
                .show()
            isOverlayEnabled = true
            updateGoButtonState()
        }
    }

    // Checking notification permission
    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            } else {
                isNotificationEnabled = true
                updateGoButtonState()
            }
        } else {
            Toast.makeText(requireContext(), "Notification Permission Granted", Toast.LENGTH_SHORT)
                .show()
            isNotificationEnabled = true
            updateGoButtonState()
        }
    }

    // Checking all file permission
    private fun checkALLFilePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    val intent =
                        Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                            data = Uri.parse("package:${requireContext().packageName}")
                        }
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                    Toast.makeText(context, "Permission settings not available", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                isAllFileEnabled = true
                updateGoButtonState()
            }
        } else {
            Toast.makeText(requireContext(), "All Files Permission Granted", Toast.LENGTH_SHORT)
                .show()
            isAllFileEnabled = true
            updateGoButtonState()
        }
    }

    //giving heading the gradient
    private fun setGradientText() {
        val customColors = intArrayOf(
            Color.parseColor("#FC9502"),
            Color.parseColor("#FF6726")
        )
        val paint: TextPaint = binding.toolBarHeading.paint
        val width: Float = paint.measureText("Permissions")

        val shader = LinearGradient(
            0f, 0f, width, binding.toolBarHeading.textSize,
            customColors, null, Shader.TileMode.CLAMP
        )
        binding.toolBarHeading.paint.shader = shader
    }

    /*   //checking overlay permission
       private fun checkOverlayPermission() {
           if (!Settings.canDrawOverlays(requireContext())) {
               val intent = Intent(
                   Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                   Uri.parse("package:${requireContext().packageName}")
               )
               startActivityForResult(intent, REQUEST_CODE_OVERLAY_PERMISSION)
           } else {
               // Permission already granted, proceed with overlay functionality
           }
       }

       //checking notification permission
       private fun checkNotificationPermission() {
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
               if (ContextCompat.checkSelfPermission(
                       requireContext(), android.Manifest.permission.POST_NOTIFICATIONS
                   ) != PackageManager.PERMISSION_GRANTED
               ) {
                   // Request permission
                   ActivityCompat.requestPermissions(
                       requireActivity(),
                       arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                       NOTIFICATION_PERMISSION_REQUEST_CODE
                   )
               } else {
                   // Permission already granted, you can proceed with showing notifications
                   Toast.makeText(
                       requireContext(),
                       "Notification Permission Granted",
                       Toast.LENGTH_SHORT
                   ).show()
               }
           } else {
               // Below Android 13, no permission is required
           }
       }

       //checking all file permission
       private fun checkALLFilePermission() {
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
                   // Permission is already granted
               }
           } else {
               // For Android versions below R, No need
           }
       }*/

}