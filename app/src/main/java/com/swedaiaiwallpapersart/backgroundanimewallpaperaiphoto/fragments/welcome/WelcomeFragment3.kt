package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.welcome

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentWelcome3Binding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.ViewPagerCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WelcomeFragment3 : Fragment() {

    private var _binding: FragmentWelcome3Binding? = null
    private val binding get() = _binding!!
    private var scrollJob: Job? = null
    var viewPagerCallback: ViewPagerCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ViewPagerCallback) {
            viewPagerCallback = context
        } else {
            Log.e("WelcomeFragment3", "Parent fragment is not implementing ViewPagerCallback")
        }
    }

    override fun onDetach() {
        super.onDetach()
        viewPagerCallback = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcome3Binding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onPause() {
        super.onPause()
        scrollJob?.cancel()
    }

    override fun onStop() {
        super.onStop()
        scrollJob?.cancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scrollJob?.cancel()
    }

    override fun onResume() {
        super.onResume()
        if (isAdded) {
            startAutoScroll()
        }
    }

    private fun startAutoScroll() {
        scrollJob?.cancel()
        if (AdConfig.autoNext) {
            scrollJob = lifecycleScope.launch {
                delay(AdConfig.timeNext)
                viewPagerCallback?.swipe() ?: Log.e("WelcomeFragment3", "viewPagerCallback is null")
            }
        }
    }
}