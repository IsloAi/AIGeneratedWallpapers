package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.menuFragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.CommonAdsListenerAdapter
import com.bumptech.glide.Glide
import com.example.hdwallpaper.Fragments.MainFragment

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyFavouriteViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.ApiCategoriesListAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.GemsTextUpdate
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.PositionCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.google.gson.Gson
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.debug.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.debug.databinding
.FragmentFavouriteBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.adaptersIG.MyCreationFavAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.interfaces.GetFavouriteImagePath
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.FavouriteListIGEntity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.RoomViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.ViewModelFactory
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.GetLoginDetails

class FavouriteFragment : Fragment() {
   private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var myViewModel: MyFavouriteViewModel
    private var cachedCatResponses: ArrayList<CatResponse>? = ArrayList()
    private lateinit var myActivity : MainActivity
    private lateinit var roomViewModel: RoomViewModel

    private var cachedIGList: ArrayList<FavouriteListIGEntity>? = ArrayList()
    private var isLoadedData = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)

        return binding.root}


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreateViewCalling()

        Glide.with(requireContext())
            .asGif()
            .load(R.raw.gems_animaion)
            .into(binding.animationDdd)
    }

    private fun onCreateViewCalling(){


        val roomDatabase = AppDatabase.getInstance(requireContext())
        roomViewModel = ViewModelProvider(this, ViewModelFactory(roomDatabase,0))[RoomViewModel::class.java]
        myActivity = activity as MainActivity
//        val auth = FirebaseAuth.getInstance()
//        val currentUser = auth.currentUser
//        if (currentUser != null) {
            viewVisible()
//        }else{
//            binding.errorMessage.visibility = VISIBLE
//            binding.aiRecyclerView.visibility = INVISIBLE
//            binding.selfCreationRecyclerView.visibility = INVISIBLE
//            binding.switchLayout.visibility = INVISIBLE
//            binding.errorLotti.setAnimation(R.raw.not_logged)
//            binding.errorText.text = "Please login to see your favorite wallpapers."
//            binding.googleLogin.setOnClickListener {
//                requireParentFragment().findNavController().navigate(R.id.action_mainFragment_to_signInFragment)
//            }
//        }
    }
    private fun viewVisible(){
        binding.errorMessage.visibility = INVISIBLE
        binding.aiRecyclerView.visibility = VISIBLE
        binding.selfCreationRecyclerView.visibility = VISIBLE
        binding.switchLayout.visibility = VISIBLE
        binding.progressBar.visibility = VISIBLE
        binding.progressBar.setAnimation(R.raw.main_loading_animation)
        binding.gemsText.text = MySharePreference.getGemsValue(requireContext()).toString()
        binding.aiRecyclerView.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        binding.selfCreationRecyclerView.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)



        loadData()
        loadDataFromRoomDB()
        if(MySharePreference.getFavouriteSaveState(requireContext())==1){
            binding.selfCreationRecyclerView.visibility = INVISIBLE
            binding.aiRecyclerView.visibility = VISIBLE
            selector(binding.aiWallpaper,binding.selfCreation)
            binding.emptySupportAI.visibility = View.GONE

            if(!isLoadedData){
                binding.progressBar.visibility = VISIBLE
            }

        }else if(MySharePreference.getFavouriteSaveState(requireContext()) ==2){
            binding.selfCreationRecyclerView.visibility = VISIBLE
            binding.aiRecyclerView.visibility =  INVISIBLE
            selector(binding.selfCreation,binding.aiWallpaper)
            binding.progressBar.visibility = INVISIBLE
            binding.emptySupport.visibility = View.GONE
        }

        binding.aiWallpaper.setOnClickListener {
            selector(binding.aiWallpaper,binding.selfCreation)
            binding.selfCreationRecyclerView.visibility = INVISIBLE
            binding.aiRecyclerView.visibility = VISIBLE
            binding.emptySupportAI.visibility = View.GONE
            if (cachedCatResponses?.isEmpty() == true){
                binding.emptySupport.visibility = View.VISIBLE
            }
           MySharePreference.setFavouriteSaveState(requireContext(),1)
            if(!isLoadedData){
                binding.progressBar.visibility = VISIBLE
            }


        }
        binding.selfCreation.setOnClickListener {
            selector(binding.selfCreation,binding.aiWallpaper)
            binding.selfCreationRecyclerView.visibility = VISIBLE
            binding.aiRecyclerView.visibility = INVISIBLE
            binding.emptySupport.visibility = View.GONE
            if (cachedIGList?.isEmpty() == true){
                binding.emptySupportAI.visibility = View.VISIBLE
            }
            MySharePreference.setFavouriteSaveState(requireContext(),2)
            binding.progressBar.visibility = INVISIBLE
        }


        binding.addToFav.setOnClickListener {
            (requireParentFragment() as MainFragment).navigateToYourDestination(2)
        }

        binding.addToFavAI.setOnClickListener {
            (requireParentFragment() as MainFragment).navigateToYourDestination(1)
        }

    }
    private fun selector(selector: TextView,unSelector:TextView){
        selector.setBackgroundResource(R.drawable.text_selector)
        unSelector.setBackgroundResource(0)
        selector.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
         unSelector.setTextColor(ContextCompat.getColor(requireContext(), R.color.black)) }
    private fun loadData() {
        myViewModel = ViewModelProvider(this)[MyFavouriteViewModel::class.java]
        // Observe the LiveData in the ViewModel and update the UI accordingly
        myViewModel.getWallpapers().observe(viewLifecycleOwner) { catResponses ->
            if (catResponses != null) {

                binding.emptySupport.visibility = View.GONE
                isLoadedData = true
                cachedCatResponses = catResponses as ArrayList
                if (view != null) {
                    updateUIWithFetchedData(catResponses)
                }
            }else{
                if (MySharePreference.getFavouriteSaveState(requireContext())==1){
                    isLoadedData = true
                    binding.emptySupport.visibility = View.VISIBLE
                }

            }
        }
        myViewModel.fetchWallpapers(requireContext(), MySharePreference.getDeviceID(requireContext())!!,binding.progressBar)
    }
    private fun updateUIWithFetchedData(catResponses: List<CatResponse>) {
        val adapter = ApiCategoriesListAdapter(catResponses as ArrayList, object :
            PositionCallback {
            override fun getPosition(position: Int) {

                SDKBaseController.getInstance().showInterstitialAds(
                    requireActivity(),
                    "mainscr_favorite_tab_click_item",
                    "mainscr_favorite_tab_click_item",
                    showLoading = true,
                    adsListener = object : CommonAdsListenerAdapter() {
                        override fun onAdsShowFail(errorCode: Int) {
                            Log.e("********ADS", "onAdsShowFail: "+errorCode )
                            //do something
                        }

                        override fun onAdsDismiss() {
                            navigateToDestination(catResponses,position)
                        }
                    }
                )
            }
        },requireParentFragment().findNavController(),R.id.action_mainFragment_to_premiumPlanFragment,object :
            GemsTextUpdate {
            override fun getGemsBack(gems: Int) {
                binding.gemsText.text = gems.toString()
            }
        },object: GetLoginDetails{
            override fun loginDetails() {
               requireParentFragment().findNavController().navigate(R.id.action_mainFragment_to_signInFragment)
            }
        },null,0,myActivity)
        binding.aiRecyclerView.adapter = adapter

    }
    private fun navigateToDestination(arrayList: ArrayList<CatResponse>, position:Int) {
        val gson = Gson()
        val arrayListJson = gson.toJson(arrayList)
        Bundle().apply {
            putString("arrayListJson",arrayListJson)
            putInt("position",position)
            requireParentFragment().findNavController().navigate(R.id.action_mainFragment_to_wallpaperViewFragment,this)
        }
        myViewModel.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun loadDataFromRoomDB() {
        roomViewModel.myFavouriteList.observe(viewLifecycleOwner){
            Log.d("tracingFavouriteList", "loadDataFromRoomDB: $it")
            if(it.isNotEmpty()){

                cachedIGList?.addAll(it)

                binding.emptySupportAI.visibility = View.GONE
                val adapter = MyCreationFavAdapter(it.reversed(),object:GetFavouriteImagePath{
                    override fun getPath(path: String) {
                        roomViewModel.deleteItem(path)
                    }
                    override fun getImageClick(position: Int, prompt: String?, imageId: Int) {
                        val gson = Gson()
                        val arrayListJson = gson.toJson(it.reversed())
                        val bundle =  Bundle().apply {
                            putString("arrayListJson",arrayListJson)
                            putInt("position",position)
                        }
                        requireParentFragment().findNavController().navigate(R.id.action_mainFragment_to_favouriteSliderViewFragment,bundle)
                    }
                })
                binding.selfCreationRecyclerView.adapter = adapter
            }else{
                if (MySharePreference.getFavouriteSaveState(requireContext()) == 2){
                    binding.emptySupportAI.visibility = View.VISIBLE
                    Toast.makeText(requireContext(),
                        getString(R.string.your_favorite_list_is_currently_empty_start_adding_items_by_tapping_the), Toast.LENGTH_SHORT).show()
                }

            }

        }
    }
}