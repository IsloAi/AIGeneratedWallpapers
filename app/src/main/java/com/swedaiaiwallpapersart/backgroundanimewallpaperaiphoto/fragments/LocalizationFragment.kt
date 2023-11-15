package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bmik.android.sdk.listener.CustomSDKAdsListenerAdapter
import com.bmik.android.sdk.widgets.IkmWidgetAdLayout
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.debug.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.debug.databinding
.FragmentLocalizationBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.LocalizationAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.models.DummyModelLanguages
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.LocaleManager
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import java.util.Locale

class LocalizationFragment : Fragment() {
    private var _binding: FragmentLocalizationBinding?= null
    private val binding get() = _binding!!

    var selectedItem: DummyModelLanguages? = null

    var sel: String = "en"
    var selected = -1

    var pos = 0
    var adapter: LocalizationAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLocalizationBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pos = MySharePreference.getLanguageposition(requireContext())!!
        loadNativeAd()
        initLanguages()
        setEvents()
    }

    fun loadNativeAd(){
        val adLayout = LayoutInflater.from(activity).inflate(
            R.layout.layout_custom_admob,
            null, false
        ) as? IkmWidgetAdLayout
        adLayout?.titleView = adLayout?.findViewById(R.id.custom_headline)
        adLayout?.bodyView = adLayout?.findViewById(R.id.custom_body)
        adLayout?.callToActionView = adLayout?.findViewById(R.id.custom_call_to_action)
        adLayout?.iconView = adLayout?.findViewById(R.id.custom_app_icon)
        adLayout?.mediaView = adLayout?.findViewById(R.id.custom_media)

        binding.adsView.setCustomNativeAdLayout(
            R.layout.shimmer_loading_native,
            adLayout!!
        )
        binding.adsView.loadAd(requireActivity(),"languagescr_bottom","languagescr_bottom",
            object : CustomSDKAdsListenerAdapter() {
                override fun onAdsLoadFail() {
                    super.onAdsLoadFail()
                    Log.e("TAG", "onAdsLoadFail: native failded " )
                }

                override fun onAdsLoaded() {
                    super.onAdsLoaded()
                    Log.e("TAG", "onAdsLoaded: native loaded" )
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun initLanguages(){
        binding.rvLanguages.layoutManager = LinearLayoutManager(requireContext())
        adapter =
            LocalizationAdapter(getLanguageList(sel)!!,  pos, object :
                LocalizationAdapter.OnLanguageChangeListener {

                override fun onLanguageItemClick(language: DummyModelLanguages?, position: Int) {
                    Toast.makeText(
                        requireContext(),
                        language!!.lan_code,
                        Toast.LENGTH_SHORT
                    ).show()
                    selectedItem = language
                    selected = position
                }
            })

        binding.rvLanguages.adapter = adapter
    }


    fun getLanguageList(pos:String): ArrayList<DummyModelLanguages> {
        val languagesList = ArrayList<DummyModelLanguages>()
        languagesList.add(DummyModelLanguages("English", "en", R.drawable.flag_en, false))
        languagesList.add(DummyModelLanguages("Arabic", "ar", R.drawable.flag_ar, false))
        languagesList.add(DummyModelLanguages("German", "de", R.drawable.flag_gr, false))
        languagesList.add(DummyModelLanguages("Spanish", "es", R.drawable.flag_sp, false))
        languagesList.add(DummyModelLanguages("Hindi", "hi", R.drawable.flag_hi, false))
        languagesList.add(DummyModelLanguages("Turkish", "tr", R.drawable.flag_tr, false))
        languagesList.add(DummyModelLanguages("French", "fr", R.drawable.flag_fr, false))
        languagesList.add(DummyModelLanguages("Portuguese", "pt", R.drawable.flag_pr, false))
        languagesList.add(DummyModelLanguages("Chinese", "zh", R.drawable.flag_cn, false))
        for (item in languagesList) {
            if (item.lan_code == pos) {
                item.isSelected_lan =true
                break
            }
        }
        return languagesList
    }

    private fun setEvents() {

        binding.backButton.setOnClickListener { findNavController().navigateUp() }


        binding.applyLanguage.setOnClickListener {
            if (selectedItem != null) {
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

                findNavController().navigateUp()

            } else {

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

                findNavController().navigateUp()

            }
        }
    }

}