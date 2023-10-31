package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.models

data class GetResponseIG(
    var status: String? = null,
    var tip: String? = null,
    var eta: Double? = null,
    var messege: String? = null,
    var generationTime: Double? = null,
    var id: Int? = null,
    var output: ArrayList<String> = arrayListOf(),
    var webhook_status: String? = null,
    var meta: Meta? = Meta(),
    var future_links: ArrayList<String> = arrayListOf(),
    var fetch_result: String? = null,
    )