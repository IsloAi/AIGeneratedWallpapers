package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.favouriteFragment

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
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.FavouriteStaticAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.PositionCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyFavouriteViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class FavouriteStaticFragment : Fragment() {
    lateinit var binding: FragmentFavouriteStaticBinding

    private val favouriteViewModel: MyFavouriteViewModel by activityViewModels()
    val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var wallpapers: MutableList<CatResponse>
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
        favouriteViewModel.loadFavourites(deviceId!!)
        // Observe the data from the ViewModel
        lifecycleScope.launch(Dispatchers.IO) {
            favouriteViewModel.favourites.collect { response ->
                when (response) {
                    is Response.Processing<*> -> {
                        // Show loading state (e.g., a progress bar)
                    }

                    is Response.Success<*> -> {
                        val data = response.data as? ArrayList<String> ?: ArrayList()
                        Log.d("TAG", "loadDataFromRoomDB: Data = $data")
                        // Create a new list to hold wallpapers
                        val newWallpapers = mutableListOf<CatResponse>()
                        data.forEach { id ->
                            val wallpaper =
                                appDatabase.wallpapersDao().getFavouritesByWallpaperId(id)
                            if (wallpaper != null) {
                                newWallpapers.add(wallpaper) // Add to the new list
                                Log.d("TAG", "LD: Wallpaper in Room = $wallpaper")
                            } else {
                                Log.d("TAG", "LD: No wallpaper found for ID $id")
                            }
                        }
                        // Now update the wallpapers list
                        wallpapers.clear()
                        wallpapers.addAll(newWallpapers)
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

                    is Response.Error -> {
                        Log.d("TAG", "Fav Frag Error: ${response.message}")
                    }

                    Response.Loading -> {}

                }//End of when
            }//End of viewmodel scope
        }//End of LifeScope*/
    }

    override fun onResume() {
        super.onResume()
        // Observe the data from the ViewModel
        lifecycleScope.launch(Dispatchers.IO) {
            favouriteViewModel.favourites.collect { response ->
                when (response) {
                    is Response.Processing<*> -> {
                        // Show loading state (e.g., a progress bar)
                    }

                    is Response.Success<*> -> {
                        val data = response.data as? ArrayList<String> ?: ArrayList()
                        Log.d("TAG", "loadDataFromRoomDB: Data = $data")
                        // Create a new list to hold wallpapers
                        val newWallpapers = mutableListOf<CatResponse>()
                        data.forEach { id ->
                            val wallpaper =
                                appDatabase.wallpapersDao().getFavouritesByWallpaperId(id)
                            if (wallpaper != null) {
                                newWallpapers.add(wallpaper) // Add to the new list
                                Log.d("TAG", "LD: Wallpaper in Room = $wallpaper")
                            } else {
                                Log.d("TAG", "LD: No wallpaper found for ID $id")
                            }
                        }
                        // Now update the wallpapers list
                        wallpapers.clear()
                        wallpapers.addAll(newWallpapers)
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

                    is Response.Error -> {
                        Log.d("TAG", "Fav Frag Error: ${response.message}")
                    }

                    Response.Loading -> {}

                }//End of when
            }//End of viewmodel scope
        }//End of LifeScope*/
    }

    private fun navigateToDestination(arrayList: ArrayList<CatResponse?>, position: Int) {
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