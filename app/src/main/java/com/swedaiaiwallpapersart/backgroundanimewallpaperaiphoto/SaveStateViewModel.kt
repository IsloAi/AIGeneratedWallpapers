package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse


class SaveStateViewModel:ViewModel() {
    private var myData: Boolean = true


    private var currentTab:String = ""
    private val catList: ArrayList<CatResponse> = ArrayList()

    private val _selectedTab = MutableLiveData<String>()
    val selectedTab: LiveData<String> = _selectedTab

    fun setCatList(list: ArrayList<CatResponse>) {
        catList.clear()
        catList.addAll(list)
    }




    fun getCatList(): ArrayList<CatResponse> {
        return catList
    }

    fun setData(data: Boolean) {
        myData = data
    }

    fun setTab(tab:String){
        _selectedTab.value = tab

    }


    fun getTab():String{
        return currentTab
    }

    fun getData(): Boolean {
        return myData
    }
}