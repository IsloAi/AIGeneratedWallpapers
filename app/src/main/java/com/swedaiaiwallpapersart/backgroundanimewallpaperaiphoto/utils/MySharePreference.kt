package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class MySharePreference {
    companion object{
        private const val LAST_DISMISSED_TIME = "lastDismissedTime"

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


        fun setAnimationPath(context: Context,value:String){
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            val myEdit = sharedPreferences.edit()
            myEdit.putString("battery",value)
            myEdit.apply()
        }

        fun getAnimationPath(context: Context):String?{
            val sp: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            return sp.getString("battery","")
        }


        fun setFireBaseToken(context: Context,value:String){
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            val myEdit = sharedPreferences.edit()
            myEdit.putString("token",value)
            myEdit.apply()
        }

        fun getFirebaseToken(context: Context):String?{
            val sp: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            return sp.getString("token","")
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


        fun setLanguageposition(context: Context,value:Int){
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            val myEdit = sharedPreferences.edit()
            myEdit.putInt("position",value)
            myEdit.apply()
        }

        fun getLanguageposition(context: Context):Int?{
            val sp: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            return sp.getInt("position",0)
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


        fun setFileName(context: Context,value:String){
            val sharedPreferences: SharedPreferences = context.applicationContext.getSharedPreferences("Shared", MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()
            val myEdit = sharedPreferences.edit()
            myEdit.putString("video",value)
            myEdit.apply()
        }
        fun getFileName(context: Context):String?{
            val sp: SharedPreferences = context.applicationContext.getSharedPreferences("Shared", MODE_PRIVATE)
            return sp.getString("video","")
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


        fun firstLiveWallpaper(context: Context,value:Boolean){
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            val myEdit = sharedPreferences.edit()
            myEdit.putBoolean("Live",value)
            myEdit.apply()
        }

        fun getfirstLiveWallpaper(context: Context):Boolean{
            val sp: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            return sp.getBoolean("Live",false)
        }

        fun firstWallpaperSet(context: Context,value:Boolean){
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            val myEdit = sharedPreferences.edit()
            myEdit.putBoolean("regular",value)
            myEdit.apply()
        }

        fun getfirstWallpaperSet(context: Context):Boolean{
            val sp: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            return sp.getBoolean("regular",false)
        }

        fun artGeneratedFirst(context: Context,value:Boolean){
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            val myEdit = sharedPreferences.edit()
            myEdit.putBoolean("art",value)
            myEdit.apply()
        }

        fun getartGeneratedFirst(context: Context):Boolean{
            val sp: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            return sp.getBoolean("art",false)
        }


        fun setFeedbackSession1Completed(context: Context,value:Boolean){
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            val myEdit = sharedPreferences.edit()
            myEdit.putBoolean("session1",value)
            myEdit.apply()
        }

        fun getFeedbackSession1Completed(context: Context):Boolean{
            val sp: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            return sp.getBoolean("session1",false)
        }


        fun setFeedbackSession2Completed(context: Context,value:Boolean){
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            val myEdit = sharedPreferences.edit()
            myEdit.putBoolean("session2",value)
            myEdit.apply()
        }

        fun getFeedbackSession2Completed(context: Context):Boolean{
            val sp: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            return sp.getBoolean("session2",false)
        }
        fun setReviewedSuccess(context: Context,value:Boolean){
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            val myEdit = sharedPreferences.edit()
            myEdit.putBoolean("reviewed",value)
            myEdit.apply()
        }

        fun getReviewedSuccess(context: Context):Boolean{
            val sp: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            return sp.getBoolean("reviewed",false)
        }

        fun setUserCancelledprocess(context: Context,value:Boolean){
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            val myEdit = sharedPreferences.edit()
            myEdit.putBoolean("cancel",value)
            myEdit.apply()
        }

        fun getUserCancelledprocess(context: Context):Boolean{
            val sp: SharedPreferences = context.getSharedPreferences("MySpValue", MODE_PRIVATE)
            return sp.getBoolean("cancel",false)
        }

        fun setLastDismissedTime(context: Context, time: Long) {
            val sharedPreferences = context.getSharedPreferences("MySpValue",MODE_PRIVATE)
            sharedPreferences.edit().putLong(LAST_DISMISSED_TIME, time).apply()
        }

        fun getLastDismissedTime(context: Context): Long {
            val sharedPreferences = context.getSharedPreferences("MySpValue", Context.MODE_PRIVATE)
            return sharedPreferences.getLong(LAST_DISMISSED_TIME, 0L)
        }

    }
}