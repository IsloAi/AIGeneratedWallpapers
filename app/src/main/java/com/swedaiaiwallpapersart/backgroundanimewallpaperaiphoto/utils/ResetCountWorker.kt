package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.endpoints.ResetCounterInterface
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.RetrofitInstance
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.Counter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class ResetCountWorker(context: Context,workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        Log.d("tracingWorkManager", "doWork: working")
        val currentTime = Calendar.getInstance()
        val targetTime = Calendar.getInstance().apply {
             add(Calendar.MINUTE, 0)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.SECOND, 0)
        }
        if (currentTime.timeInMillis >= targetTime.timeInMillis) {
            Log.d("tracingWorkManager", "doWork: working after 15 mints")
            resetCounter(MySharePreference.getDeviceID(applicationContext)!!)
            MySharePreference.setDailyRewardCounter(applicationContext,false)
        }
        return Result.success()
    }
    private fun resetCounter(uniqueId: String) {
        val retrofit = RetrofitInstance.getInstance()
        val service = retrofit.create(ResetCounterInterface::class.java)
        val body: MutableMap<String, Any> = HashMap()
        body["uid"] = uniqueId
        val call = service.resetCounter(body)
        call.enqueue(object : Callback<Counter> {
            override fun onResponse(call: Call<Counter>, response: Response<Counter>) {
                if (response.isSuccessful) {
                    Log.d("CounterTestingapi", "onResponse: success ${response.body()}")
                    response.body()?.let {
                        val gemData = Counter(it.message)
                        Log.d("CounterTestingapi", "onResponse: model ${gemData}")
                    }
                } else {
                    Log.d("CounterTestingapi", "onResponse: Response not successful")
                }
            }
            override fun onFailure(call: Call<Counter>, t: Throwable) {
                Log.d("CounterTestingapi", "onFailure: Failed to fetch data $t")
            }
        })
    }

}
