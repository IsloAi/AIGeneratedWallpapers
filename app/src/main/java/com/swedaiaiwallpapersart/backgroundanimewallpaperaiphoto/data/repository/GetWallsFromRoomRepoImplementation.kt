package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.repository

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.roomDB.dao.ChargingAnimationDao
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.roomDB.dao.DoubleWallpaperDao
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.roomDB.dao.LiveWallpaperDao
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.roomDB.dao.WallpapersDao
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.ChargingAnimModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.DoubleWallModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.LiveWallpaperModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.models.SingleDatabaseResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.repository.GetWallpaperFromRoomRepository
import javax.inject.Inject

//Created on 14/5/25 by U5M4N-K071N
/*It's the fear of the bug unseen 
that makes coding harder to begin.*/

class GetWallsFromRoomRepoImplementation @Inject constructor(
    val dao: WallpapersDao,
    private val liveDao: LiveWallpaperDao,
    private val doubleDao: DoubleWallpaperDao,
    private val chargingDao: ChargingAnimationDao

) : GetWallpaperFromRoomRepository {

    override suspend fun getAllWallpapersFromRoom(): List<SingleDatabaseResponse> {
        return dao.getAllWallpapers()
    }

    override suspend fun getTrendingWallpapersFromRoom(): List<SingleDatabaseResponse> {
        return dao.getTrendingWallpapers()
    }

    override suspend fun getPopularWallpapersFromRoom(): List<SingleDatabaseResponse> {
        return dao.getPopularWallpaper()
    }

    override suspend fun getAllLiveWallpapersFromRoom(): List<LiveWallpaperModel> {
        return liveDao.getAllWallpapers()
    }

    override suspend fun getAllDoubleWallpapersFromRoom(): List<DoubleWallModel> {
        return doubleDao.getAllDoubleWallpapers()
    }

    override suspend fun getCategoryWallpaperFromRoom(cat: String): List<SingleDatabaseResponse> {
        return dao.getCategoryWallpaper(cat)
    }

    override suspend fun getCarWallpapersFromRoom(): List<SingleDatabaseResponse> {
        return dao.getCarWallpapers()
    }

    override suspend fun getBatteryWallpapersFromRoom(): List<ChargingAnimModel> {
        return chargingDao.getAllAnimations()
    }

    override suspend fun getLiveCategoryWallpaperFromRoom(cat: String): List<LiveWallpaperModel> {
        return liveDao.getLiveCategoryWallpaper(cat)
    }

    override suspend fun updateLiveFavourite(liked: Boolean, Id: Int) {
        return liveDao.updateLiveFavourite(liked, Id)
    }

    override suspend fun getLiveFavourites(): List<LiveWallpaperModel> {
        return liveDao.getLiveFavourites()
    }

    override suspend fun updateStaticFavourite(liked: Boolean, Id: Int) {
        return dao.updateStaticFavourite(liked, Id)
    }

    override suspend fun getStaticFavourites(): List<SingleDatabaseResponse> {
        return dao.getStaticFavourites()
    }


}