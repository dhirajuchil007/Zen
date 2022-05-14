package com.velocityappsdj.zen.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface NotificationDao {
    @Query("Select * from notification_entity order by timestamp desc")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notificationEntity: NotificationEntity): Void

    @Query("select * from notification_entity where timestamp > :startTimestamp")
    fun getTodaySNotificationCount(startTimestamp: Long): LiveData<List<NotificationEntity>>

    @Query("select * from notification_entity where batchNumber = :batchId and autoDismissed = 1" +
            "")
    fun getBatchNotifications(batchId: Int): Flow<List<NotificationEntity>>
}