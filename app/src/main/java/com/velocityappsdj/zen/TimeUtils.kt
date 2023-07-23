package com.velocityappsdj.zen

import android.util.Log
import com.velocityappsdj.zen.room.BatchTimeEntity
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*
import java.util.concurrent.TimeUnit

object TimeUtils {
    private  val TAG = "TimeUtils"

    fun getTimePassed(providedTime: Long): String {
        var timeDiff = System.currentTimeMillis() - providedTime
        var seconds = TimeUnit.MILLISECONDS.toSeconds(timeDiff)
        if (seconds < 60) return "${seconds}s ago"

        var minutes = TimeUnit.MILLISECONDS.toMinutes(timeDiff)
        if (minutes < 60) return "${minutes}m ago"

        var hours = TimeUnit.MILLISECONDS.toHours(timeDiff)
        if (hours < 24) return "${hours}h ago"

        var days = TimeUnit.MILLISECONDS.toDays(timeDiff)
        if (days < 7) return "${days}d ago"

        return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(providedTime)

    }

    fun sortTimeOfDay(it: List<BatchTimeEntity>) =
        it.sortedWith() { a, b ->
            if (a.batchTimeHour > b.batchTimeHour)
                1
            else if (a.batchTimeHour == b.batchTimeHour) {
                if (a.batchTimeMinutes > b.batchTimeMinutes) 1 else -1
            } else -1

        }

    fun getNextBatch(
        currentTime: Long,
        sortedBatches: List<BatchTimeEntity>,
    ): BatchTimeEntity? {
        Log.d(
            TAG,
            "getNextBatch() called with: currentTime = $currentTime, sortedBatches = $sortedBatches"
        )
        if (sortedBatches.isEmpty()) return null
        for (sortedBatch in sortedBatches) {
            if (sortedBatch.timeStamp > currentTime)
                return sortedBatch
        }

        return sortedBatches[0]
    }
}