package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models

data class CatResponse(
    val id: Int,
    var image_name:String ?= null,
    var cat_name: String? = null,
    val hd_image_url: String? = null,
    val compressed_image_url: String? = null,
    var gems: Int? = null,
    var likes: Int? = null,
    var liked: Boolean? = null,
    var unlockimges: Boolean? = null,
    var img_size: Int? = null,
    var Tags: String? = null,
    var capacity: String? = null
)
