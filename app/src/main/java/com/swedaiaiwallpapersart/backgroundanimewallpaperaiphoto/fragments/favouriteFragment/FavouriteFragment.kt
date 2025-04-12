package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.favouriteFragment

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
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
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

    fun replaceFragmentInFrame(fragment: Fragment, containerId: Int) {
        childFragmentManager.beginTransaction()
            .replace(containerId, fragment)
            .addToBackStack(null) // optional: add to backstack if you want to go back
            .commit()
    }

    /*fun replaceFragment(fragment: Fragment, activity: FragmentActivity) {
        val fragmentManager = activity.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.FavFrame, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    } //End of replaceFRAG*/

    /*private fun onCreateViewCalling() {

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

        lifecycleScope.launch(Dispatchers.IO) {
            favouritesLive = ArrayList()
            favouritesLive = appDatabase.liveWallpaperDao().getAllFavouriteWallpapers()

            withContext(Dispatchers.Main) {
                Log.d("LIVEADAPTER", "LIVE: favorites: $favouritesLive")
                updateUIWithFetchedDataLive(favouritesLive)
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

    private fun updateUIWithFetchedData(catResponses: List<CatResponse>) {
        if (catResponses.isNullOrEmpty()) {
            binding.emptySupport.visibility = VISIBLE
            binding.noFavImg.visibility = VISIBLE
        } else {
            binding.emptySupport.visibility = GONE
            binding.noFavImg.visibility = GONE
        }
        val adapter = FavouriteStaticAdapter(catResponses,
            myActivity,
            "favorites",
            object : PositionCallback {
                override fun getPosition(position: Int) {
                    if (isAdded) {
                        navigateToDestination(ArrayList(catResponses), position)
                    }
                }

                override fun getFavorites(position: Int) {}
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
    }*/

}