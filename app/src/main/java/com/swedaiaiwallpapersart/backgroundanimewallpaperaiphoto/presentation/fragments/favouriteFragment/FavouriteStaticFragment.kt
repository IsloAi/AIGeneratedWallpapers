package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.fragments.favouriteFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentFavouriteStaticBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.FavouriteStaticAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.SingleDatabaseResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.activity.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.PositionCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.viewmodels.DataFromRoomViewmodel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class FavouriteStaticFragment : Fragment() {
    lateinit var binding: FragmentFavouriteStaticBinding

    private val favouriteViewModel: DataFromRoomViewmodel by activityViewModels()
    val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var wallpapers: MutableList<SingleDatabaseResponse>
    private lateinit var adapter: FavouriteStaticAdapter

    @Inject
    lateinit var appDatabase: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavouriteStaticBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //for loading staticWallpapers
        val deviceId = MySharePreference.getDeviceID(requireContext())
        Log.d("StaticFav", "loadDataFromRoomDB: $deviceId ")

        wallpapers = mutableListOf()
        adapter = FavouriteStaticAdapter(wallpapers,
            myActivity = activity as MainActivity,
            "favorites",
            object : PositionCallback {
                override fun getPosition(position: Int) {
                    if (isAdded) {
                        navigateToDestination(ArrayList(wallpapers), position)
                    }
                }

                override fun getFavorites(position: Int) {}
            })

        binding.selfCreationRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.selfCreationRecyclerView.adapter = adapter
        binding.selfCreationRecyclerView.addItemDecoration(RvItemDecore(2, 20, false, 10000))

        // Trigger data load in the ViewModel
        favouriteViewModel.getStaticFavourites()
        // Observe the data from the ViewModel
        lifecycleScope.launch(Dispatchers.IO) {
            favouriteViewModel.staticFavouriteWallpapers.collect { result ->
                // Now update the wallpapers list
                wallpapers.addAll(result)
                Log.d("TAG", "LD: All wallpapers = ${wallpapers.toList()}")
                withContext(Dispatchers.Main) {
                    if (wallpapers.isNotEmpty()) {
                        binding.lottieAnimationStatic.visibility = View.GONE
                        binding.emptySupportStatic.visibility = View.GONE
                    } else {
                        binding.lottieAnimationStatic.visibility = View.GONE
                        binding.emptySupportStatic.visibility = View.VISIBLE
                    }
                    adapter.updateData(wallpapers.toList())
                }
            }
        }//End of LifeScope
    }

    override fun onResume() {
        super.onResume()

    }

    private fun navigateToDestination(
        arrayList: ArrayList<SingleDatabaseResponse?>,
        position: Int
    ) {
        val gson = Gson()
        gson.toJson(arrayList.filterNotNull())

        val countOfNulls = arrayList.subList(0, position).count { it == null }

        sharedViewModel.clearData()
        sharedViewModel.setData(arrayList.filterNotNull(), position - countOfNulls)
        Bundle().apply {
            putInt("position", position - countOfNulls)
            putString("from", "trending")
            putString("wall", "home")
            putBoolean("Fav", true)
            findNavController().navigate(R.id.wallpaperViewFragment, this)
        }
    }

}