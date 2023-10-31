package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_list")
data class FavouriteListIGEntity(
    @PrimaryKey(autoGenerate = true)
    val id :Int? = null,
    val imageId: Int = 0,
    val image: String?,
    val prompt: String?
)
