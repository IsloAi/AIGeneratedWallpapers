package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.DownloadListener
import com.bmik.android.sdk.IkmSdkController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentDownloadLiveWallpaperBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.service.LiveWallpaperService
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.BlurView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.viewmodels.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


class DownloadLiveWallpaperFragment : Fragment() {

    private var _binding:FragmentDownloadLiveWallpaperBinding ?= null

    private val binding get() = _binding!!

    val sharedViewModel: SharedViewModel by activityViewModels()

    private var bitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDownloadLiveWallpaperBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        AndroidNetworking.initialize(requireContext())

        setEvents()
        initObservers()

    }


    fun setEvents(){
        binding.buttonApplyWallpaper.setOnClickListener {
           findNavController().navigate(R.id.liveWallpaperPreviewFragment)
        }
    }

    private fun initObservers(){

        val file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)

        val video = File(file,"video.mp4")
        sharedViewModel.liveWallpaperResponseList.observe(viewLifecycleOwner){wallpaper ->
            if (wallpaper.isNotEmpty()){

                Log.e("TAG", "initObservers: $wallpaper")
                downloadVideo(wallpaper[0].livewallpaper_url,video,wallpaper[0].videoSize)
                getBitmapFromGlide(wallpaper[0].thumnail_url)


            }
        }
    }


    private fun getBitmapFromGlide(url:String){
        Glide.with(requireContext()).asBitmap().load(url)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    bitmap = resource

                    if (isAdded){
                        val blurImage: Bitmap = BlurView.blurImage(requireContext(), bitmap!!)!!
                        binding.backImage.setImageBitmap(blurImage)
                    }



                }
                override fun onLoadCleared(placeholder: Drawable?) {
                } })
    }



    private fun downloadVideo(url: String, destinationFile: File,size:Float) {

        val file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)

        val fileName = System.currentTimeMillis().toString() + ".mp4"

        val filepath = File(file,fileName)

        BlurView.filePath = filepath.path
        BlurView.fileName = fileName
        MySharePreference.setFileName(requireContext(),fileName)
        Log.e("TAG", "downloadVideo: "+BlurView.fileName )

        val totalSize = (size * 1048576).toLong()

        lifecycleScope.launch(Dispatchers.IO) {
            AndroidNetworking.download(url, file.path, fileName)
                .setTag("downloadTest")
                .setPriority(Priority.HIGH)
                .doNotCacheResponse()
                .build()
                .setDownloadProgressListener { bytesDownloaded, totalBytes ->
                    Log.e("TAG", "downloadVideo: $bytesDownloaded")
                    Log.e("TAG", "downloadVideo total bytes: $totalBytes")
                    lifecycleScope.launch(Dispatchers.Main) {
                        val percentage = (bytesDownloaded * 100 / totalSize).toInt()
                        Log.e("TAG", "downloadVideo: $percentage")
                                binding.progress.progress =percentage
                            binding.progressTxt.text =
                                (bytesDownloaded * 100 / totalSize).toString()

                    }
                }
                .startDownload(object : DownloadListener {
                    override fun onDownloadComplete() {
                        lifecycleScope.launch(Dispatchers.Main) {
                            Toast.makeText(requireContext(),"File Downloaded",Toast.LENGTH_SHORT).show()
                            binding.buttonApplyWallpaper.visibility = View.VISIBLE

                        }
                    }

                    override fun onError(error: ANError?) {
                        // handle error
                    }
                })
        }



//        lifecycleScope.launch(Dispatchers.IO) {
//            val client = OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS) // Increase the timeout duration
//                .build()
//            val request = Request.Builder()
//                .url(url)
//                .build()
//
//            client.newCall(request).execute().use { response ->
//                if (!response.isSuccessful) throw IOException("Unexpected code $response")
//                val inputStream = response.body?.byteStream()
//
//                try {
//                    inputStream?.use { input ->
//                        val resolver = requireContext().contentResolver
//
//                        val buffer = ByteArray(1024 * 4) // Adjust buffer size as needed
//                        var totalBytesRead = 0L
//                        val contentLength = response.body?.contentLength() ?: -1L // Total content length
//                        var bytesRead: Int
//
//                        val output = FileOutputStream(destinationFile)
//                        while (input.read(buffer).also { bytesRead = it } != -1) {
//                            output.write(buffer, 0, bytesRead)
//                            totalBytesRead += bytesRead
//
//                            // Calculate progress percentage
//                            val progress = if (contentLength != -1L) {
//                                ((totalBytesRead.toFloat() / contentLength) * 100).toInt()
//                            } else {
//                                -1 // Indicate unknown progress
//                            }
//                            progressCallback(progress) // Report progress to UI
//                        }
//
//                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
//                            saveVideoToFile(inputStream, destinationFile)
//                            IkmSdkController.setEnableShowResumeAds(false)
//                            LiveWallpaperService.setToWallPaper(requireContext())
//                            return@use
//                        }
//
//                        val contentValues = ContentValues().apply {
//                            put(MediaStore.MediaColumns.DISPLAY_NAME, "video")
//                            put(MediaStore.MediaColumns.MIME_TYPE, "video/m4v")
//                            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
//                        }
//
//                        val contentUri = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
//
//                        val item = resolver.insert(contentUri, contentValues)
//
//                        item?.let { uri ->
//                            resolver.openOutputStream(uri)?.use { output ->
//                                input.copyTo(output)
//                            }
//
//                            IkmSdkController.setEnableShowResumeAds(false)
//                            LiveWallpaperService.setToWallPaper(requireContext())
//                        }
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    // Handle exceptions
//                }
//            }
//        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding =  null
    }
}