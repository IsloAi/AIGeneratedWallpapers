package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.defaultSetterFragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentDefaultSetterBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.LauncherActivity

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

        binding.BtnSetDefault.setOnClickListener {
            startActivity(Intent(requireContext(), LauncherActivity::class.java))
        }

    }
}