package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.ApiService
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.RetrofitInstance
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyDialogs
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyWallpaperManager
import com.example.hdwallpaper.adapters.WallpaperApiSliderAdapter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.ViewPagerImageClick
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.PostData
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentWallpaperViewBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.menuFragments.HomeFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.FullViewImage
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.FullViewImagePopup
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.GoogleLogin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.Executors
class WallpaperViewFragment : Fragment() {
    private var _binding: FragmentWallpaperViewBinding? = null
    private val binding get() = _binding!!
    private var arrayList = ArrayList<CatResponse>()
    private var isFragmentAttached: Boolean = false
    private var position :Int =0
    private var viewPager2: ViewPager2? = null
    private var bitmap: Bitmap? = null
    private val myExecutor = Executors.newSingleThreadExecutor()
    private val myHandler = Handler(Looper.getMainLooper())
    private var reviewManager: ReviewManager? = null
    private var getLargImage: String = ""
    private var getSmallImage: String = ""
    private var state = true
    private val STORAGE_PERMISSION_CODE = 1
    private var mImage: Bitmap? = null
    private var dialog: Dialog?= null
    private lateinit var myWallpaperManager : MyWallpaperManager
    private var navController: NavController? = null
    val myDialogs = MyDialogs()
    private lateinit var auth: FirebaseAuth
    private val googleLogin = GoogleLogin()
    private lateinit var myActivity : MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View{
        _binding = FragmentWallpaperViewBinding.inflate(inflater,container,false)
        myWallpaperManager = MyWallpaperManager(requireContext(),requireActivity())
        navController = findNavController()
        functionality()
        return binding.root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        reviewManager = ReviewManagerFactory.create(requireContext())
        val arrayListJson = arguments?.getString("arrayListJson")
        val pos = arguments?.getInt("position")
        if (arrayListJson != null && pos != null) {
            val gson = Gson()
            val arrayListType = object : TypeToken<ArrayList<CatResponse>>() {}.type
            val arrayListOfImages = gson.fromJson<ArrayList<CatResponse>>(arrayListJson, arrayListType)
            position = pos
            arrayList = arrayListOfImages
            Log.d("gsonParsingData", "onCreate:  $arrayListOfImages"  )
        }
    }
   private fun functionality(){
       myActivity = activity as MainActivity
       viewPager2 = binding.viewPager
       binding.toolbar.setOnClickListener {
           // Set up the onBackPressed callback
           navController?.navigateUp()
       }
       getLargImage = arrayList[position].hd_image_url!!
       getSmallImage = arrayList[position].compressed_image_url!!
       binding.gemsText.text = MySharePreference.getGemsValue(requireContext()).toString()
       setViewPager()
       checkRedHeart(position)
       getBitmapFromGlide(getLargImage)
       binding.buttonApplyWallpaper.setOnClickListener {
           if(arrayList[position].gems==0 || arrayList[position].unlockimges==true){
               if(bitmap != null){
                   openPopupMenu()
               }else{
                   Toast.makeText(requireContext(), "your image not fetched properly", Toast.LENGTH_SHORT).show()
               }
           }else{
               Toast.makeText(requireContext(), "Please first buy your wallpaper", Toast.LENGTH_SHORT).show()
           }

       }
       binding.favouriteButton.setOnClickListener {

           val auth = FirebaseAuth.getInstance()
           val currentUser = auth.currentUser
           if (currentUser == null){
               findNavController().navigate(R.id.action_wallpaperViewFragment_to_signInFragment)
           }else{
               binding.favouriteButton.isEnabled = false
               if(arrayList[position].liked==true){
                   arrayList[position].liked = false
                   binding.favouriteButton.setImageResource(R.drawable.heart_unsel)
               }else{
                   arrayList[position].liked = true
                   binding.favouriteButton.setImageResource(R.drawable.heart_red)
               }
               addFavourite(requireContext(),position,binding.favouriteButton)

           }
       }
       binding.downloadWallpaper.setOnClickListener{
           if(arrayList[position].gems==0 || arrayList[position].unlockimges==true){
               mSaveMediaToStorage(bitmap)
           }else{
               Toast.makeText(requireContext(), "Please first buy your wallpaper", Toast.LENGTH_SHORT).show()

           }
       }
   }
    private fun setViewPager() {
        val adopter = WallpaperApiSliderAdapter(arrayList, viewPager2!!,object :
            ViewPagerImageClick {
            @SuppressLint("SuspiciousIndentation")
            override fun getImagePosition(pos: Int, layout: ConstraintLayout) {
                val auth = FirebaseAuth.getInstance()
                val currentUser = auth.currentUser
                if (currentUser!= null) {
                    val model = arrayList[pos]
                    myDialogs.getWallpaperPopup(
                        context!!,
                        model,
                        navController!!, R.id.action_wallpaperViewFragment_to_premiumPlanFragment,
                        RetrofitInstance.getInstance(), binding.gemsText, layout)
                }else{
                    findNavController().navigate(R.id.action_wallpaperViewFragment_to_signInFragment)
                }
            }
        },object:FullViewImage{
            override fun getFullImageUrl(image:String) {
               FullViewImagePopup.openFullViewWallpaper(myContext(),image)
            }
        })
        viewPager2?.adapter = adopter
        viewPager2?.setCurrentItem(position, false)
        viewPager2?.clipToPadding = false
        viewPager2?.clipChildren = false
        viewPager2?.offscreenPageLimit = 3
        viewPager2?.getChildAt(0)!!.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
        transformer.addTransformer { page, position ->
            val r: Float = 1 - Math.abs(position)
            page.scaleY = 0.75f + r * 0.13f
        }
        viewPager2?.setPageTransformer(transformer)
        val viewPagerChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(positi: Int) {
                 getLargImage = arrayList[positi].hd_image_url!!
                 getSmallImage = arrayList[positi].compressed_image_url!!
                 position = positi
                checkRedHeart(positi)
                getBitmapFromGlide(getLargImage)
            }
        }
        viewPager2?.registerOnPageChangeCallback(viewPagerChangeCallback)
    }
    private fun getBitmapFromGlide(url:String){
        Glide.with(requireContext()).asBitmap().load(url)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    bitmap = resource }
                override fun onLoadCleared(placeholder: Drawable?) {
                } })
    }
    private fun checkRedHeart(position: Int) {
        if (isAdded) {
        if (arrayList[position].liked == true) {
            binding.favouriteButton.setImageResource(R.drawable.heart_red) }
        else
        {
            binding.favouriteButton.setImageResource(R.drawable.heart_unsel)
        }
    }
    }
    private fun addFavourite(
        context: Context,
        position: Int,
        favouriteButton: ImageView
    ){
        val retrofit = RetrofitInstance.getInstance()
        val apiService = retrofit.create(ApiService::class.java)
        val postData = PostData(MySharePreference.getUserID(context)!!, arrayList[position].id.toString())
        val call = apiService.postData(postData)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val message = response.body()?.string()
                    if(message=="Liked"){
                        arrayList[position].liked = true
                        favouriteButton.setImageResource(R.drawable.heart_red)
                    }
                    else
                    {
                        favouriteButton.setImageResource(R.drawable.heart_unsel)
                        arrayList[position].liked = false
                    }
                    favouriteButton.isEnabled = true
                }
                else
                {
                    favouriteButton.isEnabled = true
                    Toast.makeText(context, "onResponse error", Toast.LENGTH_SHORT).show()
                    favouriteButton.setImageResource(R.drawable.heart_unsel)
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "onFailure error", Toast.LENGTH_SHORT).show()
                favouriteButton.isEnabled = true
            }
        })
    }
    private suspend fun loadBitmapFromUrlAsync(urlString: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.connect()
            val inputStream: InputStream = BufferedInputStream(connection.inputStream)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            connection.disconnect()
            bitmap
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun mLoad(string: String): Bitmap? {
        val url: URL = mStringToURL(string)!!
        val connection: HttpURLConnection?
        try {
            connection = url.openConnection() as HttpURLConnection
            connection.connect()
            val inputStream: InputStream = connection.inputStream
            val bufferedInputStream = BufferedInputStream(inputStream)
            return BitmapFactory.decodeStream(bufferedInputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
    private fun mStringToURL(string: String): URL? {
        try {
            return URL(string)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return null
    }
    @SuppressLint("ResourceType")
    private fun openPopupMenu() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.set_wallpaper_menu, null)
        dialog.setContentView(view)
        val width = WindowManager.LayoutParams.MATCH_PARENT
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.setLayout(width, height)
        val params = (view.getParent() as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        (view.getParent() as View).setBackgroundColor(Color.TRANSPARENT)
        dialog.setCancelable(false)
        val buttonHome = view.findViewById<Button>(R.id.buttonHome)
        val buttonLock = view.findViewById<Button>(R.id.buttonLock)
        val buttonBothScreen = view.findViewById<Button>(R.id.buttonBothScreen)
        val closeButton = view.findViewById<RelativeLayout>(R.id.closeButton)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }
        buttonHome.setOnClickListener {
            myExecutor.execute {myWallpaperManager.homeScreen(bitmap!!)}
            myHandler.post { if(state){
                interstitialAdWithToast("Set Successfully on Home Screen", dialog)
                state = false
                postDelay()
            } }
            showRateApp()
        }
        buttonLock.setOnClickListener {
            myExecutor.execute {
                myWallpaperManager.lockScreen(bitmap!!)
            }
            myHandler.post {
                if(state){
                    interstitialAdWithToast("Set Successfully on Lock Screen", dialog)
                    state = false
                    postDelay()
                }
            }
            showRateApp()
        }
        buttonBothScreen.setOnClickListener {
            myExecutor.execute {
                myWallpaperManager.homeAndLockScreen(bitmap!!)
            }
            myHandler.post {
                if(state){
                    interstitialAdWithToast("Set Successfully on Both",dialog)
                    state = false
                    postDelay()
                }
            }
            showRateApp()
        }
        dialog.show()
    }
    private fun postDelay(){
        Handler().postDelayed({
            state = true
        }, 5000)
    }
    private fun interstitialAdWithToast (message: String, dialog: BottomSheetDialog){
        Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show()
        dialog.dismiss()
    }
    private fun mSaveMediaToStorage(bitmap: Bitmap?) {
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requireContext().contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "Wallpapers")
                }
                val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(requireContext() as Activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
            } else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+ File.separator + "Wallpapers")
                if(!imagesDir.exists()){
                    imagesDir.mkdir()
                }
                val image = File(imagesDir, filename)
                fos = FileOutputStream(image)
            }
        }
        fos?.use {
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(requireContext() , "Saved to Gallery" , Toast.LENGTH_SHORT).show()
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Permission Granted Click again to save image", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Storage Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadingPopup() {
        dialog = Dialog(requireContext())
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.loading)
        val width = WindowManager.LayoutParams.WRAP_CONTENT
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog?.window!!.setLayout(width, height)
        dialog?.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCancelable(false)
    }

    fun myContext(): Context {
        return this.context
            ?: throw IllegalStateException("Fragment $this not attached to a context.")
    }

   private fun showRateApp() {
       if(isFragmentAttached){
        val request: Task<ReviewInfo> = reviewManager!!.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful()) {
                // Getting the ReviewInfo object
                val reviewInfo: ReviewInfo = task.getResult()
                val flow: Task<Void> = reviewManager!!.launchReviewFlow(requireActivity(), reviewInfo)
                flow.addOnCompleteListener { task1 -> }
            }
        }
       }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        isFragmentAttached = false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        isFragmentAttached = true
    }

}