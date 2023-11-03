package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.GetGemsData
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.GetGemsInterface
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GoogleLogin {
  fun fetchGems(context: Context, id: String, dialog: Dialog?, gemsText: TextView) {
        val postDataOnServer = PostDataOnServer()
        val retrofit = RetrofitInstance.getInstance()
        val service = retrofit.create(GetGemsInterface::class.java)
        val body: MutableMap<String, Any> = HashMap()
        body["uid"] = id
        val call = service.getGems(body)
        call.enqueue(object : Callback<GetGemsData> {
            override fun onResponse(call: Call<GetGemsData>, response: Response<GetGemsData>) {
                if (response.isSuccessful) {
                    Log.d("postDataTesting", "onResponse: success ${response.body()}")
                    response.body()?.let {
                        val gemData = GetGemsData(it.uid,it.gems,it.counter,it.message)
                        Log.d("postDataTesting", "onResponse: model ${gemData}")
                        if (gemData.uid == id) {
                            MySharePreference.setGemsValue(context, gemData.gems!!)
                            MySharePreference.setCounterValue(context,gemData.counter!!)
                            MySharePreference.setDeviceID(context,id)
                            gemsText.text = MySharePreference.getGemsValue(context)!!.toString()
                        }else{
                            MySharePreference.setDate(context,id)
                            postDataOnServer.gemsPostData(context,id,RetrofitInstance.getInstance(),20,"plan")
                            gemsText.text= "20"
                        }
                    }
                    dialog?.dismiss()
                } else {
                    Log.d("responseNotOk", "onResponse: Response not successful")
                }
            }
            override fun onFailure(call: Call<GetGemsData>, t: Throwable) {
                Toast.makeText(context, "Error Loading", Toast.LENGTH_SHORT).show()
                // Handle failure case
                Log.d("responseFailure", "onFailure: Failed to fetch data $t")
            }
        })
    }
    fun fetchGems(context: Context, id: String) {
        val postDataOnServer = PostDataOnServer()
        val retrofit = RetrofitInstance.getInstance()
        val service = retrofit.create(GetGemsInterface::class.java)
        val body: MutableMap<String, Any> = HashMap()
        body["uid"] = id
        val call = service.getGems(body)
        call.enqueue(object : Callback<GetGemsData> {
            override fun onResponse(call: Call<GetGemsData>, response: Response<GetGemsData>) {
                if (response.isSuccessful) {
                    Log.d("postDataTesting", "onResponse: success ${response.body()}")
                    response.body()?.let {
                        val gemData = GetGemsData(it.uid,it.gems,it.counter,it.message)
                        Log.d("postDataTesting", "onResponse: model ${gemData}")
                        if (gemData.uid == id) {
                            MySharePreference.setGemsValue(context, gemData.gems!!)
                            MySharePreference.setCounterValue(context,gemData.counter!!)
                            MySharePreference.setDeviceID(context,id)
                        }else{
                            MySharePreference.setDeviceID(context,id)
                            postDataOnServer.gemsPostData(context,id,RetrofitInstance.getInstance(),20,"plan")
                        }
                    }
                } else {
                    Log.d("responseNotOk", "onResponse: Response not successful")
                }
            }
            override fun onFailure(call: Call<GetGemsData>, t: Throwable) {
                Toast.makeText(context, "Error Loading", Toast.LENGTH_SHORT).show()
                // Handle failure case
                Log.d("responseFailure", "onFailure: Failed to fetch data $t")
            }
        })
    }

}