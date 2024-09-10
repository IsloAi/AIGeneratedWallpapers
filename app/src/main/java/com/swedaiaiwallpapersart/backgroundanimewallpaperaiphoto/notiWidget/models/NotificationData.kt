package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.notiWidget.models

data class NotificationData(
    val id: Int,
    val btn_text: Map<String, String>,
    val title: Map<String, String>,
    val message: Map<String, String>,
    val image: Map<String, String>
)