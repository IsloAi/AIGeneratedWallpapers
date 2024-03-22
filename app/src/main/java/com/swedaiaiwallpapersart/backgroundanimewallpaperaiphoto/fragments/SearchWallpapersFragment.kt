package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments

import ApiCategoriesNameAdapter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.CommonAdsListenerAdapter
import com.bmik.android.sdk.listener.CustomSDKAdsListenerAdapter
import com.bmik.android.sdk.listener.keep.IKLoadNativeAdListener
import com.bmik.android.sdk.widgets.IkmNativeAdView
import com.google.gson.Gson
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentSearchWallpapersBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.ApiCategoriesListAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.PositionCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.StringCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatNameResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.AllWallpapersViewmodel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class SearchWallpapersFragment : Fragment() {

    private var _binding:FragmentSearchWallpapersBinding ?= null
    private val binding get() = _binding!!

    private lateinit var myActivity : MainActivity



    val catlist = ArrayList<CatNameResponse?>()
    var adapter:ApiCategoriesNameAdapter ?= null
    var searchAdapter:ApiCategoriesListAdapter ?= null

    val catListViewmodel: MyViewModel by activityViewModels()

    var catgories = true

    private var addedItems: ArrayList<CatResponse?>? = ArrayList()

    var searched = false

    val TAG = "SEARCH"


    private var cachedCatResponses: ArrayList<CatResponse>? = ArrayList()
    private  val myViewModel: AllWallpapersViewmodel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSearchWallpapersBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myActivity = activity as MainActivity


        binding.adsView.loadAd(requireContext(),"searchscr_bottom",
            " searchscr_bottom", object : CustomSDKAdsListenerAdapter() {
                override fun onAdsLoaded() {
                    super.onAdsLoaded()
                    Log.e("*******ADS", "onAdsLoaded: Banner loaded", )
                }

                override fun onAdsLoadFail() {
                    super.onAdsLoadFail()

                    if (isAdded){
//                        binding.adsView.reCallLoadAd(this)
                    }
                    Log.e("*******ADS", "onAdsLoaded: Banner failed", )
                }
            })

        initCatgories()

        initDataObserver()
        initSearchData()

        initSearchRv()
        setEvents()




    }

    override fun onResume() {
        super.onResume()

        if (addedItems?.isNotEmpty() == true){
            searchAdapter?.addNewData()
            searchAdapter?.updateMoreData(addedItems!!)
//            searchAdapter?.updateData(addedItems!!)
        }

        if (catgories){
            binding.searchSuggestions.visibility = View.GONE
            binding.recyclerviewCatgory.visibility = View.VISIBLE
            binding.recyclerviewAll.visibility = View.GONE
            editTextLayoutsFocus()
        }else{

            if (addedItems?.isNotEmpty() == true){
                binding.searchSuggestions.visibility = View.GONE
            }else{
                binding.searchSuggestions.visibility = View.VISIBLE
            }
            binding.recyclerviewCatgory.visibility = View.GONE
            binding.recyclerviewAll.visibility = View.VISIBLE
            editTextLayoutsFocus()
        }



    }

    private fun initSearchRv(){
        val layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerviewAll.layoutManager = layoutManager
        binding.recyclerviewAll.addItemDecoration(RvItemDecore(3,5,false,10000))
        searchAdapter = ApiCategoriesListAdapter(arrayListOf(), object :
            PositionCallback {
            override fun getPosition(position: Int) {
                searched = true

                val items = searchAdapter?.getAllItems()
                addedItems?.addAll(items!!)

                catgories = false

                SDKBaseController.getInstance().showInterstitialAds(
                    requireActivity(),
                    "mainscr_trending_tab_click_item",
                    "mainscr_trending_tab_click_item",
                    showLoading = true,
                    adsListener = object : CommonAdsListenerAdapter() {
                        override fun onAdsShowFail(errorCode: Int) {
                            Log.e("********ADS", "onAdsShowFail: "+errorCode )
                            navigateToDestination(items!!,position)
                            //do something
                        }

                        override fun onAdsDismiss() {
                            Log.e("********ADS", "onAdsDismiss: " )
                            navigateToDestination(items!!,position)
                        }
                    }
                )


            }

            override fun getFavorites(position: Int) {
                //
            }
        },myActivity,"search")
        searchAdapter!!.setCoroutineScope(fragmentScope)

        SDKBaseController.getInstance().loadIkmNativeAdView(requireContext(),"searchscr_scroll_view","searchscr_scroll_view",object :
            IKLoadNativeAdListener {
            override fun onAdFailedToLoad(errorCode: Int) {
                Log.e(TAG, "onAdFailedToLoad: "+errorCode )

            }

            override fun onAdLoaded(adsResult: IkmNativeAdView?) {
                if (isAdded && view!= null){
                    searchAdapter?.nativeAdView = adsResult
                    binding.recyclerviewAll.adapter = searchAdapter
                }
            }

        })
        binding.recyclerviewAll.adapter = searchAdapter


    }

    private val fragmentScope: CoroutineScope by lazy { MainScope() }


    fun setEvents(){
        binding.suggestCar.setOnClickListener {
            binding.searchEdt.setText("Car")
        }

        binding.suggestDark.setOnClickListener {
            binding.searchEdt.setText("Dark")
        }

        binding.suggestNeon.setOnClickListener {
            binding.searchEdt.setText("Neon")
        }

        binding.suggestAIWallpapers.setOnClickListener {
            binding.searchEdt.setText("Ai Wallpaper")
        }

        binding.suggestHoroscope.setOnClickListener {
            binding.searchEdt.setText("Horoscope")
        }

        binding.suggestGameLOL.setOnClickListener {
            binding.searchEdt.setText("Game")
        }
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.searchEdt.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(p0: Editable?) {

                        if (p0.isNullOrBlank()) {
                            binding.searchSuggestions.visibility = View.VISIBLE
                            binding.recyclerviewCatgory.visibility = View.GONE
                            binding.recyclerviewAll.visibility = View.GONE
                            binding.emptySupport.visibility = View.GONE
                        } else {
                            if (!searched){
                                searchInList(p0.toString(), cachedCatResponses) { filteredList ->

                                    Log.e("TAG", "afterTextChanged: " + filteredList)


                                    if (filteredList.isNotEmpty()) {
                                        binding.searchSuggestions.visibility = View.GONE
                                        binding.recyclerviewCatgory.visibility = View.GONE
                                        binding.recyclerviewAll.visibility = View.VISIBLE
                                        binding.emptySupport.visibility = View.GONE

                                        searchAdapter?.addNewData()
                                        searchAdapter?.updateMoreData(filteredList)
//                                        searchAdapter?.updateData(filteredList)
                                    } else {
                                        binding.searchSuggestions.visibility = View.GONE
                                        binding.recyclerviewCatgory.visibility = View.GONE
                                        binding.recyclerviewAll.visibility = View.GONE
                                        binding.emptySupport.visibility = View.VISIBLE
                                    }


                                }
                            }else{
                                searched = false
                            }


                        }



//                Log.e("TAG", "afterTextChanged: "+searchInList("Car",cachedCatResponses) )
                }

            })

    }

    fun searchInList(
        searchString: String,
        catResponses: List<CatResponse>?,
        callback: (ArrayList<CatResponse?>) -> Unit
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            val filteredList = catResponses?.filter { catResponse ->
                catResponse.Tags?.let { tags ->
                    tags.split(", ").any { tag ->
                        tag.contains(searchString, ignoreCase = true)
                    }
                } ?: false
            }
            val resultList = ArrayList(filteredList ?: listOf())
            withContext(Dispatchers.Main) {
                callback(resultList)
            }
        }
    }

    private fun initSearchData(){
//        myViewModel.getWallpapers().observe(viewLifecycleOwner) { catResponses ->
//            if (catResponses != null) {
//                if (!cachedCatResponses.isNullOrEmpty()){
//                    cachedCatResponses?.clear()
//                }
//                cachedCatResponses?.addAll(catResponses)
////
//
//                Log.e("TAG", "initSearchData: "+catResponses.size )
//
//
//            }else{
//
//                Log.e("TAG", "initSearchData: no data" )
//            }
//        }


        myViewModel.allCreations.observe(viewLifecycleOwner){result ->
            when (result) {
                is Response.Loading -> {
//                    binding.tvNoData.visibility=View.GONE
//                    customProgressBar.show(requireContext())
                }

                is Response.Success -> {
//                    binding.tvNoData.visibility=View.GONE
//                    customProgressBar.getDialog()?.dismiss()
                    if (!result.data.isNullOrEmpty()) {
                        result.data.forEach { item ->
                            val model = CatResponse(item.id,item.image_name,item.cat_name,item.hd_image_url,item.compressed_image_url,null,item.likes,item.liked,item.unlocked,item.size,item.Tags,item.capacity)
                            if (cachedCatResponses?.contains(model) != true){
                                cachedCatResponses?.add(model)
                            }

                        }
                    }
                }

                is Response.Error -> {
//                    binding.tvNoData.visibility=View.VISIBLE
//                    customProgressBar.getDialog()?.dismiss()
                    Log.e("TAG", "error: ${result.message}")
                    Toast.makeText(requireContext(), "${result.message}", Toast.LENGTH_SHORT)
                        .show()
                }

                else -> {
//                    customProgressBar.getDialog()?.dismiss()
//                    binding.tvNoData.visibility=View.GONE
                }
            }
        }



    }


    private fun editTextLayoutsFocus(){
        binding.searchEdt.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (addedItems?.isNotEmpty() == true){
                    binding.searchSuggestions.visibility = View.GONE
                }else{
                    binding.searchSuggestions.visibility = View.VISIBLE
                }
                binding.recyclerviewCatgory.visibility = View.GONE
            } else {
                binding.searchSuggestions.visibility = View.GONE
                binding.recyclerviewCatgory.visibility = View.VISIBLE
            }
        }
    }




    private fun initCatgories(){
        binding.recyclerviewCatgory.layoutManager = GridLayoutManager(requireContext(),3)
        binding.recyclerviewCatgory.addItemDecoration(RvItemDecore(3,5  ,false,10000))
        adapter = ApiCategoriesNameAdapter(catlist,object : StringCallback {
            override fun getStringCall(string: String) {

                catgories = true

                catListViewmodel.getAllCreations(string)

                SDKBaseController.getInstance().showInterstitialAds(
                    requireActivity(),
                    "mainscr_cate_tab_click_item",
                    "mainscr_cate_tab_click_item",
                    showLoading = true,
                    adsListener = object : CommonAdsListenerAdapter() {
                        override fun onAdsShowFail(errorCode: Int) {
                            Log.e("********ADS", "onAdsShowFail: $errorCode")
                            setFragment(string)
                            //do something
                        }

                        override fun onAdsDismiss() {
                            setFragment(string)
                        }
                    }
                )

            }
        },myActivity,"")

        SDKBaseController.getInstance().loadIkmNativeAdView(requireContext(),"mainscr_cate_tab_scroll_view","mainscr_cate_tab_scroll_view",object :
            IKLoadNativeAdListener {
            override fun onAdFailedToLoad(errorCode: Int) {
                Log.e(TAG, "onAdFailedToLoad: $errorCode")

            }

            override fun onAdLoaded(adsResult: IkmNativeAdView?) {
                if (isAdded && view!= null){
                    adapter?.nativeAdView = adsResult
                    binding.recyclerviewCatgory.adapter = adapter
                }
            }

        })
        binding.recyclerviewCatgory.adapter = adapter
    }

    fun initDataObserver(){
        myActivity.myCatNameViewModel.wallpaper.observe(viewLifecycleOwner) { wallpapersList ->
            Log.e("TAG", "onCustomCreateView: no data exists" )
            if (wallpapersList?.size!! > 0){
                Log.e("TAG", "onCustomCreateView: data exists" )
                val list = addNullValueInsideArray(wallpapersList)
                adapter?.updateData(newData = list)
            }
        }


    }


    private fun navigateToDestination(arrayList: ArrayList<CatResponse?>, position: Int) {
        val gson = Gson()
        val arrayListJson = gson.toJson(arrayList.filterNotNull())

        val countOfNulls = arrayList.subList(0, position).count { it == null }
        val sharedViewModel: SharedViewModel by activityViewModels()


        sharedViewModel.clearData()

        sharedViewModel.setData(arrayList.filterNotNull(), position - countOfNulls)

        Bundle().apply {
            putString("from", "trending")
            putInt("position", position - countOfNulls)
            requireParentFragment().findNavController().navigate(R.id.wallpaperViewFragment, this)
        }
    }





    private fun addNullValueInsideArray(data: List<CatNameResponse?>): ArrayList<CatNameResponse?>{

        val firstAdLineThreshold = if (AdConfig.firstAdLineCategoryArt != 0) AdConfig.firstAdLineCategoryArt else 4
        val firstLine = firstAdLineThreshold * 3

        val lineCount = if (AdConfig.lineCountCategoryArt != 0) AdConfig.lineCountCategoryArt else 5
        val lineC = lineCount * 3
        val newData = arrayListOf<CatNameResponse?>()

        for (i in data.indices){
            if (i > firstLine && (i - firstLine) % (lineC)  == 0) {
                newData.add(null)
                Log.e("******NULL", "addNullValueInsideArray: null "+i )

            }else if (i == firstLine){
                newData.add(null)
                Log.e("******NULL", "addNullValueInsideArray: null first "+i )
            }
            Log.e("******NULL", "addNullValueInsideArray: not null "+i )
            Log.e("******NULL", "addNullValueInsideArray: "+data[i] )
            newData.add(data[i])

        }
        Log.e("******NULL", "addNullValueInsideArray:size "+newData.size )
        return newData
    }

    private fun setFragment(name:String){
        val bundle =  Bundle().apply {
            putString("name",name)
            putString("from","category")

        }

        if (isAdded ){
            if (findNavController().currentDestination?.id != R.id.listViewFragment) {

                findNavController().navigate(R.id.listViewFragment, bundle)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        fragmentScope.cancel()
    }
}