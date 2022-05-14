package com.velocityappsdj.zen.services

import android.app.Notification
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.velocityappsdj.zen.SharedPrefUtil
import com.velocityappsdj.zen.room.NotificationDBBuilder
import com.velocityappsdj.zen.room.NotificationEntity
import com.velocityappsdj.zen.room.WhiteListEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import java.util.*


class NotifListener : NotificationListenerService() {
    private val TAG = "NotifListener"


    override fun onCreate() {
        super.onCreate()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val context = this

        if (sbn?.isClearable == false || sbn == null || (sbn.notification.flags and Notification.FLAG_GROUP_SUMMARY != 0))

            return

        val packageName = sbn?.packageName ?: ""
        val key = sbn?.key

        val notification: Notification = sbn.notification
        var extras: Bundle? = notification.extras
        /*for (key in extras!!.keySet()) {
            *//* Log.d(
                "myApplication",
                "$key is ${extras.get(key)} "
            )*//*
        }*/
        val title: String = extras?.get("android.title").toString()
        val text: String = extras?.get("android.text").toString()
        Log.d(TAG, "Notification Received $title $text")
        var autoDismissed = false

        val sharedPrefUtil = SharedPrefUtil(context)
        CoroutineScope(Dispatchers.IO).launch {
            var toBeDismissed =
                withContext(Dispatchers.Default) {
                    isNotWhitelisted(packageName)
                }
            if (sharedPrefUtil.isZenModeEnabled() && toBeDismissed) {
                autoDismissed = true
                cancelNotification(sbn.key)
            }
            NotificationDBBuilder.getInstance(context).notificationDao()
                .insertNotification(
                    NotificationEntity(
                        key + title,
                        title,
                        text,
                        packageName,
                        Date().time,
                        autoDismissed,
                        sharedPrefUtil.getCurrentBatchId()
                    )
                )
        }

        //notification.contentIntent.send()
        //

    }

    private suspend fun isNotWhitelisted(packageName: String): Boolean {
        val items = NotificationDBBuilder.getInstance(this).whiteListDao().getItem(packageName)
        return if (items.isEmpty())
            true
        else
            !checkIfWhiteListedNow(items[0])

    }

    private fun checkIfWhiteListedNow(whiteListEntity: WhiteListEntity): Boolean {
        val timeNow = ZonedDateTime.now()
        val startTime = ZonedDateTime.now().withHour(whiteListEntity.whiteListStartHour)
            .withMinute(whiteListEntity.whiteListStartMinutes)
        var endTime = ZonedDateTime.now().withHour(whiteListEntity.whiteListEndHour)
            .withMinute(whiteListEntity.whiteListEndMinutes)
        if (isWhitelistedRangeSplitOver2Days(whiteListEntity)) {
            return timeNow.isAfter(startTime) || timeNow.isBefore(endTime)
        }
        return timeNow.isAfter(startTime) && timeNow.isBefore(endTime)

    }

    private fun isWhitelistedRangeSplitOver2Days(whiteListEntity: WhiteListEntity) =
        whiteListEntity.whiteListStartHour > whiteListEntity.whiteListEndHour ||
                (whiteListEntity.whiteListStartHour == whiteListEntity.whiteListEndHour) && (whiteListEntity.whiteListStartMinutes > whiteListEntity.whiteListEndMinutes)
}