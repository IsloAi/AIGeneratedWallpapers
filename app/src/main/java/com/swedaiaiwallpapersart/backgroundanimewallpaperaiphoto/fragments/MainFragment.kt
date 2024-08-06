package com.example.hdwallpaper.Fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.menuFragments.CategoryFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.menuFragments.FavouriteFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.menuFragments.HomeFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.menuFragments.SettingFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyDialogs
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentMainBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.SplashOnFragment.Companion.exit
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.fragmentsIG.GenerateImageFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.InternetState
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.LocaleManager
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import java.util.Locale


class MainFragment : Fragment(){
    private var _binding : FragmentMainBinding? = null
    private val binding get() = _binding!!
    private var existDialog = MyDialogs()
    private lateinit var homeButton: LinearLayout
    private lateinit var categoryButton: LinearLayout
    private lateinit var favouriteButton: LinearLayout
    private lateinit var generateImageButton: LinearLayout
    private lateinit var settingButton: LinearLayout
    private lateinit var homeIcon :ImageView
    private lateinit var categoryIcon :ImageView
    private lateinit var favouriteIcon:ImageView
    private lateinit var generateImageIcon:ImageView
    private lateinit var settingIcon :ImageView
    private lateinit var homeTitle:TextView
    private lateinit var categoryTitle:TextView
    private lateinit var favouriteTitle:TextView
    private lateinit var generateImageTitle:TextView
    private lateinit var settingTitle:TextView
    private val home_sel = R.drawable.home_sel
    private val home_unsel =R.drawable.home_unsel
    private val category_sel =R.drawable.category_sel
    private val category_unsel =R.drawable.category_unsel
    private val favourit_sel =R.drawable.heart_sel
    private val favourit_unsel =R.drawable.heart_unsel
    private val setting_sel =R.drawable.setting_sel
    private val setting_unsel =R.drawable.setting_unsel
    private val creation_sel =R.drawable.creation_sel
    private val creation_unsel =R.drawable.creation_unsel
    private var click = 1
    private lateinit var myActivity : MainActivity



    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val lan = MySharePreference.getLanguage(requireContext())
        val context = LocaleManager.setLocale(requireContext(), lan!!)
        val resources = context.resources
        val newLocale = Locale(lan)
        val resources1 = getResources()
        val configuration = resources1.configuration
        configuration.setLocale(newLocale)
        configuration.setLayoutDirection(Locale(lan));
        resources1.updateConfiguration(configuration, resources.displayMetrics)
      _binding = FragmentMainBinding.inflate(inflater, container, false)
        exit = false

       return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        IKSdkController.getInstance()
//            .loadBannerAds(
//                requireActivity(),
//                binding.adsWidget as? ViewGroup,
//                "mainscr_bottom",
//                " mainscr_bottom", object : CustomSDKAdsListenerAdapter() {
//                    override fun onAdsLoaded() {
//                        super.onAdsLoaded()
//                        Log.e("*******ADS", "onAdsLoaded: Banner loaded", )
//                    }
//
//                    override fun onAdsLoadFail() {
//                        super.onAdsLoadFail()
//                        Log.e("*******ADS", "onAdsLoaded: Banner failed", )
//                    }
//                }
//            )

        backHandle()
        onViewCreatingCalling()

    }
    private fun backHandle(){
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                existDialog.exitPopup(requireContext(),requireActivity(),myActivity)
            }
        })
    }
    private fun onViewCreatingCalling(){
       myActivity = activity as MainActivity
        if (InternetState.checkForInternet(requireContext())){
            binding.bottomMenu.visibility = VISIBLE
            binding.fragmentContainer.visibility= VISIBLE
            binding.errorMessage.visibility = INVISIBLE
            homeButton = binding.homeButton
            categoryButton = binding.categoriesButton
            favouriteButton = binding.favouriteButton
            generateImageButton = binding.generateImageButton
            settingButton = binding.settingButton
            homeIcon = binding.homeIcon
            categoryIcon = binding.categoryIcon
            favouriteIcon = binding.favouriteIcon
            generateImageIcon = binding.generateImageIcon
            settingIcon = binding.settingIcon
            homeTitle = binding.homeTitle
            categoryTitle = binding.categoryTitle
            favouriteTitle = binding.favouriteTitle
            generateImageTitle = binding.generateImageTitle
            settingTitle = binding.settingTitle
            setFragment(click)
            iconAndTitleSetting(click)
            homeButton.setOnClickListener {
                if (click !=  2){
                    click = 2
                    setFragment(click)
                    iconAndTitleSetting(click)
                }
            }
            categoryButton.setOnClickListener {
                if (click != 3) {
                    click = 3
                    iconAndTitleSetting(click)
                    setFragment(click)
                }
            }
            favouriteButton.setOnClickListener {
                if (click != 4) {
                    click = 4
                    iconAndTitleSetting(click)
                    setFragment(click)
                }
            }
            generateImageButton.setOnClickListener{
                if (click != 1) {
                    click = 1
                    iconAndTitleSetting(click)
                    setFragment(click)
                }
            }
            settingButton.setOnClickListener {
                if (click != 5) {
                    click = 5
                    iconAndTitleSetting(click)
                    setFragment(click)
                }
            }
        }else{
            binding.bottomMenu.visibility = INVISIBLE
            binding.fragmentContainer.visibility= INVISIBLE
            binding.errorMessage.visibility = VISIBLE
        }


    }
    private fun iconAndTitleSetting(whichClicked: Int){
      when(whichClicked){
          1->{

              iconSelect(generateImageIcon,creation_sel,generateImageTitle)
              iconUnselected(homeIcon,home_unsel,categoryIcon,category_unsel,favouriteIcon,favourit_unsel,settingIcon,setting_unsel,)
              unselectedColor(homeTitle,categoryTitle,favouriteTitle,settingTitle)

          }
          2->{

              iconSelect(homeIcon,home_sel,homeTitle)
              iconUnselected(categoryIcon,category_unsel,favouriteIcon,favourit_unsel,settingIcon,setting_unsel,generateImageIcon,creation_unsel)
              unselectedColor(categoryTitle,favouriteTitle,settingTitle,generateImageTitle)


          }
          3->{

              iconSelect(categoryIcon,category_sel,categoryTitle)
              iconUnselected(homeIcon,home_unsel,favouriteIcon,favourit_unsel,settingIcon,setting_unsel,generateImageIcon,creation_unsel)
              unselectedColor(homeTitle,favouriteTitle,settingTitle,generateImageTitle)


          }
          4->{
              iconSelect(favouriteIcon,favourit_sel,favouriteTitle)
              iconUnselected(homeIcon,home_unsel,categoryIcon,category_unsel,settingIcon,setting_unsel,generateImageIcon,creation_unsel)
              unselectedColor(homeTitle,categoryTitle,settingTitle,generateImageTitle)
          }
          5->{
              iconSelect(settingIcon,setting_sel,settingTitle)
              iconUnselected(homeIcon,home_unsel,categoryIcon,category_unsel,favouriteIcon,favourit_unsel,generateImageIcon,creation_unsel)
              unselectedColor(homeTitle,categoryTitle,favouriteTitle,generateImageTitle)
          }
      }
    }

    fun navigateToYourDestination(pos:Int) {
        click = pos
        iconAndTitleSetting(click)
        setFragment(click)
        // If you want to perform a click on the categoryButton programmatically
        if (pos == 1){
            generateImageButton.performClick()
        }else{
            homeButton.performClick()
        }
    }
    private fun setFragment(click: Int){
        val transaction = childFragmentManager.beginTransaction()
        when(click){
            1->  transaction.replace(R.id.fragmentContainer, GenerateImageFragment())
            2-> transaction.replace(R.id.fragmentContainer, HomeFragment())
            3-> transaction.replace(R.id.fragmentContainer, CategoryFragment())
            4->{
                //Toast.makeText(requireContext(), "coming soon", Toast.LENGTH_SHORT).show()
                transaction.replace(R.id.fragmentContainer, FavouriteFragment())
            }
            5-> transaction.replace(R.id.fragmentContainer, SettingFragment())
        }
            transaction.commit()
    }
     private fun iconSelect(imageView: ImageView, drawable: Int, title: TextView){
         imageView.setImageResource(drawable)
         selectColor(title)
     }
    private fun iconUnselected(
        imageView1: ImageView, drawable1: Int,
        imageView2: ImageView,drawable2: Int,
        imageView3: ImageView,drawable3: Int,
        imageView4: ImageView,drawable4: Int

    )
    {
     imageView1.setImageResource(drawable1)
     imageView2.setImageResource(drawable2)
     imageView3.setImageResource(drawable3)
     imageView4.setImageResource(drawable4)
    }
    private fun selectColor(textView: TextView){
    textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.button_bg))
    }
    private fun unselectedColor(textView1: TextView,textView2: TextView,textView3: TextView,textView4: TextView){
        textView1.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        textView2.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        textView3.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        textView4.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
    }

    private fun noBackground(background1:LinearLayout,background2:LinearLayout,background3:LinearLayout){
        background1.setBackgroundResource(0)
        background2.setBackgroundResource(0)
        background3.setBackgroundResource(0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}