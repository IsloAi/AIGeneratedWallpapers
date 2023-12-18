package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto

import androidx.lifecycle.ViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.CatResponse


class SaveStateViewModel:ViewModel() {
    private var myData: Boolean = true


    private val catList: ArrayList<CatResponse> = ArrayList()

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

    fun getData(): Boolean {
        return myData
    }
}