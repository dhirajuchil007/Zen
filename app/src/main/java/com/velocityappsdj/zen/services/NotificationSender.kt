package com.velocityappsdj.zen.services

import android.app.AlarmManager
import android.app.IntentService
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.velocityappsdj.zen.*
import com.velocityappsdj.zen.activities.BatchNotificationsListActivity
import com.velocityappsdj.zen.room.BatchTimeEntity
import com.velocityappsdj.zen.room.NotificationDBBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class NotificationSender : IntentService("NotificationSender") {
    private val TAG = "NotificationSender"

    @OptIn(InternalCoroutinesApi::class)
    override fun onHandleIntent(intent: Intent?) {
        var isUpdated = false
        var isAlarmSet = false
        val sharedPrefUtil = SharedPrefUtil(applicationContext)
        val batchId = sharedPrefUtil.getCurrentBatchId()
        Log.d(TAG, "onReceive: $batchId")
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            notificationId,
            BatchNotificationsListActivity.getActivityIntent(
                applicationContext, batchId
            ),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channel)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Zen test batch")
            .setAutoCancel(true)
            .setContentText("zen body").build()
        notification.contentIntent = pendingIntent
        val currentBatchPrimaryKey = sharedPrefUtil.getCurrentBatchPrimaryKey()
        val instance = NotificationDBBuilder.getInstance(applicationContext)
        if (currentBatchPrimaryKey == null)
            return

        CoroutineScope(Dispatchers.IO).launch {
            instance.batchTimeDao().getBatches().collectLatest { list ->

                if (!isUpdated) {
                    var batch = list.find { batchTimeEntity ->
                        batchTimeEntity.batchTime == currentBatchPrimaryKey
                    }
                    batch?.let {
                        var timeSTamp = it.timeStamp
                        val timestamp: Instant = Instant.ofEpochMilli(timeSTamp)
                        var zomeTime: ZonedDateTime = timestamp.atZone(ZoneId.systemDefault())
                        zomeTime = zomeTime.plusDays(1)

                        it.timeStamp = zomeTime.toInstant().toEpochMilli()
                        isUpdated = true
                        instance.batchTimeDao().updateBatch(it)
                        sharedPrefUtil.setCurrentBatchId(batchId + 1)
                    }

                    if (!isAlarmSet) {
                        var batch = TimeUtils.getNextBatch(System.currentTimeMillis(), list)
                        isAlarmSet = true
                        AlarmUtil.scheduleAlarm(batch, applicationContext)
                    }

                }
            }
        }


        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification)


    }

}