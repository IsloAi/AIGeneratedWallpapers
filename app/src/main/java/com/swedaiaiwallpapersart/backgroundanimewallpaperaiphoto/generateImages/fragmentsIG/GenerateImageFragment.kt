package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.fragmentsIG

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.analytics.FirebaseAnalytics
import com.ikame.android.sdk.IKSdkController
import com.ikame.android.sdk.tracking.IKTrackingHelper
import com.ikame.android.sdk.widgets.IkmWidgetAdLayout
import com.ikame.android.sdk.widgets.IkmWidgetAdView
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentGenerateImageBinding
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.ImageGenerationDialogBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.remote.EndPointsInterface
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
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.BlurView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.ImageListViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyDialogs
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.PostDataOnServer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GenerateImageFragment : Fragment() {
    private lateinit var binding : FragmentGenerateImageBinding
    private lateinit var viewModel: ImageGenerateViewModel
    private lateinit var listViewModel: ImageListViewModel
    private var myContext :Context? = null
    private var  dialog: Dialog? = null
    private lateinit var myActivity : MainActivity
    private val myDialogs = MyDialogs()
    private var existGems:Int? = null
    private val postDataOnServer = PostDataOnServer()
    private var hasNavigated = false

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    @Inject
    lateinit var endPointsInterface: EndPointsInterface
    private var reviewManager: ReviewManager? = null


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reviewManager = ReviewManagerFactory.create(requireContext())
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

//        IKSdkController.getInstance().loadRewardedAds(requireActivity(), "mainscr_generate_tab_reward")
    }
    private fun customOnCreateCalling() {

        val roomDatabase = AppDatabase.getInstance(requireContext())
        myActivity = activity as MainActivity
        binding.progressBar.setAnimation(R.raw.main_loading_animation)
        existGems = MySharePreference.getGemsValue(requireContext())
        loadRecyclerView()
        viewModel = ViewModelProvider(this)[ImageGenerateViewModel::class.java]



        listViewModel = ViewModelProvider(requireActivity())[ImageListViewModel::class.java]
        viewModel.responseData.observe(viewLifecycleOwner) { response ->
            response?.let {
                var timeDisplay = it.eta?.toInt() ?: 0
                Log.d("imageLists", "time Display: $timeDisplay")

                Log.e("TAG", "customOnCreateCalling: future links empty" )
                val data: GetResponseIGEntity? = it.id?.let { id ->
                    GetResponseIGEntity(
                        id,
                        it.status,
                        it.generationTime,
                        it.output,
                        it.webhook_status,
                        it.future_links,
                        it.meta?.prompt
                    )
                }

                val oldData = data?.id?.let { roomId ->
                    roomDatabase.getResponseIGDao().getCreationsByIdNotLive(roomId)
                }

//                val oldData = roomDatabase.getResponseIGDao().getCreationsByIdNotLive(data?.id!!)
                CoroutineScope(Dispatchers.IO).launch {
                        if (data != null) {

                            if (oldData == null){
                                roomDatabase.getResponseIGDao().insert(data)
                            }

                        }
                    }

                if (timeDisplay == null){
                    timeDisplay =0
                }

                if (it.id != null && !hasNavigated){
                    navigate(it.id!!,timeDisplay)
                    hasNavigated =  true
                }




            }
        }

        otherWorking()
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

    override fun onResume() {
        super.onResume()
        val roomDatabase = AppDatabase.getInstance(requireContext())
        loadCreationHistory(roomDatabase)





        if (isAdded){
            sendTracking("screen_active",Pair("action_type", "Tab"), Pair("action_name", "MainScr_GenAITab_View"))
        }

        if (isAdded){
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Generate Screen")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, javaClass.simpleName)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }
    }




    private fun sendTracking(
        eventName: String,
        vararg param: Pair<String, String?>
    )
    {
        IKTrackingHelper.sendTracking( eventName, *param)
    }


    private fun navigate(listId: Int, timeDisplay: Int?){
        if (isAdded){
            MySharePreference.artGeneratedFirst(requireContext(),true)
        }

            val bundle = Bundle().apply {
                putInt("listId",listId)
                putInt("timeDisplay", timeDisplay!!)
            }
           findNavController().navigate(R.id.myViewCreationFragment,bundle)

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
                    BlurView.genFrom = "main"
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

    fun showDialog(){

        val builder = AlertDialog.Builder(requireContext())
        val binding = ImageGenerationDialogBinding.inflate(layoutInflater)
        builder.setView(binding.root)
        val alertDialog = builder.create()

        alertDialog.setCancelable(false)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        alertDialog.show()

    }
    private fun otherWorking() {
        binding.generateButton.setOnClickListener {
            if (binding.edtPrompt.text.isNotEmpty()){
                BlurView.genFrom = "main"
//                IKSdkController.getInstance().showRewardedAds(requireActivity(),"mainscr_generate_tab_reward","mainscr_generate_tab_reward",object:CustomSDKRewardedAdsListener{
//                    override fun onAdsDismiss() {
//                        Log.e("********ADS", "onAdsDismiss: ", )
//                    }
//
//                    override fun onAdsRewarded() {
//                        Log.e("********ADS", "onAdsRewarded: ", )
//                        val getPrompt = binding.edtPrompt.text
//                        if(getPrompt.isNotEmpty()){
//                            getUserIdDialog()
//
//                            viewModel.loadData(myContext!!,getPrompt.toString() + " highly detailed,studio lighting,professional,vivid colors, cinematic lighting, HDR, UHD, 4K, 8k, 64K", dialog!!)
//                            hasNavigated = false
//                        }else{
//                            Toast.makeText(requireContext(),
//                                getString(R.string.enter_your_prompt), Toast.LENGTH_SHORT).show()
//                        }
//                    }
//
//                    override fun onAdsShowFail(errorCode: Int) {
//
//                        if (isAdded && activity != null) {
//                            if (AdConfig.ISPAIDUSER){
//                                val getPrompt = binding.edtPrompt.text
//                                if(getPrompt.isNotEmpty()){
//                                    getUserIdDialog()
//
//                                    viewModel.loadData(myContext!!,getPrompt.toString()+ " highly detailed,studio lighting,professional,vivid colors,bokeh, cinematic lighting, HDR, UHD, 4K, 8k, 64K", dialog!!)
//                                    hasNavigated = false
//                                }else{
//                                    Toast.makeText(requireContext(),
//                                        getString(R.string.enter_your_prompt), Toast.LENGTH_SHORT).show()
//                                }
//                            }else{
//                                IKSdkController.getInstance().showInterstitialAds(
//                                    requireActivity(),
//                                    "mainscr_generate_tab_reward_inter",
//                                    "mainscr_generate_tab_reward_inter",
//                                    showLoading = true,
//                                    adsListener = object : CommonAdsListenerAdapter() {
//                                        override fun onAdsShowFail(errorCode: Int) {
////                                        val getPrompt = binding.edtPrompt.text
////                                        if(getPrompt.isNotEmpty()){
////                                            getUserIdDialog()
////                                            viewModel.loadData(myContext!!,getPrompt.toString(), dialog!!)
////                                        }else{
////                                            Toast.makeText(requireContext(),
////                                                getString(R.string.enter_your_prompt), Toast.LENGTH_SHORT).show()
////                                        }
//
//
//                                            Toast.makeText(
//                                                requireContext(),
//                                                "Ad not available,Please try again...",
//                                                Toast.LENGTH_SHORT
//                                            ).show()
//                                            Log.e("TAG", "onAdsShowFail: inter",)
//                                        }
//
//                                        override fun onAdsDismiss() {
//                                            if (isAdded && activity != null) {
//                                                val getPrompt = binding.edtPrompt.text
//                                                if (getPrompt.isNotEmpty()) {
//                                                    getUserIdDialog()
//                                                    viewModel.loadData(
//                                                        requireContext(),
//                                                        getPrompt.toString()+ " highly detailed,studio lighting,professional,vivid colors,bokeh, cinematic lighting, HDR, UHD, 4K, 8k, 64K",
//                                                        dialog!!
//                                                    )
//                                                } else {
//                                                    Toast.makeText(
//                                                        requireContext(),
//                                                        getString(R.string.enter_your_prompt),
//                                                        Toast.LENGTH_SHORT
//                                                    ).show()
//                                                }
//                                            }
//                                        }
//                                    }
//                                )
//                            }
//
//                        }else{
//                            Log.e("TAG", "Fragment not attached to an activity in onAdsShowFail")
//                        }
//                        Log.e("********ADS", "onAdsShowFail: ", )
//
//                    }
//
//                })
            } else{
                Toast.makeText(requireContext(), getString(R.string.enter_your_prompt), Toast.LENGTH_SHORT).show()

            }



        }


        binding.seeAllCreations.setOnClickListener {
            BlurView.genFrom = "all"
            findNavController().navigate(R.id.viewAllCreations)
        }
    }
    var textIndex = 0
    fun getLoadingTexts(): List<String> {
        return listOf(
            binding.root.context.getString(R.string.hold_tight_your_masterpiece_is_rendering),
            binding.root.context.getString(R.string.loading_the_beauty_just_for_you),
            binding.root.context.getString(R.string.your_wallpaper_is_in_the_making),
            binding.root.context.getString(R.string.creating_your_visual_delight),
            binding.root.context.getString(R.string.sit_back_and_relax_while_we_prepare_your_wallpaper),
            binding.root.context.getString(R.string.designing_your_screen_s_new_look),
            binding.root.context.getString(R.string.almost_there_customizing_your_background),
            binding.root.context.getString(R.string.your_wallpaper_is_on_its_way),
            binding.root.context.getString(R.string.crafting_your_unique_backdrop)
        )
    }
    private fun getUserIdDialog() {
        dialog = Dialog(requireContext())
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.image_generation_dialog)
        val width = WindowManager.LayoutParams.MATCH_PARENT
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog?.window!!.setLayout(width, height)
        dialog?.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCancelable(false)
        val adsView = dialog?.findViewById<IkmWidgetAdView>(R.id.adsView)

//        val adLayout = LayoutInflater.from(dialog?.context).inflate(
//            R.layout.native_dialog_layout,
//            null, false
//        ) as? IkmWidgetAdLayout
//        adLayout?.titleView = adLayout?.findViewById(R.id.custom_headline)
//        adLayout?.bodyView = adLayout?.findViewById(R.id.custom_body)
//        adLayout?.callToActionView = adLayout?.findViewById(R.id.custom_call_to_action)
//        adLayout?.iconView = adLayout?.findViewById(R.id.custom_app_icon)
//        adLayout?.mediaView = adLayout?.findViewById(R.id.custom_media)
//        adsView?.setCustomNativeAdLayout(
//            R.layout.shimmer_loading_native,
//            adLayout!!
//        )
//
//        adsView?.loadAd(requireActivity(),
//            "generate_renderdialog_bottom",
//            "generate_renderdialog_bottom",
//            object : CustomSDKAdsListenerAdapter() {
//                override fun onAdsLoadFail() {
//                    super.onAdsLoadFail()
//                    Log.e("**********ADS", "onAdsLoadFail: ", )
//                }
//
//                override fun onAdsLoaded() {
//                    super.onAdsLoaded()
//                    Log.e("**********ADS", "onAdsLoaded: ", )
//                }
//            }
//
//        )



        val image = dialog?.findViewById<ImageView>(R.id.generationAnimation)

        Glide.with(dialog?.context!!)
            .asGif()
            .load(R.raw.generation_animation)
            .into(image!!)

        val animatedText = dialog?.findViewById<TextView>(R.id.animated_text)

        val animation = AnimationUtils.loadAnimation(dialog?.context, android.R.anim.fade_in)
        animation.duration = 1000
        // Initial update
        updateTextAndAnimate(animatedText!!,animation)

        CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                delay(3000)
                updateTextAndAnimate(animatedText!!,animation)
            }
        }

        dialog?.show()
    }

    fun updateTextAndAnimate(animatedText:TextView,animation: Animation) {
        val texts = getLoadingTexts()
        animatedText?.text = texts[textIndex]
        animatedText?.startAnimation(animation)
        textIndex = (textIndex + 1) % texts.size
    }

}