package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.temp

import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query


interface PixabayApiService {
    @POST("/api/videos/")
    suspend fun getVideos(
        @Query("key") apiKey: String,
        @Query("q") query: String
    ): Response<PixabayVideoResponse>
}
