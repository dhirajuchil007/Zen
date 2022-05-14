package com.velocityappsdj.zen.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification_entity")
data class NotificationEntity(
    @PrimaryKey
    var id: String,
    var title: String,
    var text: String,
    var packageName: String,
    var timestamp: Long,
    var autoDismissed: Boolean,
    var batchNumber:Int
)
