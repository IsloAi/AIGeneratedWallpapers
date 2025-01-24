package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.appsDrawerFragment

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AppsInfo")
data class AppInfo(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo("AppLabel")
    var label: String,
    @ColumnInfo("packageName")
    var packageName: String
)
