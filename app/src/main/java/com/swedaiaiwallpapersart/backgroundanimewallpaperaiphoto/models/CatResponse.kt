package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models
data class CatResponse(
    val id                   :Int,
    val cat_name             : String? = null,
    val hd_image_url         : String? = null,
    val compressed_image_url : String? = null,
    val gems                 :Int? =null,
    var likes                :Int? =null,
    var liked                :Boolean? = null,
    var unlockimges                :Boolean? = null
)
