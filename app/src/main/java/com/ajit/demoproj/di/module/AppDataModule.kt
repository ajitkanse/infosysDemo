package com.ajit.demoproj.di.module

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ajit.demoproj.BuildConfig
import com.ajit.demoproj.data.api.ApiService
import com.ajit.demoproj.data.local.PostDao
import com.ajit.demoproj.data.local.PostDb
import com.ajit.demoproj.util.Constants
import com.ajit.demoproj.util.SchedulerProvider
import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
class AppDataModule {


    //  Remote Database Module
    @Provides
    @Singleton
    fun provideOkHttpClient(application: Application): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BASIC
        interceptor.level = HttpLoggingInterceptor.Level.HEADERS
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val cacheDir = File(application.cacheDir, UUID.randomUUID().toString())
        // 10 MiB cache
        val cache = Cache(cacheDir, 10 * 1024 * 1024)

        return OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(gson: Gson, okHttpClient: OkHttpClient): ApiService {
        return Retrofit.Builder()
            .baseUrl(Constants.BaseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build().create(ApiService::class.java)

    }

    @Provides
    @Singleton
    fun provideSchedulerProvider() = SchedulerProvider(
        Schedulers.io(),
        AndroidSchedulers.mainThread())

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
    }


    // Local Database Module

    @Provides
    @Singleton
    fun providePostDb(context: Context): PostDb {
        return Room.databaseBuilder(context, PostDb::class.java, Constants.ROOM_DB_NAME).build()
    }

    @Provides
    @Singleton
    fun providePostDao(postDb:PostDb): PostDao = postDb.postDao()


    // common

    @Provides
    @Singleton
    fun provideContext(application: Application): Context = application


}