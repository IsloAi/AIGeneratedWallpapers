package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {
    fun getInstance(): Retrofit {
        val retrofit : Retrofit = Retrofit.Builder()
            .baseUrl("https://edecator.com/wallpaperApp/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit
    }
}