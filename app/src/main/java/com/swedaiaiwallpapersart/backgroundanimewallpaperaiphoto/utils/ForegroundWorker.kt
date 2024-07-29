package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.SingleDatabaseResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.usecases.FetechAllWallpapersUsecase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.AppDatabase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class ForegroundWorker @AssistedInject constructor(
     @Assisted appContext : Context,
     @Assisted params : WorkerParameters
) : CoroutineWorker(appContext, params) {
    private val notificationManager =
        appContext.getSystemService(NotificationManager::class.java)



    private val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle("Downloading...!Please wait")

    lateinit var notification: Notification

    var currentPage = 2


    override suspend fun doWork(): Result {
        Log.d(TAG, "Started job")


        try {
            val inputData = inputData.getString("key")
            createNotificationChannel()

            var resulta = Result.failure()

//            fetechAllWallpapersUsecase.invoke("Bearer $inputData",currentPage.toString(),"200").collect(){result ->
////                Log.e(TAG, "doWork: $it")
//
//                when (result) {
//                    is Response.Loading -> {
//
//                    }
//
//                    is Response.Success -> {
//                        result.data?.forEach { item ->
//
//                            val model = SingleDatabaseResponse(item.id,item.cat_name,item.image_name,AdConfig.HD_ImageUrl+item.url,AdConfig.Compressed_Image_url+item.url,item.likes,item.liked,item.size,item.Tags,item.capacity)
//                            appDatabase.wallpapersDao().insert(model)
//
//                        }
//
//                        currentPage++
//
//                        loadNextData(inputData!!)
//
//                        // Set the result as success only when it's the last item in the page
////                        if (result.data?.isEmpty() == true || result.data?.size!! < 100) {
////                            resulta = Result.success()
////                        }
//                    }
//
//                    is Response.Error -> {
//
//                    }
//
//                    else -> {
////                        Toast.makeText(requireContext(), "it is in else clause", Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//
//
//            }




            return resulta
        } catch (e: Exception) {
            return Result.failure(workDataOf("error" to e.localizedMessage))
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = notificationManager?.getNotificationChannel(CHANNEL_ID)
            if (notificationChannel == null) {
                notificationManager?.createNotificationChannel(
                    NotificationChannel(
                        CHANNEL_ID, TAG, NotificationManager.IMPORTANCE_LOW
                    )
                )
            }
        }
    }

//    private suspend fun loadNextData(inputData:String){
//        fetechAllWallpapersUsecase.invoke("Bearer $inputData",currentPage.toString(),"200").collect(){result ->
////                Log.e(TAG, "doWork: $it")
//
//            when (result) {
//                is Response.Loading -> {
//
//                }
//
//                is Response.Success -> {
//                    result.data?.forEach { item ->
//
//                        val model = SingleDatabaseResponse(item.id,item.cat_name,item.image_name,AdConfig.HD_ImageUrl+item.url,AdConfig.Compressed_Image_url+item.url,item.likes,item.liked,item.size,item.Tags,item.capacity)
//                        appDatabase.wallpapersDao().insert(model)
//
//
////                            if (item == result.data.last()){
////                                resulta = Result.success()
////                            }
//                    }
//
//                    currentPage++
//
//                    loadNextData(inputData)
//
//                    // Set the result as success only when it's the last item in the page
//                    if (result.data?.isEmpty() == true || result.data?.size!! < 100) {
//
//                    }
//                }
//
//                is Response.Error -> {
//
//                }
//
//                else -> {
////                        Toast.makeText(requireContext(), "it is in else clause", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//
//
//        }
//    }

//    private suspend fun downloadFileFromUri(
//        urlImage: String
//    ): Result {
//        var count = 0
//        notification = notificationBuilder
//            .build()
//        val foregroundInfo = ForegroundInfo(NOTIFICATION_ID, notification)
//
//        setForeground(foregroundInfo)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
////            try {
////                val url = URL(urlImage)
////                val urlConnection: URLConnection =
////                    withContext(Dispatchers.IO) {
////                        url.openConnection()
////                    }
////                withContext(Dispatchers.IO) {
////                    urlConnection.connect()
////                }
////
////                val inputStream: InputStream = BufferedInputStream(withContext(Dispatchers.IO) {
////                    urlConnection.getInputStream()
////                })
////
////                val contentValues = ContentValues().apply {
////                    put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis().toString() + ".png")
////                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
////                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/ArtBotic")
////                }
////
////                val resolver = appContext.contentResolver
////                val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
////
////                if (imageUri != null) {
////                    resolver.openOutputStream(imageUri)?.use { outputStream ->
////                        val buffer = ByteArray(54 * 1024 * 1024)
////                        var bytesRead: Int
////                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
////                            outputStream.write(buffer, 0, bytesRead)
////                        }
////                    }
////                }
////
////                inputStream.close()
////
////                return Result.success()
////            } catch (e: IOException) {
////                isException = true
////                localizedmessage = "File in URL not found"
////                return Result.failure(workDataOf("error" to localizedmessage))
////            }
//
//        } else {
////            try {
////                val url = URL(urlImage)
////                val urlConnection: URLConnection = withContext(Dispatchers.IO) {
////                    url.openConnection()
////                }
////                withContext(Dispatchers.IO) {
////                    urlConnection.connect()
////                }
////
////                val inputStream: InputStream = BufferedInputStream(withContext(Dispatchers.IO) {
////                    urlConnection.getInputStream()
////                })
////
////                val appContext = applicationContext // Make sure you have the correct context
////
////                // Get the app's private Pictures directory
//////            val picturesDirectory = appContext.getExternalFilesDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES))
////                val picturesDirectory =
////                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/ArtBotic")
////
////                // Check if the directory exists or create it if needed
////                if (picturesDirectory != null && !picturesDirectory.exists()) {
////                    picturesDirectory.mkdirs()
////                }
////
////                // Generate a unique file name (you can modify this as needed)
////                val fileName = System.currentTimeMillis().toString() + ".png"
////
////                // Create a File object for the destination file
////                val destinationFile = File(picturesDirectory, fileName)
////
////                // Create an output stream to write the image data
////                val outputStream = withContext(Dispatchers.IO) {
////                    FileOutputStream(destinationFile)
////                }
////
////                val buffer = ByteArray(16 * 1024) // Reduced buffer size
////                var bytesRead: Int
////                while (withContext(Dispatchers.IO) {
////                        inputStream.read(buffer)
////                    }.also { bytesRead = it } != -1) {
////                    withContext(Dispatchers.IO) {
////                        outputStream.write(buffer, 0, bytesRead)
////                    }
////                }
////
////                withContext(Dispatchers.IO) {
////                    inputStream.close()
////                    outputStream.close()
////                }
////
////
////                // Notify the MediaStore about the new file
////                MediaScannerConnection.scanFile(
////                    appContext,
////                    arrayOf(destinationFile.absolutePath),
////                    null,
////                    null
////                )
////
////                return Result.success()
////            } catch (e: IOException) {
////                val errorMessage = "Error downloading and saving image: ${e.message}"
////                return Result.failure(workDataOf("error" to errorMessage))
////            }
//        }
//
//
//    }

    companion object {

        const val TAG = "ForegroundWorker"
        const val NOTIFICATION_ID = 42
        const val CHANNEL_ID = "Job progress"
        const val ARG_PROGRESS = "Progress"

    }
}