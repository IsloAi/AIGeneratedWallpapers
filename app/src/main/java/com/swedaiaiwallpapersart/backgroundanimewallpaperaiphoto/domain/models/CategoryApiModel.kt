package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models

import com.google.gson.annotations.SerializedName


data class CategoryApiModel(
    @SerializedName("cat_name")
    val categoryName: String?,
    @SerializedName("img_url")
    val categoryImage: String?
)
