package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments

import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bmik.android.sdk.IkmSdkController
import com.bmik.android.sdk.SDKBaseController
import com.bmik.android.sdk.listener.CommonAdsListenerAdapter
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentLiveWallpaperBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.ApiCategoriesListAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.LiveWallpaperAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.GemsTextUpdate
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.GetLoginDetails
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.PositionCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.downloadCallback
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.service.LiveWallpaperService
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.temp.PixabayResponseViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.temp.VideoItem
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MyHomeViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.RvItemDecore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit

class LiveWallpaperFragment : Fragment() {

    private var _binding:FragmentLiveWallpaperBinding ?= null
    private val binding get() = _binding!!
    private lateinit var myViewModel: PixabayResponseViewModel

    private lateinit var myActivity : MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLiveWallpaperBinding.inflate(inflater,container,false)
        myViewModel = ViewModelProvider(this)[PixabayResponseViewModel::class.java]
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myActivity = activity as MainActivity

        binding.liveReccyclerview.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.liveReccyclerview.addItemDecoration(RvItemDecore(3,5,false,10000))
        loadData()
    }


    private fun loadData() {
        Log.d("functionCallingTest", "onCreateCustom:  home on create")
        // Observe the LiveData in the ViewModel and update the UI accordingly
        myViewModel.wallpaperData.observe(viewLifecycleOwner) { catResponses ->
            if (catResponses != null) {
                Log.e("TAG", "loadData: "+catResponses )
                if (view != null) {
                    // If the view is available, update the UI
//
                    updateUIWithFetchedData(catResponses)
                }
            }else{

            }
        }
        myViewModel.fetchWallpapers()
    }


    private fun updateUIWithFetchedData(catResponses: List<VideoItem?>) {

        val file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)

        val video = File(file,"video.mp4")

        val list = addNullValueInsideArray(catResponses)

        val adapter = LiveWallpaperAdapter(list, object :
            downloadCallback {
            override fun getPosition(position: Int, model: VideoItem) {
                Log.e("TAG", "getPosition: "+model.videos.medium.toString() )
                downloadVideo(model.videos.small.url,video)
            }
        },myActivity)
        binding.liveReccyclerview.adapter = adapter
    }


    fun downloadVideo(url: String, destinationFile: File) {
        lifecycleScope.launch(Dispatchers.IO) {

            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // Increase the timeout duration
                .build()
            val request = Request.Builder()
                .url("https://edecator.com/wallpaperApp/Live_Wallpaper/19.m4v")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val inputStream = response.body?.byteStream()

                try {
                    inputStream?.use { input ->
                        val resolver = requireContext().contentResolver

                        val contentValues = ContentValues().apply {
                            put(MediaStore.MediaColumns.DISPLAY_NAME, "video") // Set the display name
                            put(MediaStore.MediaColumns.MIME_TYPE, "video/m4v") // Set the MIME type
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM) // Use Environment.DIRECTORY_DOWNLOADS for API 29+
                            } else {
                                // For Android 26 and below, use direct file operation
                                saveVideoToFile(input, destinationFile)
                                IkmSdkController.setEnableShowResumeAds(false)
                                LiveWallpaperService.setToWallPaper(requireContext())
                                return@use
                            }
                        }

                        val contentUri = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

                        val item = resolver.insert(contentUri, contentValues)

                        item?.let { uri ->
                            resolver.openOutputStream(uri)?.use { output ->
                                input.copyTo(output)
                            }

                            IkmSdkController.setEnableShowResumeAds(false)
                            LiveWallpaperService.setToWallPaper(requireContext())
                        }


                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Handle exceptions
                }
            }
        }
    }

    // Function for saving video file directly to the destination file
    private fun saveVideoToFile(inputStream: InputStream, destinationFile: File) {
        val outputStream = FileOutputStream(destinationFile)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
    }


    private fun addNullValueInsideArray(data: List<VideoItem?>): ArrayList<VideoItem?>{

        val firstAdLineThreshold = if (AdConfig.firstAdLineViewListWallSRC != 0) AdConfig.firstAdLineViewListWallSRC else 4
        val firstLine = firstAdLineThreshold * 3

        val lineCount = if (AdConfig.lineCountViewListWallSRC != 0) AdConfig.lineCountViewListWallSRC else 5
        val lineC = lineCount * 3
        val newData = arrayListOf<VideoItem?>()

        for (i in data.indices){
            if (i > firstLine && (i - firstLine) % (lineC + 1)  == 0) {
                newData.add(null)



                Log.e("******NULL", "addNullValueInsideArray: null "+i )

            }else if (i == firstLine){
                newData.add(null)
                Log.e("******NULL", "addNullValueInsideArray: null first "+i )
            }
            Log.e("******NULL", "addNullValueInsideArray: not null "+i )
            newData.add(data[i])

        }
        Log.e("******NULL", "addNullValueInsideArray:size "+newData.size )




        return newData
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding =  null
    }
}