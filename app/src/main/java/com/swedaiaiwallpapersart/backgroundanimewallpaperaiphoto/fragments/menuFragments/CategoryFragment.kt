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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hdwallpaper.Fragments.MainFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyCatNameViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.StringCallback
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentCategoryBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity

class CategoryFragment : Fragment() {
   private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var myCatNameViewModel: MyCatNameViewModel
    private lateinit var myActivity : MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View{
        _binding = FragmentCategoryBinding.inflate(inflater,container,false)
        onCustomCreateView()
       return  binding.root }
    @SuppressLint("SuspiciousIndentation")
    private fun onCustomCreateView() {
        myActivity = activity as MainActivity
        binding.progressBar.visibility = VISIBLE
        binding.gemsText.text = MySharePreference.getGemsValue(requireContext()).toString()
        binding.progressBar.setAnimation(R.raw.main_loading_animation)
        binding.recyclerviewAll.layoutManager = LinearLayoutManager(requireContext())
        myCatNameViewModel = ViewModelProvider(this)[MyCatNameViewModel::class.java]
        myCatNameViewModel.getWallpapers().observe(viewLifecycleOwner) { wallpapersList ->
            wallpapersList?.let { list ->
                Log.d("arrayListError", "onCustomCreateView: $list")
                val adapter = ApiCategoriesNameAdapter(list,object : StringCallback {
                    override fun getStringCall(string: String) {
                        setFragment(string)
                    }
                })
                binding.recyclerviewAll.adapter = adapter
            }
        }
        myCatNameViewModel.fetchWallpapers(binding.progressBar)
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

