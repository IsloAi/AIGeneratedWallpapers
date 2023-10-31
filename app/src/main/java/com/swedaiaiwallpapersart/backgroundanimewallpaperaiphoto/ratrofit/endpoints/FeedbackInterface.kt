package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.FeedbackModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface FeedbackInterface {
    @POST("post_feedback.php")
    fun postData(@Body data: FeedbackModel): Call<ResponseBody>
}

