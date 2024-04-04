package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.utilsIG

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.View.GONE
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.airbnb.lottie.LottieAnimationView
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.models.GetResponseIG
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.models.PostRequestModelIG
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.retrofitIG.RetrofitIntenseIG
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.retrofitIG.endPointsIG.PostRequestIG
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.FirebaseUtils
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ImageGenerateViewModel:ViewModel() {
    val responseData: MutableLiveData<GetResponseIG> by lazy {
        MutableLiveData<GetResponseIG>()
    }
    fun loadData(context: Context,prompt: String, progress: Dialog) {
        val retrofit = RetrofitIntenseIG.getInstance()
        val apiService = retrofit.create(PostRequestIG::class.java)
        val postData = PostRequestModelIG(
            Parameters.canvasPos,
            Parameters.varendpoint,
            Parameters.enhance_prompt,
            Parameters.exact_Nprompt,
            Parameters.guess_mode,
            Parameters.guidance_scale,
            Parameters.height,
            Parameters.instance_prompt,
            Parameters.key,
            Parameters.model_id,
            Parameters.model_name,
            Parameters.multi_lingual,
            Parameters.negative_prompt,
            Parameters.num_inference_steps,
            Parameters.panorama,
            prompt,
            Parameters.promptsBuilder,
            Parameters.safety_checker,
            Parameters.samples,
            Parameters.scheduler,
            Parameters.self_attention,
            Parameters.steps,
            Parameters.strength,
            MySharePreference.getFirebaseToken(context),
            Parameters.type,
            Parameters.upscale,
            Parameters.webhook,
            Parameters.width
        )
        Log.e("TAG", "loadData: $postData")
        val call = apiService.postData(postData)
        call.enqueue(object : Callback<GetResponseIG> {
            override fun onResponse(call: Call<GetResponseIG>, response: Response<GetResponseIG>) {
                if (response.isSuccessful) {
                    progress.dismiss()
                    val model: GetResponseIG? = response.body()
                    Log.d("imageLists", "model : ${model}")
//                    Toast.makeText(context, "Generated Successfully", Toast.LENGTH_SHORT).show()
                    model?.let {
                        responseData.postValue(model)
                    }
                }
            }
            override fun onFailure(call: Call<GetResponseIG>, t: Throwable) {
                progress.dismiss()
                t.printStackTrace()
                Log.d("imageLists", "error : ${t.printStackTrace()}")
                Toast.makeText(context, "Generated Error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}