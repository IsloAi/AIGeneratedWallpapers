package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.defaultSetterFragment

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentDefaultSetterBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference

class DefaultSetterFragment : Fragment() {

    lateinit var binding: FragmentDefaultSetterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDefaultSetterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.appCompatTextView.setOnClickListener {
            findNavController().navigate(R.id.permissionFragment)
        }
        binding.BtnSetDefault.setOnClickListener {
            openSetDefaultHomeScreen()
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