package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.MostDownloadedImageModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST


interface MostDownloadImages {
    @GET("get_mostdownloaded.php")
    fun postData(): Call<ResponseBody>
}