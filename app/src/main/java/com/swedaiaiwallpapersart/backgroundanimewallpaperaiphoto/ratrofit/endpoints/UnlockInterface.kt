package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.Counter
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UnlockInterface {
    @POST("post_unlockimg.php")
    @JvmSuppressWildcards
    fun unlock(@Body body: Map<String, Any>): Call<Counter>
}