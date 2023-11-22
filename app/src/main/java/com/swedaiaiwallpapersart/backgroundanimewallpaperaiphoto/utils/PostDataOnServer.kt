package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.View.GONE
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieAnimationView
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.ApiService
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.FeedbackInterface
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.PostGemsInterface
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.RetrofitInstance
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.UnlockInterface
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.GemsTextUpdate
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.Counter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.FeedbackModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.PostData
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.PostGemsData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
class PostDataOnServer {
    companion object{
        const val isAdFree = "free"
        const val isPlan = "Plan"
    }
    fun gemsPostData(context:Context,uniqueId: String, retrofit: Retrofit,gems:Int,isAds:String){
        val apiService = retrofit.create(PostGemsInterface::class.java)
        val postData = PostGemsData(uniqueId,gems,isAds)
        val call = apiService.postData(postData)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful){
                    MySharePreference.setGemsValue(context,gems)
                    if(isAds.contains("free")){
                        MySharePreference.setCounterValue(context, MySharePreference.getCounterValue(context)!!+1) }
                } else {
                    Toast.makeText(context, "onResponse error", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "onFailure error", Toast.LENGTH_SHORT).show()
            }
        })
    }

//    fun gemsPostData(context:Context,uniqueId: String, retrofit: Retrofit,gems:Int,isAds:String,layout:ConstraintLayout,model: CatResponse){
//        val apiService = retrofit.create(PostGemsInterface::class.java)
//        val postData = PostGemsData(uniqueId,gems,isAds)
//        val call = apiService.postData(postData)
//        call.enqueue(object : Callback<ResponseBody> {
//            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                if (response.isSuccessful) {
//                    model.unlockimges = true
//                    layout.visibility = GONE
//                    MySharePreference.setGemsValue(context,gems)
//                    if(isAds.contains("free")){
//                        MySharePreference.setCounterValue(context, MySharePreference.getCounterValue(context)!!+1)
//                    }
//                    Log.d("postDataTesting", "onResponse: Data updated successfully post request ${response.body()}")
//                } else {
//                    Toast.makeText(context, "onResponse error", Toast.LENGTH_SHORT).show()
//                }
//            }
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                Toast.makeText(context, "onFailure error", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
        fun unLocking(
            uniqueId: String,
            model: CatResponse,
            context: Context,
            leftGems: Int,
            gemsTextUpdate: GemsTextUpdate,
            dialog: Dialog,
            lockButton: ImageView,
            diamondIcon: ImageView,
            gemsView: TextView
        ) {
            val retrofit = RetrofitInstance.getInstance()
            val service = retrofit.create(UnlockInterface::class.java)
            val body: MutableMap<String, Any> = HashMap()
            body["uid"] = uniqueId
            body["imageid"] = model.id
            val call = service.unlock(body)
            call.enqueue(object : Callback<Counter> {
                override fun onResponse(call: Call<Counter>, response: Response<Counter>) {
                    if (response.isSuccessful) {
                        model.unlockimges = true
                        lockButton.visibility = GONE
                        diamondIcon.visibility = GONE
                        gemsView.visibility = GONE
                        gemsPostData(context, uniqueId, retrofit, leftGems, isPlan)
                        gemsTextUpdate.getGemsBack(leftGems)
                        dialog.dismiss()
                        Toast.makeText(context, "Successful buy", Toast.LENGTH_SHORT).show()
                        response.body()?.let {
                            val gemData = Counter(it.message)
                            Log.d("unloackTestingapi", "onResponse: model ${gemData}")
                        }
                    } else {
                        Log.d("unloackTestingapi", "onResponse: Response not successful")
                    }
                }

                override fun onFailure(call: Call<Counter>, t: Throwable) {
                    Log.d("unloackTestingapi", "onFailure: Failed to fetch data $t")
                }
            })
        }

    fun unLocking(
        uniqueId: String,
        model: CatResponse,
        context: Context,
        leftGems: Int
    ) {
        val retrofit = RetrofitInstance.getInstance()
        val service = retrofit.create(UnlockInterface::class.java)
        val body: MutableMap<String, Any> = HashMap()
        body["uid"] = uniqueId
        body["imageid"] = model.id
        val call = service.unlock(body)
        call.enqueue(object : Callback<Counter> {
            override fun onResponse(call: Call<Counter>, response: Response<Counter>) {
                if (response.isSuccessful) {
                    model.unlockimges = true
                    gemsPostData(context, uniqueId, retrofit, leftGems, isPlan)
//                    Toast.makeText(context, "Unlocked Successfully", Toast.LENGTH_SHORT).show()
                    response.body()?.let {
                        val gemData = Counter(it.message)
                        Log.d("unloackTestingapi", "onResponse: model ${gemData}")
                    }
                } else {
                    Log.d("unloackTestingapi", "onResponse: Response not successful")
                }
            }
            override fun onFailure(call: Call<Counter>, t: Throwable) {
                Log.d("unloackTestingapi", "onFailure: Failed to fetch data $t")
            }
        })
    }
        fun sendFeedback(
            context: Context,
            email: String,
            name: String,
            subject: String,
            message: String,
            uid: String
        ) {
            val retrofit = RetrofitInstance.getInstance()
            val apiService = retrofit.create(FeedbackInterface::class.java)
            val postData = FeedbackModel(email, name, subject, message, uid)
            val call = apiService.postData(postData)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        MySharePreference.setFeedbackValue(context, true)
                        Toast.makeText(
                            context,
                            "your feedback send successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(context, "onFailure error", Toast.LENGTH_SHORT).show()
                }
            })
        }

    private fun addFavourite(
        context: Context,
        model: CatResponse,
        favouriteButton: ImageView,
        likesTextView: TextView){

        val retrofit = RetrofitInstance.getInstance()
        val apiService = retrofit.create(ApiService::class.java)
        Log.d("modelidofImage", "addFavourite: ${model.id}")
        val postData = PostData(MySharePreference.getDeviceID(context)!!, model.id.toString())
        val call = apiService.postData(postData)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val message = response.body()?.string()
                    if(message=="Liked"){
                        val like = model.likes
                        val newLike = like!! + 1
                        model.likes=newLike
                        likesTextView.text = newLike.toString()
                        favouriteButton.setImageResource(R.drawable.heart_red)
                        Toast.makeText(context, "Liked", Toast.LENGTH_SHORT).show()
                    }else{
                        val like = model.likes
                        val newLike = like!! - 1
                        model.likes=newLike
                        likesTextView.text = newLike.toString()
                        favouriteButton.setImageResource(R.drawable.heart_unsel)
                        Toast.makeText(context, "UnLiked", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "onResponse error", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "onFailure error", Toast.LENGTH_SHORT).show()
            }
        })
    }


    }