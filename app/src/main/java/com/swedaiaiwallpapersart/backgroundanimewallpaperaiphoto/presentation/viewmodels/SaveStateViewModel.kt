package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.CatResponse


class SaveStateViewModel : ViewModel() {
    private var myData: Boolean = true


    private var currentTab: Int = 0
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

    fun setTab(tab: String) {
        _selectedTab.value = tab

    }


    fun getTab(): Int {
        return currentTab
    }

    fun setTab(tab: Int) {
        currentTab = tab
    }

    fun getData(): Boolean {
        return myData
    }
}