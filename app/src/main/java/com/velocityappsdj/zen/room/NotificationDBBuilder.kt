package com.velocityappsdj.zen.room

import android.content.Context
import androidx.room.Room

object NotificationDBBuilder {
    private var instance: NotificationDatabase? = null
    fun getInstance(context: Context): NotificationDatabase {
        if (instance == null) {
            synchronized(NotificationDatabase::class) {
                instance = buildRoomDB(context)
            }
        }
        return instance!!
    }

    private fun buildRoomDB(context: Context) =
        Room.databaseBuilder(
            context.applicationContext,
            NotificationDatabase::class.java,
            "notifications"
        ).build()
}