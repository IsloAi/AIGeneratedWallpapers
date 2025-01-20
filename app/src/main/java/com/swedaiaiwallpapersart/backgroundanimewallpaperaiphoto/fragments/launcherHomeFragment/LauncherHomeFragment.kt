package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.launcherHomeFragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentLauncherHomeBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.HomeTabsFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.getStartedFragment.pagerAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.launcherHomeFragment.homeScreen.HomeScreen
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.menuFragments.HomeFragment

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
        binding.homePager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            @SuppressLint("SetTextI18n")
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 0) {

                }
            }
        })

    }
}