package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.menuFragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.ApiCategoriesListAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyHomeViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.GemsTextUpdate
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.PositionCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.google.gson.Gson
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentHomeBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.FullViewImage
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.GetLoginDetails
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.RetrofitInstance
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.GoogleLogin
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyDialogs
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
    private var isLogin = false
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
        onCreatingCalling()
        return binding.root
    }
    private fun onCreatingCalling(){
        Log.d("TraceLogingHomaeHHH", "onCreatingCalling   ")
        checkDailyReward()
        myActivity = activity as MainActivity
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        isLogin = currentUser != null
        Log.d("TraceLoging", "onCreate: $isLogin")
        navController = findNavController()
        binding.progressBar.visibility = VISIBLE
        binding.gemsText.text = MySharePreference.getGemsValue(requireContext()).toString()
        binding.progressBar.setAnimation(R.raw.main_loading_animation)
        binding.recyclerviewAll.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
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

    private fun loadData() {
        Log.d("functionCallingTest", "onCreateCustom:  home on create")
        myViewModel = ViewModelProvider(this)[MyHomeViewModel::class.java]
        // Observe the LiveData in the ViewModel and update the UI accordingly
        myViewModel.getWallpapers().observe(viewLifecycleOwner) { catResponses ->
            if (catResponses != null) {
                cachedCatResponses = catResponses
                if (view != null) {
                    // If the view is available, update the UI
                    updateUIWithFetchedData(catResponses)
                }
            }
        }
        myViewModel.fetchWallpapers(requireContext(), binding.progressBar,isLogin)
    }
    private fun updateUIWithFetchedData(catResponses: List<CatResponse>) {
       adapter = ApiCategoriesListAdapter(catResponses as ArrayList, object :
            PositionCallback {
            override fun getPosition(position: Int) {
                navigateToDestination(catResponses,position)
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
    private fun getUserIdDialog() {
        dialog = Dialog(requireContext())
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.get_user_id)
        val width = WindowManager.LayoutParams.MATCH_PARENT
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog?.window!!.setLayout(width, height)
        dialog?.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCancelable(false)
        dialog?.findViewById<RelativeLayout>(R.id.closeButton)?.setOnClickListener { dialog?.dismiss()
        isPopOpen=false
        }
        dialog?.findViewById<LinearLayout>(R.id.googleLogin)?.setOnClickListener {
            isPopOpen=false
           dialog2 = Dialog(requireContext())
            myDialogs.waiting(dialog2!!)
            dialog?.dismiss()

        }
        dialog?.show()
    }


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
    private fun checkDailyReward(){
       if(!MySharePreference.getDailyRewardCounter(requireContext())){
           val dialog = Dialog(requireContext())
           dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
           dialog.setContentView(R.layout.daily_reward)
           val width = WindowManager.LayoutParams.MATCH_PARENT
           val height = WindowManager.LayoutParams.WRAP_CONTENT
           dialog.window!!.setLayout(width, height)
           dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
           dialog.setCancelable(false)
           dialog.findViewById<ImageView>(R.id.closePopup).setOnClickListener {
               dialog.dismiss()
               MySharePreference.setDailyRewardCounter(requireContext(),true)
           }
           dialog.findViewById<Button>(R.id.buttonGetReward).setOnClickListener {
               val auth = FirebaseAuth.getInstance()
               val currentUser = auth.currentUser
               if (currentUser != null){
                   val gems = MySharePreference.getGemsValue(requireContext())!!+rewardAdWatched
                   postDataOnServer.gemsPostData(requireContext(), MySharePreference.getUserID(requireContext())!!,
                       RetrofitInstance.getInstance(),gems, PostDataOnServer.isPlan)
                   MySharePreference.setDailyRewardCounter(requireContext(),true)
                   binding.gemsText.text = gems.toString()
                   dialog.dismiss()
                   Toast.makeText(context, "You earned the 20 gems successfully", Toast.LENGTH_SHORT).show()
               }else{
                   requireParentFragment().findNavController().navigate(R.id.action_mainFragment_to_signInFragment)
               }
           }
           dialog.show()
       }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding =null
    }

}