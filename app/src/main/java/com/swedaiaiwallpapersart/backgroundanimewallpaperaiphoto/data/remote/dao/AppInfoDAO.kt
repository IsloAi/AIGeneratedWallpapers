package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.remote.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.appsDrawerFragment.AppInfo


@Dao
interface AppInfoDAO {

    @Query("Select * from AppsInfo")
    suspend fun getAllApps(): List<AppInfo>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertApp(app: AppInfo)


}