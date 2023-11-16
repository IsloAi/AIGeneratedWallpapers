package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "get_response_ig")
data class GetResponseIGEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val status: String?,
    val generationTime: Double?,
    var output: ArrayList<String>? = arrayListOf(),
    val webhook_status: String?,
    val future_links: ArrayList<String>? = arrayListOf(),
    val prompt: String?,
    var isSelected:Boolean = false
)
