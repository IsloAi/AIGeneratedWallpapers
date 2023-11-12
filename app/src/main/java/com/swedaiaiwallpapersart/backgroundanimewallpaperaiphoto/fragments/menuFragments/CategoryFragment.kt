package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.menuFragments
import ApiCategoriesNameAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.CommonAdsListenerAdapter
import com.bumptech.glide.Glide
import com.example.hdwallpaper.Fragments.MainFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyCatNameViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.StringCallback
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentCategoryBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatNameResponse

class CategoryFragment : Fragment() {
   private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!
    val myCatNameViewModel: MyCatNameViewModel by viewModels()
    private lateinit var myActivity : MainActivity

    val catlist = ArrayList<CatNameResponse>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View{
        _binding = FragmentCategoryBinding.inflate(inflater,container,false)

       return  binding.root }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCustomCreateView()
//        SDKBaseController.getInstance().loadInterstitialAds(requireActivity(),"mainscr_cate_tab_click_item","mainscr_cate_tab_click_item")
    }
    @SuppressLint("SuspiciousIndentation")
    private fun onCustomCreateView() {
        myActivity = activity as MainActivity
        binding.progressBar.visibility = VISIBLE
        binding.gemsText.text = MySharePreference.getGemsValue(requireContext()).toString()
        binding.progressBar.setAnimation(R.raw.main_loading_animation)

        Glide.with(requireContext())
            .asGif()
            .load(R.raw.gems_animaion)
            .into(binding.animationDdd)
        binding.progressBar.visibility = View.GONE
        binding.recyclerviewAll.layoutManager = LinearLayoutManager(requireContext())
        val adapter = ApiCategoriesNameAdapter(catlist,object : StringCallback {
            override fun getStringCall(string: String) {

                SDKBaseController.getInstance().showInterstitialAds(
                    requireActivity(),
                    "home",
                    "home_screen_tracking",
                    showLoading = true,
                    adsListener = object : CommonAdsListenerAdapter() {
                        override fun onAdsShowFail(errorCode: Int) {
                            Log.e("********ADS", "onAdsShowFail: "+errorCode )
                            //do something
                        }

                        override fun onAdsDismiss() {
                            setFragment(string)
                        }
                    }
                )

            }
        })
        binding.recyclerviewAll.adapter = adapter

        myActivity.myCatNameViewModel.wallpaper.observe(viewLifecycleOwner) { wallpapersList ->
            Log.e("TAG", "onCustomCreateView: no data exists" )
           if (wallpapersList?.size!! > 0){
               Log.e("TAG", "onCustomCreateView: data exists" )
               adapter.updateData(newData = wallpapersList)
           }
        }
    }
    private fun setFragment(name:String){
        Bundle().apply {
            putString("name",name)
            (requireParentFragment() as MainFragment).findNavController().navigate(R.id.action_mainFragment_to_listViewFragment,this)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

