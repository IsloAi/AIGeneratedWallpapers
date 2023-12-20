package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.CommonAdsListenerAdapter
import com.google.gson.Gson
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentPopularWallpaperBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.ApiCategoriesListAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.ApicategoriesListHorizontalAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.MostUsedWallpaperAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.OnboardingAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.PopularSliderAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.GemsTextUpdate
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.GetLoginDetails
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.PositionCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.MostDownloadImageResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyHomeViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.MostDownloadedViewmodel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel

class PopularWallpaperFragment : Fragment() {

    private var _binding:FragmentPopularWallpaperBinding ?= null
    private val binding get() = _binding!!

    private lateinit var welcomeAdapter: PopularSliderAdapter


    private  val myViewModel: MyHomeViewModel by activityViewModels()

    private var cachedCatResponses: ArrayList<CatResponse>? = ArrayList()

    val orignalList = arrayListOf<CatResponse?>()

    private lateinit var myActivity : MainActivity


    private var mostUsedWallpaperAdapter :MostUsedWallpaperAdapter ?= null


    private val viewModel: MostDownloadedViewmodel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPopularWallpaperBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myActivity = activity as MainActivity


        populateOnbaordingItems()
        binding.sliderPager.adapter = welcomeAdapter

        setIndicator()
        setCurrentIndicator(0)
        binding.sliderPager.registerOnPageChangeCallback(object:ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)

            }
        })

        initTrendingData()
        initMostUsedRV()
        initMostDownloadedData()
    }

    private fun initMostUsedRV(){

        val layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerviewMostUsed.layoutManager = layoutManager
        binding.recyclerviewMostUsed.addItemDecoration(RvItemDecore(3,5,false,10000))
        val list = ArrayList<MostDownloadImageResponse?>()
        mostUsedWallpaperAdapter = MostUsedWallpaperAdapter(list, object : PositionCallback{
            override fun getPosition(position: Int) {

            }

            override fun getFavorites(position: Int) {

            }

        },myActivity)

        binding.recyclerviewMostUsed.adapter = mostUsedWallpaperAdapter
    }


    private fun populateOnbaordingItems() {
        val welcomeItems: MutableList<Int> = ArrayList<Int>()
        welcomeItems.add(1)
        welcomeItems.add(2)
        welcomeItems.add(3)
        welcomeItems.add(4)

        welcomeAdapter = PopularSliderAdapter(welcomeItems)


    }


    private fun initTrendingData() {
        binding.progressBar.setAnimation(R.raw.main_loading_animation)
        binding.progressBar.visibility = View.GONE
        Log.d("functionCallingTest", "onCreateCustom:  home on create")
        myViewModel.getWallpapers().observe(viewLifecycleOwner) { catResponses ->
            if (catResponses != null) {
                cachedCatResponses = catResponses

                Log.e("TAG", "loadData: "+catResponses )
                if (view != null) {
                    // If the view is available, update the UI
                    orignalList.clear()
                    orignalList.addAll(catResponses)

                    updateUIWithFetchedData(catResponses)
                }
            }else{
                Log.e("TAG", "loadData: $catResponses")
            }
        }
    }


    private fun updateUIWithFetchedData(catResponses: List<CatResponse?>) {


        val adapter = ApicategoriesListHorizontalAdapter(catResponses as ArrayList<CatResponse?>, object :
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
                            navigateToDestination(catResponses,position)
                            //do something
                        }

                        override fun onAdsDismiss() {
                            navigateToDestination(catResponses,position)
                        }
                    }
                )


            }

            override fun getFavorites(position: Int) {
                //
            }
        },myActivity)

        binding.recyclerviewTrending.adapter = adapter
    }

    private fun setIndicator() {
        val welcomeIndicators = arrayOfNulls<ImageView>(welcomeAdapter.itemCount)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in welcomeIndicators.indices) {
            welcomeIndicators[i] = ImageView(requireContext())
            welcomeIndicators[i]!!.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.banner_slider_indicator_inactive
                )
            )
            welcomeIndicators[i]!!.layoutParams = layoutParams
            binding.layoutOnboardingIndicators.addView(welcomeIndicators[i])
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val childCount = binding.layoutOnboardingIndicators.childCount
        val isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                Configuration.UI_MODE_NIGHT_YES

        for (i in 0 until childCount) {
            val imageView = binding.layoutOnboardingIndicators.getChildAt(i) as ImageView

            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.banner_slider_indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.banner_slider_indicator_inactive
                    )
                )
            }
        }
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



    fun initMostDownloadedData(){
        viewModel.wallpaperData.observe(viewLifecycleOwner){wallpapers ->
            if (wallpapers != null) {

                if (view != null) {
                    // If the view is available, update the UI
                    val list = addNullValueInsideArray(wallpapers)

                    mostUsedWallpaperAdapter?.updateMoreData(list)


                }
            }else{
                Toast.makeText(requireContext(),"No Data Found",Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun addNullValueInsideArray(data: List<MostDownloadImageResponse?>): ArrayList<MostDownloadImageResponse?>{

        val firstAdLineThreshold = if (AdConfig.firstAdLineViewListWallSRC != 0) AdConfig.firstAdLineViewListWallSRC else 4
        val firstLine = firstAdLineThreshold * 3

        val lineCount = if (AdConfig.lineCountViewListWallSRC != 0) AdConfig.lineCountViewListWallSRC else 5
        val lineC = lineCount * 3
        val newData = arrayListOf<MostDownloadImageResponse?>()

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

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }


}