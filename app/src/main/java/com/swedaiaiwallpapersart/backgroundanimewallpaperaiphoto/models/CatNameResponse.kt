package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CatNameResponse (
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,
    var cat_name : String? = null,
    var img_url  : String? = null)