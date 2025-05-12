package com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.domain.models

data class SingleResponse(
    val id: Int,
    var cat_name: String? = null,
    var image_name: String? = null,
    val url: String? = null,
    var likes: Int? = null,
    var liked: Boolean? = null,
    var size: Int? = null,
    var Tags: String? = null,
    var capacity: String? = null
)
