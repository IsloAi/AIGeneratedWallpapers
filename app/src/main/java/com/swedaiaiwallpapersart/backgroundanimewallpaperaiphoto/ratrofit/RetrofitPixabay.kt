package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.temp.PixabayApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitPixabay {
    private const val BASE_URL = "https://pixabay.com"

    val service: PixabayApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(PixabayApiService::class.java)
    }
}