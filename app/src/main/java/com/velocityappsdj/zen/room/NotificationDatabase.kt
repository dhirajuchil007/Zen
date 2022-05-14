package com.velocityappsdj.zen.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.velocityappsdj.zen.fragments.WhiteList

@Database(
    entities = [NotificationEntity::class, WhiteListEntity::class, BatchTimeEntity::class],
    version = 1
)
abstract class NotificationDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao
    abstract fun whiteListDao(): WhiteListDao
    abstract fun batchTimeDao(): BatchTimeDao
}