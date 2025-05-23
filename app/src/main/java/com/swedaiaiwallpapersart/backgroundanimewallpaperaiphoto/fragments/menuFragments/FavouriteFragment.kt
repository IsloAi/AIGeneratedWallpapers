package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.menuFragments

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.CommonAdsListenerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.hdwallpaper.Fragments.MainFragment

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyFavouriteViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.ApiCategoriesListAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.GemsTextUpdate
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.PositionCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.google.gson.Gson
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding
.FragmentFavouriteBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.LiveWallpaperAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.HomeTabsFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.adaptersIG.MyCreationFavAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.interfaces.GetFavouriteImagePath
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.FavouriteListIGEntity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.RoomViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.ViewModelFactory
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.GetLoginDetails
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.downloadCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.LiveWallpaperModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.BlurView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.LiveWallpaperViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlin.random.Random

class FavouriteFragment : Fragment() {
   private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var myViewModel: MyFavouriteViewModel
    private var cachedCatResponses: ArrayList<CatResponse>? = ArrayList()
    private lateinit var myActivity : MainActivity
    private lateinit var roomViewModel: RoomViewModel

    private var cachedIGList: ArrayList<FavouriteListIGEntity>? = ArrayList()
    private var isLoadedData = false

    private val liveWallpaperViewModel: LiveWallpaperViewModel by activityViewModels()



    val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)

        return binding.root}


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreateViewCalling()

    }

    private fun onCreateViewCalling(){


        val roomDatabase = AppDatabase.getInstance(requireContext())
        roomViewModel = ViewModelProvider(this, ViewModelFactory(roomDatabase,0))[RoomViewModel::class.java]
        myActivity = activity as MainActivity
        viewVisible()

        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    private fun viewVisible(){
        binding.errorMessage.visibility = INVISIBLE
        binding.aiRecyclerView.visibility = VISIBLE
        binding.selfCreationRecyclerView.visibility = VISIBLE
        binding.switchLayout.visibility = VISIBLE
        binding.progressBar.visibility = VISIBLE
        binding.progressBar.setAnimation(R.raw.main_loading_animation)
        binding.aiRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.aiRecyclerView.addItemDecoration(RvItemDecore(2,20,false,10000))
        binding.selfCreationRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.selfCreationRecyclerView.addItemDecoration(RvItemDecore(2,20,false,10000))
        binding.liveRecyclerview.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.liveRecyclerview.addItemDecoration(RvItemDecore(3,5,false,10000))


        loadData()
        loadDataFromRoomDB()
        if(MySharePreference.getFavouriteSaveState(requireContext())==1){
            binding.selfCreationRecyclerView.visibility = INVISIBLE
            binding.aiRecyclerView.visibility = VISIBLE
            selector(binding.aiWallpaper,binding.selfCreation,binding.live)
            binding.emptySupportAI.visibility = View.GONE
            binding.liveRecyclerview.visibility = View.GONE


            if(!isLoadedData){
                binding.progressBar.visibility = VISIBLE
            }

        }else if(MySharePreference.getFavouriteSaveState(requireContext()) ==2){
            binding.selfCreationRecyclerView.visibility = VISIBLE
            binding.aiRecyclerView.visibility =  INVISIBLE
            selector(binding.selfCreation,binding.aiWallpaper,binding.live)
            binding.progressBar.visibility = INVISIBLE
            binding.emptySupport.visibility = View.GONE
            binding.liveRecyclerview.visibility = View.GONE
        }else if(MySharePreference.getFavouriteSaveState(requireContext()) ==3){
            initObservers()
            binding.selfCreationRecyclerView.visibility = INVISIBLE
            binding.aiRecyclerView.visibility =  INVISIBLE
            binding.liveRecyclerview.visibility = View.VISIBLE
            selector(binding.live,binding.selfCreation,binding.aiWallpaper)
            binding.progressBar.visibility = INVISIBLE
            binding.emptySupport.visibility = View.GONE
            binding.emptySupportAI.visibility = View.GONE
        }

        binding.aiWallpaper.setOnClickListener {
            loadData()
            selector(binding.aiWallpaper,binding.selfCreation,binding.live)
            binding.selfCreationRecyclerView.visibility = INVISIBLE
            binding.aiRecyclerView.visibility = VISIBLE
            binding.emptySupportAI.visibility = View.GONE
            binding.liveRecyclerview.visibility = INVISIBLE
            if (cachedCatResponses?.isEmpty() == true){
                binding.emptySupport.visibility = View.VISIBLE
            }
           MySharePreference.setFavouriteSaveState(requireContext(),1)
            if(!isLoadedData){
                binding.progressBar.visibility = VISIBLE
            }


        }
        binding.selfCreation.setOnClickListener {
            loadDataFromRoomDB()
            selector(binding.selfCreation,binding.aiWallpaper,binding.live)
            binding.selfCreationRecyclerView.visibility = VISIBLE
            binding.aiRecyclerView.visibility = INVISIBLE
            binding.liveRecyclerview.visibility = INVISIBLE
            binding.emptySupport.visibility = View.GONE
            if (cachedIGList?.isEmpty() == true){
                binding.emptySupportAI.visibility = View.VISIBLE
            }
            MySharePreference.setFavouriteSaveState(requireContext(),2)
            binding.progressBar.visibility = INVISIBLE
        }

        binding.live.setOnClickListener {
            selector(binding.live,binding.selfCreation,binding.aiWallpaper)
            MySharePreference.setFavouriteSaveState(requireContext(),3)
            initObservers()
            binding.liveRecyclerview.visibility = VISIBLE
            binding.aiRecyclerView.visibility = INVISIBLE
            binding.selfCreationRecyclerView.visibility = INVISIBLE
            binding.emptySupport.visibility = View.GONE
            binding.emptySupportAI.visibility = View.GONE
            binding.progressBar.visibility = INVISIBLE
        }


        binding.addToFav.setOnClickListener {

            sharedViewModel.selectTab(1)
            findNavController().popBackStack(R.id.homeTabsFragment,false)


        }

        binding.addToFavAI.setOnClickListener {
            sharedViewModel.selectTab(5)
            findNavController().popBackStack(R.id.homeTabsFragment,false)
        }

    }
    private fun selector(selector: TextView,unSelector:TextView, unselector1:TextView){
        selector.setBackgroundResource(R.drawable.text_selector)
        unSelector.setBackgroundResource(0)
        unselector1.setBackgroundResource(0)
        selector.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
         unSelector.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        unselector1.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

    }
    private fun loadData() {
        myViewModel = ViewModelProvider(this)[MyFavouriteViewModel::class.java]
        // Observe the LiveData in the ViewModel and update the UI accordingly
        myViewModel.getWallpapers().observe(viewLifecycleOwner) { catResponses ->
            if (catResponses != null) {
                if (MySharePreference.getFavouriteSaveState(requireContext())==1){
                    binding.emptySupport.visibility = View.GONE
                    binding.aiRecyclerView.visibility = View.VISIBLE
//                    val randomNumber = if (catResponses.size > 1) {
//                        Random.nextInt(0, catResponses.size - 1)
//                    } else {
//                        0
//                    }

//                    getBitmapFromGlide(catResponses[randomNumber].compressed_image_url!!)
                }
                isLoadedData = true
                cachedCatResponses = catResponses as ArrayList
                if (view != null) {
                    updateUIWithFetchedData(catResponses)


                }
            }else{
                if (MySharePreference.getFavouriteSaveState(requireContext())==1){
                    isLoadedData = true
                    binding.emptySupport.visibility = View.VISIBLE
                    binding.aiRecyclerView.visibility = View.GONE
                }

            }
        }
        myViewModel.fetchWallpapers(requireContext(), MySharePreference.getDeviceID(requireContext())!!,binding.progressBar)
    }

    private fun addNullValueInsideArray(data: List<CatResponse?>): ArrayList<CatResponse?>{

        val firstAdLineThreshold = if (AdConfig.firstAdLineViewListWallSRC != 0) AdConfig.firstAdLineViewListWallSRC else 4
        val firstLine = firstAdLineThreshold * 2

        val lineCount = if (AdConfig.lineCountViewListWallSRC != 0) AdConfig.lineCountViewListWallSRC else 5
        val lineC = lineCount * 2
        val newData = arrayListOf<CatResponse?>()

        for (i in data.indices){
            if (i > firstLine && (i - firstLine) % (lineC + 1)  == 0) {
                newData.add(null)



                Log.e("******NULL", "addNullValueInsideArray: null "+i )

            }else if (i == firstLine){
                newData.add(null)
                Log.e("******NULL", "addNullValueInsideArray: null first "+i )
            }
            Log.e("******NULL", "addNullValueInsideArray: not null "+i )
            newData.add(data[i])

        }
        Log.e("******NULL", "addNullValueInsideArray:size "+newData.size )




        return newData
    }

    fun initObservers(){
        liveWallpaperViewModel.wallpaperData.observe(viewLifecycleOwner) { catResponses ->
            if (catResponses != null) {
                Log.e("TAG", "loadData: "+catResponses )
                if (view != null) {
                    // If the view is available, update the UI

                    val filtered = catResponses.filter { it.liked }
//
                    val list = addNullValueInsideArrayLive(filtered)
                    updateUIWithFetchedDataLive(list)
                }
            }else{

            }
        }
    }

    private fun updateUIWithFetchedDataLive(catResponses: ArrayList<LiveWallpaperModel?>) {


        val adapter = LiveWallpaperAdapter(catResponses, object :
            downloadCallback {
            override fun getPosition(position: Int, model: LiveWallpaperModel) {
                BlurView.filePath = ""
                sharedViewModel.clearLiveWallpaper()
                sharedViewModel.setLiveWallpaper(listOf(model))
                findNavController().navigate(R.id.downloadLiveWallpaperFragment)
            }
        },myActivity)
        binding.liveRecyclerview.adapter = adapter
    }
    private val fragmentScope: CoroutineScope by lazy { MainScope() }
    private fun updateUIWithFetchedData(catResponses: List<CatResponse?>) {
        val list = addNullValueInsideArray(catResponses)
        val adapter = ApiCategoriesListAdapter(list as ArrayList, object :
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
                            navigateToDestination(list,position)
                            //do something
                        }

                        override fun onAdsDismiss() {
                            navigateToDestination(list,position)
                        }
                    }
                )
            }

            override fun getFavorites(position: Int) {
                loadData()
            }
        },myActivity,"favorites")
        adapter.setCoroutineScope(fragmentScope)
        binding.aiRecyclerView.adapter = adapter

    }

    private fun addNullValueInsideArrayLive(data: List<LiveWallpaperModel?>): ArrayList<LiveWallpaperModel?>{

        val firstAdLineThreshold = if (AdConfig.firstAdLineViewListWallSRC != 0) AdConfig.firstAdLineViewListWallSRC else 4
        val firstLine = firstAdLineThreshold * 3

        val lineCount = if (AdConfig.lineCountViewListWallSRC != 0) AdConfig.lineCountViewListWallSRC else 5
        val lineC = lineCount * 3
        val newData = arrayListOf<LiveWallpaperModel?>()

        for (i in data.indices){
            if (i > firstLine && (i - firstLine) % (lineC + 1)  == 0) {
                newData.add(null)



                Log.e("******NULL", "addNullValueInsideArray: null "+i )

            }else if (i == firstLine){
                newData.add(null)
                Log.e("******NULL", "addNullValueInsideArray: null first "+i )
            }
            Log.e("******NULL", "addNullValueInsideArray: not null "+i )
            newData.add(data[i])

        }
        Log.e("******NULL", "addNullValueInsideArray:size "+newData.size )




        return newData
    }

    private fun navigateToDestination(arrayList: ArrayList<CatResponse?>, position:Int) {
        val gson = Gson()
        val arrayListJson = gson.toJson(arrayList.filterNotNull())

        val countOfNulls = arrayList.subList(0, position).count { it == null }

        sharedViewModel.clearData()
        sharedViewModel.setData(arrayList.filterNotNull(), position - countOfNulls)
        Bundle().apply {
            putInt("position",position - countOfNulls)
            putString("from","trending")
            findNavController().navigate(R.id.wallpaperViewFragment,this)
        }
        myViewModel.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        fragmentScope.cancel()
    }
    private fun loadDataFromRoomDB() {
        roomViewModel.myFavouriteList.observe(viewLifecycleOwner){
            Log.d("tracingFavouriteList", "loadDataFromRoomDB: $it")
            if(it.isNotEmpty()){

                cachedIGList?.addAll(it)
                val randomNumber = if (it.size > 1) {
                    Random.nextInt(0, it.size - 1)
                } else {
                    0 // Handle the case where it.size is 0 or 1
                }
//                getBitmapFromGlide(it[randomNumber].image!!)
                if (MySharePreference.getFavouriteSaveState(requireContext()) == 2){
                    binding.selfCreationRecyclerView.visibility = View.VISIBLE
                }

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
                        findNavController().navigate(R.id.favouriteSliderViewFragment,bundle)
                    }
                })
                binding.selfCreationRecyclerView.adapter = adapter
            }else{
                if (MySharePreference.getFavouriteSaveState(requireContext()) == 2){
                    binding.emptySupportAI.visibility = View.VISIBLE
                    binding.selfCreationRecyclerView.visibility = View.GONE
//                    Toast.makeText(requireContext(),
//                        getString(R.string.your_favorite_list_is_currently_empty_start_adding_items_by_tapping_the), Toast.LENGTH_SHORT).show()
                }

            }

        }
    }
}