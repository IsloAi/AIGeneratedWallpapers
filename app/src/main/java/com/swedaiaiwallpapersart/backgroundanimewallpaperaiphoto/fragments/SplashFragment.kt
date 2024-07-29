package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.ikame.android.sdk.widgets.IkmWidgetAdLayout
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentSplashBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.SplashSliderAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.GetGemsData
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.SplashModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.RetrofitInstance
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.GetGemsInterface
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.PostDataOnServer
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Math.abs

class SplashFragment : Fragment() {
    private var _binding : FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private var viewPager2: ViewPager2? = null
     private var handler2 : Handler? = null
    private var arrayList = ArrayList<SplashModel>()
    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0
    private val delayTime: Long = 1000
    lateinit var circle1:RelativeLayout
    lateinit var circle2:RelativeLayout
    lateinit var circle3:RelativeLayout
    lateinit var circle4:RelativeLayout
    lateinit var circle5:RelativeLayout
    private var isFragmentAttached: Boolean = false
    private val postDataOnServer = PostDataOnServer()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater,container,false)

        return binding.root}


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val adLayout = LayoutInflater.from(activity).inflate(
//            R.layout.layout_custom_admob,
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
//        binding.adsView.loadAd(requireActivity(),"home_native","home_native",
//            object : CustomSDKAdsListenerAdapter() {
//                override fun onAdsLoadFail() {
//                    super.onAdsLoadFail()
//                    Log.e("TAG", "onAdsLoadFail: native failded " )
//                    binding.adsView.visibility = View.GONE
//                }
//
//                override fun onAdsLoaded() {
//                    super.onAdsLoaded()
//                    Log.e("TAG", "onAdsLoaded: native loaded" )
//                }
//            }
//        )
        allOnCreateCalling()




    }


   private fun allOnCreateCalling(){
        circle1 = binding.circle1
        circle2 = binding.circle2
        circle3 = binding.circle3
        circle4 = binding.circle4
        circle5 = binding.circle5
        viewPager2 = binding.viewPager
//        arrayList.add(SplashModel(R.drawable.splash1))
//        arrayList.add(SplashModel(R.drawable.splash2))
//        arrayList.add(SplashModel(R.drawable.splash3))
//        arrayList.add(SplashModel(R.drawable.splash4))
//        arrayList.add(SplashModel(R.drawable.splash5))
        setViewPager()
    }
    override fun onResume() {
        super.onResume()
             navigate()
            fetchGems()

    }

    override fun onPause() {
        super.onPause()
        handler2?.removeCallbacksAndMessages(null)
    }
    private val autoSlideRunnable = object : Runnable {
        override fun run() {
            currentPage = (currentPage + 1) % arrayList.size
            viewPager2?.setCurrentItem(currentPage, true)
            handler.postDelayed(this, delayTime)
        }
    }
    private fun startAutoSlide() {
        handler.postDelayed(autoSlideRunnable, delayTime)
    }
    private fun stopAutoSlide() {
        handler.removeCallbacks(autoSlideRunnable)
    }
    private fun setViewPager() {
        val adopter = SplashSliderAdapter(arrayList, viewPager2!!)
        viewPager2?.adapter = adopter
//       viewPager2?.setCurrentItem(arrayList[1].image, false)
        startAutoSlide()
        viewPager2?.clipToPadding = false
        viewPager2?.clipChildren = false
        viewPager2?.offscreenPageLimit = 3
        viewPager2?.getChildAt(0)!!.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
        transformer.addTransformer { page, position ->
            val r: Float = 1 - abs(position)
            page.scaleY = 0.75f + r * 0.13f
        }
        viewPager2?.setPageTransformer(transformer)

        viewPager2?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentPage = position
                stopAutoSlide()
                startAutoSlide()
                super.onPageSelected(position)
            }
        })

        val viewPagerChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
               if(position==0){
                   fillCircle(circle1)
                   unFillCircle(circle2,circle3,circle4,circle5)
               }
                if(position==1){
                    fillCircle(circle2)
                    unFillCircle(circle1,circle3,circle4,circle5)
                }
                if(position==2){
                    fillCircle(circle3)
                    unFillCircle(circle1,circle2,circle4,circle5)
                }
                if(position==3){
                    fillCircle(circle4)
                    unFillCircle(circle1,circle2,circle3,circle5)
                }
                if(position==4){
                    fillCircle(circle5)
                    unFillCircle(circle1,circle2,circle3,circle4)
                }
            }
        }
        viewPager2?.registerOnPageChangeCallback(viewPagerChangeCallback)
    }
    private fun fillCircle(layout: RelativeLayout){
        layout.setBackgroundResource(R.drawable.fill_circle)
    }
    private fun unFillCircle(layout1: RelativeLayout,layout2: RelativeLayout,layout3: RelativeLayout,layout4: RelativeLayout){
        layout1.setBackgroundResource(R.drawable.circle)
        layout2.setBackgroundResource(R.drawable.circle)
        layout3.setBackgroundResource(R.drawable.circle)
        layout4.setBackgroundResource(R.drawable.circle)
    }
    private fun fetchGems() {

        lifecycleScope.launch {
            val retrofit = RetrofitInstance.getInstance()
            val service = retrofit.create(GetGemsInterface::class.java)
            val body: MutableMap<String, Any> = HashMap()
            body["uid"] = MySharePreference.getDeviceID(requireContext())!!
            val call = service.getGems(body)
            call.enqueue(object : Callback<GetGemsData> {
                override fun onResponse(call: Call<GetGemsData>, response: Response<GetGemsData>) {
                    if (response.isSuccessful) {
                        Log.d("postDataTesting", "onResponse: success ${response.body()}")
                        response.body()?.let {
                            val gemData = GetGemsData(it.uid,it.gems,it.counter,it.message)
                            Log.d("postDataTesting", "onResponse: model ${gemData}")
                            if (gemData.uid == MySharePreference.getDeviceID(requireContext())!!) {
                                MySharePreference.setGemsValue(requireContext(), gemData.gems!!)
                                MySharePreference.setCounterValue(requireContext(),gemData.counter!!)
                            }
                        }
                    } else {
                        Log.d("responseNotOk", "onResponse: Response not successful")
                    }
                }
                override fun onFailure(call: Call<GetGemsData>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error Loading", Toast.LENGTH_SHORT).show()
                    // Handle failure case
                    Log.d("responseFailure", "onFailure: Failed to fetch data $t")
                }
            })
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        isFragmentAttached = false
        _binding = null
        handler.removeCallbacksAndMessages(null)
        handler2?.removeCallbacksAndMessages(null)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        isFragmentAttached = true
    }
        private fun navigate() {
        handler2 = Handler()
        handler2?.postDelayed({
            findNavController().apply { navigate(R.id.action_splashFragment_to_mainFragment) }
        },6000)
    }


}