package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils
import android.util.Log
import android.view.View.GONE
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.airbnb.lottie.LottieAnimationView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.CatResponseInterface
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.RetrofitInstance
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatNameResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyCatNameViewModel: ViewModel()  {

    private val wallpaperData = MutableLiveData<ArrayList<CatNameResponse>?>()
    fun getWallpapers(): MutableLiveData<ArrayList<CatNameResponse>?> {
        return wallpaperData
    }
    fun fetchWallpapers(progressBar: LottieAnimationView) {
        val retrofit = RetrofitInstance.getInstance()
        val service = retrofit.create(CatResponseInterface::class.java)
        val call: Call<ArrayList<CatNameResponse>> = service.getCategories()
        call.enqueue(object : Callback<ArrayList<CatNameResponse>> {
            override fun onResponse(call: Call<ArrayList<CatNameResponse>>, response: Response<ArrayList<CatNameResponse>>) {
                if (response.isSuccessful) {
                    progressBar.visibility= GONE
                    val catNameResponses: ArrayList<CatNameResponse>? = response.body()
                    if (catNameResponses != null) {
                      getWallpapers().value = catNameResponses
                    }
                } else {
                    // Handle error case
                    Log.d("responseOk", "onResponse: response not Empty ")
                }
            }

            override fun onFailure(call: Call<ArrayList<CatNameResponse>>, t: Throwable) {
                // Handle failure case
                Log.d("responseOk", "onResponse: response onFailure ")
            }

        })
    }
}