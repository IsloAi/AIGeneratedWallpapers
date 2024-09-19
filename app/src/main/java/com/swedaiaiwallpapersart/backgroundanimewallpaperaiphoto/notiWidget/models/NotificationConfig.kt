package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.notiWidget.models

import java.io.Serializable

data class NotificationConfig(
    val is_active: Boolean,
    val notification_scheduler: List<NotificationScheduler>,
    val notification_data: List<NotificationData>
):Serializable