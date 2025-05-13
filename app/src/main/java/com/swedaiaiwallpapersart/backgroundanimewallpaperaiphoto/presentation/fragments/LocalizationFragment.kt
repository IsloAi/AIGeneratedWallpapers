package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.fragments

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
import com.google.firebase.analytics.FirebaseAnalytics
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentLocalizationBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.adapters.LocalizationAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.LanguagesModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.fragments.SplashOnFragment.Companion.exit
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.LocaleManager
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.RvItemDecore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class LocalizationFragment : Fragment() {
    private lateinit var _binding: FragmentLocalizationBinding
    private val binding get() = _binding

    var selectedItem: LanguagesModel? = null

    private var sel: String = "en"
    var selected = -1

    var posnew = 0
    var adapter: LocalizationAdapter? = null

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    var adnext = false
    val TAG = "Localization"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocalizationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        posnew = MySharePreference.getLanguagePosition(requireContext())
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        if (AdConfig.globalTemplateNativeAdView != null) {
            // Detach globalNativeAdView from its previous parent if it has one
            AdConfig.globalTemplateNativeAdView?.parent?.let { parent ->
                (parent as ViewGroup).removeView(AdConfig.globalTemplateNativeAdView)
            }
            binding.NativeAd.removeAllViews()
            binding.NativeAd.addView(AdConfig.globalTemplateNativeAdView)
        } else {
            // maybe show a placeholder or hide the view
            binding.NativeAd.visibility = View.GONE
        }
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        lifecycleScope.launch {
            delay(200)
            Log.e(TAG, "getLanguageList: " + getDefaultLocaleInfo())
        }
        setGradienttext()
        setEvents()
        initLanguages()
        backHandle()
    }

    private fun setGradienttext() {
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

    override fun onResume() {
        super.onResume()
        if (isAdded) {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Language Screen")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, javaClass.simpleName)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }
    }

    private fun initLanguages() {
        binding.rvLanguages.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvLanguages.addItemDecoration(RvItemDecore(2, 20, false, 10000))

        sel = getDefaultLocaleInfo()
        val list = getLanguageList(sel)


        Log.e(TAG, "initLanguages: " + AdConfig.languagesOrder)
        val sortedList = sortLanguages(list, AdConfig.languagesOrder)


        val arrayList: ArrayList<LanguagesModel> = sortedList.toCollection(ArrayList())
        adapter = LocalizationAdapter(arrayList,
            getSelectedLanguagePosition(arrayList),
            object : LocalizationAdapter.OnLanguageChangeListener {

                override fun onLanguageItemClick(language: LanguagesModel?, position: Int) {
                    selectedItem = language
                    selected = position
                }
            })

        binding.rvLanguages.adapter = adapter
    }

    private fun getDefaultLocaleInfo(): String {
        val locale = Locale.getDefault()
        val name = locale.displayName
        Log.e(TAG, "getLanguageList: $name")
        val language = locale.language
        Log.e(TAG, "getLanguageList: $language")
        return language
    }

    private fun getSelectedLanguagePosition(sortedLanguages: ArrayList<LanguagesModel>): Int {
        return sortedLanguages.indexOfFirst { it.isSelected_lan }
    }

    private fun getLanguageList(pos: String): ArrayList<LanguagesModel> {
        val languagesList = ArrayList<LanguagesModel>()
        languagesList.add(LanguagesModel("German", "de", R.drawable.flag_gr, false))
        languagesList.add(LanguagesModel("Japanese ", "ja", R.drawable.flag_japan, false))
        languagesList.add(LanguagesModel("Chinese", "zh", R.drawable.flag_cn, false))
        languagesList.add(LanguagesModel("Italian", "it", R.drawable.flag_itly, false))
        languagesList.add(LanguagesModel("Russian", "ru", R.drawable.flag_russia, false))
        languagesList.add(LanguagesModel("English (US)", "en", R.drawable.flag_en, false))
        languagesList.add(LanguagesModel("Korean", "ko", R.drawable.flag_korean, false))
        languagesList.add(LanguagesModel("Portuguese", "pt", R.drawable.flag_pr, false))
        languagesList.add(LanguagesModel("Spanish", "es", R.drawable.flag_sp, false))
        languagesList.add(LanguagesModel("Arabic", "ar", R.drawable.flag_ar, false))
        languagesList.add(LanguagesModel("English (UK)", "en-rGB", R.drawable.flag_uk, false))
        languagesList.add(LanguagesModel("French", "fr", R.drawable.flag_fr, false))
        languagesList.add(LanguagesModel("Thai", "th", R.drawable.flag_thai, false))
        languagesList.add(LanguagesModel("Turkish", "tr", R.drawable.flag_tr, false))
        languagesList.add(
            LanguagesModel(
                "Vietnamese ", "vi", R.drawable.flag_vietnamese, false
            )
        )
        languagesList.add(LanguagesModel("Hindi", "hi", R.drawable.flag_hi, false))
        languagesList.add(LanguagesModel("Dutch", "nl", R.drawable.flag_ducth, false))
        languagesList.add(LanguagesModel("Indonesian", "in", R.drawable.flag_indona, false))
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

    private fun sortLanguages(
        languages: ArrayList<LanguagesModel>, order: List<String>
    ): List<LanguagesModel> {
        val orderMap = order.withIndex().associate { it.value.trim() to it.index }

        // Sort the languages based on the order specified in the map
        return languages.sortedWith(compareBy { orderMap[it.lan_name.trim()] ?: Int.MAX_VALUE })
    }

    private fun setEvents() {
        val onBoard = MySharePreference.getOnboarding(requireContext())

        binding.applyLanguage.setOnClickListener {
            val lan = MySharePreference.getLanguage(requireContext())
            if (selectedItem != null) {
                MySharePreference.setLanguage(requireContext(), selectedItem!!.lan_code)
                MySharePreference.setLanguagePosition(requireContext(), selected)
                val context =
                    com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.LocaleManager.setLocale(
                        requireContext(),
                        selectedItem!!.lan_code
                    )
                val resources = context.resources
                val newLocale = Locale(selectedItem!!.lan_code)
                val resources1 = getResources()
                val configuration = resources1.configuration
                configuration.setLocale(newLocale)
                configuration.setLayoutDirection(Locale(selectedItem!!.lan_code));
                resources1.updateConfiguration(configuration, resources.displayMetrics)
                if (exit) {
                    if (onBoard) {
                        findNavController().navigate(R.id.homeTabsFragment)
                    } else {
                        if (AdConfig.showOnboarding) {
                            findNavController().navigate(R.id.onBoardingFragment)
                        } else {
                            findNavController().navigate(R.id.homeTabsFragment)
                        }
                    }

                } else {
                    if (!onBoard) {
                        if (AdConfig.showOnboarding) {
                            findNavController().navigate(R.id.onBoardingFragment)
                        } else {
                            findNavController().navigate(R.id.homeTabsFragment)
                        }
                    } else {
                        findNavController().navigateUp()
                    }
                }
            } else {
                MySharePreference.setLanguage(requireContext(), lan!!)
                MySharePreference.setLanguagePosition(requireContext(), 0)
                val context = LocaleManager.setLocale(requireContext(), lan)
                val resources = context.resources
                val newLocale = Locale(lan)
                val resources1 = getResources()
                val configuration = resources1.configuration
                configuration.setLocale(newLocale)
                configuration.setLayoutDirection(Locale(lan));
                requireActivity().window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
                resources1.updateConfiguration(configuration, resources.displayMetrics)

                if (exit) {
                    if (onBoard) {
                        findNavController().navigate(R.id.homeTabsFragment)
                    } else {
                        if (AdConfig.showOnboarding) {
                            findNavController().navigate(R.id.onBoardingFragment)
                        } else {
                            findNavController().navigate(R.id.homeTabsFragment)
                        }
                    }
                } else {
                    if (!onBoard) {
                        if (AdConfig.showOnboarding) {
                            findNavController().navigate(R.id.onBoardingFragment)
                        } else {
                            findNavController().navigate(R.id.homeTabsFragment)
                        }
                    } else {
                        findNavController().navigateUp()
                    }
                }
            }
        }
    }

    private fun backHandle() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (exit) {
                        findNavController().navigate(R.id.onBoardingFragment)
                    } else {
                        findNavController().navigateUp()
                    }
                }
            })

        if (BuildCompat.isAtLeastT()) {
            requireActivity().onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                if (exit) {
                    findNavController().navigate(R.id.onBoardingFragment)
                } else {
                    findNavController().navigateUp()
                }
            }
        }
    }

}