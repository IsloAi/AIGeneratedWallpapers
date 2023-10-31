package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ImageListViewModel:ViewModel() {
    var imageList = MutableLiveData<ArrayList<String>>()
    var prompt = MutableLiveData<String>()
}