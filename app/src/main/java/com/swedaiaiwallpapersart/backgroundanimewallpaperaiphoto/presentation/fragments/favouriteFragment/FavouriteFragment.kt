package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.fragments.favouriteFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentFavouriteBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.MySharePreference
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavouriteFragment : Fragment() {

    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    val TAG = "FAVORITES"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        replaceFragmentInFrame(FavouriteStaticFragment(), binding.FavFrame.id)

        binding.StaticWallpaper.setOnClickListener {
            selector(binding.StaticWallpaper, binding.live)
            replaceFragmentInFrame(FavouriteStaticFragment(), binding.FavFrame.id)
            MySharePreference.setFavouriteSaveState(requireContext(), 2)
        }

        binding.live.setOnClickListener {
            selector(binding.live, binding.StaticWallpaper)
            MySharePreference.setFavouriteSaveState(requireContext(), 3)
            replaceFragmentInFrame(FavouriteLiveFragment(), binding.FavFrame.id)
        }

        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
        }


    }

    private fun selector(selector: TextView, unSelector: TextView) {
        selector.setBackgroundResource(R.drawable.text_selector)
        unSelector.setBackgroundResource(0)
        selector.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        unSelector.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

    }


    override fun onResume() {
        super.onResume()
        if (isAdded) {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Favorites Screen")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, javaClass.simpleName)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }
    }

    private fun replaceFragmentInFrame(fragment: Fragment, containerId: Int) {
        childFragmentManager.beginTransaction()
            .replace(containerId, fragment)
            .addToBackStack(null) // optional: add to backstack if you want to go back
            .commit()
    }

}