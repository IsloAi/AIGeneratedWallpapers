package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.Counter
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ResetCounterInterface {
    @POST("make_counter_zero.php")
    @JvmSuppressWildcards
    fun resetCounter(@Body body: Map<String, Any>): Call<Counter>
}