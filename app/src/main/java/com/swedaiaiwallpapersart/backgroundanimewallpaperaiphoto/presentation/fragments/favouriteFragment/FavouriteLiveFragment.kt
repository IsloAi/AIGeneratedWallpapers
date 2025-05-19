package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.fragments.favouriteFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentFavouriteLiveBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.adapters.FavouriteLiveAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.LiveWallpaperModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.activity.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.fragments.liveWallpaper.DownloadLiveWallpaperFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.BlurView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.FavouriteDownloadCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.viewmodels.DataFromRoomViewmodel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class FavouriteLiveFragment : Fragment() {

    lateinit var binding: FragmentFavouriteLiveBinding
    private lateinit var favouritesLive: List<LiveWallpaperModel>
    val sharedViewModel: SharedViewModel by activityViewModels()
    private val viewmodel: DataFromRoomViewmodel by viewModels()

    @Inject
    lateinit var appDatabase: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavouriteLiveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addToFav.setOnClickListener {
            findNavController().popBackStack(R.id.homeTabsFragment, false)
        }
        initObservers()
    }

    private fun initObservers() {
        lifecycleScope.launch(Dispatchers.IO) {
            viewmodel.getLiveFavourites()
            viewmodel.liveFavouriteWallpapers.collect { result ->
                favouritesLive = result
                withContext(Dispatchers.Main) {
                    Log.d("LIVE_ADAPTER", "LIVE: favourites: $favouritesLive")
                    updateUIWithFetchedDataLive(favouritesLive)
                }
            }
        }
    }

    private fun updateUIWithFetchedDataLive(list: List<LiveWallpaperModel>) {
        Log.d("LIVEADAPTER", "updateUIWithFetchedDataLive:list Size: ${list.size} ")
        if (list.isEmpty()) {
            binding.emptySupportLive.visibility = VISIBLE
            binding.noFavImg.visibility = VISIBLE
            binding.lottieAnimation.visibility = GONE
        } else {
            binding.emptySupportLive.visibility = GONE
            binding.noFavImg.visibility = GONE
            binding.lottieAnimation.visibility = GONE

            val adapter =
                FavouriteLiveAdapter(
                    list,
                    activity as MainActivity,
                    object : FavouriteDownloadCallback {
                        override fun getPosition(position: Int, model: LiveWallpaperModel) {
                            BlurView.filePath = ""
                            sharedViewModel.clearLiveWallpaper()
                            sharedViewModel.setFavLiveWallpaper(listOf(model))
                            DownloadLiveWallpaperFragment.shouldObserveFavorites = true
                            DownloadLiveWallpaperFragment.shouldObserveLiveWallpapers = false
                            MySharePreference.setLiveComingFrom(requireContext(), "Favourite")
                            // Create the action using Safe Args
                            findNavController().navigate(R.id.downloadLiveWallpaperFragment)
                        }
                    })
            binding.liveRecyclerview.layoutManager = GridLayoutManager(requireContext(), 2)
            binding.liveRecyclerview.adapter = adapter
            binding.liveRecyclerview.addItemDecoration(RvItemDecore(2, 20, false, 10000))
        }
    }

}