package com.velocityappsdj.zen

import java.time.LocalDateTime
import java.time.ZoneId

fun LocalDateTime.getTimeInMillis(): Long {
    val zoneId: ZoneId = ZoneId.systemDefault()

    return this.atZone(zoneId).toInstant().toEpochMilli()
}