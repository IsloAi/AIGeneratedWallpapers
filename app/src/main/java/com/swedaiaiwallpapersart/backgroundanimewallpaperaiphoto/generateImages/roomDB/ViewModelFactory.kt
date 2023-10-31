package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(private val database: AppDatabase,private val id: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoomViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RoomViewModel(database,id) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
