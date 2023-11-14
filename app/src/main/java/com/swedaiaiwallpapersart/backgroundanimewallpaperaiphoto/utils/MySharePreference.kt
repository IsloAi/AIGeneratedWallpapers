package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class MySharePreference {
    companion object{
    fun setDeviceID(context: Context,value:String){
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
        val myEdit = sharedPreferences.edit()
        myEdit.putString("key",value)
        myEdit.apply()
    }

    fun getDeviceID(context: Context):String?{
        val sp: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
        return sp.getString("key","")
    }


        fun setLanguage(context: Context,value:String){
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            val myEdit = sharedPreferences.edit()
            myEdit.putString("language",value)
            myEdit.apply()
        }



        fun getLanguage(context: Context):String?{
            val sp: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            return sp.getString("language","")
        }

        fun setGemsValue(context: Context,value:Int){
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            val myEdit = sharedPreferences.edit()
            myEdit.putInt("gems",value)
            myEdit.apply()
        }

        fun getGemsValue(context: Context):Int?{
            val sp: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            return sp.getInt("gems",0)
        }

        fun setCounterValue(context: Context,value:Int){
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            val myEdit = sharedPreferences.edit()
            myEdit.putInt("counter",value)
            myEdit.apply()
        }

        fun getCounterValue(context: Context):Int?{
            val sp: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            return sp.getInt("counter",0)
        }

        fun setFeedbackValue(context: Context,value:Boolean){
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            val myEdit = sharedPreferences.edit()
            myEdit.putBoolean("feedback",value)
            myEdit.apply()
        }

        fun getFeedbackValue(context: Context):Boolean{
            val sp: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            return sp.getBoolean("feedback",false)
        }

        fun setOnboarding(context: Context,value:Boolean){
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            val myEdit = sharedPreferences.edit()
            myEdit.putBoolean("onboard",value)
            myEdit.apply()
        }

        fun getOnboarding(context: Context):Boolean{
            val sp: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            return sp.getBoolean("onboard",false)
        }

        fun setUserIsSaved(context: Context,value:Boolean){
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            val myEdit = sharedPreferences.edit()
            myEdit.putBoolean("setuser",value)
            myEdit.apply()
        }

        fun getUserIsSaved(context: Context):Boolean{
            val sp: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            return sp.getBoolean("setuser",false)
        }

        fun setDailyRewardCounter(context: Context,value:Boolean){
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            val myEdit = sharedPreferences.edit()
            myEdit.putBoolean("dailyRewardCounter",value)
            myEdit.apply()
        }

        fun getDailyRewardCounter(context: Context):Boolean{
            val sp: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            return sp.getBoolean("dailyRewardCounter",false)
        }

        fun setDate(context: Context,value:String){
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            val myEdit = sharedPreferences.edit()
            myEdit.putString("date",value)
            myEdit.apply()
        }
        fun getDate(context: Context):String?{
            val sp: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            return sp.getString("date","")
        }

        fun setFavouriteSaveState(context: Context,setFavouriteSaveState:Int){
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            val myEdit = sharedPreferences.edit()
            myEdit.putInt("setFavouriteSaveState",setFavouriteSaveState)
            myEdit.apply()
        }
        fun getFavouriteSaveState(context: Context):Int{
            val sp: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            return sp.getInt("setFavouriteSaveState",1)
        }


    }
}