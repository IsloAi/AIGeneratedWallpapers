package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.dependencyInjection

import android.content.Context
import androidx.work.WorkManager
import com.google.gson.GsonBuilder
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.endpoints.APIEndpoints
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.repository.WallpaperAPIRepositoryImplementation
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MyApp
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.domain.repository.WallpaperAPIRepository
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.presentation.utils.AdConfig
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
    fun providesWebApiInterface(): APIEndpoints {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()
//        if (BuildConfig.DEBUG) {
//            httpClient.addInterceptor(logging)
//        }
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
            .build().create(APIEndpoints::class.java)
    }

    @Provides
    fun providesWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    fun provideWallpaperRepo(webApiInterface: APIEndpoints): WallpaperAPIRepository {
        return WallpaperAPIRepositoryImplementation(webApiInterface)
    }

    @Provides
    fun providesAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase(context)
    }

    /*@Provides
    fun provideFetchDataRepository(
        appDatabase: AppDatabase
    ): FetchDataRepository {
        return FetchDataRepositoryImpl(appDatabase)
    }*/

    @Provides
    @Singleton
    fun providesApplication(@ApplicationContext app: Context): MyApp {
        return app as MyApp
    }

}



