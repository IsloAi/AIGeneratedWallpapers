package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "allWallpapers")
data class SingleDatabaseResponse(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,
    var cat_name: String? = null,
    var image_name:String ?= null,
    val hd_image_url: String? = null,
    val compressed_image_url: String? = null,
    var likes: Int? = null,
    var liked: Boolean? = null,
    var size: Int? = null,
    var Tags: String? = null,
    var capacity: String? = null,
    var unlocked:Boolean = true
)
