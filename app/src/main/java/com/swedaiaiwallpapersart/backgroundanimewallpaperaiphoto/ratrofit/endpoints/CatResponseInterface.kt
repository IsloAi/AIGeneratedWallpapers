package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatNameResponse
import retrofit2.Call
import retrofit2.http.GET
interface CatResponseInterface {
    @GET("getcategory.php")
    fun getCategories(): Call<ArrayList<CatNameResponse>>

}