package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager

import com.google.gson.GsonBuilder
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ads.MyApp
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.remote.EndPointsInterface
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.remote.dao.AppInfoDAO
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.remote.dao.LiveWallpaperDao
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.remote.dao.WallpapersDao
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.repositry.FetchDataRepositoryImpl
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.repositry.WallpaperRepositoryImp
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.repositry.FetchDataRepository
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.repositry.WallpaperRepositry
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.FavouriteListIGDao
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.GetResponseIGDao
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.AdConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object HiltModule {

    @Singleton
    @Provides
    fun providesWebApiInterface(): EndPointsInterface {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()

        httpClient.addInterceptor(logging)
        val gson = GsonBuilder()
            .setLenient()
            .create()
        httpClient.addInterceptor(Interceptor { chain ->
            val original: Request = chain.request()
            val originalHttpUrl: HttpUrl = original.url
            val url = originalHttpUrl.newBuilder()
                .build()
            val requestBuilder: Request.Builder = original.newBuilder()
                .url(url)
            val request: Request = requestBuilder.build()
            chain.proceed(request)
        })
        httpClient.readTimeout(120, TimeUnit.SECONDS)
            .connectTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
        return Retrofit.Builder().baseUrl(AdConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient.build())
            .build().create(EndPointsInterface::class.java)
    }

    @Provides
    fun providesWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    fun provideWallpaperRepo(webApiInterface: EndPointsInterface): WallpaperRepositry {
        return WallpaperRepositoryImp(webApiInterface)
    }

    /*@Provides
    fun providesAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase(context)
    }*/

    @Provides
    @Singleton
    fun providesAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "appDatabase"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun providesAppDao(appDatabase: AppDatabase): AppInfoDAO {
        return appDatabase.AppsDAO()
    }
    @Provides
    fun providesGetResponseIGDao(appDatabase: AppDatabase): GetResponseIGDao {
        return appDatabase.getResponseIGDao()
    }

    @Provides
    fun providesFavouriteListIGDao(appDatabase: AppDatabase): FavouriteListIGDao {
        return appDatabase.getFavouriteList()
    }

    @Provides
    fun providesWallpapersDao(appDatabase: AppDatabase): WallpapersDao {
        return appDatabase.wallpapersDao()
    }

    @Provides
    fun providesLiveWallpaperDao(appDatabase: AppDatabase): LiveWallpaperDao {
        return appDatabase.liveWallpaperDao()
    }

    @Provides
    fun provideFetchDataRepository(
        appDatabase: AppDatabase
    ): FetchDataRepository {
        return FetchDataRepositoryImpl(appDatabase)
    }

    @Provides
    @Singleton
    fun providesApplication(@ApplicationContext app: Context): MyApp {
        return app as MyApp
    }

}



