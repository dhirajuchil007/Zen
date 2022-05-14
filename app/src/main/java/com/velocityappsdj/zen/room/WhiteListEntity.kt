package com.velocityappsdj.zen.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "white_list")
data class WhiteListEntity(
    @PrimaryKey var appId: String, var whiteListStartHour: Int, var whiteListStartMinutes: Int,
    var whiteListEndHour: Int, var whiteListEndMinutes: Int
)