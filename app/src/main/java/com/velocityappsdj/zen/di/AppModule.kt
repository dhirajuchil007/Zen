package com.velocityappsdj.zen.di

import android.content.Context
import android.content.pm.PackageManager
import com.velocityappsdj.zen.room.NotificationDBBuilder
import com.velocityappsdj.zen.room.NotificationDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun providesDbRepository(@ApplicationContext appContext: Context): NotificationDatabase {
        return NotificationDBBuilder.getInstance(appContext)
    }

    @Provides
    @Singleton
    fun providesPackageManager(@ApplicationContext appContext: Context): PackageManager =
        appContext.packageManager
}