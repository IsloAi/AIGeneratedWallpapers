package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments

import ApiCategoriesNameAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.CommonAdsListenerAdapter
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentSearchWallpapersBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.StringCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatNameResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyHomeViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.RvItemDecore
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.AllWallpapersViewmodel

class SearchWallpapersFragment : Fragment() {

    private var _binding:FragmentSearchWallpapersBinding ?= null
    private val binding get() = _binding!!

    private lateinit var myActivity : MainActivity

    val catlist = ArrayList<CatNameResponse?>()
    var adapter:ApiCategoriesNameAdapter ?= null

    private  val myViewModel: AllWallpapersViewmodel by activityViewModels()

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

        initCatgories()

        initDataObserver()
        initSearchData()
        editTextLayoutsFocus()

    }

    private fun initSearchData(){
        myViewModel.getWallpapers().observe(viewLifecycleOwner) { catResponses ->
            if (catResponses != null) {

                Log.e("TAG", "initSearchData: "+catResponses.size )


            }else{

                Log.e("TAG", "initSearchData: no data" )
            }
        }
    }


    private fun editTextLayoutsFocus(){
        binding.searchEdt.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.searchSuggestions.visibility = View.VISIBLE
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
        },myActivity)
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
        if (findNavController().currentDestination?.id != R.id.listViewFragment) {

            findNavController().navigate(R.id.listViewFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}