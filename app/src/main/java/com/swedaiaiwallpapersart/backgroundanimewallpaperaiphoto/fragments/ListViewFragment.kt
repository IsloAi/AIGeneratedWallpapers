package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.CommonAdsListenerAdapter
import com.bmik.android.sdk.listener.CustomSDKAdsListenerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.Gson
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding
.FragmentListViewBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.ApiCategoriesListAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.GemsTextUpdate
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.GetLoginDetails
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.PositionCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatNameResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.BlurView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

class ListViewFragment : Fragment() {
    private var _binding: FragmentListViewBinding? = null
    private val binding get() = _binding!!
    private lateinit var myViewModel: MyViewModel
    private var name = ""
    private var from = ""
    private var isLogin = true
    private lateinit var myActivity : MainActivity

    val sharedViewModel: SharedViewModel by activityViewModels()

    val orignalList = arrayListOf<CatResponse?>()

    var adcount = 0
    var totalADs = 0

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
        binding.progressBar.setAnimation(R.raw.main_loading_animation)
         name = arguments?.getString("name").toString()
         from = arguments?.getString("from").toString()
        Log.d("tracingNameCategory", "onViewCreated: name $name")
        binding.catTitle.text = name
        binding.recyclerviewAll.layoutManager = GridLayoutManager(requireContext(), 3)

        binding.recyclerviewAll.addItemDecoration(RvItemDecore(3,5,false,10000))
        loadData()
//        if (!isDataFetched) {
//            if(name!=null){
//                myViewModel.fetchWallpapers(requireContext(),name,binding.progressBar)
//            }
//        } else {
//            binding.progressBar.visibility = View.INVISIBLE
//            updateUIWithFetchedData(cachedCatResponses!!)
//        }
        binding.toolbar.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    private fun loadData() {
        Log.d("functionCallingTest", "onCreateCustom:  home on create")
        myViewModel = ViewModelProvider(this)[MyViewModel::class.java]
        // Observe the LiveData in the ViewModel and update the UI accordingly
        myViewModel.getWallpapers().observe(viewLifecycleOwner) { catResponses ->
            if (catResponses != null) {

                orignalList.clear()
                orignalList.addAll(catResponses)


                    updateUIWithFetchedData(catResponses as ArrayList)
            }
        }
        myViewModel.fetchWallpapers(requireContext(),name,binding.progressBar,isLogin)
    }


    private fun addNullValueInsideArray(data: List<CatResponse?>): ArrayList<CatResponse?>{

        val firstAdLineThreshold = if (AdConfig.firstAdLineViewListWallSRC != 0) AdConfig.firstAdLineViewListWallSRC else 4
        val firstLine = firstAdLineThreshold * 3

        val lineCount = if (AdConfig.lineCountViewListWallSRC != 0) AdConfig.lineCountViewListWallSRC else 5
        val lineC = lineCount * 3
        val newData = arrayListOf<CatResponse?>()

        for (i in data.indices){
            if (i > firstLine && (i - firstLine) % (lineC + 1)  == 0) {
                newData.add(null)
                    totalADs++
                    Log.e("******NULL", "addNullValueInsideArray adcount: "+adcount )
                    Log.e("******NULL", "addNullValueInsideArray adcount: "+totalADs )

                Log.e("******NULL", "addNullValueInsideArray: null "+i )

            }else if (i == firstLine){
                newData.add(null)
                totalADs++
                Log.e("******NULL", "addNullValueInsideArray adcount: "+adcount )
                Log.e("******NULL", "addNullValueInsideArray adcount: "+totalADs )

                Log.e("******NULL", "addNullValueInsideArray: null first "+i )
            }
            Log.e("******NULL", "addNullValueInsideArray: not null "+i )
            newData.add(data[i])

        }
        Log.e("******NULL", "addNullValueInsideArray:size "+newData.size )




        return newData
    }
    private val fragmentScope: CoroutineScope by lazy { MainScope() }
    @SuppressLint("NotifyDataSetChanged")
    private fun updateUIWithFetchedData(catResponses: ArrayList<CatResponse>) {

        val shuffled = catResponses.shuffled()

//        getBitmapFromGlide(shuffled[0]?.compressed_image_url!!)


        val list = addNullValueInsideArray(shuffled)
        val adapter = ApiCategoriesListAdapter(list as ArrayList, object :
            PositionCallback {
            override fun getPosition(position: Int) {

                SDKBaseController.getInstance().showInterstitialAds(
                    requireActivity(),
                    "categoryscr_fantasy_click_item",
                    "categoryscr_fantasy_click_item",
                    showLoading = true,
                    adsListener = object : CommonAdsListenerAdapter() {
                        override fun onAdsShowFail(errorCode: Int) {
                            navigateToDestination(list,position)
                            Log.e("********ADS", "onAdsShowFail: "+errorCode )
                            //do something
                        }

                        override fun onAdsDismiss() {
                            navigateToDestination(list,position)
                        }
                    }
                )



            }

            override fun getFavorites(position: Int) {
            }
        },myActivity)
        adapter.setCoroutineScope(fragmentScope)
        binding.recyclerviewAll.adapter = adapter


        binding.swipeLayout.setOnRefreshListener {
            val newList = orignalList.shuffled()
//            getBitmapFromGlide(newList[0]!!.compressed_image_url!!)
            adapter.shuffleImage(newList as ArrayList)
            binding.swipeLayout.isRefreshing = false
        }
    }
    private fun navigateToDestination(arrayList: ArrayList<CatResponse?>, position:Int) {
        var newPosition = position
        var totalAdsCount = 0
        val firstAdLineThreshold = if (AdConfig.firstAdLineViewListWallSRC != 0) AdConfig.firstAdLineViewListWallSRC else 4
        val firstLine = firstAdLineThreshold * 2

        val lineCount = if (AdConfig.lineCountViewListWallSRC != 0) AdConfig.lineCountViewListWallSRC else 5
        val lineC = lineCount * 2

        for (i in arrayList.indices){
            if (i > firstLine && (i - firstLine) % (lineC + 1)  == 0) {
                if (i <= newPosition){
                    totalAdsCount++

                }
            }else if (i == firstLine){
                totalAdsCount++
            }


        }


        val gson = Gson()
        val arrayListJson = gson.toJson(arrayList.filterNotNull())
//        val countOfNulls = arrayList.count { it == null }
        newPosition = if (position == firstLine){
            position - totalAdsCount
        }else if (position < firstLine){
            position
        }else{
            position - totalAdsCount
        }

        val countOfNulls = arrayList.subList(0, position).count { it == null }

        sharedViewModel.clearData()

        sharedViewModel.setData(arrayList.filterNotNull(), position - countOfNulls)
        val bundle =  Bundle().apply {
            putString("from",from)
            putInt("position",position - countOfNulls)
        }
        findNavController().navigate(R.id.action_listViewFragment_to_wallpaperViewFragment,bundle)
        myViewModel.clear()

    }

//    private fun getBitmapFromGlide(url:String){
//        Glide.with(requireContext()).asBitmap().load(url)
//            .into(object : CustomTarget<Bitmap>() {
//                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
//                    Log.e("TAG", "onResourceReady: bitmap loaded" )
//                    if (isAdded){
//                        val blurImage: Bitmap = BlurView.blurImage(requireContext(), resource!!)!!
//                        binding.backImage.setImageBitmap(blurImage)
//                    }
//
//
//
//                }
//                override fun onLoadCleared(placeholder: Drawable?) {
//                    Log.e("TAG", "onLoadCleared: cleared" )
//                } })
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        fragmentScope.cancel()
    }

}