package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.text.TextPaint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.core.os.BuildCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.ikame.android.sdk.widgets.IkmWidgetAdLayout
import com.google.firebase.analytics.FirebaseAnalytics
import com.ikame.android.sdk.IKSdkController
import com.ikame.android.sdk.data.dto.pub.IKAdError
import com.ikame.android.sdk.listener.pub.IKLoadAdListener
import com.ikame.android.sdk.listener.pub.IKShowWidgetAdListener
import com.ikame.android.sdk.tracking.IKTrackingHelper
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentLocalizationBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.LocalizationAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.SplashOnFragment.Companion.exit
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.models.DummyModelLanguages
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.LocaleManager
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.RvItemDecore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class LocalizationFragment : Fragment() {
    private var _binding: FragmentLocalizationBinding?= null
    private val binding get() = _binding!!

    var selectedItem: DummyModelLanguages? = null

    var sel: String = "en"
    var selected = -1

    var posnew = 0
    var adapter: LocalizationAdapter? = null

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    var adnext = false
    val TAG = "Localization"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLocalizationBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        posnew = MySharePreference.getLanguageposition(requireContext())!!

        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        if (AdConfig.ISPAIDUSER){
            binding.adsView.visibility = View.GONE
        }
        if (isAdded){
            sendTracking("screen_active",Pair("action_type", "screen"), Pair("action_name", "LanguageScr_View"))
        }

        lifecycleScope.launch {
            delay(2000)
            Log.e(TAG, "getLanguageList: "+getDefaultLocaleInfo())
        }

        setGradienttext()

        setEvents()
        initLanguages()
        backHandle()
    }

    private fun setGradienttext(){
        val customColors = intArrayOf(
            Color.parseColor("#FC9502"),
            Color.parseColor("#FF6726")
        )
        val paint: TextPaint = binding.applyLanguage.paint
        val width: Float = paint.measureText("Next")

        val shader = LinearGradient(
            0f, 0f, width, binding.applyLanguage.textSize,
            customColors, null, Shader.TileMode.CLAMP
        )
        binding.applyLanguage.paint.shader = shader
    }

    private fun sendTracking(
        eventName: String,
        vararg param: Pair<String, String?>
    )
    {
        IKTrackingHelper.sendTracking( eventName, *param)
    }

    fun loadNativeAd(){
        binding.adsView.attachLifecycle(this.lifecycle)
        val adLayout = LayoutInflater.from(activity).inflate(
            R.layout.new_native_language,
            null, false
        ) as? IkmWidgetAdLayout
        adLayout?.titleView = adLayout?.findViewById(R.id.custom_headline)
        adLayout?.bodyView = adLayout?.findViewById(R.id.custom_body)
        adLayout?.callToActionView = adLayout?.findViewById(R.id.custom_call_to_action)
        adLayout?.iconView = adLayout?.findViewById(R.id.custom_app_icon)
        adLayout?.mediaView = adLayout?.findViewById(R.id.custom_media)
        binding.adsView.loadAd(R.layout.shimmer_loading_native, adLayout!!,"languagescr_bottom",
            object : IKShowWidgetAdListener {
                override fun onAdShowFail(error: IKAdError) {
                    binding.adsView.visibility = View.GONE
                }

                override fun onAdShowed() {

                }
            }
        )
    }

//    fun loadNextAd(){
//        val adLayout = LayoutInflater.from(activity).inflate(
//            R.layout.new_native_language,
//            null, false
//        ) as? IkmWidgetAdLayout
//        adLayout?.titleView = adLayout?.findViewById(R.id.custom_headline)
//        adLayout?.bodyView = adLayout?.findViewById(R.id.custom_body)
//        adLayout?.callToActionView = adLayout?.findViewById(R.id.custom_call_to_action)
//        adLayout?.iconView = adLayout?.findViewById(R.id.custom_app_icon)
//        adLayout?.mediaView = adLayout?.findViewById(R.id.custom_media)
//
//        binding.adsView.setCustomNativeAdLayout(
//            R.layout.shimmer_loading_native,
//            adLayout!!
//        )
//
//        binding.adsView.loadAd(requireActivity(),"languagescr_bottom","languagescr_bottom",
//            object : CustomSDKAdsListenerAdapter() {
//                override fun onAdsLoadFail() {
//                    super.onAdsLoadFail()
//                    if (AdConfig.ISPAIDUSER){
//                        binding.adsView.visibility = View.GONE
//                    }
//                    Log.e("TAG", "onAdsLoadFail: native failded " )
//                }
//
//                override fun onAdsLoaded() {
//                    super.onAdsLoaded()
//                    Log.e("TAG", "onAdsLoaded: native loaded" )
//                }
//            }
//        )
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        if (AdConfig.ISPAIDUSER){
            binding.adsView.visibility = View.GONE
        }else{
            binding.adsView.visibility = View.VISIBLE
            loadNativeAd()
        }

        if (isAdded){
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Language Screen")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, javaClass.simpleName)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }
    }

    fun initLanguages(){
        binding.rvLanguages.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvLanguages.addItemDecoration(RvItemDecore(2,20,false,10000))

        sel = getDefaultLocaleInfo()
        val list = getLanguageList(sel)


        Log.e(TAG, "initLanguages: "+AdConfig.languagesOrder )
        val sortedList = sortLanguages(list, AdConfig.languagesOrder)


        val arrayList: ArrayList<DummyModelLanguages> = sortedList.toCollection(ArrayList())
        adapter =
            LocalizationAdapter(arrayList,  getSelectedLanguagePosition(arrayList), object :
                LocalizationAdapter.OnLanguageChangeListener {

                override fun onLanguageItemClick(language: DummyModelLanguages?, position: Int) {

                    if (AdConfig.languageLogicShowNative == 1 && !AdConfig.ISPAIDUSER){
                        if (!adnext){
                            adnext = true
                            loadNativeAd()
                        }
                    }

                    selectedItem = language
                    selected = position
                }
            })

        binding.rvLanguages.adapter = adapter
    }


    fun getDefaultLocaleInfo(): String {
        val locale = Locale.getDefault()
        val name = locale.displayName
        Log.e(TAG, "getLanguageList: $name")
        val language = locale.language
        Log.e(TAG, "getLanguageList: $language" )
        return language
    }

    fun getSelectedLanguagePosition(sortedLanguages: ArrayList<DummyModelLanguages>): Int {
        return sortedLanguages.indexOfFirst { it.isSelected_lan }
    }

    fun getLanguageList(pos:String): ArrayList<DummyModelLanguages> {
        val languagesList = ArrayList<DummyModelLanguages>()
        languagesList.add(DummyModelLanguages("German", "de", R.drawable.flag_gr, false))
        languagesList.add(DummyModelLanguages("Japanese ", "ja", R.drawable.flag_japan, false))
        languagesList.add(DummyModelLanguages("Chinese", "zh", R.drawable.flag_cn, false))
        languagesList.add(DummyModelLanguages("Italian", "it", R.drawable.flag_itly, false))
        languagesList.add(DummyModelLanguages("Russian", "ru", R.drawable.flag_russia, false))
        languagesList.add(DummyModelLanguages("English (US)", "en", R.drawable.flag_en, false))
        languagesList.add(DummyModelLanguages("Korean", "ko", R.drawable.flag_korean, false))
        languagesList.add(DummyModelLanguages("Portuguese", "pt", R.drawable.flag_pr, false))
        languagesList.add(DummyModelLanguages("Spanish", "es", R.drawable.flag_sp, false))
        languagesList.add(DummyModelLanguages("Arabic", "ar", R.drawable.flag_ar, false))
        languagesList.add(DummyModelLanguages("English (UK)", "en-rGB", R.drawable.flag_uk, false))
        languagesList.add(DummyModelLanguages("French", "fr", R.drawable.flag_fr, false))
        languagesList.add(DummyModelLanguages("Thai", "th", R.drawable.flag_thai, false))
        languagesList.add(DummyModelLanguages("Turkish", "tr", R.drawable.flag_tr, false))
        languagesList.add(DummyModelLanguages("Vietnamese ", "vi", R.drawable.flag_vietnamese, false))
        languagesList.add(DummyModelLanguages("Hindi", "hi", R.drawable.flag_hi, false))
        languagesList.add(DummyModelLanguages("Dutch", "nl", R.drawable.flag_ducth, false))
        languagesList.add(DummyModelLanguages("Indonesian", "in", R.drawable.flag_indona, false))
        for (item in languagesList) {
            if (item.lan_code == pos) {
                item.isSelected_lan = true
                break
            }
        }

        if (posnew == 0 && pos.isNotEmpty()) {
            posnew = languagesList.indexOfFirst { it.lan_code == pos }
            if (posnew == -1) {
                posnew = 0 // Default to the first language if the device language is not found
            }
        }
        Log.e(TAG, "getLanguageList: $posnew")
        return languagesList
    }

    fun sortLanguages(languages: ArrayList<DummyModelLanguages>, order: List<String>): List<DummyModelLanguages> {
        val orderMap = order.withIndex().associate { it.value.trim() to it.index }

        // Sort the languages based on the order specified in the map
        return languages.sortedWith(compareBy { orderMap[it.lan_name.trim()] ?: Int.MAX_VALUE })
    }

    private fun setEvents() {
        val onBoard = MySharePreference.getOnboarding(requireContext())

        if (!onBoard){
            IKSdkController.preloadNativeAd("onboardscr_bottom", object : IKLoadAdListener {
                override fun onAdLoaded() {
                    // Ad loaded successfully
                }

                override fun onAdLoadFail(error: IKAdError) {
                    Log.e(TAG, "onAdLoadFail: ")
                }
            })
        }
//        binding.backButton.setOnClickListener {
//            if (exit){
//                requireActivity().finishAffinity()
//            }else{
//                    findNavController().navigateUp()
//
//            }
//
//        }


        binding.applyLanguage.setOnClickListener {

            if (isAdded){
                sendTracking("click_button",Pair("action_type", "button"), Pair("action_name", "LanguageScr_Next_Click"))

            }

            if (selectedItem != null) {
                if (isAdded){
                    sendTracking("language_selected",Pair("language", selectedItem?.lan_name))
                }
                MySharePreference.setLanguage(requireContext(),selectedItem!!.lan_code)
                MySharePreference.setLanguageposition(requireContext(),selected)
                val context = LocaleManager.setLocale(requireContext(), selectedItem!!.lan_code)
                val resources = context.resources
                val newLocale = Locale(selectedItem!!.lan_code)
                val resources1 = getResources()
                val configuration = resources1.configuration
                configuration.setLocale(newLocale)
                configuration.setLayoutDirection(Locale(selectedItem!!.lan_code));
                resources1.updateConfiguration(configuration, resources.displayMetrics)
                if (exit){
                    Log.e(TAG, "setEvents:  exit true", )

                    if (onBoard){
                        findNavController().navigate(R.id.homeTabsFragment)
                    }else{
                        if (AdConfig.showOnboarding){
                            Log.e(TAG, "setEvents:  exit true, Adconfig.showonboarding true", )
                            findNavController().navigate(R.id.onBoardingFragment)
                        }else{
                            Log.e(TAG, "setEvents:  exit true, Adconfig.showonboarding false", )
                            findNavController().navigate(R.id.homeTabsFragment)
                        }
                    }

                }else{
                    Log.e(TAG, "setEvents:  exit false", )
                    if (!onBoard){
                        Log.e(TAG, "setEvents:  exit false, onboard false", )
                        if (AdConfig.showOnboarding){
                            Log.e(TAG, "setEvents:  exit false, onboard false,Adconfig.showonbaording true", )
                            findNavController().navigate(R.id.onBoardingFragment)
                        }else{
                            Log.e(TAG, "setEvents:  exit false, onboard false,Adconfig.showonbaording false", )

                            findNavController().navigate(R.id.homeTabsFragment)
                        }


                    }else{
                        Log.e(TAG, "setEvents:  exit false, onboard true", )
                        findNavController().navigateUp()
                    }
                }




            } else {

                if (isAdded){
                    sendTracking("language_selected",Pair("language", "English"))
                }

                MySharePreference.setLanguage(requireContext(),"en")
                MySharePreference.setLanguageposition(requireContext(),0)
                val context = LocaleManager.setLocale(requireContext(), "en")
                val resources = context.resources
                val newLocale = Locale("en")
                val resources1 = getResources()
                val configuration = resources1.configuration
                configuration.setLocale(newLocale)
                configuration.setLayoutDirection(Locale("en"));

                requireActivity().window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR

                resources1.updateConfiguration(configuration, resources.displayMetrics)

                if (exit){
                    Log.e(TAG, "setEvents:  exit true", )
                    if (onBoard){
                        findNavController().navigate(R.id.homeTabsFragment)
                    }else{
                        if (AdConfig.showOnboarding){
                            Log.e(TAG, "setEvents:  exit true, Adconfig.showonboarding true", )
                            findNavController().navigate(R.id.onBoardingFragment)
                        }else{
                            Log.e(TAG, "setEvents:  exit true, Adconfig.showonboarding false", )
                            findNavController().navigate(R.id.homeTabsFragment)
                        }
                    }
                }else{
                    Log.e(TAG, "setEvents:  exit false", )
                    if (!onBoard){
                        Log.e(TAG, "setEvents:  exit false, onboard false", )
                        if (AdConfig.showOnboarding){
                            Log.e(TAG, "setEvents:  exit false, onboard false,Adconfig.showonbaording true", )
                            findNavController().navigate(R.id.onBoardingFragment)
                        }else{
                            Log.e(TAG, "setEvents:  exit false, onboard false,Adconfig.showonbaording false", )

                            findNavController().navigate(R.id.homeTabsFragment)
                        }


                    }else{
                        Log.e(TAG, "setEvents:  exit false, onboard true", )
                        findNavController().navigateUp()
                    }
                }

            }
        }
    }


    private fun backHandle(){
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isAdded){
                    sendTracking("click_button",Pair("action_type", "button"), Pair("action_name", "Sytem_BackButton_Click"))
                }
                if (exit){
                    findNavController().navigate(R.id.onBoardingFragment)
                }else{
                    findNavController().navigateUp()
                }
            }
        })

        if (BuildCompat.isAtLeastT()) {
            requireActivity().onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                if (exit){
                    findNavController().navigate(R.id.onBoardingFragment)
                }else{
                    findNavController().navigateUp()
                }
            }
        }
    }




}