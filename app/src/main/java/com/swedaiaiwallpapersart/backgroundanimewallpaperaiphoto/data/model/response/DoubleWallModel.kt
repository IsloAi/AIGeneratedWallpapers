package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response

data class DoubleWallModel(
    val id:Int,
    val hd_url1: String,
    val compress_url1: String,
    val size1: Int,
    val hd_url2: String,
    val compress_url2: String,
    val size2: Int,
    var downloaded:Boolean = false
)
