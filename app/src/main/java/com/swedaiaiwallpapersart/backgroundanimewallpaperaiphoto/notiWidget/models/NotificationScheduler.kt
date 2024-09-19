package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.notiWidget.models

import java.io.Serializable

data class NotificationScheduler(val is_active: Boolean, val day: Int, val time: String):
    Serializable
