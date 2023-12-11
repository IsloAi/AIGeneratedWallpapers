package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.menuFragments

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.CommonAdsListenerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.Gson
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentHomeBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.ApiCategoriesListAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.GemsTextUpdate
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.GetLoginDetails
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.PositionCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.BlurView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyDialogs
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyHomeViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.PostDataOnServer
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

class HomeFragment : Fragment(){
    private var _binding: FragmentHomeBinding?=null
    private val binding get() = _binding!!
    private lateinit var myViewModel: MyHomeViewModel
    private var navController: NavController? = null
    private var cachedCatResponses: ArrayList<CatResponse>? = ArrayList()
    private var  dialog:Dialog? = null
    private var  dialog2:Dialog? = null
    companion object{const val RC_SIGN_IN = 9001}
    private var isPopOpen = false
    private val myDialogs = MyDialogs()
    private var isLoading = false
    private lateinit var myActivity : MainActivity
    private val allData = mutableListOf<CatResponse>()
    val orignalList = arrayListOf<CatResponse?>()
    private lateinit var adapter:ApiCategoriesListAdapter
    private val postDataOnServer = PostDataOnServer()

    private val rewardAdWatched = 20

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View{
        _binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SDKBaseController.getInstance(). loadInterstitialAds(activity, "mainscr_trending_tab_click_item","mainscr_trending_tab_click_item")

        onCreatingCalling()
        setEvents()
        observeNetworkStatus()
    }


    private fun setEvents(){
        binding.retryBtn.setOnClickListener {
            loadData()
        }

        binding.swipeLayout.setOnRefreshListener {
            val newList = orignalList.shuffled()
            getBitmapFromGlide(newList[0]!!.compressed_image_url!!)
            adapter.shuffleImage(newList as ArrayList)
            binding.swipeLayout.isRefreshing = false

        }
    }
    private fun onCreatingCalling(){
        Log.d("TraceLogingHomaeHHH", "onCreatingCalling   ")
//        checkDailyReward()
        myActivity = activity as MainActivity
        navController = findNavController()

        Glide.with(requireContext())
            .asGif()
            .load(R.raw.gems_animaion)
            .into(binding.animationDdd)
        binding.progressBar.visibility = VISIBLE
        binding.gemsText.text = MySharePreference.getGemsValue(requireContext()).toString()
        binding.progressBar.setAnimation(R.raw.main_loading_animation)
        binding.recyclerviewAll.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerviewAll.addItemDecoration(RvItemDecore(2,20,false,10000))
        loadData()
//        binding.recyclerviewAll.addOnScrollListener(object : RecyclerView.OnScrollListener(){
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                val layoutManager = recyclerView.layoutManager as StaggeredGridLayoutManager
//                val visibleItemCount = layoutManager.childCount
//                val totalItemCount = layoutManager.itemCount
//                val firstVisibleItemPositions = layoutManager.findFirstVisibleItemPositions(null)
//
//                if (!isLoading) {
//                    val lastVisibleItemPosition = firstVisibleItemPositions.max()
//                    // Load more data when reaching the end of the list
//                    if (visibleItemCount + lastVisibleItemPosition >= totalItemCount) {
//                        isLoading = true
//                        loadMoreData()
//                    }
//                }
//            }
//        })
    }
    private fun loadMoreData(){
           cachedCatResponses?.let { allData.addAll(it) }
           adapter.notifyDataSetChanged()
           isLoading = false
    }

    fun observeNetworkStatus(){
        myViewModel.networkRequestStatus.observe(viewLifecycleOwner) { isRequestInProgress ->
            // Update UI based on the network request status
            if (isRequestInProgress) {
                // Show loading state, hide retry button
                binding.progressBar.visibility = View.VISIBLE
                binding.retryBtn.visibility = View.GONE
            } else {
                // Hide loading state, show retry button if there was an error
                if (myViewModel.wallpaperData.value == null) {
                    binding.retryBtn.visibility = View.VISIBLE
                } else {
                    binding.retryBtn.visibility = View.GONE
                }
            }
        }
    }

    private fun loadData() {
        Log.d("functionCallingTest", "onCreateCustom:  home on create")
        myViewModel = ViewModelProvider(this)[MyHomeViewModel::class.java]
        // Observe the LiveData in the ViewModel and update the UI accordingly
        myViewModel.getWallpapers().observe(viewLifecycleOwner) { catResponses ->
            if (catResponses != null) {
                cachedCatResponses = catResponses

                Log.e("TAG", "loadData: "+catResponses )
                if (view != null) {
                    // If the view is available, update the UI
                    orignalList.clear()
                    orignalList.addAll(catResponses)
                    binding.retryBtn.visibility = View.GONE

                    updateUIWithFetchedData(catResponses)
                }
            }else{
                binding.retryBtn.visibility = View.VISIBLE
                Log.e("TAG", "loadData: "+catResponses )
            }
        }
        myViewModel.fetchWallpapers(requireContext(), binding.progressBar,true)
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
    private val fragmentScope: CoroutineScope by lazy { MainScope() }


    private fun updateUIWithFetchedData(catResponses: List<CatResponse?>) {

        val shuffled = catResponses.shuffled()
        getBitmapFromGlide(shuffled[0]?.compressed_image_url!!)


        val list = addNullValueInsideArray(shuffled)

       adapter = ApiCategoriesListAdapter(list as ArrayList, object :
            PositionCallback {
            override fun getPosition(position: Int) {

                SDKBaseController.getInstance().showInterstitialAds(
                    requireActivity(),
                    "mainscr_trending_tab_click_item",
                    "mainscr_trending_tab_click_item",
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
               //
           }
       },requireParentFragment().findNavController(),R.id.action_mainFragment_to_premiumPlanFragment,object :
            GemsTextUpdate {
            override fun getGemsBack(gems: Int) {
                binding.gemsText.text = gems.toString()
            }
        },object :GetLoginDetails{
            override fun loginDetails() {
                    requireParentFragment().findNavController().navigate(R.id.action_mainFragment_to_signInFragment)
            }
        },null,0,myActivity)
        adapter.setCoroutineScope(fragmentScope)
            binding.recyclerviewAll.adapter = adapter
    }

    private fun getBitmapFromGlide(url:String){
        Glide.with(requireContext()).asBitmap().load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Log.e("TAG", "onResourceReady: bitmap loaded" )
                    if (isAdded){
                        val blurImage: Bitmap = BlurView.blurImage(requireContext(), resource!!)!!
                        binding.backImage.setImageBitmap(blurImage)
                    }



                }
                override fun onLoadCleared(placeholder: Drawable?) {
                    Log.e("TAG", "onLoadCleared: cleared" )
                } })
    }



    private fun navigateToDestination(arrayList: ArrayList<CatResponse?>, position:Int) {
        val gson = Gson()
        val arrayListJson = gson.toJson(arrayList.filterNotNull())

        val countOfNulls = arrayList.subList(0, position).count { it == null }
         val sharedViewModel: SharedViewModel by activityViewModels()


        sharedViewModel.clearData()

        sharedViewModel.setData(arrayList.filterNotNull(), position - countOfNulls)

        Bundle().apply {
            putString("from","trending")
            putInt("position",position - countOfNulls)
            requireParentFragment().findNavController().navigate(R.id.wallpaperViewFragment,this)
        }
        myViewModel.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding =null
        fragmentScope.cancel()
    }

}