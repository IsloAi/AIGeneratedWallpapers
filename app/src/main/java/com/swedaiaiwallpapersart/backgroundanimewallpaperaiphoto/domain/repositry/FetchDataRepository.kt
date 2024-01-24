package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.repositry

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.model.response.SingleDatabaseResponse
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.Response
import kotlinx.coroutines.flow.Flow


interface FetchDataRepository {

    fun fetechAllWallpapers(): Flow<Response<List<SingleDatabaseResponse>>>
}