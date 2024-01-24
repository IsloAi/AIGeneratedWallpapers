package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response


data class SingleAllResponse(
    val id: Int,
    var cat_name: String? = null,
    var image_name:String ?= null,
    val url: String? = null,
    var likes: Int? = null,
    var liked: Boolean? = null,
    var size: Int? = null,
    var Tags: String? = null,
    var capacity: String? = null
)
