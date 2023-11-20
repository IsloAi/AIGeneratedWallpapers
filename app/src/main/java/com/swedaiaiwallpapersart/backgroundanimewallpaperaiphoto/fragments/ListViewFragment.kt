package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.CommonAdsListenerAdapter
import com.bmik.android.sdk.listener.CustomSDKAdsListenerAdapter
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.debug.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.debug.databinding
.FragmentListViewBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.ApiCategoriesListAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.GemsTextUpdate
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.GetLoginDetails
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.PositionCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.RvItemDecore

class ListViewFragment : Fragment() {
    private var _binding: FragmentListViewBinding? = null
    private val binding get() = _binding!!
    private lateinit var myViewModel: MyViewModel
    private var name = ""
    private var isLogin = true
    private lateinit var myActivity : MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentListViewBinding.inflate(inflater,container,false)
        onCreateViewCalling()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SDKBaseController.getInstance()
            .loadBannerAds(
                requireActivity(),
                binding.adsWidget as? ViewGroup,
                "home_banner",
                " home_banner_tracking", object : CustomSDKAdsListenerAdapter() {
                    override fun onAdsLoaded() {
                        super.onAdsLoaded()
                        Log.e("*******ADS", "onAdsLoaded: Banner loaded", )
                    }

                    override fun onAdsLoadFail() {
                        super.onAdsLoadFail()
                        Log.e("*******ADS", "onAdsLoaded: Banner failed", )
                    }
                }
            )
    }
    private fun onCreateViewCalling(){
        myActivity = activity as MainActivity
        binding.progressBar.visibility = View.VISIBLE
        binding.gemsText.text = MySharePreference.getGemsValue(requireContext()).toString()
        binding.progressBar.setAnimation(R.raw.main_loading_animation)

        Glide.with(requireContext())
            .asGif()
            .load(R.raw.gems_animaion)
            .into(binding.animationDdd)
         name = arguments?.getString("name").toString()
        Log.d("tracingNameCategory", "onViewCreated: name $name")
        binding.catTitle.text = name
        binding.recyclerviewAll.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerviewAll.addItemDecoration(RvItemDecore(2,20,false,10))
        loadData()
//        if (!isDataFetched) {
//            if(name!=null){
//                myViewModel.fetchWallpapers(requireContext(),name,binding.progressBar)
//            }
//        } else {
//            binding.progressBar.visibility = View.INVISIBLE
//            updateUIWithFetchedData(cachedCatResponses!!)
//        }
        binding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
    private fun loadData() {
        Log.d("functionCallingTest", "onCreateCustom:  home on create")
        myViewModel = ViewModelProvider(this)[MyViewModel::class.java]
        // Observe the LiveData in the ViewModel and update the UI accordingly
        myViewModel.getWallpapers().observe(viewLifecycleOwner) { catResponses ->
            if (catResponses != null) {
//                cachedCatResponses = catResponses as ArrayList
//                Log.d("cachedCatResponses", "loadData: all list ${cachedCatResponses}")
//                isDataFetched = true
                    // If the view is available, update the UI
                    updateUIWithFetchedData(catResponses)
            }
        }
        myViewModel.fetchWallpapers(requireContext(),name,binding.progressBar,isLogin)
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun updateUIWithFetchedData(catResponses: List<CatResponse>) {
        val adapter = ApiCategoriesListAdapter(catResponses as ArrayList, object :
            PositionCallback {
            override fun getPosition(position: Int) {

                SDKBaseController.getInstance().showInterstitialAds(
                    requireActivity(),
                    "categoryscr_fantasy_click_item",
                    "categoryscr_fantasy_click_item",
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
        },findNavController(),R.id.action_listViewFragment_to_premiumPlanFragment,object :
            GemsTextUpdate {
            override fun getGemsBack(gems: Int) {
                binding.gemsText.text = gems.toString()
            }
        },object: GetLoginDetails {
            override fun loginDetails() {
                findNavController().navigate(R.id.action_listViewFragment_to_signInFragment)
            }
        },myViewModel,1,myActivity)
        binding.recyclerviewAll.adapter = adapter
    }
    private fun navigateToDestination(arrayList: ArrayList<CatResponse>, position:Int) {
        val gson = Gson()
        val arrayListJson = gson.toJson(arrayList)
        val bundle =  Bundle().apply {
            putString("arrayListJson",arrayListJson)
            putInt("position",position)
        }
        findNavController().navigate(R.id.action_listViewFragment_to_wallpaperViewFragment,bundle)
        myViewModel.clear()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}