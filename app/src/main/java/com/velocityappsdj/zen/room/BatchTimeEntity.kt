package com.velocityappsdj.zen.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "batch_time_entity")
data class BatchTimeEntity(
    @PrimaryKey
    var batchTime: String, var batchTimeHour: Int, var batchTimeMinutes: Int, var timeStamp: Long
)