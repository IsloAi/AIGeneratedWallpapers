package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.menuFragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.CommonAdsListenerAdapter
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.debug.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.debug.databinding.FragmentHomeBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.ApiCategoriesListAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.GemsTextUpdate
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.GetLoginDetails
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.PositionCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.RetrofitInstance
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyDialogs
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyHomeViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.PostDataOnServer

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
    private fun updateUIWithFetchedData(catResponses: List<CatResponse>) {
       adapter = ApiCategoriesListAdapter(catResponses as ArrayList, object :
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
        },object :GetLoginDetails{
            override fun loginDetails() {
                    requireParentFragment().findNavController().navigate(R.id.action_mainFragment_to_signInFragment)
            }
        },null,0,myActivity)
            binding.recyclerviewAll.adapter = adapter
    }
//    private fun getUserIdDialog() {
//        dialog = Dialog(requireContext())
//        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog?.setContentView(R.layout.get_user_id)
//        val width = WindowManager.LayoutParams.MATCH_PARENT
//        val height = WindowManager.LayoutParams.WRAP_CONTENT
//        dialog?.window!!.setLayout(width, height)
//        dialog?.window!!.setBackgroundDrawableResource(android.R.color.transparent)
//        dialog?.setCancelable(false)
//        dialog?.findViewById<RelativeLayout>(R.id.closeButton)?.setOnClickListener { dialog?.dismiss()
//        isPopOpen=false
//        }
//        dialog?.findViewById<LinearLayout>(R.id.googleLogin)?.setOnClickListener {
//            isPopOpen=false
//           dialog2 = Dialog(requireContext())
//            myDialogs.waiting(dialog2!!)
//            dialog?.dismiss()
//
//        }
//        dialog?.show()
//    }


    private fun navigateToDestination(arrayList: ArrayList<CatResponse>, position:Int) {
        val gson = Gson()
        val arrayListJson = gson.toJson(arrayList)
        Bundle().apply {
            putString("arrayListJson",arrayListJson)
            putInt("position",position)
            requireParentFragment().findNavController().navigate(R.id.wallpaperViewFragment,this)
        }
        myViewModel.clear()
    }
//    private fun checkDailyReward(){
//       if(!MySharePreference.getDailyRewardCounter(requireContext())){
//           val dialog = Dialog(requireContext())
//           dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//           dialog.setContentView(R.layout.daily_reward)
//           val width = WindowManager.LayoutParams.MATCH_PARENT
//           val height = WindowManager.LayoutParams.WRAP_CONTENT
//           dialog.window!!.setLayout(width, height)
//           dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
//           dialog.setCancelable(false)
//           dialog.findViewById<ImageView>(R.id.closePopup).setOnClickListener {
//               dialog.dismiss()
//               MySharePreference.setDailyRewardCounter(dialog.context,true)
//           }
//           dialog.findViewById<Button>(R.id.buttonGetReward).setOnClickListener {
//               dialog.dismiss()
//                   val gems = MySharePreference.getGemsValue(requireContext())!!+rewardAdWatched
//                   postDataOnServer.gemsPostData(requireContext(), MySharePreference.getDeviceID(requireContext())!!,
//                       RetrofitInstance.getInstance(),gems, PostDataOnServer.isPlan)
//                   MySharePreference.setDailyRewardCounter(requireContext(),true)
//                   binding.gemsText.text = gems.toString()
//                   Toast.makeText(context, "You earned the 20 gems successfully", Toast.LENGTH_SHORT).show()
//
//           }
//           dialog.show()
//       }
//    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding =null
    }

}