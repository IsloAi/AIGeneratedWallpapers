package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ListResponseInterfaceNoUid {
    @GET("api.php")
    fun getList (@Query("cat") catName: String):
            Call<List<CatResponse>>
}