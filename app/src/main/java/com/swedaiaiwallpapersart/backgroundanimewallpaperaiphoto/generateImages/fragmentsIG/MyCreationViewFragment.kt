package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.fragmentsIG

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.CountDownTimer
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
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.CommonAdsListenerAdapter
import com.bmik.android.sdk.listener.CustomSDKAdsListenerAdapter
import com.bmik.android.sdk.listener.CustomSDKRewardedAdsListener
import com.bmik.android.sdk.widgets.IkmWidgetAdLayout
import com.bmik.android.sdk.widgets.IkmWidgetAdView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.gson.Gson
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.debug.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.debug.databinding.FragmentMyCreationViewBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.FavouriteListIGEntity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.GetResponseIGEntity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.RoomViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.ViewModelFactory
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.utilsIG.ImageGenerateViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.DummyFavorite
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.ImageListViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class MyCreationViewFragment : Fragment() {
    private var _binding: FragmentMyCreationViewBinding?= null
    private val binding get() = _binding!!

    private var bindingRef: WeakReference<FragmentMyCreationViewBinding>? = null

    private var timer:CountDownTimer ?= null

    private var myContext: Context?= null
    private lateinit var listViewModel: ImageListViewModel
    private var timeDisplay = 0
    private lateinit var  viewModel:RoomViewModel
    private var myId = 0
    private var myActivity : MainActivity? = null
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var getLargImage: String = ""

    private var  dialog: Dialog? = null

    private var favouriteListIGEntity : ArrayList<FavouriteListIGEntity>? = ArrayList()
    private var imagesList:ArrayList<DummyFavorite> = ArrayList()

    private lateinit var imageGenerateViewModel: ImageGenerateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val roomDatabase = AppDatabase.getInstance(requireContext())
        listViewModel = ViewModelProvider(requireActivity())[ImageListViewModel::class.java]
        myId = arguments?.getInt("listId")!!
        val  getTime= arguments?.getInt("timeDisplay")!!
        viewModel = ViewModelProvider(this,ViewModelFactory(roomDatabase,myId))[RoomViewModel::class.java]
        imageGenerateViewModel = ViewModelProvider(this)[ImageGenerateViewModel::class.java]
        if(getTime>0){
            timeDisplay = getTime+5
        }else{
            timeDisplay = 0
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View{
        // Inflate the layout for this fragment
        _binding = FragmentMyCreationViewBinding.inflate(inflater,container,false)
        bindingRef = WeakReference(_binding)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SDKBaseController.getInstance()
            .loadBannerAds(
                requireActivity(),
                binding.adsWidget as? ViewGroup,
                "createdwallscr_bottom",
                " createdwallscr_bottom", object : CustomSDKAdsListenerAdapter() {
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

        binding.editPrompt.setOnClickListener {
            binding.prompt.isEnabled = true
            binding.prompt.isFocusable = true
            binding.prompt.isFocusableInTouchMode = true
            binding.prompt.requestFocus()
            val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(binding.prompt, InputMethodManager.SHOW_IMPLICIT)

        }
        if(myContext!= null){
            onCreateCalling()
            initObservers()

        }


        Glide.with(requireContext())
            .asGif()
            .load(R.raw.gems_animaion)
            .into(binding.animationDdd)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context
    }
    private fun onCreateCalling(){
        myActivity = activity as MainActivity
        swipeRefreshLayout = binding.swipeLayout
        binding.gemsText.text = MySharePreference.getGemsValue(requireContext()).toString()
        val clipboardManager = myContext?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        binding.copyButton.visibility = INVISIBLE
        if(timeDisplay==0){
            binding.notificationLayout.visibility = GONE
            binding.copyButton.visibility = VISIBLE
        }
        val time = (timeDisplay*1000)
        binding.notificationMessage.text = getString(R.string.estimated_time_to_load)
        startCountdown(timeDisplay,binding.textCounter)
        loadDate()

        lifecycleScope.launch(Dispatchers.Main) {

            delay(time.toLong())
            val binding = bindingRef?.get()
            binding?.copyButton?.visibility = VISIBLE
        }
        binding.backButton.setOnClickListener { findNavController().navigateUp()}
        swipeRefreshLayout.setOnRefreshListener {
            loadDate()
        }

        binding.addToFav1.setOnClickListener {
            getLargImage = imagesList[0].url
            if(favouriteListIGEntity!!.isEmpty()){
                addFavouriteList(myId!!,binding.prompt.text.toString()!!)
                imagesList[0].isFavorite = true
                binding.fav1.setImageResource(R.drawable.heart_red)
            }else{
                val image = favouriteListIGEntity!!.any {it.image == getLargImage}
                if(!image){
                    addFavouriteList(myId!!,binding.prompt.text.toString()!!)
                    binding.fav1.setImageResource(R.drawable.heart_red)
                    imagesList[0].isFavorite = true
                }
                else{
                    viewModel.deleteItem(getLargImage)
                    binding.fav1.setImageResource(R.drawable.heart_unsel)
                    imagesList[0].isFavorite = false
                }
            }
        }


        binding.addToFav2.setOnClickListener {
            getLargImage = imagesList[1].url
            if(favouriteListIGEntity!!.isEmpty()){
                addFavouriteList(myId!!,binding.prompt.text.toString()!!)
                imagesList[1].isFavorite = true
                binding.fav2.setImageResource(R.drawable.heart_red)
            }else{
                val image = favouriteListIGEntity!!.any {it.image == getLargImage}
                if(!image){
                    addFavouriteList(myId!!,binding.prompt.text.toString()!!)
                    binding.fav2.setImageResource(R.drawable.heart_red)
                    imagesList[1].isFavorite = true
                }
                else{
                    viewModel.deleteItem(getLargImage)
                    binding.fav2.setImageResource(R.drawable.heart_unsel)
                    imagesList[1].isFavorite = false
                }
            }
        }


        binding.addToFav3.setOnClickListener {
            getLargImage = imagesList[2].url
            if(favouriteListIGEntity!!.isEmpty()){
                addFavouriteList(myId!!,binding.prompt.text.toString()!!)
                imagesList[2].isFavorite = true
                binding.fav3.setImageResource(R.drawable.heart_red)
            }else{
                val image = favouriteListIGEntity!!.any {it.image == getLargImage}
                if(!image){
                    addFavouriteList(myId!!,binding.prompt.text.toString()!!)
                    binding.fav3.setImageResource(R.drawable.heart_red)
                    imagesList[2].isFavorite = true
                }
                else{
                    viewModel.deleteItem(getLargImage)
                    binding.fav3.setImageResource(R.drawable.heart_unsel)
                    imagesList[2].isFavorite = false
                }
            }
        }

        binding.addToFav4.setOnClickListener {
            getLargImage = imagesList[3].url
            if(favouriteListIGEntity!!.isEmpty()){
                addFavouriteList(myId!!,binding.prompt.text.toString()!!)
                imagesList[3].isFavorite = true
                binding.fav4.setImageResource(R.drawable.heart_red)
            }else{
                val image = favouriteListIGEntity!!.any {it.image == getLargImage}
                if(!image){
                    addFavouriteList(myId!!,binding.prompt.text.toString()!!)
                    binding.fav4.setImageResource(R.drawable.heart_red)
                    imagesList[3].isFavorite = true
                }
                else{
                    viewModel.deleteItem(getLargImage)
                    binding.fav4.setImageResource(R.drawable.heart_unsel)
                    imagesList[3].isFavorite = true
                }
            }
        }


        binding.copyButton.setOnClickListener {
            val textToCopy = binding.prompt.text.toString()
            val clip = ClipData.newPlainText("Copied Text", textToCopy)
            clipboardManager.setPrimaryClip(clip)
            Toast.makeText(myContext,
                getString(R.string.text_copied_to_clipboard), Toast.LENGTH_SHORT).show()
        }

        binding.reGenerateBtn.setOnClickListener {
            SDKBaseController.getInstance().showRewardedAds(requireActivity(),"createdwallscr_regen_click","createdwallscr_regen_click",object:
                CustomSDKRewardedAdsListener {
                override fun onAdsDismiss() {
                    Log.e("********ADS", "onAdsDismiss: ", )
                }

                override fun onAdsRewarded() {
                    Log.e("********ADS", "onAdsRewarded: ", )
                    val getPrompt = binding.prompt.text
                    if(getPrompt.isNotEmpty()){

                        getUserIdDialog()
                        imageGenerateViewModel.loadData(myContext!!,getPrompt.toString(), dialog!!)
                    }else{
                        Toast.makeText(requireContext(),
                            getString(R.string.enter_your_prompt), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onAdsShowFail(errorCode: Int) {
                    Log.e("********ADS", "onAdsShowFail: ", )
                    val getPrompt = binding.prompt.text
                    if(getPrompt.isNotEmpty()){

                        getUserIdDialog()
                        imageGenerateViewModel.loadData(myContext!!,getPrompt.toString(), dialog!!)
                    }else{
                        Toast.makeText(requireContext(), getString(R.string.enter_your_prompt), Toast.LENGTH_SHORT).show()
                    }
                }

            })
        }

        binding.newGenerate.setOnClickListener {
            SDKBaseController.getInstance().showInterstitialAds(
                requireActivity(),
                "createdwallscr_newgen_click",
                "createdwallscr_newgen_click",
                showLoading = true,
                adsListener = object : CommonAdsListenerAdapter() {
                    override fun onAdsShowFail(errorCode: Int) {
                        Log.e("********ADS", "onAdsShowFail: "+errorCode )
                        findNavController().navigateUp()
                        //do something
                    }

                    override fun onAdsDismiss() {
                        findNavController().navigateUp()
                    }
                }
            )
        }
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

        val adLayout = LayoutInflater.from(dialog?.context).inflate(
            R.layout.layout_custom_admob,
            null, false
        ) as? IkmWidgetAdLayout
        adLayout?.titleView = adLayout?.findViewById(R.id.custom_headline)
        adLayout?.bodyView = adLayout?.findViewById(R.id.custom_body)
        adLayout?.callToActionView = adLayout?.findViewById(R.id.custom_call_to_action)
        adLayout?.iconView = adLayout?.findViewById(R.id.custom_app_icon)
        adLayout?.mediaView = adLayout?.findViewById(R.id.custom_media)
        adsView?.setCustomNativeAdLayout(
            R.layout.shimmer_loading_native,
            adLayout!!
        )

        adsView?.loadAd(requireActivity(),
            "generate_renderdialog_bottom",
            "generate_renderdialog_bottom",
            object : CustomSDKAdsListenerAdapter() {
                override fun onAdsLoadFail() {
                    super.onAdsLoadFail()
                    Log.e("**********ADS", "onAdsLoadFail: ", )
                }

                override fun onAdsLoaded() {
                    super.onAdsLoaded()
                    Log.e("**********ADS", "onAdsLoaded: ", )
                }
            }

        )



        val image = dialog?.findViewById<ImageView>(R.id.generationAnimation)

        Glide.with(dialog?.context!!)
            .asGif()
            .load(R.raw.generation_animation)
            .into(image!!)

        val animatedText = dialog?.findViewById<TextView>(R.id.animated_text)

        val animation = AnimationUtils.loadAnimation(dialog?.context, android.R.anim.fade_in)
        animation.duration = 1000
        // Initial update
        updateTextAndAnimate(animatedText!!,animation, dialog?.context!!)

        CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                delay(3000)
                updateTextAndAnimate(animatedText!!,animation,dialog?.context!!)
            }
        }

        dialog?.show()
    }

    var textIndex = 0

    fun updateTextAndAnimate(animatedText:TextView,animation: Animation,context: Context) {
        val texts = listOf(
            context.getString(R.string.hold_tight_your_masterpiece_is_rendering),
            context.getString(R.string.loading_the_beauty_just_for_you),
            context.getString(R.string.your_wallpaper_is_in_the_making),
            context.getString(R.string.creating_your_visual_delight),
            context.getString(R.string.sit_back_and_relax_while_we_prepare_your_wallpaper),
            context.getString(R.string.designing_your_screen_s_new_look),
            context.getString(R.string.almost_there_customizing_your_background),
            context.getString(R.string.your_wallpaper_is_on_its_way),
            context.getString(R.string.crafting_your_unique_backdrop)
        )
        animatedText?.text = texts[textIndex]
        animatedText?.startAnimation(animation)
        textIndex = (textIndex + 1) % texts.size
    }

    private fun addFavouriteList(myId:Int,prompt:String){
        val data = FavouriteListIGEntity(imageId = myId, image = getLargImage, prompt = prompt)
        viewModel.insertFavourite(data)
//        binding.addToFav1.setImageResource(R.drawable.heart_red)
    }

    private fun loadDate(){
            viewModel.getResponseIGById.observe(viewLifecycleOwner){
                it.let {
                    Log.e("TAG", "loadDate: "+it )
                    binding.prompt.setText(it?.prompt)
                    if(it?.output?.size!! >= 3){
                        Log.d("imageLists", "future_links is Empty")
                        imagesList.clear()
                        for (i in 0 until it.output!!.size){
                            imagesList.add(DummyFavorite(it.output!![i],false))

                        }

                        setImage(it?.output!!,it.prompt,myId)


                        checkfavorites()
                    }
                }

            }

        swipeRefreshLayout.isRefreshing = false
    }

    private fun updateFavoriteIcons() {
        binding.fav1.setImageResource(getFavoriteIcon(imagesList[0].isFavorite))
        binding.fav2.setImageResource(getFavoriteIcon(imagesList[1].isFavorite))
        binding.fav3.setImageResource(getFavoriteIcon(imagesList[2].isFavorite))
        binding.fav4.setImageResource(getFavoriteIcon(imagesList[3].isFavorite))
    }

    fun checkfavorites(){
        viewModel.myFavouriteList.observe(viewLifecycleOwner) { list ->
            val favoriteUrls = list.map { it.image }

            imagesList.forEach { image ->
                image.isFavorite = favoriteUrls.contains(image.url)
            }

            updateFavoriteIcons()
        }
    }


    private fun getFavoriteIcon(isFavorited: Boolean): Int {
        return if (isFavorited) {
            R.drawable.heart_red
        } else {
            R.drawable.heart_unsel
        }
    }
    fun initObservers(){
        val roomDatabase = AppDatabase.getInstance(requireContext())
        imageGenerateViewModel.responseData.observe(viewLifecycleOwner) { response ->
            response?.let {
                val timeDisplay = it.eta?.toInt()
                Log.d("imageLists", "time Display: $timeDisplay")
                var data: GetResponseIGEntity? = null
                if(it.output.isNotEmpty()){
                    refreshFragment(it.id!!,timeDisplay)
                    data = GetResponseIGEntity(it.id!!,it.status,it.generationTime,it.output,it.webhook_status, future_links = null,it.meta?.prompt)
                }else if(it.future_links.isNotEmpty()){
                    refreshFragment(it.id!!,timeDisplay)
                    data = GetResponseIGEntity(it.id!!,it.status,it.generationTime, output = null,it.webhook_status,it.future_links,it.meta?.prompt)
                }
                CoroutineScope(Dispatchers.IO).launch {
                    if (data != null) {
                        roomDatabase.getResponseIGDao().insert(data)
                    }
                }
            }
        }
    }

    private fun refreshFragment(listId: Int, timeDisplay: Int?){
        if(timeDisplay != null){
            val bundle = Bundle().apply {
                putInt("listId",listId)
                putInt("timeDisplay", timeDisplay)
            }
            findNavController().popBackStack(R.id.myViewCreationFragment, true)

            findNavController().navigate(R.id.myViewCreationFragment,bundle)
        }else{
            Toast.makeText(requireContext(), "Error please try again", Toast.LENGTH_SHORT).show()
        }
    }
    private fun setImage(list: ArrayList<String>, prompt: String?, id: Int){
        Glide.with(requireContext())
            .load(list[0])
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    // Handle the case where the image failed to load
                    return false // Return false to allow Glide to handle the failure event
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.editPrompt.visibility = View.VISIBLE
                    binding.loading1?.visibility = GONE
                    binding.loading2?.visibility = GONE
                    binding.loading3?.visibility = GONE
                    binding.loading4?.visibility = GONE
                    binding.notificationLayout.visibility = View.GONE
                    binding.btns.visibility = View.VISIBLE
                    binding.addToFav1.visibility = VISIBLE
                    binding.addToFav2.visibility = VISIBLE
                    binding.addToFav3.visibility = VISIBLE
                    binding.addToFav4.visibility = VISIBLE
                    return false // Return false to allow Glide to handle the resource ready event
                }
            })
            .into(binding.imageView1)

        Glide.with(myContext!!).load(list[1]).into(binding.imageView2)
        Glide.with(myContext!!).load(list[2]).into(binding.imageView3)
        Glide.with(myContext!!).load(list[3]).into(binding.imageView4)
        binding.imageView1.setOnClickListener {navigate(0, list, prompt, id)}
        binding.imageView2.setOnClickListener {navigate(1, list, prompt, id)}
        binding.imageView3.setOnClickListener {navigate(2, list, prompt, id)}
        binding.imageView4.setOnClickListener {navigate(3, list, prompt, id)}
    }
    private fun navigate(click: Int, list: ArrayList<String>, prompt: String?, id: Int){
        timeDisplay = 0
        val gson = Gson()
        val arrayListJson = gson.toJson(list)
       val bundle =  Bundle().apply {
            putString("arrayListJson",arrayListJson)
            putInt("position",click)
           putString("prompt",prompt)
           putInt("id",id)
        }
        findNavController().navigate(R.id.action_myViewCreationFragment_to_creationSliderViewFragment,bundle)
    }
    private fun startCountdown(initialSeconds: Int,textView:TextView) {
      timer = object : CountDownTimer((initialSeconds * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Update the UI with the remaining seconds
                val binding = bindingRef?.get()
                val remainingSeconds = (millisUntilFinished / 1000).toInt()
                if (binding != null){
                    textView.text = "$remainingSeconds sec"

                }
            }
            override fun onFinish() {
                val binding = bindingRef?.get()
                if (binding!=null){
                    textView.text = ""
                    binding.notificationMessage.text =
                        getString(R.string.if_still_not_loaded_then_swipe_down_to_refresh_after_few_second)
                }

            }
        }
        timer?.start()


    }





    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        _binding = null
    }


}