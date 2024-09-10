package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.notiWidget.models

data class NotificationConfig(
    val is_active: Boolean,
    val notification_scheduler: List<NotificationScheduler>,
    val notification_data: List<NotificationData>
)