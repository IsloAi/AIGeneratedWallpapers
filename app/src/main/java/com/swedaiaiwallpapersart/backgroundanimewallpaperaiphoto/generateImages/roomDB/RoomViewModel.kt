package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class RoomViewModel(private val database: AppDatabase, id: Int) : ViewModel() {
    val allGetResponseIG: LiveData<List<GetResponseIGEntity>> = database.getResponseIGDao().getAllGetResponseIG()
    val getResponseIGById: LiveData<GetResponseIGEntity?> = database.getResponseIGDao().getGetResponseIGByID(id)

    val myFavouriteList : LiveData<List<FavouriteListIGEntity>> = database.getFavouriteList().getAllFavouriteList()

//    val allMeta: LiveData<List<MetaEntity>> = database.metaDao().getAllMeta()

    fun insertGetResponseIG(getResponseIG: GetResponseIGEntity) {
        viewModelScope.launch {
            database.getResponseIGDao().insert(getResponseIG)
        }
    }


    fun deleteSingleImage(responseIGDao: GetResponseIGEntity){
        viewModelScope.launch {
            database.getResponseIGDao().deleteCreation(responseIGDao)
        }
    }


    fun deleteAll(list:ArrayList<GetResponseIGEntity>){
        viewModelScope.launch {
            database.getResponseIGDao().deleteCreations(list)
        }
    }

    fun insertFavourite(favouriteListIGEntity: FavouriteListIGEntity){
        viewModelScope.launch {
            database.getFavouriteList().insert(favouriteListIGEntity)
        }
    }

    fun deleteItem(path:String){
        viewModelScope.launch {
            database.getFavouriteList().deleteByPath(path)
        }
    }

}
