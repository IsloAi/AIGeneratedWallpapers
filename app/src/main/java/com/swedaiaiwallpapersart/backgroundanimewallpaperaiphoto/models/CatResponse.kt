package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models
data class CatResponse(
    val id                   :Int,
    var cat_name             : String? = null,
    val hd_image_url         : String? = null,
    val compressed_image_url : String? = null,
    var gems                 :Int? =null,
    var likes                :Int? =null,
    var liked                :Boolean? = null,
    var unlockimges                :Boolean? = null,
    var img_size: Int? = null, // Added field
    var tags: String? = null, // Added field (assuming "Tags" is an array)
    var capacity: String? = null // Added field
)
