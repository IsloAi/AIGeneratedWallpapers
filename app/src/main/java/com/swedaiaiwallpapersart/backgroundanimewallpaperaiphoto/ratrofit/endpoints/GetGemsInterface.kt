package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.GetGemsData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface GetGemsInterface {
    @POST("getgems.php?")
    @JvmSuppressWildcards
    fun getGems( @Body body: Map<String, Any>): Call<GetGemsData>

}