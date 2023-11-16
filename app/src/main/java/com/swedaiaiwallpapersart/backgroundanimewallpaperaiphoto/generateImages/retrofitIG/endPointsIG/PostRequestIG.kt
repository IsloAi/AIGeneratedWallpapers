package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.retrofitIG.endPointsIG

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.models.GetResponseIG
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.models.PostRequestModelIG
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface PostRequestIG {
    @POST("text_to_img.php")
    fun postData(@Body data: PostRequestModelIG): Call<GetResponseIG>
}