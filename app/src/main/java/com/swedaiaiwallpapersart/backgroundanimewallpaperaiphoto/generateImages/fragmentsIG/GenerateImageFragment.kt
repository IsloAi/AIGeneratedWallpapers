package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.fragmentsIG

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentGenerateImageBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.menuFragments.HomeFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.adaptersIG.CatListAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.adaptersIG.HistoryAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.interfaces.GetbackNameOfCat
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.interfaces.GetbackOfID
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.models.CatListModelIG
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.GetResponseIGEntity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.RoomViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.ViewModelFactory
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.utilsIG.ImageGenerateViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.RetrofitInstance
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.GoogleLogin
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.ImageListViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyDialogs
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.PostDataOnServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GenerateImageFragment : Fragment() {
    private lateinit var binding : FragmentGenerateImageBinding
    private lateinit var viewModel: ImageGenerateViewModel
    private lateinit var listViewModel: ImageListViewModel
    private var myContext :Context? = null
    private var  dialog: Dialog? = null
    private var  dialog2: Dialog? = null
//    private lateinit var auth: FirebaseAuth
//    private val googleLogin = GoogleLogin()
    private lateinit var myActivity : MainActivity
    private val myDialogs = MyDialogs()
    private var existGems:Int? = null
    private val postDataOnServer = PostDataOnServer()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentGenerateImageBinding.inflate(inflater,container,false)
        if(myContext != null){
            customOnCreateCalling()
        }
        return binding.root
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context
    }
    private fun customOnCreateCalling() {

        Glide.with(requireContext())
            .asGif()
            .load(R.raw.gems_animaion)
            .into(binding.animationDdd)


        val roomDatabase = AppDatabase.getInstance(requireContext())
        myActivity = activity as MainActivity
        binding.progressBar.setAnimation(R.raw.main_loading_animation)
        existGems = MySharePreference.getGemsValue(requireContext())
        binding.gemsText.text = existGems.toString()
        loadRecyclerView()
        viewModel = ViewModelProvider(this)[ImageGenerateViewModel::class.java]
        listViewModel = ViewModelProvider(requireActivity())[ImageListViewModel::class.java]
        viewModel.responseData.observe(viewLifecycleOwner) { response ->
            response?.let {
                val timeDisplay = it.eta?.toInt()
                Log.d("imageLists", "time Display: $timeDisplay")
                var data: GetResponseIGEntity? = null
                if(it.output.isNotEmpty()){
                    navigate(it.id!!,timeDisplay)
                    data = GetResponseIGEntity(it.id!!,it.status,it.generationTime,it.output,it.webhook_status, future_links = null,it.meta?.prompt)
                }else if(it.future_links.isNotEmpty()){
                    navigate(it.id!!,timeDisplay)
                    data = GetResponseIGEntity(it.id!!,it.status,it.generationTime, output = null,it.webhook_status,it.future_links,it.meta?.prompt)
                }
                CoroutineScope(Dispatchers.IO).launch {
                    if (data != null) {
                        roomDatabase.getResponseIGDao().insert(data)
                    }
                }
            }
        }

        otherWorking()
        loadCreationHistory(roomDatabase)
        binding.edtPrompt.setBackgroundResource(0)
        binding.clearTextView.setOnClickListener {
            binding.edtPrompt.setText("")
        }
        binding.edtPrompt.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.textLayout.setBackgroundResource(R.drawable.input_field_sel)
                binding.edtPrompt.setBackgroundResource(0)
                binding.clearTextView.setImageResource(R.drawable.cross_white)
            }
        }
    }
    @SuppressLint("SuspiciousIndentation")
    private fun postGems(){
      val totalGems = existGems?.minus(10)
        postDataOnServer.gemsPostData(requireContext(), MySharePreference.getUserID(requireContext())!!,
            RetrofitInstance.getInstance(),totalGems!!, PostDataOnServer.isPlan)
            MySharePreference.setGemsValue(requireContext(),totalGems)
           binding.gemsText.text = totalGems.toString()
    }
    private fun navigate(listId: Int, timeDisplay: Int?){
        if(timeDisplay != null){
            if(timeDisplay>0){
                postGems()
            }
            val bundle = Bundle().apply {
                putInt("listId",listId)
                putInt("timeDisplay", timeDisplay)
            }
            requireParentFragment().findNavController().navigate(R.id.action_mainFragment_to_myViewCreationFragment,bundle)
        }else{
            Toast.makeText(requireContext(), "Error please try again", Toast.LENGTH_SHORT).show()
        }
    }
    private fun loadCreationHistory(database: AppDatabase) {
        val viewModel = ViewModelProvider(this, ViewModelFactory(database,0))[RoomViewModel::class.java]
        viewModel.allGetResponseIG.observe(viewLifecycleOwner){myList->
            if(myList.isNotEmpty()){
                binding.errorTitle.visibility = GONE
            val gridLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.historyRecyclerView.layoutManager = gridLayoutManager
            val adapter  = HistoryAdapter(myList.reversed(),object:GetbackOfID{
                override fun getId(id:Int){
                    navigate(id,0)
                }
            })
            binding.historyRecyclerView.adapter = adapter
            }else{
               binding.historyRecyclerView.visibility = INVISIBLE
               binding.errorTitle.visibility = VISIBLE
            }
        }
    }
    private fun loadRecyclerView() {
        val gridLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.layoutManager = gridLayoutManager
        val adapter = CatListAdapter(listOfPromptCat(),object :GetbackNameOfCat{
            @RequiresApi(Build.VERSION_CODES.N)
            override fun getName(name: String) {
               myActivity.openPopupMenu(name,binding.edtPrompt)
            }
        })
        binding.recyclerView.adapter = adapter
    }
    private fun listOfPromptCat():ArrayList<CatListModelIG>{
        val arrayList = ArrayList<CatListModelIG>()
        arrayList.add(CatListModelIG(R.drawable.nature_min, "Nature"))
        arrayList.add(CatListModelIG(R.drawable.anime_min, "Anime"))
        arrayList.add(CatListModelIG(R.drawable.fantasy_min, "Fantasy"))
        arrayList.add(CatListModelIG(R.drawable.pattern_min, "Pattern"))
        arrayList.add(CatListModelIG(R.drawable.space_min, "Space"))
        arrayList.add(CatListModelIG(R.drawable.super_hero_min, "Super Heroes"))
        arrayList.add(CatListModelIG(R.drawable.art_min, "Art"))
        arrayList.add(CatListModelIG(R.drawable.city2_min, "City & Building"))
        arrayList.add(CatListModelIG(R.drawable.ocean_min, "Ocean"))
        arrayList.add(CatListModelIG(R.drawable.travel2_min, "Travel"))
        arrayList.add(CatListModelIG(R.drawable.love_min, "Love"))
        arrayList.add(CatListModelIG(R.drawable.sadness_min, "Sadness"))
        arrayList.add(CatListModelIG(R.drawable.mountains_min, "Mountain"))
        arrayList.add(CatListModelIG(R.drawable.music_insp, "Music"))
        return arrayList
    }
    private fun otherWorking() {
        binding.generateButton.setOnClickListener {
                if(existGems!! >=10){
                    val getPrompt = binding.edtPrompt.text
                    if(getPrompt.isNotEmpty()){
                        binding.progressBar.visibility = VISIBLE
                        viewModel.loadData(myContext!!,getPrompt.toString(), binding.progressBar)
                    }else{
                        Toast.makeText(requireContext(), "enter your promt", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(requireContext(), "you have no gems to generate this", Toast.LENGTH_SHORT).show()
                }

        }


        binding.seeAllCreations.setOnClickListener {
            findNavController().navigate(R.id.viewAllCreations)
        }
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
        dialog?.findViewById<RelativeLayout>(R.id.closeButton)?.setOnClickListener { dialog?.dismiss() }
        dialog?.findViewById<LinearLayout>(R.id.googleLogin)?.setOnClickListener {
            dialog2 = Dialog(requireContext())
            myDialogs.waiting(dialog2!!)
            dialog?.dismiss()
            login()
        }
        dialog?.show()
    }
    private fun login() {
//        auth = FirebaseAuth.getInstance()
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//        val googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
//        val signInIntent = googleSignInClient.signInIntent
//        startActivityForResult(signInIntent, HomeFragment.RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == HomeFragment.RC_SIGN_IN) {
            dialog2?.dismiss()
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            try {
//                val account = task.getResult(ApiException::class.java)
//                firebaseAuthWithGoogle(account.idToken!!)
//            } catch (e: ApiException) {
//                Toast.makeText(requireContext(), "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
        }
    }
//    private fun firebaseAuthWithGoogle(idToken: String) {
//        val credential = GoogleAuthProvider.getCredential(idToken, null)
//        auth.signInWithCredential(credential)
//            .addOnCompleteListener(requireActivity()){ task ->
//                if (task.isSuccessful) {
//                    Toast.makeText(requireContext(), "Successfully login", Toast.LENGTH_SHORT).show()
//                    val user = auth.currentUser
//                    val  name = user?.displayName
//                    val  email = user?.email
//                    val  image = user?.photoUrl
//                    if(email != null){
//                        googleLogin.fetchGems(requireContext(),email,dialog,binding.gemsText)
//                    }
//                } else {
//                    Toast.makeText(requireContext(), "Authentication failed", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }


}