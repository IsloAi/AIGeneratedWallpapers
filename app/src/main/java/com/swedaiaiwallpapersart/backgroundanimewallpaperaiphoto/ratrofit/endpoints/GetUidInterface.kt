package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.Counter
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface GetUidInterface {
    @POST("check_uid.php?")
    @JvmSuppressWildcards
    fun getUid( @Body body: Map<String, Any>): Call<Counter>

}