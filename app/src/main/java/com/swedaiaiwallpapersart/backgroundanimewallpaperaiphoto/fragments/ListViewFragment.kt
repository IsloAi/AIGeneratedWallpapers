package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.ApiCategoriesListAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.GemsTextUpdate
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.PositionCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.google.gson.Gson
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentListViewBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.menuFragments.HomeFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.FullViewImage
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.GetLoginDetails
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.GoogleLogin
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyDialogs

class ListViewFragment : Fragment() {
    private var _binding: FragmentListViewBinding? = null
    private val binding get() = _binding!!
    private lateinit var myViewModel: MyViewModel
    private var name = ""
    private var isLogin = false
    private lateinit var myActivity : MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentListViewBinding.inflate(inflater,container,false)
        onCreateViewCalling()
        return binding.root
    }
    private fun onCreateViewCalling(){
        myActivity = activity as MainActivity
        binding.progressBar.visibility = View.VISIBLE
        binding.gemsText.text = MySharePreference.getGemsValue(requireContext()).toString()
        binding.progressBar.setAnimation(R.raw.main_loading_animation)
         name = arguments?.getString("name").toString()
        Log.d("tracingNameCategory", "onViewCreated: name $name")
        binding.catTitle.text = name
        binding.recyclerviewAll.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
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
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        isLogin = currentUser != null

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
                navigateToDestination(catResponses,position)
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