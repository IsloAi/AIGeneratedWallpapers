package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitInstance {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    fun getInstance(): Retrofit {
        val retrofit : Retrofit = Retrofit.Builder()
            .baseUrl("https://vps.edecator.com/wallpaper_App/V4/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit
    }
}