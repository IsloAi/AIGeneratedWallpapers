package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.menuFragments

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentFavouriteBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.FavouriteLiveAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.FavouriteStaticAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.livewallpaper.DownloadLiveWallpaperFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.FavouriteListIGEntity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.RoomViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.ViewModelFactory
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.FavouriteDownloadCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.PositionCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.FavouriteLiveModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.BlurView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Constants
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
class FavouriteFragment : Fragment() {

    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!
    private val favouriteViewModel: MyFavouriteViewModel by activityViewModels()
    private lateinit var wallpapers: MutableList<CatResponse>
    private var cachedCatResponses: ArrayList<CatResponse>? = ArrayList()
    private lateinit var myActivity: MainActivity
    private lateinit var roomViewModel: RoomViewModel
    private var cachedIGList: ArrayList<FavouriteListIGEntity>? = ArrayList()
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    val sharedViewModel: SharedViewModel by activityViewModels()
    val TAG = "FAVORITES"

    @Inject
    lateinit var appDatabase: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        wallpapers = mutableListOf()
        onCreateViewCalling()
        binding.selfCreationRecyclerView.visibility = GONE
        Handler().postDelayed({
            binding.selfCreationRecyclerView.visibility = VISIBLE
        }, 4000)

        //for loading staticWallpapers
        val deviceId = MySharePreference.getDeviceID(requireContext())
        Log.d(TAG, "loadDataFromRoomDB: $deviceId ")
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
                        Log.d(TAG, "loadDataFromRoomDB: Data = $data")

                        // Create a new list to hold wallpapers
                        val newWallpapers = mutableListOf<CatResponse>()

                        data.forEach { id ->
                            val wallpaper =
                                appDatabase.wallpapersDao().getFavouritesByWallpaperId(id)
                            if (wallpaper != null) {
                                newWallpapers.add(wallpaper) // Add to the new list
                                Log.d(TAG, "LD: Wallpaper in Room = $wallpaper")
                            } else {
                                Log.d(TAG, "LD: No wallpaper found for ID $id")
                            }
                        }

                        // Now update the wallpapers list
                        wallpapers.clear() // Clear the existing list
                        wallpapers.addAll(newWallpapers) // Add all from the new list

                        Log.d(TAG, "LD: All wallpapers = $wallpapers")
                    }

                    is Response.Error -> {
                        Log.d(TAG, "Fav Frag Error: ${response.message}")
                        // Update the UI with the retrieved data
                    }

                    Response.Loading -> {


                    }

                }//End of when
            }//End of viewmodel scope
        }//End of LifeScope

        binding.StaticWallpaper.setOnClickListener {
            selector(binding.StaticWallpaper, binding.live)
            //loadStaticFavourite()
            binding.selfCreationRecyclerView.visibility = VISIBLE
            binding.liveRecyclerview.visibility = GONE
            binding.emptySupport.visibility = GONE
            MySharePreference.setFavouriteSaveState(requireContext(), 2)
            binding.progressBar.visibility = INVISIBLE

        }
        binding.live.setOnClickListener {
            selector(binding.live, binding.StaticWallpaper)
            MySharePreference.setFavouriteSaveState(requireContext(), 3)
            //initObservers()
            binding.liveRecyclerview.visibility = VISIBLE
            binding.selfCreationRecyclerView.visibility = GONE
            binding.progressBar.visibility = INVISIBLE
        }
        binding.addToFav.setOnClickListener {

            sharedViewModel.selectTab(1)
            findNavController().popBackStack(R.id.homeTabsFragment, false)


        }

        /*binding.addToFavAI.setOnClickListener {
            sharedViewModel.selectTab(5)
            findNavController().popBackStack(R.id.homeTabsFragment, false)
        }*/


    }

    private fun onCreateViewCalling() {

        val roomDatabase = AppDatabase.getInstance(requireContext())
        roomViewModel =
            ViewModelProvider(this, ViewModelFactory(roomDatabase, 0))[RoomViewModel::class.java]
        myActivity = activity as MainActivity
        //viewVisible()

        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
            Constants.checkInter = false
            Constants.checkAppOpen = false
        }
    }

    //this function gets Live fav wallpapers from the Room DB
    fun initObservers() {
        var favourites: List<FavouriteLiveModel>
        lifecycleScope.launch(Dispatchers.IO) {
            favourites = ArrayList()
            favourites = appDatabase.liveWallpaperDao().getAllFavouriteWallpapers()

            withContext(Dispatchers.Main) {
                Log.d("LIVEADAPTER", "LIVE: favorites: $favourites")
                updateUIWithFetchedDataLive(favourites)
            }
        }
    }

    //this function gets static fav wallpapers from the Room DB
    private fun loadStaticFavourite() {
        binding.emptySupport.visibility = GONE
        Handler().postDelayed({
            updateUIWithFetchedData(wallpapers)
        }, 500)

    }//End of function

    private fun updateUIWithFetchedDataLive(list: List<FavouriteLiveModel>) {
        if (list.isEmpty()) {
            binding.emptySupport.visibility = VISIBLE
            binding.noFavImg.visibility = VISIBLE
        } else {
            binding.emptySupport.visibility = GONE
            binding.noFavImg.visibility = GONE

            val adapter =
                FavouriteLiveAdapter(list, myActivity, object : FavouriteDownloadCallback {
                    override fun getPosition(position: Int, model: FavouriteLiveModel) {
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

    private fun selector(selector: TextView, unSelector: TextView) {
        selector.setBackgroundResource(R.drawable.text_selector)
        unSelector.setBackgroundResource(0)
        selector.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        unSelector.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

    }

    private fun updateUIWithFetchedData(catResponses: List<CatResponse>) {
        //val nonNullCatResponses = catResponses.filterNotNull()
        val adapter = FavouriteStaticAdapter(catResponses,
            myActivity,
            "favorites",
            object : PositionCallback {
                override fun getPosition(position: Int) {
                    if (isAdded) {
                        navigateToDestination(ArrayList(catResponses), position)
                    }
                }

                override fun getFavorites(position: Int) {

                }
            })

        binding.selfCreationRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.selfCreationRecyclerView.adapter = adapter
        binding.selfCreationRecyclerView.addItemDecoration(RvItemDecore(2, 20, false, 10000))
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



    override fun onResume() {
        super.onResume()
        if (isAdded) {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Favorites Screen")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, javaClass.simpleName)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }
        loadStaticFavourite()
        initObservers()
    }

}