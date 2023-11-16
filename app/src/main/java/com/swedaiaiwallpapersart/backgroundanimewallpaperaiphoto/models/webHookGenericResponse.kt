package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models

data class webHookGenericResponse(
    val id: Int,
    val output: List<String>,
    val status: String,
    val track_id: String,
    val webhook_type: String
){}